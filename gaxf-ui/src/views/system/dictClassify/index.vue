<template>
  <div class="app-container dict-classify-page">
    <el-row :gutter="16">
      <!-- 左侧：分类树 -->
      <el-col :span="6">
        <el-card shadow="hover" class="tree-card">
          <template #header>
            <div class="card-header">
              <span>字典分类</span>
              <el-button type="primary" link @click="handleAddKind">
                <el-icon><Plus /></el-icon> 新增分类
              </el-button>
            </div>
          </template>
          <el-input
            v-model="filterText"
            placeholder="搜索分类"
            clearable
            class="tree-filter"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-tree
            ref="treeRef"
            :data="kindTree"
            :props="{ label: 'name', children: 'children' }"
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            highlight-current
            node-key="id"
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <span class="node-label">{{ node.label }}</span>
                <span class="node-actions">
                  <el-button type="primary" link size="small" @click.stop="handleAddChild(data)">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                  <el-button type="primary" link size="small" @click.stop="handleEdit(data)">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="handleDelete(data)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧：字典列表 -->
      <el-col :span="18">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>{{ currentKindName ? currentKindName + ' - 字典项' : '字典项列表' }}</span>
              <div class="header-actions">
                <el-button type="primary" :disabled="!currentKindId" @click="handleAddItem">
                  <el-icon><Plus /></el-icon> 新增
                </el-button>
              </div>
            </div>
          </template>

          <!-- 搜索 -->
          <el-form v-show="showSearch" :model="queryParams" :inline="true" class="search-form">
            <el-form-item label="名称" prop="name">
              <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 表格 -->
          <el-table v-loading="loading" :data="itemList" border stripe>
            <el-table-column label="序号" type="index" width="60" align="center" />
            <el-table-column label="名称" prop="name" min-width="160" show-overflow-tooltip />
            <el-table-column label="所属分类" prop="parentName" width="140" align="center" />
            <el-table-column label="排序" prop="sort" width="80" align="center" />
            <el-table-column label="创建时间" prop="createTime" width="170" align="center" />
            <el-table-column label="操作" width="160" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">
                  <el-icon><Edit /></el-icon> 修改
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/修改对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级节点" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="kindTreeForSelect"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择上级节点"
            check-strictly
            clearable
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="字典种类" prop="kind">
          <el-select v-model="form.kind" placeholder="请选择种类" class="w-full">
            <el-option label="问题分类" :value="1" />
            <el-option label="设备分类" :value="2" />
            <el-option label="预警分类" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="DictClassify">
import { treeDcDict, listDcDict, getDcDict, addDcDict, updateDcDict, delDcDict } from '@/api/xf/dictClassify'
import type { DcDict } from '@/api/xf/dictClassify'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const showSearch = ref(true)
const filterText = ref('')
const treeRef = ref()
const kindTree = ref<DcDict[]>([])
const itemList = ref<DcDict[]>([])
const currentKindId = ref<number | undefined>()
const currentKindName = ref('')

// 查询参数
const queryParams = ref({
  name: undefined as string | undefined,
  kind: undefined as number | undefined,
  parentId: undefined as number | undefined,
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref<DcDict>({})
const formRef = ref()

const rules = {
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
}

// 用于 el-tree-select 的树数据（增加顶级节点）
const kindTreeForSelect = computed(() => {
  return [{ id: 0, name: '顶级', children: kindTree.value }]
})

// 树搜索过滤
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

function filterNode(value: string, data: any) {
  if (!value) return true
  return data.name?.includes(value)
}

// 获取分类树
function getTree() {
  treeDcDict().then(res => {
    kindTree.value = handleTree(res.data, 'id', 'parentId')
  })
}

// 获取字典项列表
function getList() {
  loading.value = true
  const params: any = {}
  if (currentKindId.value !== undefined) {
    params.parentId = currentKindId.value
  }
  if (queryParams.value.name) {
    params.name = queryParams.value.name
  }
  if (queryParams.value.kind) {
    params.kind = queryParams.value.kind
  }
  listDcDict(params).then(res => {
    itemList.value = res.data || []
  }).finally(() => {
    loading.value = false
  })
}

// 树节点点击
function handleNodeClick(data: DcDict) {
  currentKindId.value = data.id
  currentKindName.value = data.name || ''
  queryParams.value.parentId = data.id
  getList()
}

// 搜索
function handleQuery() {
  getList()
}

// 重置搜索
function resetQuery() {
  queryParams.value = { name: undefined, kind: undefined, parentId: currentKindId.value }
  getList()
}

// 新增分类（顶级）
function handleAddKind() {
  resetForm()
  form.value.parentId = 0
  dialogTitle.value = '新增分类'
  dialogVisible.value = true
}

// 新增子节点
function handleAddChild(data: DcDict) {
  resetForm()
  form.value.parentId = data.id
  form.value.kind = data.kind
  dialogTitle.value = '新增子项'
  dialogVisible.value = true
}

// 新增字典项（右侧列表）
function handleAddItem() {
  resetForm()
  form.value.parentId = currentKindId.value
  dialogTitle.value = '新增字典项'
  dialogVisible.value = true
}

// 编辑
function handleEdit(data: DcDict) {
  resetForm()
  getDcDict(data.id!).then(res => {
    form.value = res.data
    dialogTitle.value = '修改'
    dialogVisible.value = true
  })
}

// 删除
function handleDelete(data: DcDict) {
  proxy.$modal.confirm('是否确认删除该条数据？').then(() => {
    return delDcDict([data.id!])
  }).then(() => {
    getTree()
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

// 提交表单
function submitForm() {
  formRef.value?.validate((valid: boolean) => {
    if (!valid) return
    if (form.value.id) {
      updateDcDict(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        dialogVisible.value = false
        getTree()
        getList()
      })
    } else {
      addDcDict(form.value).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        dialogVisible.value = false
        getTree()
        getList()
      })
    }
  })
}

// 重置表单
function resetForm() {
  form.value = {
    parentId: 0,
    name: undefined,
    kind: undefined,
    sort: 0,
  }
}

// handleTree 工具函数
function handleTree(data: DcDict[], idKey = 'id', parentKey = 'parentId', childrenKey = 'children') {
  const result: DcDict[] = []
  const map: Record<number, DcDict> = {}

  data.forEach(item => {
    map[item[idKey] as number] = { ...item, [childrenKey]: [] }
  })

  data.forEach(item => {
    const node = map[item[idKey] as number]
    const parentId = item[parentKey] as number
    if (parentId && map[parentId]) {
      map[parentId][childrenKey]!.push(node)
    } else {
      result.push(node)
    }
  })

  return result
}

onMounted(() => {
  getTree()
  getList()
})
</script>

<style scoped lang="scss">
.dict-classify-page {
  padding: 16px;
}

.tree-card {
  :deep(.el-card__body) {
    padding: 12px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
}

.tree-filter {
  margin-bottom: 12px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  font-size: 14px;

  .node-label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .node-actions {
    display: none;
    margin-left: 8px;
  }

  &:hover .node-actions {
    display: inline-flex;
  }
}

.header-actions {
  display: flex;
  gap: 8px;
}

.search-form {
  margin-bottom: 12px;
}

.w-full {
  width: 100%;
}
</style>
