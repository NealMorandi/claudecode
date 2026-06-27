<template>
  <div class="page">

    <!-- ━━ スケルトン（ローディング中） ━━ -->
    <template v-if="loading">
      <div class="glass-panel settings-panel">
        <!-- Section 1 skeleton -->
        <div class="section">
          <div class="section-header">
            <div class="sk sk-icon"></div>
            <div class="sk-header-texts">
              <div class="sk sk-title"></div>
              <div class="sk sk-desc"></div>
            </div>
          </div>
          <div class="field-row">
            <div class="field-info">
              <div class="sk sk-label"></div>
              <div class="sk sk-hint"></div>
            </div>
            <div class="sk sk-control"></div>
          </div>
        </div>
        <div class="section-divider"></div>
        <!-- Section 2 skeleton -->
        <div class="section">
          <div class="section-header">
            <div class="sk sk-icon"></div>
            <div class="sk-header-texts">
              <div class="sk sk-title"></div>
              <div class="sk sk-desc"></div>
            </div>
          </div>
          <div class="field-row">
            <div class="field-info">
              <div class="sk sk-label"></div>
              <div class="sk sk-hint"></div>
            </div>
            <div class="sk sk-control"></div>
          </div>
          <div class="field-row">
            <div class="field-info">
              <div class="sk sk-label sk-label--wide"></div>
              <div class="sk sk-hint sk-hint--wide"></div>
            </div>
            <div class="sk sk-control sk-control--wide"></div>
          </div>
        </div>
      </div>
      <!-- Save area skeleton -->
      <div class="save-area">
        <div class="sk sk-notice"></div>
        <div class="sk sk-btn"></div>
      </div>
    </template>

    <!-- ━━ メイン設定パネル ━━ -->
    <template v-else>
      <div class="glass-panel settings-panel">

        <!-- ── セクション①：長期利用管理 ── -->
        <div class="section">
          <div class="section-header">
            <div class="section-icon" style="--c:#f59e0b;--bg:rgba(245,158,11,0.15)">
              <el-icon><Warning /></el-icon>
            </div>
            <div>
              <p class="section-title">長期利用管理</p>
              <p class="section-desc">入居者の長期滞在に関する警告基準を設定します</p>
            </div>
          </div>

          <div class="field-row">
            <div class="field-info">
              <label class="field-label">長期利用警告発動年数</label>
              <p class="field-hint">この年数を超えた入居者に警告フラグが付きます（最小：1年）</p>
            </div>
            <div class="number-control">
              <button class="num-btn" @click="adjust('longTermYears', -1, 1)">−</button>
              <div class="num-display">
                <span class="num-value">{{ form.longTermYears }}</span>
                <span class="num-unit">年</span>
              </div>
              <button class="num-btn" @click="adjust('longTermYears', 1, 1)">＋</button>
            </div>
          </div>
        </div>

        <!-- セクション区切り線 -->
        <div class="section-divider"></div>

        <!-- ── セクション②：寮費計算 ── -->
        <div class="section">
          <div class="section-header">
            <div class="section-icon" style="--c:#4f8ef7;--bg:rgba(79,142,247,0.15)">
              <el-icon><Money /></el-icon>
            </div>
            <div>
              <p class="section-title">寮費計算</p>
              <p class="section-desc">各社員区分に適用する基本単価を設定します</p>
            </div>
          </div>

          <div class="field-row">
            <div class="field-info">
              <label class="field-label">基本単価（日本社員）</label>
              <p class="field-hint">日本籍社員に適用する部屋あたりの日額単価。デフォルト：2,000円</p>
            </div>
            <div class="number-control">
              <button class="num-btn num-btn--blue" @click="adjust('unitPriceJapan', -100, 0)">−</button>
              <div class="num-display">
                <span class="num-value">{{ form.unitPriceJapan.toLocaleString() }}</span>
                <span class="num-unit">円</span>
              </div>
              <button class="num-btn num-btn--blue" @click="adjust('unitPriceJapan', 100, 0)">＋</button>
            </div>
          </div>

          <div class="field-row">
            <div class="field-info">
              <label class="field-label">基本単価（中国出張社員）</label>
              <p class="field-hint">中国出張社員に適用する部屋あたりの日額単価。デフォルト：0円</p>
            </div>
            <div class="number-control">
              <button class="num-btn num-btn--blue" @click="adjust('unitPriceChina', -100, 0)">−</button>
              <div class="num-display">
                <span class="num-value">{{ form.unitPriceChina.toLocaleString() }}</span>
                <span class="num-unit">円</span>
              </div>
              <button class="num-btn num-btn--blue" @click="adjust('unitPriceChina', 100, 0)">＋</button>
            </div>
          </div>
        </div>
      </div>

      <!-- ━━ 保存エリア ━━ -->
      <div class="save-area">
        <div class="save-notice">
          <el-icon class="notice-icon"><InfoFilled /></el-icon>
          <span>設定を変更後、必ず保存してください</span>
        </div>
        <button class="btn-save" :disabled="saving" @click="handleSave">
          <span class="btn-shimmer"></span>
          <el-icon v-if="saving" class="is-loading"><Loading /></el-icon>
          <el-icon v-else><Check /></el-icon>
          <span>{{ saving ? '保存中...' : '設定を保存' }}</span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { getSettings, updateSettings } from '@/api/settings'

