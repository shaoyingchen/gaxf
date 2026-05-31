<template>
   <div class="police-page">
      <div class="content-grid full-width">
         <div class="main-panel">
            <!-- 面板头部 -->
            <div class="panel-header">
               <h3>
                  <svg class="icon-medium" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                     <path d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                  </svg>
                  部门管理
                  <span class="status-tag info" v-if="deptList.length">共 {{ deptList.length }} 个部门</span>
               </h3>
               <div class="header-actions">
                  <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
               </div>
            </div>

            <!-- 搜索区域 -->
            <div class="search-area" v-show="showSearch">
               <el-form :model="queryParams" ref="queryRef" class="search-form">
                  <div class="search-item">
                     <label>部门名称</label>
                     <el-input
                        v-model="queryParams.deptName"
                        placeholder="请输入部门名称"
                        clearable
                        class="search-input"
                        @keyup.enter="handleQuery"
                     />
                  </div>
                  <div class="search-item">
                     <label>状态</label>
                     <el-select v-model="queryParams.status" placeholder="部门状态" clearable class="search-select">
                        <el-option
                           v-for="dict in sys_normal_disable"
                           :key="dict.value"
                           :label="dict.label"
                           :value="dict.value"
                        />
                     </el-select>
                  </div>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
               </el-form>
            </div>

            <!-- 工具栏 -->
            <div class="toolbar">
               <div class="toolbar-left">
                  <el-button
                     type="primary"
                     plain
                     icon="Plus"
                     @click="handleAdd"
                    
                  >新增</el-button>
                  <el-button
                     type="warning"
                     plain
                     icon="Check"
                     @click="handleSaveSort"
                    
                  >保存排序</el-button>
                  <el-button
                     type="info"
                     plain
                     icon="Sort"
                     @click="toggleExpandAll"
                  >展开/折叠</el-button>
               </div>
            </div>

            <!-- 表格区域 -->
            <div class="table-wrapper">
               <el-table
                  v-if="refreshTable"
                  v-loading="loading"
                  :data="deptList"
                  row-key="deptId"
                  :default-expand-all="isExpandAll"
                  :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
               >
                  <el-table-column prop="deptName" label="部门名称" min-width="200"></el-table-column>
                  <el-table-column prop="orderNum" label="排序" width="120">
                     <template #default="scope">
                        <el-input-number v-model="scope.row.orderNum" controls-position="right" :min="0" style="width: 88px" />
                     </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="100">
                     <template #default="scope">
                        <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
                     </template>
                  </el-table-column>
                  <el-table-column label="创建时间" align="center" prop="createTime" width="180">
                     <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime) }}</span>
                     </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="130">
                     <template #default="scope">
                        <div class="table-action">
                           <el-tooltip content="修改" placement="top">
                              <button class="action-icon-btn edit" @click="handleUpdate(scope.row)">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                 </svg>
                              </button>
                           </el-tooltip>
                           <el-tooltip content="新增" placement="top">
                              <button class="action-icon-btn" @click="handleAdd(scope.row)">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <line x1="12" y1="5" x2="12" y2="19"/>
                                    <line x1="5" y1="12" x2="19" y2="12"/>
                                 </svg>
                              </button>
                           </el-tooltip>
                           <el-tooltip content="删除" placement="top" v-if="scope.row.parentId != 0">
                              <button class="action-icon-btn delete" @click="handleDelete(scope.row)">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
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

      <!-- 添加或修改部门对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
         <el-form ref="deptRef" :model="form" :rules="rules" label-width="80px">
            <el-row>
               <el-col :span="24" v-if="form.parentId !== 0">
                  <el-form-item label="上级部门" prop="parentId">
                     <el-tree-select
                        v-model="form.parentId"
                        :data="deptOptions"
                        :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                        value-key="deptId"
                        placeholder="选择上级部门"
                        check-strictly
                     />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="部门名称" prop="deptName">
                     <el-input v-model="form.deptName" placeholder="请输入部门名称" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="显示排序" prop="orderNum">
                     <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="负责人" prop="leader">
                     <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="联系电话" prop="phone">
                     <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="邮箱" prop="email">
                     <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
                  </el-form-item>
               </el-col>
               <el-col :span="12">
                  <el-form-item label="部门状态">
                     <el-radio-group v-model="form.status">
                        <el-radio
                           v-for="dict in sys_normal_disable"
                           :key="dict.value"
                           :value="dict.value"
                        >{{ dict.label }}</el-radio>
                     </el-radio-group>
                  </el-form-item>
               </el-col>
            </el-row>
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

