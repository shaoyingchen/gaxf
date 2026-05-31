<template>
  <div class="document-wrapper">
    <div class="main-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideFileText class="title-icon" />
            <h2 class="title-text">文书管理</h2>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn primary" @click="showGenerateDialog = true">
            <LucideFilePlus :size="16" />
            <span>生成文书</span>
          </button>
        </div>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="工单编号">
            <el-input v-model="queryParams.orderId" placeholder="请输入工单ID" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="文书类型">
            <el-select v-model="queryParams.docType" placeholder="请选择" clearable style="width: 180px">
              <el-option label="交办通知书" value="交办通知书" />
              <el-option label="办理报告" value="办理报告" />
              <el-option label="审核意见书" value="审核意见书" />
              <el-option label="办结告知书" value="办结告知书" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadDocumentList">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 文书列表 -->
      <div class="table-section" v-loading="loading">
        <el-table :data="documentList" style="width: 100%">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="docName" label="文书名称" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="doc-name-cell">
                <LucideFileText :size="16" class="doc-icon" />
                <span>{{ row.docName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="docType" label="文书类型" width="130">
            <template #default="{ row }">
              <span class="type-tag">{{ row.docType || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="signStatus" label="签名状态" width="100">
            <template #default="{ row }">
              <span class="status-badge" :class="row.signStatus === '1' ? 'status-signed' : 'status-unsigned'">
                {{ row.signStatus === '1' ? '已签名' : '未签名' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="signBy" label="签名人" width="100" />
          <el-table-column prop="signTime" label="签名时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.signTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="生成时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <button v-if="row.signStatus !== '1'" class="action-link primary" @click="handleSign(row)">
                  <LucidePenLine :size="14" /> 签署
                </button>
                <button class="action-link" @click="handleDownload(row)">
                  <LucideDownload :size="14" /> 下载
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <div class="page-stats">共 <strong>{{ total }}</strong> 条记录</div>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes"
          @size-change="loadDocumentList"
          @current-change="loadDocumentList"
        />
      </div>
    </div>

    <!-- 生成文书对话框 -->
    <el-dialog v-model="showGenerateDialog" title="生成文书" width="500px" class="xf-dialog">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="关联工单">
          <el-input v-model="generateForm.orderId" placeholder="请输入工单ID" />
        </el-form-item>
        <el-form-item label="文书模板">
          <el-select v-model="generateForm.templateId" placeholder="请选择文书模板" style="width: 100%">
            <el-option v-for="tpl in docTemplates" :key="tpl.id" :label="tpl.name" :value="tpl.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleGenerate">生成文书</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LucideFileText, LucideFilePlus, LucidePenLine, LucideDownload } from 'lucide-vue-next'
import { listDocument, generateDocument, signDocument, downloadDocument } from '@/api/xf/document'
import type { XfDocument } from '@/types/api/xf/document'

const loading = ref(false)
const documentList = ref<XfDocument[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryParams = ref({ orderId: '', docType: '' })
const showGenerateDialog = ref(false)
const generateForm = ref({ orderId: '', templateId: 0 })

const docTemplates = [
  { id: 1, name: '信访事项交办通知书' },
  { id: 2, name: '信访事项办理报告' },
  { id: 3, name: '信访事项审核意见书' },
  { id: 4, name: '信访事项办结告知书' }
]

async function loadDocumentList() {
  loading.value = true
  try {
    const orderId = Number(queryParams.value.orderId) || 0
    const res = await listDocument({ orderId })
    documentList.value = (res.rows || []) as XfDocument[]
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载文书列表失败')
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.value = { orderId: '', docType: '' }
  loadDocumentList()
}

async function handleGenerate() {
  if (!generateForm.value.orderId) {
    ElMessage.warning('请输入关联工单ID')
    return
  }
  if (!generateForm.value.templateId) {
    ElMessage.warning('请选择文书模板')
    return
  }
  try {
    await generateDocument({
      orderId: Number(generateForm.value.orderId),
      templateId: generateForm.value.templateId
    })
    ElMessage.success('文书生成成功')
    showGenerateDialog.value = false
    loadDocumentList()
  } catch (error) {
    ElMessage.error('生成文书失败')
  }
}

async function handleSign(row: XfDocument) {
  try {
    await ElMessageBox.confirm('确定要签署该文书吗？签署后不可修改。', '提示', { type: 'warning' })
    await signDocument(row.id!)
    ElMessage.success('签署成功')
    loadDocumentList()
  } catch { /* cancelled */ }
}

async function handleDownload(row: XfDocument) {
  try {
    const blob = await downloadDocument(row.id!) as any
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = row.docName || '文书.docx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

function formatTime(time: string | undefined) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadDocumentList()
})
</script>

<style lang="scss" scoped>
$police-primary: #1E3A8A;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F0F4F8;
$police-card: #FFFFFF;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #64748B;

.document-wrapper { padding: 24px; background: $police-bg; min-height: calc(100vh - 48px); }

.main-card {
  background: white; border-radius: 20px; border: 1px solid $police-border;
  overflow: hidden; box-shadow: 0 4px 20px rgba(30, 58, 138, 0.08);
  display: flex; flex-direction: column;
}

.card-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
  color: white; flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 12px; }

.title-section {
  display: flex; align-items: center; gap: 12px;
  .title-icon { width: 24px; height: 24px; color: white; }
  .title-text { font-size: 18px; font-weight: 700; margin: 0; letter-spacing: 1px; }
}

.header-actions {
  display: flex; gap: 10px;
  .action-btn {
    padding: 8px 14px; border-radius: 10px; font-size: 13px; font-weight: 500;
    border: 1px solid rgba(255, 255, 255, 0.3); background: rgba(255, 255, 255, 0.1);
    color: white; cursor: pointer; transition: all 0.2s;
    display: flex; align-items: center; gap: 6px;
    &:hover { background: rgba(255, 255, 255, 0.2); }
    &.primary { background: white; color: $police-primary; border-color: white; &:hover { background: #F0F4F8; } }
  }
}

.filter-section { padding: 16px 24px; border-bottom: 1px solid $police-border; }
.table-section { padding: 16px 24px; flex: 1; }

.doc-name-cell {
  display: flex; align-items: center; gap: 8px;
  .doc-icon { color: $police-blue; }
}

.type-tag { padding: 3px 10px; border-radius: 8px; font-size: 12px; background: #F5F5F5; color: $police-text-muted; }

.status-badge {
  padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; display: inline-block;
  &.status-signed { background: #D1FAE5; color: #059669; }
  &.status-unsigned { background: #FEF3C7; color: #D97706; }
}

.table-actions { display: flex; gap: 8px; }

.action-link {
  display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 8px;
  font-size: 12px; font-weight: 500; border: 1px solid $police-border; background: white;
  color: $police-text; cursor: pointer; transition: all 0.2s;
  &:hover { background: #F5F5F5; }
  &.primary { border-color: $police-light-blue; color: $police-blue; &:hover { background: #EFF6FF; } }
}

.pagination-section {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; background: #F8FAFE; border-top: 1px solid $police-border; flex-shrink: 0;
  .page-stats { font-size: 13px; color: $police-text-muted; strong { color: $police-text; } }
}

.xf-dialog {
  :deep(.el-dialog) { border-radius: 16px; }
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white; border-radius: 16px 16px 0 0; padding: 20px 24px; margin: 0;
    .el-dialog__title { color: white; font-weight: 600; font-size: 18px; }
    .el-dialog__headerbtn { top: 20px; right: 24px; .el-dialog__close { color: white; font-size: 20px; } }
  }
  :deep(.el-dialog__body) { padding: 28px 32px; background: white; }
  :deep(.el-dialog__footer) { padding: 20px 32px; background: white; border-top: 1px solid $police-border; border-radius: 0 0 16px 16px; }
}
</style>
