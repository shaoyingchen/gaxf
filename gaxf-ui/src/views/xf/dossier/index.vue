<template>
  <div class="dossier-wrapper">
    <div class="main-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideFolderOpen class="title-icon" />
            <h2 class="title-text">卷宗管理</h2>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn primary" @click="showUploadDialog = true">
            <LucideUpload :size="16" />
            <span>上传文件</span>
          </button>
        </div>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :model="queryParams" class="filter-form">
          <div class="filter-row">
            <el-form-item label="工单编号">
              <el-input v-model="queryParams.xfCaseNo" placeholder="请输入信访件编号" clearable style="width: 200px" />
            </el-form-item>
            <div class="filter-actions">
              <button class="filter-btn primary" @click.prevent="loadDossierList">
                <LucideSearch :size="13" />
                <span>查询</span>
              </button>
              <button class="filter-btn" @click.prevent="handleReset">
                <LucideRefreshCcw :size="13" />
                <span>重置</span>
              </button>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 文件列表 -->
      <div class="table-section" v-loading="loading">
        <el-table :data="dossierList" style="width: 100%">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="fileName" label="文件名" min-width="250">
            <template #default="{ row }">
              <div class="file-name-cell">
                <LucideFile :size="16" class="file-icon" :class="getFileIconClass(row.fileType)" />
                <span>{{ row.fileName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="fileType" label="类型" width="100">
            <template #default="{ row }">
              <span class="type-tag">{{ row.fileType || '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="uploadBy" label="上传人" width="100" />
          <el-table-column prop="uploadTime" label="上传时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <button class="action-link" @click="previewFile(row)">
                  <LucideEye :size="14" /> 预览
                </button>
                <button class="action-link danger" @click="handleDelete(row)">
                  <LucideTrash2 :size="14" /> 删除
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
          @size-change="loadDossierList"
          @current-change="loadDossierList"
        />
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="showUploadDialog" title="上传卷宗材料" width="500px" class="xf-dialog">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="关联工单">
          <el-input v-model="uploadForm.orderId" placeholder="请输入工单ID" />
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="5"
            accept=".pdf,.jpg,.jpeg,.png,.zip"
            :on-change="handleFileChange"
            drag
            multiple
          >
            <div style="padding: 30px 20px; text-align: center;">
              <LucideUpload :size="36" style="color: #3B82F6; margin-bottom: 8px;" />
              <div>将文件拖到此处，或<em>点击上传</em></div>
              <div style="font-size: 12px; color: #94A3B8; margin-top: 8px;">支持 PDF、图片、ZIP 格式，单次最多5个</div>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploadLoading">确认上传</el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="showPreviewDialog" title="文件预览" width="700px" class="xf-dialog">
      <div class="preview-content">
        <img v-if="previewIsImage" :src="previewUrl" class="preview-img" />
        <iframe v-else-if="previewIsPdf" :src="previewUrl" class="preview-iframe" />
        <div v-else class="preview-unsupported">
          <LucideFileX :size="48" />
          <span>该文件类型不支持在线预览</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LucideFolderOpen, LucideUpload, LucideFile, LucideEye, LucideTrash2, LucideFileX, LucideSearch, LucideRefreshCcw } from 'lucide-vue-next'
import { listDossier, uploadDossier, delDossier } from '@/api/xf/dossier'
import type { XfDossier } from '@/types/api/xf/dossier'

const loading = ref(false)
const dossierList = ref<XfDossier[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryParams = ref({
  orderId: 0,
  xfCaseNo: ''
})

const showUploadDialog = ref(false)
const showPreviewDialog = ref(false)
const uploadLoading = ref(false)
const uploadFiles = ref<File[]>([])
const uploadForm = ref({ orderId: '' })

const previewUrl = ref('')
const previewIsImage = computed(() => {
  const url = previewUrl.value.toLowerCase()
  return url.endsWith('.jpg') || url.endsWith('.jpeg') || url.endsWith('.png') || url.endsWith('.gif')
})
const previewIsPdf = computed(() => previewUrl.value.toLowerCase().endsWith('.pdf'))

async function loadDossierList() {
  loading.value = true
  try {
    const orderId = Number(queryParams.value.xfCaseNo) || 0
    const res = await listDossier({ orderId })
    dossierList.value = (res.rows || []) as XfDossier[]
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载卷宗列表失败')
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.value = { orderId: 0, xfCaseNo: '' }
  loadDossierList()
}

function handleFileChange(file: any, fileList: any[]) {
  uploadFiles.value = fileList.map((f: any) => f.raw)
}

async function submitUpload() {
  if (!uploadForm.value.orderId) {
    ElMessage.warning('请输入关联工单ID')
    return
  }
  if (uploadFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  uploadLoading.value = true
  try {
    for (const file of uploadFiles.value) {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('orderId', uploadForm.value.orderId)
      await uploadDossier(formData)
    }
    ElMessage.success('上传成功')
    showUploadDialog.value = false
    uploadFiles.value = []
    loadDossierList()
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.value = false
  }
}

function previewFile(row: XfDossier) {
  if (row.filePath) {
    previewUrl.value = row.filePath
    showPreviewDialog.value = true
  } else {
    ElMessage.info('文件路径不存在')
  }
}

async function handleDelete(row: XfDossier) {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗？', '提示', { type: 'warning' })
    await delDossier(row.id!)
    ElMessage.success('删除成功')
    loadDossierList()
  } catch { /* cancelled */ }
}

function getFileIconClass(type: string | undefined) {
  if (!type) return ''
  if (type.includes('pdf')) return 'icon-pdf'
  if (type.includes('image') || type.includes('jpg') || type.includes('png')) return 'icon-image'
  if (type.includes('zip')) return 'icon-zip'
  return ''
}

function formatFileSize(size: number | undefined) {
  if (!size) return '-'
  if (size < 1024) return size + 'B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB'
  return (size / (1024 * 1024)).toFixed(1) + 'MB'
}

function formatTime(time: string | undefined) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadDossierList()
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

.dossier-wrapper { padding: 0; background: $police-bg; min-height: calc(100vh - 48px); }

.main-card {
  background: white; border-radius: 12px; border: 1px solid $police-border;
  overflow: hidden; box-shadow: 0 2px 12px rgba(30, 58, 138, 0.06);
  display: flex; flex-direction: column;
}

.card-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 18px; background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
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

.filter-section {
  margin: 12px 12px 0;
  padding: 12px 14px;
  background: #F8FAFE;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
}

.filter-form {
  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 0;
  }

  :deep(.el-form-item__label) {
    font-size: 12px;
    color: $police-text-muted;
    font-weight: 500;
    padding-right: 8px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 6px;
    background: white;
    box-shadow: 0 0 0 1px #E2E8F0 inset;
    transition: all 0.2s;
    &:hover { box-shadow: 0 0 0 1px $police-light-blue inset; }
    &.is-focus { box-shadow: 0 0 0 1px $police-blue inset !important; }
  }
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.filter-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.filter-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  height: 32px;
  line-height: 1;

  &:hover { border-color: $police-light-blue; color: $police-blue; }

  &.primary {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    border-color: transparent;
    color: white;
    box-shadow: 0 2px 6px rgba(37, 99, 235, 0.2);
    &:hover { color: white; box-shadow: 0 3px 10px rgba(37, 99, 235, 0.3); }
  }
}

.table-section { padding: 12px 0 0; flex: 1; }

.file-name-cell {
  display: flex; align-items: center; gap: 8px;
  .file-icon { color: $police-blue; &.icon-pdf { color: #EF4444; } &.icon-image { color: #10B981; } &.icon-zip { color: #F59E0B; } }
}

.type-tag { padding: 3px 10px; border-radius: 8px; font-size: 12px; background: #F5F5F5; color: $police-text-muted; }

.table-actions { display: flex; gap: 8px; }

.action-link {
  display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 8px;
  font-size: 12px; font-weight: 500; border: 1px solid $police-border; background: white;
  color: $police-text; cursor: pointer; transition: all 0.2s;
  &:hover { background: #F5F5F5; }
  &.danger { border-color: #FCA5A5; color: #EF4444; &:hover { background: #FEF2F2; } }
}

.pagination-section {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px; background: #F8FAFE; border-top: 1px solid $police-border; flex-shrink: 0;
  .page-stats { font-size: 13px; color: $police-text-muted; strong { color: $police-text; } }
}

.preview-content {
  min-height: 400px;
  .preview-img { max-width: 100%; max-height: 500px; display: block; margin: 0 auto; border-radius: 8px; }
  .preview-iframe { width: 100%; height: 500px; border: none; border-radius: 8px; }
  .preview-unsupported {
    display: flex; flex-direction: column; align-items: center; justify-content: center;
    padding: 60px; color: $police-text-muted; gap: 12px;
  }
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
