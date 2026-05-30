<template>
  <div class="police-page profile-page">
    <div class="profile-grid">
      <!-- 左侧：个人信息卡片 -->
      <div class="profile-card info-card">
        <div class="card-header">
          <h3><LucideUser class="icon-small" /> 个人信息</h3>
        </div>
        <div class="card-body">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img v-if="avatarUrl" :src="avatarUrl" class="avatar-img" />
              <div v-else class="avatar-placeholder">
                <LucideUser :size="48" />
              </div>
              <button class="avatar-change-btn" @click="showAvatarDialog = true">
                <LucideCamera :size="14" />
              </button>
            </div>
            <div class="user-name">{{ user.nickName || user.userName || '用户' }}</div>
            <div class="user-dept">{{ user.dept?.deptName || '未分配部门' }}</div>
          </div>

          <!-- 信息列表 -->
          <div class="info-list">
            <div class="info-item">
              <LucideUser class="info-icon" />
              <span class="info-label">用户名称</span>
              <span class="info-value">{{ user.userName || '-' }}</span>
            </div>
            <div class="info-item">
              <LucidePhone class="info-icon" />
              <span class="info-label">手机号码</span>
              <span class="info-value">{{ user.phonenumber || '-' }}</span>
            </div>
            <div class="info-item">
              <LucideMail class="info-icon" />
              <span class="info-label">用户邮箱</span>
              <span class="info-value">{{ user.email || '-' }}</span>
            </div>
            <div class="info-item">
              <LucideBuilding class="info-icon" />
              <span class="info-label">所属部门</span>
              <span class="info-value">{{ user.dept?.deptName || '-' }}</span>
            </div>
            <div class="info-item">
              <LucideShield class="info-icon" />
              <span class="info-label">所属角色</span>
              <span class="info-value">{{ roleGroup || '-' }}</span>
            </div>
            <div class="info-item">
              <LucideCalendar class="info-icon" />
              <span class="info-label">创建日期</span>
              <span class="info-value">{{ user.createTime || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：编辑区域 -->
      <div class="profile-card edit-card">
        <div class="card-header">
          <h3><LucideSettings class="icon-small" /> 账号设置</h3>
        </div>
        <div class="card-body">
          <!-- 标签页 -->
          <div class="tab-nav">
            <button
              class="tab-btn"
              :class="{ active: activeTab === 'userinfo' }"
              @click="activeTab = 'userinfo'"
            >
              <LucideUser :size="16" />
              <span>基本资料</span>
            </button>
            <button
              class="tab-btn"
              :class="{ active: activeTab === 'resetPwd' }"
              @click="activeTab = 'resetPwd'"
            >
              <LucideLock :size="16" />
              <span>修改密码</span>
            </button>
          </div>

          <!-- 基本资料表单 -->
          <div v-if="activeTab === 'userinfo'" class="tab-content">
            <form class="police-form" @submit.prevent="submitUserInfo">
              <div class="form-row">
                <label class="form-label required">用户昵称</label>
                <input
                  v-model="userInfoForm.nickName"
                  class="form-input"
                  placeholder="请输入用户昵称"
                  maxlength="30"
                  required
                />
              </div>
              <div class="form-row">
                <label class="form-label required">手机号码</label>
                <input
                  v-model="userInfoForm.phonenumber"
                  class="form-input"
                  placeholder="请输入手机号码"
                  maxlength="11"
                  required
                />
              </div>
              <div class="form-row">
                <label class="form-label required">邮箱</label>
                <input
                  v-model="userInfoForm.email"
                  class="form-input"
                  placeholder="请输入邮箱地址"
                  maxlength="50"
                  type="email"
                  required
                />
              </div>
              <div class="form-row">
                <label class="form-label">性别</label>
                <div class="radio-group">
                  <label class="radio-item" :class="{ active: userInfoForm.sex === '0' }">
                    <input type="radio" v-model="userInfoForm.sex" value="0" />
                    <span>男</span>
                  </label>
                  <label class="radio-item" :class="{ active: userInfoForm.sex === '1' }">
                    <input type="radio" v-model="userInfoForm.sex" value="1" />
                    <span>女</span>
                  </label>
                </div>
              </div>
              <div class="form-actions">
                <button type="submit" class="submit-btn" :disabled="submitLoading">
                  <LucideLoader2 v-if="submitLoading" class="icon-spin" />
                  <LucideSave v-else :size="16" />
                  <span>保存修改</span>
                </button>
              </div>
            </form>
          </div>

          <!-- 修改密码表单 -->
          <div v-if="activeTab === 'resetPwd'" class="tab-content">
            <form class="police-form" @submit.prevent="submitPwd">
              <div class="form-row">
                <label class="form-label required">旧密码</label>
                <input
                  v-model="pwdForm.oldPassword"
                  class="form-input"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password-toggle
                  required
                />
              </div>
              <div class="form-row">
                <label class="form-label required">新密码</label>
                <input
                  v-model="pwdForm.newPassword"
                  class="form-input"
                  type="password"
                  placeholder="请输入新密码（6-20位）"
                  maxlength="20"
                  required
                />
                <div class="form-hint">密码长度6-20位，不能包含特殊字符</div>
              </div>
              <div class="form-row">
                <label class="form-label required">确认密码</label>
                <input
                  v-model="pwdForm.confirmPassword"
                  class="form-input"
                  type="password"
                  placeholder="请确认新密码"
                  required
                />
              </div>
              <div class="form-actions">
                <button type="submit" class="submit-btn" :disabled="pwdLoading">
                  <LucideLoader2 v-if="pwdLoading" class="icon-spin" />
                  <LucideSave v-else :size="16" />
                  <span>修改密码</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像修改对话框 -->
    <div v-if="showAvatarDialog" class="modal-overlay" @click.self="showAvatarDialog = false">
      <div class="modal-content avatar-modal">
        <div class="modal-header">
          <h3><LucideCamera :size="16" /> 修改头像</h3>
          <button class="close-btn" @click="showAvatarDialog = false">
            <LucideX :size="16" />
          </button>
        </div>
        <div class="modal-body">
          <div class="avatar-upload-area">
            <div class="upload-preview">
              <img v-if="previewAvatar" :src="previewAvatar" class="preview-img" />
              <div v-else class="upload-placeholder">
                <LucideImagePlus :size="32" />
                <span>点击上传图片</span>
              </div>
            </div>
            <input
              type="file"
              accept="image/*"
              class="upload-input"
              @change="handleAvatarChange"
              ref="avatarInputRef"
            />
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="showAvatarDialog = false">取消</button>
            <button type="button" class="submit-btn" :disabled="avatarLoading" @click="submitAvatar">
              <LucideLoader2 v-if="avatarLoading" class="icon-spin" />
              <span v-else>确认上传</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import {
  LucideUser, LucidePhone, LucideMail, LucideBuilding, LucideShield,
  LucideCalendar, LucideSettings, LucideLock, LucideSave, LucideCamera,
  LucideLoader2, LucideX, LucideImagePlus
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, updateUserPwd, uploadAvatar } from '@/api/system/user'
import useUserStore from '@/store/modules/user'
import type { SysUser } from '@/types/api/system/user'

const userStore = useUserStore()

const activeTab = ref('userinfo')
const submitLoading = ref(false)
const pwdLoading = ref(false)
const avatarLoading = ref(false)
const showAvatarDialog = ref(false)
const avatarInputRef = ref<HTMLInputElement | null>(null)

const user = ref<SysUser>({})
const roleGroup = ref('')
const postGroup = ref('')

const avatarUrl = computed(() => {
  if (user.value.avatar) {
    return user.value.avatar.startsWith('http')
      ? user.value.avatar
      : import.meta.env.VITE_APP_BASE_API + user.value.avatar
  }
  return ''
})

const previewAvatar = ref('')

// 用户信息表单
const userInfoForm = reactive({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: '0'
})

// 密码表单
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 加载用户信息
async function loadUserProfile() {
  try {
    const response = await getUserProfile()
    user.value = response.data
    roleGroup.value = response.roleGroup
    postGroup.value = response.postGroup

    // 填充表单
    userInfoForm.nickName = response.data.nickName || ''
    userInfoForm.phonenumber = response.data.phonenumber || ''
    userInfoForm.email = response.data.email || ''
    userInfoForm.sex = response.data.sex || '0'
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 提交用户信息
async function submitUserInfo() {
  if (!userInfoForm.nickName) {
    ElMessage.warning('请输入用户昵称')
    return
  }
  if (!userInfoForm.phonenumber || !/^1[3|4|5|6|7|8|9][0-9]\d{8}$/.test(userInfoForm.phonenumber)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  if (!userInfoForm.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(userInfoForm.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }

  submitLoading.value = true
  try {
    await updateUserProfile(userInfoForm)
    ElMessage.success('修改成功')
    user.value.nickName = userInfoForm.nickName
    user.value.phonenumber = userInfoForm.phonenumber
    user.value.email = userInfoForm.email
    user.value.sex = userInfoForm.sex

    // 更新 store
    if (userStore.user) {
      userStore.user.nickName = userInfoForm.nickName
    }
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    submitLoading.value = false
  }
}

// 提交密码修改
async function submitPwd() {
  if (!pwdForm.oldPassword) {
    ElMessage.warning('请输入旧密码')
    return
  }
  if (!pwdForm.newPassword || pwdForm.newPassword.length < 6 || pwdForm.newPassword.length > 20) {
    ElMessage.warning('新密码长度需在6-20位之间')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  pwdLoading.value = true
  try {
    await updateUserPwd(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    pwdLoading.value = false
  }
}

// 头像上传
function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      previewAvatar.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

async function submitAvatar() {
  const file = avatarInputRef.value?.files?.[0]
  if (!file) {
    ElMessage.warning('请先选择图片')
    return
  }

  avatarLoading.value = true
  try {
    const formData = new FormData()
    formData.append('avatarfile', file)
    const response = await uploadAvatar(formData)
    if (response.imgUrl) {
      user.value.avatar = response.imgUrl
      previewAvatar.value = ''
      showAvatarDialog.value = false
      ElMessage.success('头像修改成功')

      // 更新 store
      if (userStore.user) {
        userStore.user.avatar = response.imgUrl
      }
    }
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarLoading.value = false
  }
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style lang="scss" scoped>
$police-primary: #1E3A8A;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F8FAFE;
$police-card: #FFFFFF;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;

.police-page {
  padding: 24px;
  background: $police-bg;
  min-height: 100%;
}

.profile-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  height: calc(100vh - 180px);
}

.profile-card {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid $police-border;

  h3 {
    font-size: 13px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.card-body {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.icon-small { width: 16px; height: 16px; }

// 头像区域
.avatar-section {
  text-align: center;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid $police-border;
}

.avatar-wrapper {
  width: 100px;
  height: 100px;
  margin: 0 auto 12px;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, $police-light-blue, $police-blue);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: white;
}

.avatar-change-btn {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: white;
  border: 2px solid $police-border;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $police-text-muted;
  transition: all 0.2s;

  &:hover {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: $police-text;
  margin-bottom: 4px;
}

.user-dept {
  font-size: 13px;
  color: $police-text-muted;
}

// 信息列表
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #F5F5F5;
  border-radius: 12px;

  .info-icon {
    width: 16px;
    height: 16px;
    color: $police-blue;
  }

  .info-label {
    font-size: 12px;
    color: $police-text-muted;
    width: 60px;
  }

  .info-value {
    font-size: 13px;
    color: $police-text;
    flex: 1;
    text-align: right;
  }
}

// 标签页导航
.tab-nav {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 20px;
  border-radius: 20px;
  border: 1px solid $police-border;
  background: white;
  font-size: 13px;
  font-weight: 500;
  color: $police-text-muted;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;

  &:hover {
    background: #F5F5F5;
    border-color: $police-text-muted;
  }

  &.active {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }
}

// 表单样式
.police-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 12px;
  color: $police-text-muted;

  &.required::after {
    content: '*';
    color: #EF4444;
    margin-left: 4px;
  }
}

.form-input {
  padding: 12px 16px;
  border: 1px solid $police-border;
  border-radius: 12px;
  font-size: 13px;
  color: $police-text;
  background: #FAFAFA;
  transition: all 0.2s;

  &:focus {
    outline: none;
    border-color: $police-blue;
    background: white;
  }
}

.form-hint {
  font-size: 11px;
  color: $police-text-muted;
  margin-top: 4px;
}

.radio-group {
  display: flex;
  gap: 12px;
}

.radio-item {
  padding: 10px 20px;
  border-radius: 20px;
  border: 1px solid $police-border;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;

  input { display: none; }

  &:hover {
    border-color: $police-blue;
  }

  &.active {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
}

.submit-btn {
  padding: 12px 24px;
  border-radius: 20px;
  border: none;
  background: $police-blue;
  font-size: 13px;
  font-weight: 500;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: $police-light-blue;
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.cancel-btn {
  padding: 12px 24px;
  border-radius: 20px;
  border: 1px solid $police-border;
  background: white;
  font-size: 13px;
  font-weight: 500;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #F5F5F5;
  }
}

// 模态框
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  overflow: hidden;
}

.avatar-modal {
  width: 400px;
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .close-btn {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    border: 1px solid $police-border;
    background: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #F5F5F5;
    }
  }
}

.modal-body {
  padding: 20px;
}

.avatar-upload-area {
  text-align: center;
  margin-bottom: 20px;
}

.upload-preview {
  width: 150px;
  height: 150px;
  margin: 0 auto;
  border-radius: 50%;
  overflow: hidden;
  border: 2px dashed $police-border;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: $police-blue;
  }
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: $police-text-muted;
  font-size: 13px;
}

.upload-input {
  display: none;
}

.icon-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@media (max-width: 900px) {
  .profile-grid {
    grid-template-columns: 1fr;
    height: auto;
  }

  .info-card {
    max-height: 400px;
  }
}
</style>