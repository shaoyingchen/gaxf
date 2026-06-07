<template>
  <div class="detail-wrapper">
    <!-- 顶部返回栏 -->
    <div class="detail-top-bar">
      <button class="back-btn" @click="goBack">
        <LucideArrowLeft :size="18" />
        <span>返回</span>
      </button>
      <div class="top-bar-right">
        <span class="status-badge" :class="getStatusClass(orderData?.status)">
          {{ getStatusText(orderData?.status) }}
        </span>
        <el-tag v-if="orderData?.status === '5'" type="danger" size="small">
          超期{{ orderData?.overdueCount || 0 }}次
        </el-tag>
      </div>
    </div>

    <div class="detail-layout" v-loading="loading">
      <!-- 左侧：工单信息 -->
      <div class="detail-left">
        <!-- 基本信息 -->
        <div class="info-card">
          <div class="card-title">
            <LucideFileText :size="18" />
            <span>基本信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="item-label">信访件编号</span>
              <span class="item-value" :class="getOrderNoClass(orderData)">{{ orderData?.xfCaseNo || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">信访形式</span>
              <span class="item-value">{{ orderData?.xfForm || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">公安业务分类</span>
              <span class="item-value">{{ orderData?.businessCategory || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">信访日期</span>
              <span class="item-value">{{ orderData?.xfDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">登记单位</span>
              <span class="item-value">{{ orderData?.registerUnit || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">登记时间</span>
              <span class="item-value">{{ formatTime(orderData?.registerTime) }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">办理期限</span>
              <span class="item-value">{{ orderData?.deadline || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">创建时间</span>
              <span class="item-value">{{ formatTime(orderData?.createTime) }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">创建人</span>
              <span class="item-value">{{ orderData?.createBy || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 信访人信息 -->
        <div class="info-card">
          <div class="card-title">
            <LucideUser :size="18" />
            <span>信访人信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="item-label">姓名</span>
              <span class="item-value">{{ orderData?.petitionerName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">信访人数</span>
              <span class="item-value">{{ orderData?.petitionerCount || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">电话</span>
              <span class="item-value">{{ orderData?.petitionerPhone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">身份证号</span>
              <span class="item-value">{{ orderData?.petitionerIdcard || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">地址</span>
              <span class="item-value">{{ orderData?.petitionerAddress || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">问题发生地</span>
              <span class="item-value">{{ orderData?.problemLocation || '-' }}</span>
            </div>
            <div class="info-item full">
              <span class="item-label">信访诉求</span>
              <span class="item-value scrollable">{{ orderData?.xfDemand || '-' }}</span>
            </div>
            <div class="info-item full">
              <span class="item-label">信访内容</span>
              <span class="item-value scrollable">{{ orderData?.xfContent || '-' }}</span>
            </div>
            <div class="info-item full">
              <span class="item-label">信访目的</span>
              <span class="item-value">{{ orderData?.xfPurpose || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 办理信息 -->
        <div class="info-card">
          <div class="card-title">
            <LucideClipboardList :size="18" />
            <span>办理信息</span>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="item-label">档案编号</span>
              <span class="item-value">{{ orderData?.archiveNo || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">信访事项编号</span>
              <span class="item-value">{{ orderData?.xfItemNo || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">转往处</span>
              <span class="item-value">{{ orderData?.transferDest || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">办理方式</span>
              <span class="item-value">{{ orderData?.handleMethod || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">具体承办单位</span>
              <span class="item-value">{{ orderData?.specificHandleUnit || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">责任部门</span>
              <span class="item-value">{{ orderData?.responsibleDept || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">信访件状态</span>
              <span class="item-value">{{ orderData?.xfCaseStatus || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">办结状态</span>
              <span class="item-value">{{ orderData?.completionStatus || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="item-label">办结时间</span>
              <span class="item-value">{{ formatTime(orderData?.completionTime) }}</span>
            </div>
            <div class="info-item full">
              <span class="item-label">异常动态</span>
              <span class="item-value">{{ orderData?.abnormalDynamic || '-' }}</span>
            </div>
          </div>
        </div>

        <!-- 办理意见 -->
        <div class="info-card">
          <div class="card-title">
            <LucideAlignLeft :size="18" />
            <span>办理意见</span>
          </div>
          <div v-if="assignRecords.filter(r => r.reportContent).length === 0" class="content-body">暂无办理意见</div>
          <div v-else class="opinion-list">
            <div v-for="record in assignRecords.filter(r => r.reportContent)" :key="record.id" class="opinion-item">
              <div class="opinion-header">
                <span class="opinion-dept">{{ record.deptName }}</span>
                <span class="opinion-time" v-if="record.reportTime">{{ formatTime(record.reportTime) }}</span>
              </div>
              <div class="opinion-text">{{ record.reportContent }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：标签页信息 -->
      <div class="detail-right">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <!-- 办理报告 -->
          <el-tab-pane label="办理报告" name="report">
            <div class="tab-content">
              <div class="tab-header">
                <span>办理报告列表</span>
              </div>
              <div v-if="assignRecords.filter(r => r.reportAttachment).length === 0" class="empty-tab">
                <LucideInbox :size="40" />
                <span>暂无办理报告</span>
              </div>
              <div v-else class="dossier-list">
                <div v-for="record in assignRecords.filter(r => r.reportAttachment)" :key="record.id" class="dossier-item">
                  <div class="file-info">
                    <LucideFileText :size="16" class="file-icon" />
                    <span class="file-name">{{ getFileName(record.reportAttachment || '办理报告') }}</span>
                  </div>
                  <div class="file-actions">
                    <button v-if="record.reportAttachment" class="action-link" @click="previewAttachment(record.reportAttachment)">
                      <LucideEye :size="14" /> 预览
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 审核记录 -->
          <el-tab-pane label="审核记录" name="review">
            <div class="tab-content">
              <div v-if="branchTaskList.length > 0" class="approve-branch-block">
                <div class="tab-header"><span>承办单位首审</span></div>
                <div class="branch-review-grid">
                  <div v-for="task in branchTaskList" :key="`branch-${task.id}`" class="branch-review-card">
                    <div class="branch-path">
                      <span class="branch-dept">{{ task.undertakeDeptName || '-' }}</span>
                      <span class="branch-arrow">→</span>
                      <span class="branch-approve">{{ task.approveDeptName }}</span>
                    </div>
                    <span class="status-badge small" :class="task.status === '1' ? 'status-done' : (task.status === '2' ? 'status-returned' : 'status-pending')">
                      {{ getApproveTaskStatusText(task.status) }}
                    </span>
                    <div v-if="task.opinion" class="branch-opinion">{{ task.opinion }}</div>
                  </div>
                </div>
              </div>
              <div v-if="approveTaskList.length > 0" class="approve-progress-block">
                <div class="tab-header"><span>审批进度</span></div>
                <el-timeline>
                  <el-timeline-item
                    v-for="task in approveTaskList"
                    :key="task.id"
                    :timestamp="formatTime(task.actionTime || task.createTime)"
                    placement="top"
                    :type="task.status === '2' ? 'danger' : (task.status === '1' ? 'primary' : 'info')"
                  >
                    <div class="timeline-card">
                      <div class="timeline-header">
                        <span class="reviewer-name">{{ task.stageName }}</span>
                        <span v-if="task.taskType === '0' && task.undertakeDeptName" class="review-type">{{ task.undertakeDeptName }} → {{ task.approveDeptName }}</span>
                        <span v-else class="review-type">{{ task.approveDeptName }}</span>
                        <span class="status-badge small" :class="task.status === '1' ? 'status-done' : (task.status === '2' ? 'status-returned' : 'status-pending')">
                          {{ getApproveTaskStatusText(task.status) }}
                        </span>
                      </div>
                      <div class="timeline-opinion" v-if="task.opinion">{{ task.opinion }}</div>
                    </div>
                  </el-timeline-item>
                </el-timeline>
              </div>
              <div v-if="reviewRecords.length === 0" class="empty-tab">
                <LucideInbox :size="40" />
                <span>暂无审核记录</span>
              </div>
              <el-timeline v-else>
                <el-timeline-item
                  v-for="record in reviewRecords"
                  :key="record.id"
                  :timestamp="formatTime(record.reviewTime)"
                  placement="top"
                  :type="record.result === '0' ? 'primary' : 'danger'"
                >
                  <div class="timeline-card">
                    <div class="timeline-header">
                      <span class="reviewer-name">{{ record.reviewerName }}</span>
                      <span class="review-type">{{ getReviewTypeText(record.reviewType) }}</span>
                      <span class="status-badge small" :class="record.result === '0' ? 'status-done' : 'status-returned'">
                        {{ record.result === '0' ? '通过' : '退回' }}
                      </span>
                    </div>
                    <div class="timeline-opinion" v-if="record.opinion">
                      {{ record.opinion }}
                    </div>
                    <div class="timeline-return" v-if="record.result === '1' && record.returnTarget">
                      退回至：{{ record.returnTarget === '1' ? '县局' : '专员' }}
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-tab-pane>

          <!-- 卷宗材料 -->
          <el-tab-pane label="卷宗材料" name="dossier">
            <div class="tab-content">
              <div class="tab-header">
                <span>卷宗材料列表</span>
                <button class="upload-btn" @click="showUploadDossier = true">
                  <LucideUpload :size="14" /> 上传文件
                </button>
              </div>
              <div v-if="dossierList.length === 0" class="empty-tab">
                <LucideInbox :size="40" />
                <span>暂无卷宗材料</span>
              </div>
              <div v-else class="dossier-list">
                <div v-for="file in dossierList" :key="file.id" class="dossier-item">
                  <div class="file-info">
                    <LucideFile :size="16" class="file-icon" />
                    <span class="file-name">{{ file.fileName }}</span>
                    <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
                  </div>
                  <div class="file-actions">
                    <button class="action-link" @click="previewFile(file)">
                      <LucideEye :size="14" /> 预览
                    </button>
                    <button class="action-link danger" @click="handleDeleteDossier(file)">
                      <LucideTrash2 :size="14" /> 删除
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 文书 -->
          <el-tab-pane label="文书" name="document">
            <div class="tab-content">
              <div class="tab-header">
                <span>文书列表</span>
                <button class="upload-btn" @click="showGenerateDoc = true">
                  <LucideFilePlus :size="14" /> 生成文书
                </button>
              </div>
              <div v-if="documentList.length === 0" class="empty-tab">
                <LucideInbox :size="40" />
                <span>暂无文书</span>
              </div>
              <div v-else class="document-list">
                <div v-for="doc in documentList" :key="doc.id" class="document-item">
                  <div class="doc-info">
                    <LucideFileText :size="16" class="doc-icon" />
                    <span class="doc-name">{{ doc.docName }}</span>
                    <span class="doc-type">{{ doc.docType }}</span>
                    <span class="status-badge small" :class="doc.signStatus === '1' ? 'status-done' : 'status-pending'">
                      {{ doc.signStatus === '1' ? '已签名' : '未签名' }}
                    </span>
                  </div>
                  <div class="doc-actions">
                    <button v-if="doc.signStatus !== '1'" class="action-link" @click="handleSignDoc(doc)">
                      <LucidePenLine :size="14" /> 签署
                    </button>
                    <button class="action-link" @click="handleDownloadDoc(doc)">
                      <LucideDownload :size="14" /> 下载
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 上传卷宗对话框 -->
    <el-dialog v-model="showUploadDossier" title="上传卷宗材料" width="500px" class="xf-dialog">
      <el-upload
        ref="dossierUploadRef"
        :auto-upload="false"
        :limit="5"
        accept=".pdf,.jpg,.jpeg,.png,.zip"
        :on-change="handleDossierFileChange"
        drag
        multiple
      >
        <div style="padding: 40px 20px; text-align: center;">
          <LucideUpload :size="36" style="color: #3B82F6; margin-bottom: 8px;" />
          <div>将文件拖到此处，或<em>点击上传</em></div>
          <div style="font-size: 12px; color: #94A3B8; margin-top: 8px;">支持 PDF、图片、ZIP 格式</div>
        </div>
      </el-upload>
      <template #footer>
        <el-button @click="showUploadDossier = false">取消</el-button>
        <el-button type="primary" @click="submitDossierUpload" :loading="uploadLoading">确认上传</el-button>
      </template>
    </el-dialog>

    <!-- 生成文书对话框 -->
    <el-dialog v-model="showGenerateDoc" title="生成文书" width="480px" class="xf-dialog">
      <el-form label-width="80px">
        <el-form-item label="文书模板">
          <el-select v-model="selectedTemplateId" placeholder="请选择文书模板" style="width: 100%">
            <el-option v-for="tpl in docTemplates" :key="tpl.id" :label="tpl.name" :value="tpl.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGenerateDoc = false">取消</el-button>
        <el-button type="primary" @click="handleGenerateDoc">生成文书</el-button>
      </template>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog v-model="showPreview" :title="previewTitle" width="800px" class="xf-dialog preview-dialog" :close-on-click-modal="true">
      <div class="preview-container">
        <vue-pdf-embed v-if="previewType === 'pdf'" :source="previewUrl" class="pdf-viewer" />
        <el-image v-else-if="previewType === 'image'" :src="previewUrl" fit="contain" class="image-viewer" />
        <div v-else class="unsupported-preview">
          <LucideFile :size="48" />
          <p>该文件类型暂不支持在线预览</p>
          <a :href="previewUrl" target="_blank" class="download-link">点击下载查看</a>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  LucideArrowLeft, LucideFileText, LucideUser, LucideAlignLeft,
  LucideInbox, LucideUpload, LucideFile, LucideTrash2, LucideEye,
  LucideFilePlus, LucidePenLine, LucideDownload, LucideClipboardList
} from 'lucide-vue-next'
import { getWorkOrder, getApproveProgress } from '@/api/xf/workOrder'
import { listAssign } from '@/api/xf/assign'
import { listReview } from '@/api/xf/review'
import { listDossier, uploadDossier, delDossier } from '@/api/xf/dossier'
import { listDocument, generateDocument, signDocument, downloadDocument } from '@/api/xf/document'
import VuePdfEmbed from 'vue-pdf-embed'
import type { XfWorkOrder } from '@/types/api/xf/workOrder'
import type { ApproveTask, ApproveProgress } from '@/types/api/xf/approve'
import type { XfAssignRecord } from '@/types/api/xf/assign'
import type { XfReviewRecord } from '@/types/api/xf/review'
import type { XfDossier } from '@/types/api/xf/dossier'
import type { XfDocument } from '@/types/api/xf/document'

const route = useRoute()
const router = useRouter()
const orderId = Number(route.params.id)

const loading = ref(false)
const orderData = ref<XfWorkOrder | null>(null)
const activeTab = ref('report')

const assignRecords = ref<XfAssignRecord[]>([])
const reviewRecords = ref<XfReviewRecord[]>([])
const approveProgress = ref<ApproveProgress | null>(null)
const approveTaskList = ref<ApproveTask[]>([])
const branchTaskList = ref<ApproveTask[]>([])
const dossierList = ref<XfDossier[]>([])
const documentList = ref<XfDocument[]>([])

const showUploadDossier = ref(false)
const showGenerateDoc = ref(false)
const uploadLoading = ref(false)
const dossierFiles = ref<File[]>([])
const selectedTemplateId = ref<number>(0)

const showPreview = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')
const previewType = ref<'pdf' | 'image' | 'other'>('pdf')

// Mock templates (would come from API)
const docTemplates = ref([
  { id: 1, name: '信访事项交办通知书' },
  { id: 2, name: '信访事项办理报告' },
  { id: 3, name: '信访事项审核意见书' },
  { id: 4, name: '信访事项办结告知书' }
])

const goBack = () => {
  router.back()
}

async function loadOrderDetail() {
  loading.value = true
  try {
    const res = await getWorkOrder(orderId)
    orderData.value = (res.data || null) as any
    approveTaskList.value = orderData.value?.approveTaskList || []
    branchTaskList.value = orderData.value?.branchTaskList || []
  } catch (error) {
    ElMessage.error('加载工单详情失败')
  } finally {
    loading.value = false
  }
}

async function loadApproveProgress() {
  try {
    const res = await getApproveProgress(orderId)
    approveProgress.value = (res.data || null) as ApproveProgress | null
    if (approveProgress.value?.taskList?.length) {
      approveTaskList.value = approveProgress.value.taskList
      branchTaskList.value = approveProgress.value.branchTaskList || approveProgress.value.taskList.filter(task => task.taskType === '0')
    }
  } catch (error) {
    console.error('加载审批进度失败:', error)
  }
}

async function loadAssignRecords() {
  try {
    const res = await listAssign({ orderId, pageNum: 1, pageSize: 100 })
    assignRecords.value = (res.rows || []) as XfAssignRecord[]
  } catch (error) {
    console.error('加载交办记录失败:', error)
  }
}

async function loadReviewRecords() {
  try {
    const res = await listReview({ orderId, pageNum: 1, pageSize: 100 })
    reviewRecords.value = (res.rows || []) as XfReviewRecord[]
  } catch (error) {
    console.error('加载审核记录失败:', error)
  }
}

async function loadDossierList() {
  try {
    const res = await listDossier({ orderId })
    dossierList.value = (res.rows || []) as XfDossier[]
  } catch (error) {
    console.error('加载卷宗列表失败:', error)
  }
}

async function loadDocumentList() {
  try {
    const res = await listDocument({ orderId })
    documentList.value = (res.rows || []) as XfDocument[]
  } catch (error) {
    console.error('加载文书列表失败:', error)
  }
}

function handleDossierFileChange(file: any, fileList: any[]) {
  dossierFiles.value = fileList.map((f: any) => f.raw)
}

async function submitDossierUpload() {
  if (dossierFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的文件')
    return
  }
  uploadLoading.value = true
  try {
    for (const file of dossierFiles.value) {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('orderId', String(orderId))
      await uploadDossier(formData)
    }
    ElMessage.success('上传成功')
    showUploadDossier.value = false
    dossierFiles.value = []
    loadDossierList()
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.value = false
  }
}

async function handleDeleteDossier(file: XfDossier) {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗？', '提示', { type: 'warning' })
    await delDossier(file.id!)
    ElMessage.success('删除成功')
    loadDossierList()
  } catch { /* cancelled */ }
}

async function handleGenerateDoc() {
  if (!selectedTemplateId.value) {
    ElMessage.warning('请选择文书模板')
    return
  }
  try {
    await generateDocument({ orderId, templateId: selectedTemplateId.value })
    ElMessage.success('文书生成成功')
    showGenerateDoc.value = false
    loadDocumentList()
  } catch (error) {
    ElMessage.error('生成文书失败')
  }
}

async function handleSignDoc(doc: XfDocument) {
  try {
    await ElMessageBox.confirm('确定要签署该文书吗？签署后不可修改。', '提示', { type: 'warning' })
    await signDocument(doc.id!)
    ElMessage.success('签署成功')
    loadDocumentList()
  } catch { /* cancelled */ }
}

async function handleDownloadDoc(doc: XfDocument) {
  try {
    const blob = await downloadDocument(doc.id!) as any
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = doc.docName || '文书.docx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

// Helpers
function getStatusClass(status: string | undefined) {
  if (!status) return ''
  const map: Record<string, string> = {
    '0': 'status-pending', '1': 'status-progress', '2': 'status-reported',
    '3': 'status-done', '4': 'status-returned', '5': 'status-overdue'
  }
  return map[status] || ''
}

function getStatusText(status: string | undefined) {
  if (!status) return '未知'
  const map: Record<string, string> = {
    '0': '待派单', '1': '待提交', '2': '审批中',
    '3': '已办结', '4': '已退回', '5': '已超期'
  }
  return map[status] || status
}

function getAssignStatusClass(status: string | undefined) {
  if (!status) return ''
  const map: Record<string, string> = {
    '0': 'status-pending', '1': 'status-progress', '2': 'status-reported', '3': 'status-returned'
  }
  return map[status] || ''
}

function getAssignStatusText(status: string | undefined) {
  if (!status) return '未知'
  const map: Record<string, string> = { '0': '待接收', '1': '办理中', '2': '已上报', '3': '已退回' }
  return map[status] || status
}

function getReviewTypeText(type: string | undefined) {
  if (!type) return ''
  const map: Record<string, string> = { '1': '派出所审核', '2': '县局审核', '3': '专员审核', '4': '领导审核' }
  return map[type] || type
}

function getApproveTaskStatusText(status: string | undefined) {
  const map: Record<string, string> = { '0': '待审批', '1': '已通过', '2': '已驳回' }
  return status ? (map[status] || status) : '未知'
}

function getOrderNoClass(order: XfWorkOrder | null) {
  if (!order) return ''
  if (order.status === '5') return 'text-overdue'
  if (order.status === '1' || order.status === '2') return 'text-progress'
  if (order.status === '3') return 'text-done'
  return ''
}

function formatTime(time: string | undefined) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

function previewAttachment(filePath: string) {
  if (!filePath) return
  previewUrl.value = filePath
  previewTitle.value = getFileName(filePath)
  previewType.value = getFilePreviewType(filePath)
  showPreview.value = true
}

function previewFile(file: XfDossier) {
  if (file.filePath) {
    previewUrl.value = file.filePath
    previewTitle.value = file.fileName || '文件预览'
    previewType.value = getFilePreviewType(file.fileName || '')
    showPreview.value = true
  }
}

function getFileName(filePath: string) {
  if (!filePath) return ''
  return filePath.split('/').pop() || filePath
}

function getFilePreviewType(fileName: string): 'pdf' | 'image' | 'other' {
  const ext = fileName.split('.').pop()?.toLowerCase() || ''
  if (ext === 'pdf') return 'pdf'
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) return 'image'
  return 'other'
}

function formatFileSize(size: number | undefined) {
  if (!size) return '-'
  if (size < 1024) return size + 'B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + 'KB'
  return (size / (1024 * 1024)).toFixed(1) + 'MB'
}

onMounted(() => {
  loadOrderDetail()
  loadApproveProgress()
  loadAssignRecords()
  loadReviewRecords()
  loadDossierList()
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

.detail-wrapper {
  padding: 24px;
  background: $police-bg;
  height: calc(100vh - 48px);
  overflow-y: auto;
}

.detail-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 24px;
  background: white;
  border-radius: 16px;
  border: 1px solid $police-border;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;

  &:hover { background: #F5F5F5; border-color: $police-light-blue; color: $police-blue; }
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;

  &.status-pending { background: #FEF3C7; color: #D97706; }
  &.status-progress { background: #DBEAFE; color: #2563EB; }
  &.status-reported { background: #E0E7FF; color: #4F46E5; }
  &.status-done { background: #D1FAE5; color: #059669; }
  &.status-returned { background: #F3E8FF; color: #7C3AED; }
  &.status-overdue { background: #FEE2E2; color: #DC2626; }

  &.small { padding: 3px 8px; font-size: 11px; }
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.detail-left, .detail-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 16px;
  border: 1px solid $police-border;
  padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: $police-primary;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid $police-border;

  svg { color: $police-blue; }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px 24px;
}

.info-item {
  display: flex;
  gap: 12px;

  &.full { grid-column: 1 / -1; }

  .item-label { width: 80px; font-size: 13px; color: $police-text-muted; flex-shrink: 0; }
  .item-value { font-size: 13px; color: $police-text; flex: 1; word-break: break-all; }

  .item-value.scrollable {
    max-height: 150px;
    overflow-y: auto;
    line-height: 1.6;
    padding-right: 4px;
  }

  .item-value.text-overdue { color: #F56C6C; font-weight: 600; }
  .item-value.text-progress { color: #409EFF; font-weight: 600; }
  .item-value.text-done { color: #303133; }
}

.opinion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.opinion-item {
  padding: 14px 16px;
  background: #F8FAFE;
  border-radius: 12px;
  border: 1px solid $police-border;
}

.opinion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.opinion-dept {
  font-size: 13px;
  font-weight: 600;
  color: $police-text;
}

.opinion-time {
  font-size: 12px;
  color: $police-text-muted;
}

.opinion-text {
  font-size: 13px;
  color: $police-text;
  line-height: 1.6;
}

.content-body {
  padding: 16px;
  background: #F8FAFE;
  border-radius: 12px;
  font-size: 14px;
  color: $police-text;
  line-height: 1.8;
  min-height: 80px;

  :deep(img) { max-width: 100%; border-radius: 8px; }
}

// Tabs area
.detail-right {
  .info-card { min-height: auto; }
}

.detail-tabs {
  :deep(.el-tabs__header) { margin-bottom: 16px; }
  :deep(.el-tabs__item.is-active) { color: $police-blue; font-weight: 600; }
  :deep(.el-tabs__active-bar) { background-color: $police-blue; }
}

.tab-content { padding: 0 4px; }

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 600;
  color: $police-primary;
}

.upload-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 10px;
  border: 1px solid $police-light-blue;
  background: white;
  color: $police-blue;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;

  &:hover { background: #EFF6FF; }
}

.empty-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: $police-text-muted;
  gap: 12px;
}

// Report cards
.report-card {
  padding: 16px;
  border-radius: 12px;
  border: 1px solid $police-border;
  margin-bottom: 12px;
  transition: all 0.2s;

  &:hover { box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); }
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  .dept-name { font-weight: 600; color: $police-text; font-size: 14px; }
}

.report-body {
  margin-bottom: 10px;

  .report-label { font-size: 12px; color: $police-text-muted; margin-bottom: 6px; }
  .report-text { font-size: 13px; color: $police-text; line-height: 1.6; padding: 10px; background: #F8FAFE; border-radius: 8px; }
}

.report-attachment {
  margin-bottom: 10px;

  .report-label { font-size: 12px; color: $police-text-muted; margin-bottom: 6px; }
}

.attachment-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #EFF6FF;
  border: 1px solid #BAE6FD;
  border-radius: 8px;
  color: $police-blue;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover { background: #DBEAFE; }
}

.report-footer {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: $police-text-muted;
}

// Timeline
.timeline-card {
  padding: 12px 16px;
  background: #F8FAFE;
  border-radius: 12px;
  border: 1px solid $police-border;
}

.timeline-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;

  .reviewer-name { font-weight: 600; font-size: 14px; color: $police-text; }
  .review-type { font-size: 12px; color: $police-text-muted; padding: 2px 8px; background: #EFF6FF; border-radius: 8px; }
}

.timeline-opinion { font-size: 13px; color: $police-text; line-height: 1.6; }
.timeline-return { font-size: 12px; color: #EF4444; margin-top: 6px; }

// Dossier list
.dossier-item, .document-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid $police-border;
  border-radius: 10px;
  margin-bottom: 8px;
  transition: all 0.2s;

  &:hover { box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04); }
}

.file-info, .doc-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .file-icon, .doc-icon { color: $police-blue; }
  .file-name, .doc-name { font-size: 13px; color: $police-text; font-weight: 500; }
  .file-size { font-size: 12px; color: $police-text-muted; }
  .doc-type { font-size: 11px; color: $police-text-muted; padding: 2px 6px; background: #F5F5F5; border-radius: 4px; }
}

.file-actions, .doc-actions {
  display: flex;
  gap: 8px;
}

.action-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;

  &:hover { background: #F5F5F5; }
  &.danger { border-color: #FCA5A5; color: #EF4444; &:hover { background: #FEF2F2; } }
}

// Dialog
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

.preview-dialog {
  :deep(.el-dialog__body) { padding: 0; }

  .preview-container {
    min-height: 500px;
    max-height: 70vh;
    overflow-y: auto;
    background: #525659;

    .pdf-viewer {
      width: 100%;
    }

    .image-viewer {
      width: 100%;
      max-height: 70vh;
    }

    .unsupported-preview {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 300px;
      color: #94A3B8;
      gap: 12px;

      .download-link {
        color: $police-blue;
        text-decoration: underline;
        cursor: pointer;
      }
    }
  }
}

@media (max-width: 1100px) {
  .detail-layout { grid-template-columns: 1fr; }
}
</style>
