<template>
  <div class="app-container">
    <el-row :gutter="20">
      <splitpanes :horizontal="appStore.device === 'mobile'" class="default-theme">
        <!-- 部门数据 -->
        <pane size="16">
          <el-col>
            <div class="head-container">
              <el-input
                v-model="deptName"
                placeholder="请输入部门名称"
                clearable
                prefix-icon="Search"
                style="margin-bottom: 20px"
              />
            </div>
            <div class="head-container">
              <el-tree
                :data="deptOptions"
                :props="{ label: 'label', children: 'children' }"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                ref="deptTreeRef"
                node-key="id"
                highlight-current
                default-expand-all
                @node-click="handleNodeClick"
              />
            </div>
          </el-col>
        </pane>
        <!-- 点位数据 -->
        <pane size="84">
          <el-col>
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
              <el-form-item label="国标编码" prop="gbCode">
                <el-input
                  v-model="queryParams.gbCode"
                  placeholder="请输入国标编码"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item label="点位名称" prop="pointName">
                <el-input
                  v-model="queryParams.pointName"
                  placeholder="请输入点位名称"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button
                  type="primary"
                  plain
                  icon="Plus"
                  @click="handleAdd"
                  v-hasPermi="['spdc:camera:add']"
                >新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="success"
                  plain
                  icon="Edit"
                  :disabled="single"
                  @click="handleUpdate"
                  v-hasPermi="['spdc:camera:edit']"
                >修改</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="danger"
                  plain
                  icon="Delete"
                  :disabled="multiple"
                  @click="handleDelete"
                  v-hasPermi="['spdc:camera:remove']"
                >删除</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="warning"
                  plain
                  icon="Download"
                  @click="handleExport"
                  v-hasPermi="['spdc:camera:export']"
                >导出</el-button>
              </el-col>
              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
            </el-row>

            <el-table v-loading="loading" :data="cameraList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55" align="center" />
              <el-table-column label="点位主键" align="center" prop="pointId" />
              <el-table-column label="国标编码" align="center" prop="gbCode" />
              <el-table-column label="部门ID" align="center" prop="deptId" />
              <el-table-column label="部门名称" align="center" prop="deptName" />
              <el-table-column label="点位名称" align="center" prop="pointName" />
              <el-table-column label="房间类型" align="center" prop="roomKind" />
              <el-table-column label="所属区域" align="center" prop="zoneType" />
              <el-table-column label="点位状态" align="center" prop="pointStatus">
                <template #default="scope">
                  <el-tag v-if="scope.row.pointStatus === 1" type="success">在线</el-tag>
                  <el-tag v-else-if="scope.row.pointStatus === 0" type="danger">离线</el-tag>
                  <el-tag v-else type="info">未知</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="摄像机类型" align="center" prop="cameraType">
                <template #default="scope">
                  <span v-if="scope.row.cameraType === 1">海康</span>
                  <span v-else-if="scope.row.cameraType === 2">大华</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="IP地址" align="center" prop="ipAddress" />
              <el-table-column label="端口号" align="center" prop="port" />
              <el-table-column label="用户名" align="center" prop="username" />
              <el-table-column label="通道号" align="center" prop="channelNo" />
              <el-table-column label="设备编码" align="center" prop="code" />
              <el-table-column label="责任人" align="center" prop="dutyUser" />
              <el-table-column label="启用状态" align="center" prop="enable">
                <template #default="scope">
                  <el-tag v-if="scope.row.enable === 1" type="success">启用</el-tag>
                  <el-tag v-else type="danger">禁用</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template #default="scope">
                  <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['spdc:camera:edit']">修改</el-button>
                  <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['spdc:camera:remove']">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="total>0"
              :total="total"
              v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize"
              @pagination="getList"
            />
          </el-col>
        </pane>
      </splitpanes>
    </el-row>

    <!-- 添加或修改点位管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="cameraRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="国标编码" prop="gbCode">
          <el-input v-model="form.gbCode" placeholder="请输入国标编码" />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="点位名称" prop="pointName">
          <el-input v-model="form.pointName" placeholder="请输入点位名称" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomKind">
          <el-input v-model="form.roomKind" placeholder="请输入房间类型" />
        </el-form-item>
        <el-form-item label="所属区域" prop="zoneType">
          <el-input v-model="form.zoneType" placeholder="请输入所属区域" />
        </el-form-item>
        <el-form-item label="IP地址" prop="ipAddress">
          <el-input v-model="form.ipAddress" placeholder="请输入IP地址" />
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="通道号" prop="channelNo">
          <el-input v-model="form.channelNo" placeholder="请输入通道号" />
        </el-form-item>
        <el-form-item label="设备编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入设备编码" />
        </el-form-item>
        <el-form-item label="责任人" prop="dutyUser">
          <el-input v-model="form.dutyUser" placeholder="请输入责任人" />
        </el-form-item>
        <el-form-item label="启用状态" prop="enable">
          <el-radio-group v-model="form.enable">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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