<script setup lang="ts" name="Dept">
import '@/assets/styles/police-system.scss'
import { listDept, getDept, delDept, addDept, updateDept, updateDeptSort, listDeptExcludeChild } from "@/api/system/dept"
import type { SysDept, DeptQueryParams } from '@/types/api/system/dept'
import type { TreeSelect } from '@/types/api/common'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const deptList = ref<any[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const title = ref<string>("")
const deptOptions = ref<TreeSelect[]>([])
const isExpandAll = ref<boolean>(true)
const refreshTable = ref<boolean>(true)
const originalOrders = ref<Record<number, number>>({})

const data = reactive({
  form: {} as SysDept,
  queryParams: {
    deptName: undefined,
    status: undefined
  } as DeptQueryParams,
  rules: {
    parentId: [{ required: true, message: "上级部门不能为空", trigger: "blur" }],
    deptName: [{ required: true, message: "部门名称不能为空", trigger: "blur" }],
    orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
    email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
    phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询部门列表 */
function getList() {
  loading.value = true
  listDept(queryParams.value).then(response => {
    deptList.value = proxy.handleTree(response.data, "deptId")
    recordOriginalOrders(deptList.value)
    loading.value = false
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
    deptId: undefined,
    parentId: undefined,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: "0"
  }
  proxy.resetForm("deptRef")
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
function handleAdd(row?: SysDept) {
  reset()
  listDept().then(response => {
    deptOptions.value = proxy.handleTree(response.data, "deptId")
  })
  if (row != undefined) {
    form.value.parentId = row.deptId
  }
  open.value = true
  title.value = "添加部门"
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

/** 修改按钮操作 */
function handleUpdate(row: SysDept) {
  reset()
  listDeptExcludeChild(row.deptId!).then(response => {
    deptOptions.value = proxy.handleTree(response.data, "deptId")
  })
  getDept(row.deptId!).then(response => {
    form.value = response.data!
    open.value = true
    title.value = "修改部门"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["deptRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.deptId != undefined) {
        updateDept(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDept(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 递归记录原始排序 */
function recordOriginalOrders(list: SysDept[]) {
  list.forEach(item => {
    originalOrders.value[item.deptId] = item.orderNum
    if (item.children && item.children.length) {
      recordOriginalOrders(item.children)
    }
  })
}

/** 保存排序 */
function handleSaveSort() {
  const changedDeptIds: number[] = []
  const changedOrderNums: number[] = []
  const collectChanged = (list: SysDept[]) => {
    list.forEach(item => {
      if (String(originalOrders.value[item.deptId!]) !== String(item.orderNum)) {
        changedDeptIds.push(item.deptId!)
        changedOrderNums.push(item.orderNum!)
      }
      if (item.children && item.children.length) {
        collectChanged(item.children)
      }
    })
  }
  collectChanged(deptList.value)
  if (changedDeptIds.length === 0) {
   proxy.$modal.msgWarning("未检测到排序修改")
    return
  }
  updateDeptSort({ deptIds: changedDeptIds.join(","), orderNums: changedOrderNums.join(",") }).then(() => {
   proxy.$modal.msgSuccess("排序保存成功")
    recordOriginalOrders(deptList.value)
  })
}

/** 删除按钮操作 */
function handleDelete(row: SysDept) {
  proxy.$modal.confirm('是否确认删除名称为"' + row.deptName + '"的数据项?').then(function() {
    return delDept(row.deptId!)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

getList()
</script>