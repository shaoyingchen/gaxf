<template>
  <div class="login-wrapper">
    <!-- 背景图片（动态绑定导入的图片） -->
    <div class="bg-image" :style="{ backgroundImage: `url(${bgImage})` }"></div>
    <div class="bg-overlay"></div>

    <!-- 主容器：左右分屏 -->
    <div class="container">
      <!-- 左侧：标题 + 警徽 + 装饰线 -->
      <div class="left-hero">
        <div class="hero-title">
          <div class="title-with-logo">
            <!-- 警徽图片动态绑定 -->
            <img :src="policeLogo" alt="警徽" class="police-logo" />
            <span class="hero-title-chinese">公安视频督察系统</span>
          </div>
          <span class="hero-title-english">YANCHENG TRAFFIC CALENDAR</span>
        </div>
        <div class="hero-decoration">
          <div class="line-bluewhite"></div>
          <div class="line-bluewhite"></div>
          <div class="line-bluewhite"></div>
        </div>
      </div>

      <!-- 右侧：登录卡片（玻璃态） -->
      <div class="right-login">
        <div class="login-card">
          <div class="emblem">
            <i class="fas fa-building-columns"></i>
            <h2>盐城交管</h2>
            <p>TRAFFIC MANAGEMENT LOGIN</p>
          </div>

          <div class="welcome-message">
            <h3>欢迎回来</h3>
            <span>请输入您的数字凭证</span>
          </div>

          <!-- 表单区域：保留 el-form 以复用原有验证逻辑 -->
          <el-form
            ref="loginRef"
            :model="loginForm"
            :rules="loginRules"
            class="custom-login-form"
            @keyup.enter="handleLogin"
          >
            <!-- 用户名 -->
            <el-form-item prop="username" class="custom-form-item">
              <div class="input-group">
                <i class="fas fa-user-tie"></i>
                <input
                  v-model="loginForm.username"
                  type="text"
                  placeholder="用户名"
                  autocomplete="off"
                />
              </div>
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="password" class="custom-form-item">
              <div class="input-group">
                <i class="fas fa-lock"></i>
                <input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  autocomplete="off"
                />
              </div>
            </el-form-item>

            <!-- 验证码（根据后台开关显示） -->
            <el-form-item prop="code" class="custom-form-item" v-if="captchaEnabled">
              <div class="input-group code-group">
                <i class="fas fa-shield-alt"></i>
                <input
                  v-model="loginForm.code"
                  type="text"
                  placeholder="验证码"
                  autocomplete="off"
                  class="code-input"
                />
                <div class="login-code">
                  <img :src="codeUrl" @click="getCode" class="login-code-img" alt="验证码" />
                </div>
              </div>
            </el-form-item>

            <!-- 记住我 & 忘记密码 -->
            <div class="options">
              <label class="remember">
                <input type="checkbox" v-model="loginForm.rememberMe" />
                <span>记住本次登录</span>
              </label>
              <a href="#" class="forgot-link" @click.prevent>忘记密码？</a>
            </div>

            <!-- 登录按钮 -->
            <button
              type="button"
              class="login-btn"
              :disabled="loading"
              @click="handleLogin"
              :data-loading="loading ? '正在验证登录，请稍候...' : ''"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </button>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="el-login-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCodeImg } from '@/api/login'
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import useUserStore from '@/store/modules/user'
import defaultSettings from '@/settings'
import type { LoginForm } from '@/types/api/login'

// 方案二：通过 import 导入位于 src/assets 目录下的图片资源
import policeLogo from '@/assets/images/login_yclogo.png'
import bgImage from '@/assets/images/login-background.jpg'

const title = import.meta.env.VITE_APP_TITLE
const footerContent = defaultSettings.footerContent
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance() as any

const loginForm = ref<LoginForm>({
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  code: '',
  uuid: ''
})

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
}

const codeUrl = ref('')
const loading = ref(false)
const captchaEnabled = ref(true)
const register = ref(false) // 未使用注册功能，保留变量
const redirect = ref<string | undefined>(undefined)

watch(
  route,
  (newRoute: any) => {
    redirect.value = (newRoute.query && newRoute.query.redirect) as string | undefined
  },
  { immediate: true }
)