<script setup lang="ts" name="Camera">
import useAppStore from '@/store/modules/app'
import { deptTree } from '@/api/system/dept'
import { listCamera, getCamera, delCamera, addCamera, updateCamera } from '@/api/spdc/camera'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import type { DcCamera, CameraQueryParams } from '@/types/api/spdc/camera'
import type { TreeSelect } from '@/types/api/common'

const appStore = useAppStore()
const { proxy } = getCurrentInstance()

const cameraList = ref<DcCamera[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>('')

const deptName = ref<string>('')
const deptOptions = ref<TreeSelect[]>([])
const selectedDeptId = ref<number | undefined>(undefined)
const deptTreeRef = ref()

const data = reactive({
  form: {} as DcCamera,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    gbCode: undefined,
    deptId: undefined,
    deptName: undefined,
    pointName: undefined,
    roomKind: undefined,
    zoneType: undefined,
    upAlarm: undefined,
    pointStatus: undefined,
    cameraType: undefined,
    ipAddress: undefined,
    port: undefined,
    username: undefined,
    password: undefined,
    channelNo: undefined,
    code: undefined,
    detect: undefined,
    dutyUser: undefined,
    enable: undefined,
  } as CameraQueryParams,
  rules: {
    deptId: [
      { required: true, message: '部门ID不能为空', trigger: 'blur' }
    ],
    pointName: [
      { required: true, message: '点位名称不能为空', trigger: 'blur' }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 部门树节点搜索 */
function filterNode(value: string, data: any) {
  if (!value) return true
  return data.label.includes(value)
}

/** 部门树节点点击 */
function handleNodeClick(data: TreeSelect) {
  selectedDeptId.value = data.id as number
  queryParams.value.deptId = selectedDeptId.value
  queryParams.value.pageNum = 1
  getList()
}

/** 查询部门树结构 */
function getDeptTree() {
  deptTree().then(response => {
    deptOptions.value = response.data || []
  })
}

/** 查询点位管理列表 */
function getList() {
  loading.value = true
  listCamera(queryParams.value).then(response => {
    cameraList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    pointId: null,
    gbCode: null,
    deptId: null,
    deptName: null,
    pointName: null,
    roomKind: null,
    zoneTypeId: null,
    zoneType: null,
    upAlarm: null,
    ipAddress: null,
    port: null,
    username: null,
    password: null,
    channelNo: null,
    cameraType: null,
    county: null,
    city: null,
    jz: null,
    pointStatus: null,
    connState: null,
    personNum: null,
    code: null,
    gbState: null,
    parentCode: null,
    detect: null,
    mustAcc: null,
    detectTag: null,
    cCount: null,
    dutyUser: null,
    dutyTel: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
    enable: null
  }
  proxy.resetForm('cameraRef')
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  selectedDeptId.value = undefined
  queryParams.value.deptId = undefined
  proxy.$refs.deptTreeRef?.setCurrentKey(null)
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection: DcCamera[]) {
  ids.value = selection.map(item => item.pointId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = '添加点位管理'
}

/** 修改按钮操作 */
function handleUpdate(row: DcCamera) {
  reset()
  const _pointId = row.pointId || ids.value[0]
  getCamera(_pointId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改点位管理'
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['cameraRef'].validate((valid: boolean) => {
    if (valid) {
      if (form.value.pointId != null) {
        updateCamera(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addCamera(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row: DcCamera) {
  const _pointIds = row.pointId || ids.value
  proxy.$modal.confirm('是否确认删除点位管理编号为"' + _pointIds + '"的数据项？').then(function() {
    return delCamera(_pointIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('spdc/camera/export', {
    ...queryParams.value
  }, `camera_${new Date().getTime()}.xlsx`)
}

// 监听部门名称搜索
watch(deptName, (val: string) => {
  proxy.$refs['deptTreeRef']?.filter(val)
})

onMounted(() => {
  getDeptTree()
  getList()
})
</script>