const loading = ref(false)
const saving = ref(false)
const form = reactive({ longTermYears: 3, unitPriceJapan: 2000, unitPriceChina: 0 })

const adjust = (field, delta, min = Number.NEGATIVE_INFINITY) => {
  const next = form[field] + delta
  if (next >= min) form[field] = next
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateSettings(form)
  } finally {
    saving.value = false
  }
  ElMessageBox.alert('設定を保存しました', '保存完了', { type: 'success', confirmButtonText: 'OK' })
}

onMounted(async () => {
  loading.value = true
  try { Object.assign(form, await getSettings()) }
  finally { loading.value = false }
})
</script>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 760px;
}

/* ━━ スケルトン ━━ */
.sk {
  border-radius: 6px;
  background: rgba(255,255,255,0.06);
  position: relative;
  overflow: hidden;
  flex-shrink: 0;
}
.sk::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(139,92,246,0.16) 38%,
    rgba(196,181,253,0.24) 52%,
    rgba(139,92,246,0.16) 65%,
    transparent 100%
  );
  animation: sk-wave 1.5s ease-in-out infinite;
  transform: translateX(-100%);
}
@keyframes sk-wave { to { transform: translateX(200%); } }

/* セクションヘッダー */
.sk-icon    { width: 40px; height: 40px; border-radius: 10px; }
.sk-header-texts { display: flex; flex-direction: column; gap: 8px; padding-top: 4px; }
.sk-title   { width: 120px; height: 14px; }
.sk-desc    { width: 260px; height: 11px; }

/* フィールド行 */
.sk-label   { width: 160px; height: 13px; margin-bottom: 8px; }
.sk-label--wide { width: 190px; }
.sk-hint    { width: 300px; height: 11px; }
.sk-hint--wide  { width: 340px; }

/* 数値コントロール */
.sk-control { width: 190px; height: 48px; border-radius: 10px; }
.sk-control--wide { width: 210px; }

/* 保存エリア */
.sk-notice  { width: 200px; height: 14px; }
.sk-btn     { width: 130px; height: 40px; border-radius: 8px; }

/* ━━ グラスカード ━━ */
.glass-panel {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  overflow: hidden;
}

/* ━━ セクション ━━ */
.section { padding: 24px; }

.section-divider {
  height: 1px;
  background: rgba(255,255,255,0.07);
  margin: 0 24px;
}

.section-header {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 24px;
}
.section-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: var(--bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: var(--c);
  flex-shrink: 0;
  box-shadow: 0 0 12px var(--bg);
}
.section-title {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}
.section-desc {
  color: rgba(255,255,255,0.38);
  font-size: 12px;
  line-height: 1.5;
}

/* ━━ フィールド行 ━━ */
.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 18px 0;
  border-top: 1px solid rgba(255,255,255,0.05);
}
.field-row:first-of-type { border-top: none; }

.field-info { flex: 1; }
.field-label {
  color: rgba(255,255,255,0.78);
  font-size: 14px;
  font-weight: 500;
  display: block;
  margin-bottom: 5px;
}
.field-hint {
  color: rgba(255,255,255,0.32);
  font-size: 12px;
  line-height: 1.5;
}

/* ━━ 数値コントロール ━━ */
.number-control {
  display: flex;
  align-items: center;
  gap: 0;
  flex-shrink: 0;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 10px;
  overflow: hidden;
}
.num-btn {
  width: 40px;
  height: 48px;
  background: transparent;
  border: none;
  color: rgba(255,255,255,0.5);
  font-size: 18px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.num-btn:hover { background: rgba(245,158,11,0.15); color: #fcd34d; }
.num-btn--blue:hover { background: rgba(79,142,247,0.15); color: #93c5fd; }
.num-btn:active { background: rgba(255,255,255,0.1); }

.num-display {
  min-width: 110px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-left: 1px solid rgba(255,255,255,0.08);
  border-right: 1px solid rgba(255,255,255,0.08);
}
.num-value {
  color: #ffffff;
  font-size: 20px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}
.num-value--negative { color: #f87171; }
.num-unit {
  color: rgba(255,255,255,0.38);
  font-size: 12px;
}

/* ━━ 保存エリア ━━ */
.save-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: rgba(79,142,247,0.06);
  border: 1px solid rgba(79,142,247,0.2);
  border-radius: 10px;
}
.save-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255,255,255,0.45);
  font-size: 13px;
}
.notice-icon {
  color: rgba(79,142,247,0.7);
  font-size: 16px;
  flex-shrink: 0;
}

.btn-save {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(79,142,247,0.38);
  transition: box-shadow 0.2s, opacity 0.2s;
  flex-shrink: 0;
}
.btn-save:hover { box-shadow: 0 6px 24px rgba(79,142,247,0.52); }
.btn-save:disabled { opacity: 0.65; cursor: not-allowed; }
.btn-shimmer {
  position: absolute;
  top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer { 0% { left: -75%; } 60% { left: 125%; } 100% { left: 125%; } }
</style>