// 登录处理（保留原逻辑）
function handleLogin(): void {
  proxy.$refs.loginRef.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      // 记住密码逻辑
      if (loginForm.value.rememberMe) {
        Cookies.set('username', loginForm.value.username, { expires: 30 })
        Cookies.set('password', encrypt(loginForm.value.password), { expires: 30 })
        Cookies.set('rememberMe', String(loginForm.value.rememberMe), { expires: 30 })
      } else {
        Cookies.remove('username')
        Cookies.remove('password')
        Cookies.remove('rememberMe')
      }
      userStore
        .login(loginForm.value)
        .then(() => {
     
          // 登录成功后的跳转逻辑
            const query = route.query
            const otherQueryParams = Object.keys(query).reduce((acc: Record<string, any>, cur) => {
              if (cur !== 'redirect') {
                acc[cur] = query[cur]
              }
              return acc
            }, {})

            // 如果 redirect 为空、为 '/' 或为 '/index'，则默认跳转至自定义首页 /home
            let targetPath = redirect.value
            if (!targetPath || targetPath === '/' || targetPath === '/index') {
              targetPath = '/home'
            }

            router.push({ path: targetPath, query: otherQueryParams })
        })
        .catch(() => {
          loading.value = false
          if (captchaEnabled.value) {
            getCode()
          }
        })
    }
  })
}

// 获取验证码
function getCode(): void {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      loginForm.value.uuid = res.uuid
    }
  })
}

// 读取 cookie 中的账号密码
function getCookie(): void {
  const username = Cookies.get('username')
  const password = Cookies.get('password')
  const rememberMe = Cookies.get('rememberMe')
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password as string),
    rememberMe: rememberMe === undefined ? false : rememberMe === 'true',
    code: '',
    uuid: ''
  }
}

getCode()
getCookie()
</script>

<style lang="scss" scoped>
/* ========= 全局重置 & 字体 ========= */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-wrapper {
  font-family: 'Noto Sans SC', sans-serif;
  min-height: 100vh;
  color: #1e3a5f;
  position: relative;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

/* ========= 背景图片 + 淡蓝色遮罩 ========= */
.bg-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center 30%;
  background-repeat: no-repeat;
  z-index: -2;
  filter: brightness(1.1) saturate(0.8);
  animation: bgSlowZoom 24s infinite alternate ease-in-out;
}

@keyframes bgSlowZoom {
  0% {
    transform: scale(1);
  }
  100% {
    transform: scale(1.05);
  }
}

.bg-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(160, 200, 240, 0.35);
  z-index: -1;
  pointer-events: none;
}

/* ========= 主容器：左右分屏 ========= */
.container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px;
  align-items: center;
  justify-content: space-between;
  gap: 50px;
  position: relative;
  z-index: 5;
  flex: 1;
}

/* ========= 左侧：标题与装饰 ========= */
.left-hero {
  flex: 1.3;
  padding: 20px 40px 20px 20px;
  animation: fadeInLeft 1.2s ease-out;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  height: 80vh;
  margin-top: -5vh;
}

