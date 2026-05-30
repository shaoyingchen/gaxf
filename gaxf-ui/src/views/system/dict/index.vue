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
                  字典管理
               </h3>
               <span class="header-count">共 {{ total }} 条记录</span>
            </div>

            <!-- 搜索区域 -->
            <div class="search-area" v-show="showSearch">
               <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-form" label-width="68px">
                  <div class="search-item">
                     <label>字典名称</label>
                     <el-input
                        v-model="queryParams.dictName"
                        placeholder="请输入字典名称"
                        clearable
                        class="search-input"
                        @keyup.enter="handleQuery"
                     />
                  </div>
                  <div class="search-item">
                     <label>字典类型</label>
                     <el-input
                        v-model="queryParams.dictType"
                        placeholder="请输入字典类型"
                        clearable
                        class="search-input"
                        @keyup.enter="handleQuery"
                     />
                  </div>
                  <div class="search-item">
                     <label>状态</label>
                     <el-select
                        v-model="queryParams.status"
                        placeholder="字典状态"
                        clearable
                        class="search-input search-select"
                     >
                        <el-option
                           v-for="dict in sys_normal_disable"
                           :key="dict.value"
                           :label="dict.label"
                           :value="dict.value"
                        />
                     </el-select>
                  </div>
                  <div class="search-item">
                     <label>创建时间</label>
                     <el-date-picker
                        v-model="dateRange"
                        value-format="YYYY-MM-DD"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        class="search-input"
                        style="width: 220px"
                     ></el-date-picker>
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
                  <el-button type="primary" @click="handleAdd" v-hasPermi="['system:dict:add']">
                     <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 5v14M5 12h14"></path>
                     </svg>
                     新增
                  </el-button>
                  <el-button type="success" :disabled="single" @click="handleUpdate" v-hasPermi="['system:dict:edit']">
                     <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                     </svg>
                     修改
                  </el-button>
                  <el-button type="danger" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:dict:remove']">
                     <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 6h18M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                     </svg>
                     删除
                  </el-button>
                  <el-button type="warning" @click="handleExport" v-hasPermi="['system:dict:export']">
                     <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                        <polyline points="7 10 12 15 17 10"></polyline>
                        <line x1="12" y1="15" x2="12" y2="3"></line>
                     </svg>
                     导出
                  </el-button>
                  <el-button type="danger" @click="handleRefreshCache" v-hasPermi="['system:dict:remove']">
                     <svg class="icon-small" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"></path>
                        <path d="M21 3v5h-5"></path>
                        <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"></path>
                        <path d="M3 21v-5h5"></path>
                     </svg>
                     刷新缓存
                  </el-button>
               </div>
               <div class="toolbar-right">
                  <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
               </div>
            </div>

            <!-- 表格区域 -->
            <div class="table-wrapper">
               <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
                  <el-table-column type="selection" width="55" align="center" />
                  <el-table-column label="字典编号" align="center" prop="dictId" />
                  <el-table-column label="字典名称" align="center" prop="dictName" :show-overflow-tooltip="true"/>
                  <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
                    <template #default="scope">
                      <a class="link-type" style="cursor:pointer" @click="handleViewData(scope.row)">{{ scope.row.dictType }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" align="center" prop="status">
                     <template #default="scope">
                        <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
                     </template>
                  </el-table-column>
                  <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
                  <el-table-column label="创建时间" align="center" prop="createTime" width="180">
                     <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime) }}</span>
                     </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
                     <template #default="scope">
                        <div class="table-action">
                           <el-tooltip content="修改" placement="top">
                              <button class="action-icon-btn" @click="handleUpdate(scope.row)" v-hasPermi="['system:dict:edit']">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                 </svg>
                              </button>
                           </el-tooltip>
                           <el-tooltip content="列表" placement="top">
                              <button class="action-icon-btn" @click="handleDataList(scope.row)" v-hasPermi="['system:dict:edit']">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M4 6h16M4 10h16M4 14h16M4 18h16"></path>
                                 </svg>
                              </button>
                           </el-tooltip>
                           <el-tooltip content="删除" placement="top">
                              <button class="action-icon-btn" @click="handleDelete(scope.row)" v-hasPermi="['system:dict:remove']">
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

            <!-- 分页区域 -->
            <div class="pagination-bar">
               <span class="page-info">显示 {{ (queryParams.pageNum - 1) * queryParams.pageSize + 1 }} - {{ Math.min(queryParams.pageNum * queryParams.pageSize, total) }} 条，共 {{ total }} 条</span>
               <pagination
                  v-show="total > 0"
                  :total="total"
                  v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize"
                  @pagination="getList"
               />
            </div>
         </div>
      </div>

      <!-- 添加或修改参数配置对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="dictRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="字典名称" prop="dictName">
               <el-input v-model="form.dictName" placeholder="请输入字典名称" />
            </el-form-item>
            <el-form-item prop="dictName">
               <el-input v-model="form.dictType" placeholder="请输入字典类型" />
               <template #label>
                 <span>
                   <el-tooltip content='数据存储中的Key值，如：sys_user_sex' placement="top">
                     <el-icon><question-filled /></el-icon>
                   </el-tooltip>
                   字典类型
                 </span>
               </template>
            </el-form-item>
            <el-form-item label="状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio
                     v-for="dict in sys_normal_disable"
                     :key="dict.value"
                     :value="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" @click="submitForm">确 定</el-button>
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>

      <dict-data-drawer v-model:visible="drawerVisible" :row="drawerRow" />
   </div>
