<template>
  <div class="police-page">
    <div class="content-grid">
      <!-- 左侧：部门树 -->
      <div class="side-panel">
        <div class="panel-header">
          <h3>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
            部门目录
          </h3>
        </div>
        <div class="tree-search">
          <svg class="search-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input v-model="deptName" placeholder="搜索部门..." />
        </div>
        <div v-if="deptLoading" class="loading-state">
          <svg class="spin-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 12a9 9 0 1 1-6-2.6"/>
          </svg>
          <span>加载中...</span>
        </div>
        <div class="dept-tree-wrapper" v-else>
          <DeptTreeItem
            v-for="dept in filteredDepts"
            :key="dept.id"
            :node="dept"
            :selected-id="queryParams.deptId"
            :depth="0"
            :load-children="loadDeptChildren"
            @select="handleDeptSelect"
          />
          <div v-if="filteredDepts.length === 0 && !deptLoading" class="empty-state">
            无匹配部门
          </div>
        </div>
      </div>

      <!-- 右侧：用户列表 -->
      <div class="main-panel">
        <div class="panel-header">
          <h3>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
            用户管理
          </h3>
          <span class="total-count">共 {{ total }} 条</span>
        </div>

        <!-- 搜索区域 -->
        <div class="search-area">
          <div class="search-form">
            <div class="search-item">
              <label>用户名称</label>
              <input v-model="queryParams.userName" class="search-input" placeholder="请输入" @keyup.enter="handleQuery" />
            </div>
            <div class="search-item">
              <label>手机号码</label>
              <input v-model="queryParams.phonenumber" class="search-input" placeholder="请输入" @keyup.enter="handleQuery" />
            </div>
            <div class="search-item">
              <label>状态</label>
              <el-select v-model="queryParams.status" class="search-select" placeholder="用户状态" clearable>
                <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </div>
            <div class="search-item">
              <label>创建时间</label>
              <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始" end-placeholder="结束" style="width: 200px" />
            </div>
            <button class="police-btn primary" @click="handleQuery">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              搜索
            </button>
            <button class="police-btn" @click="resetQuery">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                <path d="M3 3v5h5"/>
              </svg>
              重置
            </button>
          </div>
        </div>

        <!-- 工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <button class="police-btn primary" @click="handleAdd" v-hasPermi="['system:user:add']">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              新增
            </button>
            <button class="police-btn success" :disabled="single" @click="handleUpdate" v-hasPermi="['system:user:edit']">修改</button>
            <button class="police-btn danger" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:user:remove']">删除</button>
            <button class="police-btn info" @click="handleImport" v-hasPermi="['system:user:import']">导入</button>
            <button class="police-btn warning" @click="handleExport" v-hasPermi="['system:user:export']">导出</button>
          </div>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </div>

        <!-- 表格区域 -->
        <div class="table-wrapper">
          <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户编号" align="center" key="userId" prop="userId" v-if="columns.userId.visible" />
            <el-table-column label="用户名称" align="center" key="userName" prop="userName" v-if="columns.userName.visible" :show-overflow-tooltip="true" />
            <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" v-if="columns.nickName.visible" :show-overflow-tooltip="true" />
            <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" v-if="columns.deptName.visible" :show-overflow-tooltip="true" />
            <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" v-if="columns.phonenumber.visible" width="120" />
            <el-table-column label="状态" align="center" key="status" v-if="columns.status.visible" width="100">
              <template #default="scope">
                <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)" style="width: 40px" />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" v-if="columns.createTime.visible" width="160">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="160">
              <template #default="scope">
                <div class="table-action" v-if="scope.row.userId !== 1">
                  <el-tooltip content="修改" placement="top">
                    <button class="action-icon-btn edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:user:edit']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top">
                    <button class="action-icon-btn delete" @click="handleDelete(scope.row)" v-hasPermi="['system:user:remove']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="重置密码" placement="top">
                    <button class="action-icon-btn" @click="handleResetPwd(scope.row)" v-hasPermi="['system:user:resetPwd']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L7.5 14.5"/></svg>
                    </button>
                  </el-tooltip>
                  <el-tooltip content="分配角色" placement="top">
                    <button class="action-icon-btn primary" @click="handleAuthRole(scope.row)" v-hasPermi="['system:user:edit']">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><polyline points="17 11 19 13 23 9"/></svg>
                    </button>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-bar">
          <span class="page-info">第 {{ queryParams.pageNum }} 页 / 每页 {{ queryParams.pageSize }} 条</span>
          <el-pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" layout="prev, pager, next" />
        </div>
      </div>
    </div>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="userRef" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select v-model="form.deptId" :data="enabledDeptOptions" :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" placeholder="请选择归属部门" clearable check-strictly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option v-for="item in postOptions" :key="item.postId" :label="item.postName" :value="item.postId" :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status == 1"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
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

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="upload.headers" :action="upload.url + '?updateSupport=' + upload.updateSupport" :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess" :on-change="handleFileChange" :on-remove="handleFileRemove" :auto-upload="false" drag>
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" underline="never" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="User">
import { getToken } from "@/utils/auth"
import { changeUserStatus, listUser, resetUserPwd, delUser, getUser, updateUser, addUser, deptTreeSelect } from "@/api/system/user"
import { deptTree } from "@/api/system/dept"
import '@/assets/styles/police-system.scss'
import DeptTreeItem from '@/views/spdc/points/DeptTreeItem.vue'
import type { SysUser, UserQueryParams, UserFormDataResult } from '@/types/api/system/user'
import type { SysRole } from '@/types/api/system/role'
import type { SysPost } from '@/types/api/system/post'
import type { TreeSelect, TableShowColumns, AjaxResult } from '@/types/api/common'

