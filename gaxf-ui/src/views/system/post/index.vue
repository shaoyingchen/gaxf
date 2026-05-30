<template>
   <div class="police-page">
      <div class="content-grid full-width">
         <div class="main-panel">
            <!-- 面板头部 -->
            <div class="panel-header">
               <h3>
                  <svg class="icon-medium" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                     <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                     <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                  </svg>
                  岗位管理
                  <span class="total-count">共 {{ total }} 条</span>
               </h3>
               <div class="header-actions">
                  <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
               </div>
            </div>

            <!-- 搜索区域 -->
            <div class="search-area" v-show="showSearch">
               <el-form :model="queryParams" ref="queryRef" class="search-form" :inline="true">
                  <el-form-item label="岗位编码" prop="postCode">
                     <el-input
                        v-model="queryParams.postCode"
                        placeholder="请输入岗位编码"
                        clearable
                        style="width: 180px"
                        @keyup.enter="handleQuery"
                     />
                  </el-form-item>
                  <el-form-item label="岗位名称" prop="postName">
                     <el-input
                        v-model="queryParams.postName"
                        placeholder="请输入岗位名称"
                        clearable
                        style="width: 180px"
                        @keyup.enter="handleQuery"
                     />
                  </el-form-item>
                  <el-form-item label="状态" prop="status">
                     <el-select v-model="queryParams.status" placeholder="岗位状态" clearable style="width: 140px">
                        <el-option
                           v-for="dict in sys_normal_disable"
                           :key="dict.value"
                           :label="dict.label"
                           :value="dict.value"
                        />
                     </el-select>
                  </el-form-item>
                  <el-form-item>
                     <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                     <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                  </el-form-item>
               </el-form>
            </div>

            <!-- 工具栏 -->
            <div class="toolbar">
               <div class="toolbar-left">
                  <el-button
                     type="primary"
                     icon="Plus"
                     @click="handleAdd"
                     v-hasPermi="['system:post:add']"
                  >新增</el-button>
                  <el-button
                     type="success"
                     icon="Edit"
                     :disabled="single"
                     @click="handleUpdate"
                     v-hasPermi="['system:post:edit']"
                  >修改</el-button>
                  <el-button
                     type="danger"
                     icon="Delete"
                     :disabled="multiple"
                     @click="handleDelete"
                     v-hasPermi="['system:post:remove']"
                  >删除</el-button>
                  <el-button
                     type="warning"
                     icon="Download"
                     @click="handleExport"
                     v-hasPermi="['system:post:export']"
                  >导出</el-button>
               </div>
            </div>

            <!-- 表格区域 -->
            <div class="table-wrapper">
               <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
                  <el-table-column type="selection" width="55" align="center" />
                  <el-table-column label="岗位编号" align="center" prop="postId" />
                  <el-table-column label="岗位编码" align="center" prop="postCode" />
                  <el-table-column label="岗位名称" align="center" prop="postName" />
                  <el-table-column label="岗位排序" align="center" prop="postSort" />
                  <el-table-column label="状态" align="center" prop="status">
                     <template #default="scope">
                        <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
                     </template>
                  </el-table-column>
                  <el-table-column label="创建时间" align="center" prop="createTime" width="180">
                     <template #default="scope">
                        <span>{{ parseTime(scope.row.createTime) }}</span>
                     </template>
                  </el-table-column>
                  <el-table-column label="操作" align="center" width="120">
                     <template #default="scope">
                        <div class="table-action">
                           <el-tooltip content="修改" placement="top">
                              <button class="action-icon-btn edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:post:edit']">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                              </button>
                           </el-tooltip>
                           <el-tooltip content="删除" placement="top">
                              <button class="action-icon-btn delete" @click="handleDelete(scope.row)" v-hasPermi="['system:post:remove']">
                                 <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                              </button>
                           </el-tooltip>
                        </div>
                     </template>
                  </el-table-column>
               </el-table>
            </div>

            <!-- 分页区域 -->
            <div class="pagination-bar">
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

      <!-- 添加或修改岗位对话框 -->
      <el-dialog :title="title" v-model="open" width="500px" append-to-body>
         <el-form ref="postRef" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="岗位名称" prop="postName">
               <el-input v-model="form.postName" placeholder="请输入岗位名称" />
            </el-form-item>
            <el-form-item label="岗位编码" prop="postCode">
               <el-input v-model="form.postCode" placeholder="请输入编码名称" />
            </el-form-item>
            <el-form-item label="岗位顺序" prop="postSort">
               <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
            </el-form-item>
            <el-form-item label="岗位状态" prop="status">
               <el-radio-group v-model="form.status">
                  <el-radio
                     v-for="dict in sys_normal_disable"
                     :key="dict.value"
                     :value="dict.value"
                  >{{ dict.label }}</el-radio>
               </el-radio-group>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
               <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

<script setup lang="ts" name="Post">
import { listPost, addPost, delPost, getPost, updatePost } from "@/api/system/post"
import type { SysPost, PostQueryParams } from '@/types/api/system/post'
import '@/assets/styles/police-system.scss'

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const postList = ref<SysPost[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")

const data = reactive({
  form: {} as SysPost,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    postCode: undefined,
    postName: undefined,
    status: undefined
  } as PostQueryParams,
  rules: {
    postName: [{ required: true, message: "岗位名称不能为空", trigger: "blur" }],
    postCode: [{ required: true, message: "岗位编码不能为空", trigger: "blur" }],
    postSort: [{ required: true, message: "岗位顺序不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询岗位列表 */
function getList() {
  loading.value = true
  listPost(queryParams.value).then(response => {
    postList.value = response.rows
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
    postId: undefined,
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: "0",
    remark: undefined
  }
  proxy.resetForm("postRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection: SysPost[]) {
  ids.value = selection.map(item => item.postId!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加岗位"
}

/** 修改按钮操作 */
function handleUpdate(row?: SysPost) {
  reset()
  const postId = row?.postId || ids.value[0]
  getPost(postId).then(response => {
    form.value = response.data!
    open.value = true
    title.value = "修改岗位"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["postRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.postId != undefined) {
        updatePost(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPost(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row?: SysPost) {
  const postIds = row?.postId || ids.value
  proxy.$modal.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？').then(function() {
    return delPost(postIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/post/export", {
    ...queryParams.value
  }, `post_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped lang="scss">
.total-count {
  font-size: 12px;
  font-weight: normal;
  color: #5B6E8C;
  margin-left: 8px;
  padding: 2px 8px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 12px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
}
</style>