</template>

<script setup lang="ts" name="Dict">
import DictDataDrawer from './detail'
import useDictStore from '@/store/modules/dict'
import { listType, getType, delType, addType, updateType, refreshCache } from "@/api/system/dict/type"
import type { SysDictType, DictTypeQueryParams } from '@/types/api/system/dict'
import '@/assets/styles/police-system.scss'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const typeList = ref<SysDictType[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const dateRange = ref<string[]>([])
const drawerVisible = ref<boolean>(false)
const drawerRow = ref<SysDictType[]>([])

const data = reactive({
  form: {} as SysDictType,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    dictName: undefined,
    dictType: undefined,
    status: undefined
  } as DictTypeQueryParams,
  rules: {
    dictName: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
    dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }]
  },
})

const { queryParams, form, rules } = toRefs(data)

/** 查询字典类型列表 */
function getList() {
  loading.value = true
  listType(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    typeList.value = response.rows
    total.value = response.total
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
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("dictRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = []
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加字典类型"
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysDictType[]) {
  ids.value = selection.map(item => item.dictId!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 字典数据抽屉 */
function handleViewData(row: SysDictType) {
  drawerRow.value = row
  drawerVisible.value = true
}

/** 字典数据列表页面 */
function handleDataList(row: SysDictType) {
  proxy.$tab.openPage("字典数据", '/system/dict-data/index/' + row.dictId)
}

/** 修改按钮操作 */
function handleUpdate(row?: SysDictType) {
  reset()
  const dictId = row?.dictId || ids.value[0]
  getType(dictId).then(response=> {
    form.value = response.data!
    open.value = true
    title.value = "修改字典类型"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dictRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.dictId != undefined) {
        updateType(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addType(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row?: SysDictType) {
  const dictIds = row?.dictId || ids.value
  proxy.$modal.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项？').then(function() {
    return delType(dictIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/dict/type/export", {
    ...queryParams.value
  }, `dict_${new Date().getTime()}.xlsx`)
}

/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    proxy.$modal.msgSuccess("刷新成功")
    useDictStore().cleanDict()
  })
}

getList()
</script>

<style lang="scss" scoped>
.header-count {
  font-size: 12px;
  color: #5B6E8C;
  background: rgba(37, 99, 235, 0.1);
  padding: 4px 12px;
  border-radius: 20px;
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

.search-select {
  width: 140px;
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

.link-type {
  color: #2563EB;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}

.pagination-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  font-size: 12px;
  color: #5B6E8C;
}
</style>