const router = useRouter()
const { proxy } = getCurrentInstance()
const { sys_normal_disable, sys_user_sex } = proxy.useDict("sys_normal_disable", "sys_user_sex")

const userList = ref<SysUser[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const dateRange = ref<string[]>([])
const deptName = ref<string>("")
const deptLoading = ref<boolean>(false)
const deptOptions = ref<TreeSelect[]>([])
const enabledDeptOptions = ref<TreeSelect[] | undefined>(undefined)
const initPassword = ref<string | undefined>(undefined)
const postOptions = ref<SysPost[]>([])
const roleOptions = ref<SysRole[]>([])
/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData"
})
// 列显隐信息
const columns = ref<Record<string, TableShowColumns>>({
  userId: { label: '用户编号', visible: true },
  userName: { label: '用户名称', visible: true },
  nickName: { label: '用户昵称', visible: true },
  deptName: { label: '部门', visible: true },
  phonenumber: { label: '手机号码', visible: true },
  status: { label: '状态', visible: true },
  createTime: { label: '创建时间', visible: true }
})

const data = reactive({
  form: {} as SysUser,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    phonenumber: undefined,
    status: undefined,
    deptId: undefined
  } as UserQueryParams,
  rules: {
    userName: [{ required: true, message: "用户名称不能为空", trigger: "blur" }, { min: 2, max: 20, message: "用户名称长度必须介于 2 和 20 之间", trigger: "blur" }],
    nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
    password: [{ required: true, message: "用户密码不能为空", trigger: "blur" }, { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" }, { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符：< > \" ' \\\ |", trigger: "blur" }],
    email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
    phonenumber: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 过滤后的部门树
const filteredDepts = computed(() => {
  if (!deptName.value) return deptOptions.value
  const keyword = deptName.value.toLowerCase()
  const filterTree = (nodes: TreeSelect[]): TreeSelect[] => {
    return nodes.reduce<TreeSelect[]>((acc, node) => {
      const match = node.label?.toLowerCase().includes(keyword)
      const filteredChildren = filterTree(node.children || [])
      if (match || filteredChildren.length > 0) {
        acc.push({ ...node, children: filteredChildren })
      }
      return acc
    }, [])
  }
  return filterTree(deptOptions.value)
})

/** 查询部门下拉树结构 - 初始加载两级 */
function getDeptTree() {
  deptLoading.value = true
  deptTree().then(response => {
    const tree = response.data || []
    // 截断到两级：显示根节点和其子节点，子节点标记 hasChild 用于懒加载
    const truncateToLevel2 = (nodes: TreeSelect[]): TreeSelect[] => {
      return nodes.map(node => {
        if (node.children && node.children.length > 0) {
          const truncated = node.children.map(child => {
            if (child.children && child.children.length > 0) {
              return { ...child, children: undefined, hasChild: true }
            }
            return { ...child, children: undefined }
          })
          return { ...node, children: truncated }
        }
        return node
      })
    }
    deptOptions.value = truncateToLevel2(tree)
    deptLoading.value = false
  }).catch(() => {
    deptOptions.value = []
    deptLoading.value = false
  })
  // 同时获取完整树用于表单选择
  deptTreeSelect().then(res => {
    enabledDeptOptions.value = filterDisabledDept(JSON.parse(JSON.stringify(res.data)))
  })
}

/** 懒加载部门子节点 */
function loadDeptChildren(parentId: number): Promise<TreeSelect[]> {
  return deptTree({ parentId: String(parentId) }).then(response => {
    return (response.data || []).map(d => ({
      id: d.id!,
      label: d.label!,
      children: undefined as TreeSelect[] | undefined,
      hasChild: d.children && d.children.length > 0
    }))
  })
}

/** 选择部门 */
function handleDeptSelect(deptId: number) {
  queryParams.value.deptId = deptId
  handleQuery()
}

/** 查询用户列表 */
function getList() {
  loading.value = true
  listUser(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false
    userList.value = res.rows
    total.value = res.total
  })
}

/** 过滤禁用的部门 */
function filterDisabledDept(deptList: TreeSelect[]): TreeSelect[] {
  return deptList.filter(dept => {
    if (dept.disabled) {
      return false
    }
    if (dept.children && dept.children.length) {
      dept.children = filterDisabledDept(dept.children)
    }
    return true
  })
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
  queryParams.value.deptId = undefined
  handleQuery()
}

/** 删除按钮操作 */
function handleDelete(row?: SysUser) {
  const userIds = row?.userId || ids.value
  proxy.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？').then(function () {
    return delUser(userIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/user/export", {
    ...queryParams.value,
  },`user_${new Date().getTime()}.xlsx`)
}

