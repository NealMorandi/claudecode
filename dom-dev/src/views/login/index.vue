<template>
  <div class="login-page">
    <!-- 背景レイヤー -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="orb orb-3"></div>
    <div class="grid-overlay"></div>

    <!-- ログインカード -->
    <div class="login-card">
      <div class="card-top-line"></div>

      <!-- ブランドマーク -->
      <div class="brand">
        <div class="brand-icon">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
            <rect width="32" height="32" rx="8" fill="url(#brand-grad)"/>
            <path d="M16 6L28 12V20L16 26L4 20V12L16 6Z" fill="white" opacity="0.15"/>
            <path d="M16 9L25 13.5V20.5L16 25L7 20.5V13.5L16 9Z" stroke="white" stroke-width="1.5" fill="none" opacity="0.6"/>
            <circle cx="16" cy="17" r="3" fill="white" opacity="0.9"/>
            <defs>
              <linearGradient id="brand-grad" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                <stop offset="0%" stop-color="#4f8ef7"/>
                <stop offset="100%" stop-color="#7c3aed"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div class="brand-text">
          <h1 class="brand-title">社員寮管理システム</h1>
          <p class="brand-subtitle">Employee Dormitory Management</p>
        </div>
      </div>

      <!-- フォーム -->
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <div class="field-group">
          <label class="field-label">USERNAME</label>
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="ユーザー名を入力"
              prefix-icon="User"
              class="glass-input"
            />
          </el-form-item>
        </div>
        <div class="field-group">
          <label class="field-label">PASSWORD</label>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="パスワードを入力"
              prefix-icon="Lock"
              show-password
              class="glass-input"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
        </div>

        <button class="login-btn" :disabled="loading" @click.prevent="handleLogin">
          <span class="btn-shimmer"></span>
          <span class="btn-content">
            <el-icon v-if="loading" class="is-loading"><Loading /></el-icon>
            <span>{{ loading ? 'ログイン中...' : 'ログイン' }}</span>
          </span>
        </button>

        <div class="forgot-row">
          <span class="forgot-text">パスワードを忘れた方は</span>
          <a href="#" class="forgot-link">管理者へお問い合わせ</a>
        </div>
      </el-form>

      <!-- フッター：システム稼働状況 -->
      <div class="card-footer">
        <span class="status-dot"></span>
        <span class="status-text">全システム正常稼働中</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/modules/auth'

const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: 'ユーザー名を入力してください', trigger: 'blur' }],
  password: [{ required: true, message: 'パスワードを入力してください', trigger: 'blur' }],
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.login(form.value)
    ElMessage.success('ログインしました')
    router.push(route.query.redirect || '/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== ページ・背景 ===== */
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #060d1f 0%, #0d1b3e 50%, #111827 100%);
  position: relative;
  overflow: hidden;
}

/* グリッド テクスチャ */
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
}

/* 光のオーブ */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
}
.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(79,142,247,0.18) 0%, transparent 70%);
  top: -150px;
  left: -100px;
}
.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(124,58,237,0.15) 0%, transparent 70%);
  bottom: -100px;
  right: -80px;
}
.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(79,142,247,0.10) 0%, transparent 70%);
  top: 50%;
  left: 55%;
  transform: translate(-50%, -50%);
}

/* ===== カード ===== */
.login-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(255, 255, 255, 0.10);
  border-radius: 16px;
  box-shadow:
    0 24px 64px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255,255,255,0.04) inset;
}

/* カード上部のハイライトライン */
.card-top-line {
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 1px;
  background: linear-gradient(90deg, transparent, #4f8ef7 30%, #7c3aed 70%, transparent);
  border-radius: 1px;
}

/* ===== ブランドマーク ===== */
.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
}
.brand-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(79, 142, 247, 0.35);
}
.brand-title {
  color: #ffffff;
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 3px;
  letter-spacing: 0.02em;
  line-height: 1.2;
}
.brand-subtitle {
  color: rgba(255, 255, 255, 0.38);
  font-size: 11px;
  margin: 0;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

/* ===== フォーム ===== */
.login-form {
  width: 100%;
}
.field-group {
  margin-bottom: 4px;
}
.field-label {
  display: block;
  color: rgba(255, 255, 255, 0.40);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  margin-bottom: 6px;
}

/* 入力欄 — ガラス効果 */
.login-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.12) inset;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}
.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.24) inset;
}
.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 1px #4f8ef7 inset,
    0 0 12px rgba(79, 142, 247, 0.25);
}
.login-form :deep(.el-input__inner) {
  color: #ffffff;
  caret-color: #4f8ef7;
}
.login-form :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.28);
}
.login-form :deep(.el-input__prefix-icon),
.login-form :deep(.el-input__suffix-icon) {
  color: rgba(255, 255, 255, 0.38);
}
.login-form :deep(.el-form-item__error) {
  color: #f87171;
}
.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

/* ===== ログインボタン ===== */
.login-btn {
  position: relative;
  width: 100%;
  height: 44px;
  margin-top: 6px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #4f8ef7 0%, #7c3aed 100%);
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.06em;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(79, 142, 247, 0.40);
  transition: box-shadow 0.2s, opacity 0.2s;
}
.login-btn:hover {
  box-shadow: 0 6px 28px rgba(79, 142, 247, 0.55);
}
.login-btn:active {
  opacity: 0.88;
}
.login-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

/* 流光アニメーション */
.btn-shimmer {
  position: absolute;
  top: 0;
  left: -75%;
  width: 50%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.25) 50%,
    transparent 100%
  );
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer {
  0%   { left: -75%; }
  60%  { left: 125%; }
  100% { left: 125%; }
}

.btn-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* ===== 忘记密码 ===== */
.forgot-row {
  margin-top: 16px;
  text-align: center;
}
.forgot-text {
  color: rgba(255, 255, 255, 0.32);
  font-size: 12px;
}
.forgot-link {
  color: #4f8ef7;
  font-size: 12px;
  text-decoration: none;
  margin-left: 4px;
  transition: opacity 0.2s;
}
.forgot-link:hover {
  opacity: 0.75;
}

/* ===== フッター：稼働状況 ===== */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
}

/* 緑の呼吸ランプ */
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #22c55e;
  box-shadow: 0 0 6px #22c55e;
  animation: breathe 2s ease-in-out infinite;
  flex-shrink: 0;
}
@keyframes breathe {
  0%, 100% { opacity: 1;   box-shadow: 0 0 6px #22c55e; }
  50%       { opacity: 0.5; box-shadow: 0 0 2px #22c55e; }
}

.status-text {
  color: rgba(255, 255, 255, 0.35);
  font-size: 11px;
  letter-spacing: 0.04em;
}
</style>
