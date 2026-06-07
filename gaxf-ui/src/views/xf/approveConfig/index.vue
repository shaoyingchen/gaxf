<template>
  <div class="approve-config-wrapper">
    <div class="main-card">
      <div class="card-header">
        <div class="title-section">
          <h2 class="title-text">审批流程配置</h2>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleAdd">新增流程</el-button>
        </div>
      </div>

      <el-table :data="configList" v-loading="loading">
        <el-table-column prop="flowName" label="流程名称" min-width="180" />
        <el-table-column prop="applyDeptName" label="发起部门" min-width="160" />
        <el-table-column label="阶段数" width="100">
          <template #default="{ row }">{{ row.stages?.length || 0 }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'info'">{{ row.status === '0' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑审批流程' : '新增审批流程'" width="760px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="流程名称">
          <el-input v-model="form.flowName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="发起部门">
          <el-tree-select
            v-model="form.applyDeptId"
            :data="deptOptions"
            check-strictly
            node-key="id"
            :props="{ label: 'label', children: 'children' }"
            @change="handleApplyDeptChange"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">启用</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <div class="stage-header">
          <span>审批阶段</span>
          <el-button type="primary" link @click="addStage">新增阶段</el-button>
        </div>
        <div v-for="(stage, index) in form.stages" :key="index" class="stage-card">
          <div class="stage-toolbar">
            <span>阶段 {{ index + 1 }}</span>
            <el-button v-if="form.stages.length > 1" link type="danger" @click="removeStage(index)">删除</el-button>
          </div>
          <el-form-item label="阶段名称">
            <el-input v-model="stage.stageName" />
          </el-form-item>
          <el-form-item label="审批部门">
            <el-tree-select
              v-model="stage._selectedDeptIds"
              :data="deptOptions"
              multiple
              check-strictly
              node-key="id"
              :props="{ label: 'label', children: 'children' }"
              placeholder="请选择审批部门"
              style="width: 100%"
            />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deptTree } from '@/api/system/dept'
import { addApproveFlowConfig, delApproveFlowConfig, getApproveFlowConfig, listApproveFlowConfig, updateApproveFlowConfig } from '@/api/xf/approve'
import type { TreeSelect } from '@/types'
import type { ApproveFlowConfig, ApproveFlowStageDept } from '@/types/api/xf/approve'

const loading = ref(false)
const showDialog = ref(false)
const configList = ref<ApproveFlowConfig[]>([])
const deptOptions = ref<TreeSelect[]>([])

interface StageForm {
  id?: number
  flowConfigId?: number
  stageNo: number
  stageName: string
  stageType: '1'
  parallelMode: '1'
  deptList: ApproveFlowStageDept[]
  _selectedDeptIds: number[]
}

const form = ref<{ id?: number; flowName: string; applyDeptId: number | undefined; applyDeptName: string; status: '0' | '1'; stages: StageForm[] }>({
  flowName: '',
  applyDeptId: undefined,
  applyDeptName: '',
  status: '0',
  stages: [{ stageNo: 1, stageName: '第1阶段', stageType: '1', parallelMode: '1', deptList: [], _selectedDeptIds: [] }]
})

const flatDeptOptions = computed(() => {
  const result: Array<{ id: number; label: string }> = []
  const walk = (nodes: TreeSelect[]) => {
    nodes.forEach((node: any) => {
      result.push({ id: node.id, label: node.label })
      if (node.children?.length) walk(node.children)
    })
  }
  walk(deptOptions.value)
  return result
})

async function loadList() {
  loading.value = true
  try {
    const res = await listApproveFlowConfig()
    configList.value = ((res.rows || []) as any) as ApproveFlowConfig[]
  } finally {
    loading.value = false
  }
}

async function loadDeptOptions() {
  const res = await deptTree()
  deptOptions.value = (res.data || []) as TreeSelect[]
}

function handleAdd() {
  form.value = {
    flowName: '',
    applyDeptId: undefined,
    applyDeptName: '',
    status: '0',
    stages: [{ stageNo: 1, stageName: '第1阶段', stageType: '1', parallelMode: '1', deptList: [], _selectedDeptIds: [] }]
  }
  showDialog.value = true
}

async function handleEdit(row: ApproveFlowConfig) {
  const res = await getApproveFlowConfig(row.id!)
  const data = (res.data || row) as ApproveFlowConfig
  form.value = {
    ...data,
    applyDeptId: data.applyDeptId || undefined,
    stages: (data.stages || []).map(s => ({
      ...s,
      stageType: '1',
      _selectedDeptIds: (s.deptList || []).map((d: ApproveFlowStageDept) => d.approveDeptId)
    }))
  }
  showDialog.value = true
}

async function handleDelete(row: ApproveFlowConfig) {
  await ElMessageBox.confirm(`确定删除流程“${row.flowName}”吗？`, '提示', { type: 'warning' })
  await delApproveFlowConfig(row.id!)
  ElMessage.success('删除成功')
  loadList()
}

function handleApplyDeptChange(value: number) {
  const found = flatDeptOptions.value.find(item => item.id === value)
  form.value.applyDeptName = found?.label || ''
}

function addStage() {
  form.value.stages.push({
    stageNo: form.value.stages.length + 1,
    stageName: `第${form.value.stages.length + 1}阶段`,
    stageType: '1',
    parallelMode: '1',
    deptList: [],
    _selectedDeptIds: []
  })
}

function removeStage(index: number) {
  form.value.stages.splice(index, 1)
  form.value.stages.forEach((stage, idx) => {
    stage.stageNo = idx + 1
    if (!stage.stageName) stage.stageName = `第${idx + 1}阶段`
  })
}

function validateForm() {
  if (!form.value.flowName) return '请输入流程名称'
  if (!form.value.applyDeptId) return '请选择发起部门'
  if (!form.value.stages.length) return '至少保留一个审批阶段'
  if (form.value.stages.some(stage => !stage.stageName)) return '请输入阶段名称'
  if (form.value.stages.some(stage => !stage._selectedDeptIds.length)) return '每个阶段至少选择一个审批部门'
  return ''
}

async function submitForm() {
  const message = validateForm()
  if (message) {
    ElMessage.warning(message)
    return
  }
  const submitData: ApproveFlowConfig = {
    ...form.value,
    applyDeptId: form.value.applyDeptId!,
    stages: form.value.stages.map(stage => {
      const { _selectedDeptIds, ...rest } = stage
      return {
        ...rest,
        stageType: '1',
        deptList: _selectedDeptIds.map(id => {
          const dept = flatDeptOptions.value.find(d => d.id === id)
          return { approveDeptId: id, approveDeptName: dept?.label || '' }
        })
      }
    })
  }
  if (form.value.id) {
    await updateApproveFlowConfig(submitData as any)
    ElMessage.success('修改成功')
  } else {
    await addApproveFlowConfig(submitData as any)
    ElMessage.success('新增成功')
  }
  showDialog.value = false
  loadList()
}

onMounted(() => {
  loadList()
  loadDeptOptions()
})
</script>

<style scoped>
.approve-config-wrapper { padding: 24px; }
.main-card { background: #fff; border-radius: 16px; padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.title-text { margin: 0; font-size: 18px; font-weight: 700; }
.stage-header { display: flex; justify-content: space-between; align-items: center; margin: 8px 0 16px; font-weight: 600; }
.stage-card { border: 1px solid #e5e7eb; border-radius: 12px; padding: 16px; margin-bottom: 12px; }
.stage-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
</style>