/** 用户状态修改  */
function handleStatusChange(row: SysUser) {
  const text = row.status === "0" ? "启用" : "停用"
  proxy.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗?').then(function () {
    return changeUserStatus(row.userId!, row.status!)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(function () {
    row.status = row.status === "0" ? "1" : "0"
  })
}

/** 更多操作 */
function handleCommand(command: string, row: SysUser) {
  switch (command) {
    case "handleResetPwd":
      handleResetPwd(row)
      break
    case "handleAuthRole":
      handleAuthRole(row)
      break
    default:
      break
  }
}

/** 跳转角色分配 */
function handleAuthRole(row: SysUser) {
  const userId = row.userId
  router.push("/system/user-auth/role/" + userId)
}

/** 重置密码按钮操作 */
function handleResetPwd(row: SysUser) {
  proxy.$prompt('请输入"' + row.userName + '"的新密码', "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    closeOnClickModal: false,
    inputPattern: /^.{5,20}$/,
    inputErrorMessage: "用户密码长度必须介于 5 和 20 之间",
    inputValidator: (value: string) => {
      if (/<|>|"|'|\||\\/.test(value)) {
        return "不能包含非法字符：< > \" ' \\\ |"
      }
      return true
    },
  }).then(({ value }: { value: string }) => { 
    resetUserPwd(row.userId!, value).then(() => {
      proxy.$modal.msgSuccess("修改成功，新密码是：" + value)
    })
  }).catch(() => {})
}

/** 选择条数  */
function handleSelectionChange(selection: SysUser[]) {
  ids.value = selection.map(item => item.userId!)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "用户导入"
  upload.open = true
  upload.selectedFile = null
}

/** 下载模板操作 */
function importTemplate() {
  proxy.download("system/user/importTemplate", {
  }, `user_template_${new Date().getTime()}.xlsx`)
}

/**文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any, fileList: any[]) => {
  upload.isUploading = true
}

/** 文件选择处理 */
const handleFileChange = (file: any, fileList: any[]) => {
  upload.selectedFile = file
}

/** 文件删除处理 */
const handleFileRemove = (file: any, fileList: any[]) => {
  upload.selectedFile = null
}

/** 文件上传成功处理 */
const handleFileSuccess = (response: AjaxResult, file: any, fileList: any[]) => {
  upload.open = false
  upload.isUploading = false
  proxy.$refs["uploadRef"].handleRemove(file)
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true })
  getList()
}

/** 提交上传文件 */
function submitFileForm() {
  const file = upload.selectedFile
  if (!file || file.length === 0 || !file.name.toLowerCase().endsWith('.xls') && !file.name.toLowerCase().endsWith('.xlsx')) {
    proxy.$modal.msgError("请选择后缀为 “xls”或“xlsx”的文件。")
    return
  }
  proxy.$refs["uploadRef"].submit()
}

/** 重置操作表单 */
function reset() {
  form.value = {
    userId: undefined,
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: "0",
    remark: undefined,
    postIds: [],
    roleIds: []
  }
  proxy.resetForm("userRef")
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getUser().then(response => {
    postOptions.value = response.posts
    roleOptions.value = response.roles
    open.value = true
    title.value = "添加用户"
    form.value.password = initPassword.value
  })
}

/** 修改按钮操作 */
function handleUpdate(row?: SysUser) {
  reset()
  const userId = row?.userId || ids.value[0]
  getUser(userId).then(response => {
    form.value = response.data!
    postOptions.value = response.posts
    roleOptions.value = response.roles
    form.value.postIds = response.postIds
    form.value.roleIds = response.roleIds
    open.value = true
    title.value = "修改用户"
    form.value.password = ""
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["userRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.userId != undefined) {
        updateUser(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUser(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

onMounted(() => {
  getDeptTree()
  getList()
  proxy.getConfigKey("sys.user.initPassword").then((response: AjaxResult) => {
    initPassword.value = response.msg
  })
})
</script>
