<template>
  <div class="police-page">
    <div class="content-grid full-width">
      <div class="main-panel">
        <!-- 面板头部 -->
        <div class="panel-header">
          <h3>
            <svg class="icon-medium" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
            </svg>
            字典分类管理
          </h3>
        </div>

        <!-- Tab切换 -->
        <el-tabs v-model="activeKind" class="dict-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="问题分类" :name="1" />
          <el-tab-pane label="设备分类" :name="2" />
          <el-tab-pane label="预警分类" :name="3" />
        </el-tabs>

        <!-- 搜索区域 -->
        <div class="search-area" v-show="showSearch">
          <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-form">
            <div class="search-item">
              <label>名称</label>
              <el-input
                v-model="queryParams.name"
                placeholder="请输入名称"
                clearable
                class="search-input"
                @keyup.enter="handleQuery"
              />
            </div>
            <div class="search-buttons">
              <el-button type="primary" @click="handleQuery">
                <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8"></circle>
                  <path d="m21 21-4.35-4.35"></path>
                </svg>
                搜索
              </el-button>
              <el-button @click="resetQuery">
                <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"></path>
                  <path d="M21 3v5h-5"></path>
                  <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"></path>
                  <path d="M3 21v-5h5"></path>
                </svg>
                重置
              </el-button>
            </div>
          </el-form>
        </div>

        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <el-button type="primary" @click="handleAdd" v-hasPermi="['spdc:dict:add']">
              <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 5v14M5 12h14"></path>
              </svg>
              新增
            </el-button>
            <el-button type="success" :disabled="single" @click="handleUpdate" v-hasPermi="['spdc:dict:edit']">
              <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
              </svg>
              修改
            </el-button>
            <el-button type="danger" :disabled="multiple" @click="handleDelete" v-hasPermi="['spdc:dict:remove']">
              <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
              </svg>
              删除
            </el-button>
          </div>
          <div class="toolbar-right">
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </div>
        </div>

        <!-- 表格区域 -->
        <div class="table-wrapper">
          <el-table
            v-loading="loading"
            :data="dictList"
            row-key="id"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            default-expand-all
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="名称" align="left" prop="name" :show-overflow-tooltip="true" />
            <el-table-column label="排序" align="center" prop="sort" width="80" />
            <el-table-column label="创建时间" align="center" prop="createTime" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
              <template #default="scope">
                <div class="table-action">
                  <el-tooltip content="新增" placement="top">
                    <button class="action-icon-btn" @click="handleAdd(scope.row)" v-hasPermi="['spdc:dict:add']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 5v14M5 12h14"></path>
                      </svg>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="修改" placement="top">
                    <button class="action-icon-btn" @click="handleUpdate(scope.row)" v-hasPermi="['spdc:dict:edit']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                      </svg>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <button class="action-icon-btn" @click="handleDelete(scope.row)" v-hasPermi="['spdc:dict:remove']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      </svg>
                    </button>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级节点" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="dictOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            value-key="id"
            placeholder="选择上级节点"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="DictClassify">
import { listDictTree, getDict, addDict, updateDict, delDict } from "@/api/spdc/dict"
import type { DcDict, DictQueryParams } from '@/types/api/spdc/dict'
import { getKindName } from '@/types/api/spdc/dict'
import '@/assets/styles/police-system.scss'

const { proxy } = getCurrentInstance()

const dictList = ref<DcDict[]>([])
const dictOptions = ref<DcDict[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const title = ref<string>("")
const activeKind = ref<number>(1)

const data = reactive({
  form: {} as DcDict,
  queryParams: {
    kind: 1,
    name: undefined
  } as DictQueryParams,
  rules: {
    name: [{ required: true, message: "名称不能为空", trigger: "blur" }]
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询字典树列表 */
function getList() {
  loading.value = true
  listDictTree(queryParams.value).then(response => {
    dictList.value = response.data
    loading.value = false
  })
}

/** 查询字典下拉树结构 */
function getDictTree() {
  listDictTree({ kind: activeKind.value }).then(response => {
    dictOptions.value = response.data
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    parentId: undefined,
    name: undefined,
    kind: activeKind.value,
    sort: 0
  }
  proxy.resetForm("dictRef")
}

/** Tab切换 */
function handleTabChange(tabName: number) {
  queryParams.value.kind = tabName
  getList()
}

/** 搜索按钮操作 */
function handleQuery() {
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd(row?: DcDict) {
  reset()
  getDictTree()
  if (row != null && row.id) {
    form.value.parentId = row.id
  }
  open.value = true
  title.value = "添加" + getKindName(activeKind.value)
}

/** 多选框选中数据 */
function handleSelectionChange(selection: DcDict[]) {
  ids.value = selection.map(item => item.id!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 修改按钮操作 */
function handleUpdate(row?: DcDict) {
  reset()
  getDictTree()
  const id = row?.id || ids.value[0]
  getDict(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改" + getKindName(activeKind.value)
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dictRef"].validate((valid: boolean) => {
    if (valid) {
      form.value.kind = activeKind.value
      if (form.value.id != undefined) {
        updateDict(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDict(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row?: DcDict) {
  const dictIds = row?.id || ids.value
  proxy.$modal.confirm('是否确认删除选中的数据项？').then(function() {
    return delDict(dictIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>

<style lang="scss" scoped>
.dict-tabs {
  margin-bottom: 16px;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
    background: #f8fafc;
    border-radius: 8px 8px 0 0;
  }

  :deep(.el-tabs__nav-wrap) {
    padding: 0 16px;
  }

  :deep(.el-tabs__item) {
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    color: #5B6E8C;

    &.is-active {
      color: #2563EB;
      font-weight: 500;
    }
  }
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-end;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 6px;

  label {
    font-size: 12px;
    color: #5B6E8C;
    white-space: nowrap;
  }
}

.search-input {
  width: 180px;
}

.search-buttons {
  display: flex;
  gap: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-left {
  display: flex;
  gap: 8px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.icon-small {
  width: 14px;
  height: 14px;
}

.icon-medium {
  width: 18px;
  height: 18px;
}
</style>