@keyframes fadeInLeft {
  0% {
    opacity: 0;
    transform: translateX(-40px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

.hero-title {
  font-size: clamp(2.2rem, 6vw, 4rem);
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 0.3rem;
  text-shadow: 0 8px 25px rgba(0, 60, 120, 0.4), 0 0 40px rgba(255, 255, 255, 0.6);
  letter-spacing: 5px;
}

.title-with-logo {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.police-logo {
  width: 78px;
  height: auto;
  filter: drop-shadow(0 0 12px rgba(70, 130, 200, 0.7));
  transition: all 0.3s ease;
}

.police-logo:hover {
  filter: drop-shadow(0 0 20px #5a9eff);
}

.hero-title-chinese {
  display: block;
  background: linear-gradient(to right, #0a3366, #2a5c9a, #4a7db0, #6f9fd3);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  -webkit-text-stroke: 0.6px rgba(255, 255, 255, 0.25);
  font-weight: 800;
}

.hero-title-english {
  display: block;
  font-size: 1.6rem;
  font-weight: 600;
  letter-spacing: 7px;
  color: #fffaec;
  text-shadow: 0 0 30px #7fb4ff, 0 0 60px #4a7db0, 0 4px 12px #002b5c;
  border-bottom: 4px solid #4a7db0;
  padding-bottom: 12px;
  margin-top: 0;
  text-align: left;
}

.hero-decoration {
  display: flex;
  gap: 18px;
  margin-top: 32px;
  justify-content: center;
}

.line-bluewhite {
  width: 85px;
  height: 5px;
  background: linear-gradient(90deg, #2a5c9a, #aac9ff, #ffffff);
  border-radius: 3px;
  animation: pulseWidth 3.2s infinite alternate;
  box-shadow: 0 0 12px rgba(100, 160, 255, 0.6);
}

.line-bluewhite:nth-child(2) {
  width: 160px;
  animation-delay: 0.6s;
}

.line-bluewhite:nth-child(3) {
  width: 110px;
  animation-delay: 1.2s;
}

@keyframes pulseWidth {
  0% {
    opacity: 0.7;
    width: 65px;
  }
  100% {
    opacity: 1;
    width: 170px;
  }
}

/* ========= 右侧：登录卡片 ========= */
.right-login {
  flex: 0 0 460px;
  animation: fadeInRight 1.2s ease-out 0.3s both;
}

@keyframes fadeInRight {
  0% {
    opacity: 0;
    transform: translateX(40px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

.login-card {
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border: 2px solid rgba(70, 130, 200, 0.5);
  border-radius: 52px;
  padding: 3.2rem 2.8rem;
  box-shadow: 0 30px 60px rgba(30, 60, 120, 0.2), 0 0 0 1px rgba(255, 255, 255, 0.7) inset,
    0 0 70px rgba(130, 180, 255, 0.3);
  transition: all 0.4s ease;
}

.login-card:hover {
  border-color: rgba(70, 130, 200, 0.9);
  box-shadow: 0 40px 80px rgba(30, 60, 120, 0.3), 0 0 0 2px rgba(255, 255, 255, 0.8) inset,
    0 0 100px rgba(100, 160, 255, 0.5);
  transform: translateY(-3px);
}

.emblem {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 28px;
}

.emblem i {
  font-size: 3.8rem;
  background: linear-gradient(145deg, #2a5c9a, #6f9fd3);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  filter: drop-shadow(0 0 12px #5a9eff);
}

.emblem h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-top: 10px;
  color: #1e3a5f;
  text-shadow: 0 3px 8px rgba(255, 255, 255, 0.7);
  border-bottom: 3px solid #6f9fd3;
  padding-bottom: 8px;
}

.emblem p {
  font-size: 0.85rem;
  opacity: 0.85;
  letter-spacing: 2.5px;
  margin-top: 6px;
  color: #1e3a5f;
}

.welcome-message {
  text-align: center;
  margin-bottom: 2.2rem;
}

.welcome-message h3 {
  font-size: 1.7rem;
  font-weight: 500;
  color: #1e3a5f;
  text-shadow: 0 2px 6px rgba(255, 255, 255, 0.5);
}

.welcome-message span {
  font-size: 0.95rem;
  opacity: 0.8;
  color: #1e3a5f;
}

/* ========= 自定义表单样式（覆盖 element-plus） ========= */
.custom-login-form {
  width: 100%;
}

.custom-form-item {
  margin-bottom: 2rem !important;
  display: block;
}

:deep(.custom-form-item .el-form-item__content) {
  margin-left: 0 !important;
  line-height: normal;
}

:deep(.custom-form-item .el-form-item__error) {
  padding-left: 20px;
  color: #c44536;
  font-weight: 500;
}

.input-group {
  position: relative;
  width: 100%;
}

.input-group i {
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.4rem;
  color: #2a5c9a;
  text-shadow: 0 0 12px #aac9ff;
  z-index: 2;
  pointer-events: none;
}

.input-group input {
  width: 100%;
  background: #f0f7ff;
  border: 1.5px solid #9bb9e0;
  border-radius: 55px;
  padding: 1.2rem 2rem 1.2rem 4rem;
  font-size: 1.05rem;
  color: #0a3366;
  outline: none;
  transition: all 0.3s ease;
  letter-spacing: 0.6px;
}

.input-group input::placeholder {
  color: #4f6f8f;
  font-weight: 300;
}

.input-group input:hover {
  background: #e5f0ff;
  border-color: #6f9fd3;
}

.input-group input:focus {
  border-color: #2a5c9a;
  background: #ffffff;
  box-shadow: 0 0 0 5px rgba(42, 92, 154, 0.15), 0 0 35px rgba(100, 160, 255, 0.3);
  color: #0a3366;
}

/* 验证码行特殊处理 */
.code-group {
  display: flex;
  align-items: center;
}

.code-input {
  flex: 1;
  padding-right: 120px !important; /* 为验证码图片留空间 */
}

.login-code {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  height: 46px;
  width: auto;
  z-index: 3;
}

.login-code-img {
  height: 46px;
  border-radius: 30px;
  cursor: pointer;
  border: 1px solid #6f9fd3;
  box-shadow: 0 4px 12px rgba(42, 92, 154, 0.2);
  transition: 0.2s;
}

.login-code-img:hover {
  border-color: #2a5c9a;
  box-shadow: 0 6px 16px rgba(42, 92, 154, 0.4);
}

/* 选项区域（记住我 & 忘记密码） */
.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1.8rem 0 2.5rem;
  font-size: 0.95rem;
  color: #1e3a5f;
}

.remember {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.remember input[type='checkbox'] {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid #2a5c9a;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remember input[type='checkbox']:checked {
  background: #2a5c9a;
  border-color: #ffffff;
  box-shadow: 0 0 12px #6f9fd3;
}

.remember input[type='checkbox']:checked::after {
  content: '\f00c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 13px;
  color: #ffffff;
}

.forgot-link {
  color: #1e3a5f;
  text-decoration: none;
  border-bottom: 1px dashed #6f9fd3;
  transition: 0.3s ease;
}

.forgot-link:hover {
  color: #0a3366;
  border-bottom-color: #0a3366;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 1.25rem;
  border: none;
  border-radius: 65px;
  background: linear-gradient(135deg, #2a5c9a, #4a7db0, #6f9fd3);
  background-size: 220% 220%;
  color: #fff;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 6px;
  cursor: pointer;
  transition: all 0.4s ease;
  box-shadow: 0 12px 30px rgba(42, 92, 154, 0.3), 0 0 40px rgba(111, 159, 211, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.3);
  text-transform: uppercase;
  position: relative;
  overflow: hidden;
}

.login-btn:hover:not(:disabled) {
  background-position: 100% 50%;
  box-shadow: 0 18px 50px rgba(42, 92, 154, 0.4), 0 0 60px #8bb3e0;
  transform: translateY(-3px);
}

.login-btn:disabled {
  opacity: 0.8;
  cursor: not-allowed;
}

.login-btn::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -60%;
  width: 220%;
  height: 220%;
  background: repeating-linear-gradient(
    60deg,
    transparent,
    transparent 30px,
    rgba(255, 255, 255, 0.2) 30px,
    rgba(200, 225, 255, 0.2) 42px,
    transparent 52px
  );
  transform: rotate(25deg);
  animation: shine 8s infinite linear;
  opacity: 0.5;
  pointer-events: none;
}

@keyframes shine {
  0% {
    transform: translateX(-35%) rotate(25deg);
  }
  100% {
    transform: translateX(35%) rotate(25deg);
  }
}

/* 底部版权 */
.el-login-footer {
  height: 40px;
  line-height: 40px;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial, sans-serif;
  font-size: 12px;
  letter-spacing: 1px;
  text-shadow: 0 2px 6px rgba(0, 40, 80, 0.5);
  z-index: 6;
  position: relative;
}

/* ========= 响应式 ========= */
@media (max-width: 1000px) {
  .container {
    flex-direction: column;
    justify-content: center;
    padding: 25px;
    gap: 35px;
  }
  .left-hero {
    text-align: center;
    padding: 20px;
    height: auto;
    margin-top: -2vh;
    align-items: center;
  }
  .title-with-logo {
    justify-content: center;
  }
  .hero-title-english {
    text-align: center;
    border-left: none;
    border-bottom: 4px solid #4a7db0;
    padding-bottom: 18px;
    display: inline-block;
    font-size: 1.4rem;
  }
  .hero-decoration {
    justify-content: center;
    margin-top: 28px;
  }
  .right-login {
    width: 100%;
    max-width: 480px;
  }
  .hero-title {
    font-size: clamp(2rem, 7vw, 3.8rem);
  }
  .police-logo {
    width: 65px;
  }
}

@media (max-width: 600px) {
  .hero-title {
    font-size: clamp(1.9rem, 8vw, 3.2rem);
    letter-spacing: 3px;
  }
  .hero-title-english {
    font-size: 1.2rem;
    letter-spacing: 5px;
  }
  .login-card {
    padding: 2.5rem 2rem;
    border-radius: 40px;
  }
  .police-logo {
    width: 52px;
  }
  .left-hero {
    margin-top: 0;
  }
}
</style>

<!-- 引入 Font Awesome 图标库（确保图标正常显示） -->
<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');
</style>