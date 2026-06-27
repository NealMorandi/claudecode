<template>
  <div class="page">

    <!-- ━━ カスタムステッパー ━━ -->
    <div class="glass-panel stepper-panel">
      <div class="stepper">
        <div
          v-for="(s, i) in steps"
          :key="i"
          class="step-wrap"
        >
          <div class="step-item" :class="{ active: step >= i, done: step > i }">
            <div class="step-circle">
              <el-icon v-if="step > i" class="step-check"><Check /></el-icon>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <span class="step-label">{{ s }}</span>
          </div>
          <div v-if="i < steps.length - 1" class="step-line" :class="{ done: step > i }"></div>
        </div>
      </div>
    </div>

    <!-- ━━ ステップ①：対象月選択 ━━ -->
    <div class="glass-panel step-card" :class="{ 'step-card--pending': step !== 0 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 0 }">1</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 0 }">対象月選択</span>
      </div>

      <div v-if="step === 0" class="step-body">
        <div class="field-group">
          <label class="field-label">計算対象月</label>
          <el-date-picker
            v-model="targetMonth"
            type="month"
            placeholder="年月を選択"
            value-format="YYYY-MM"
            class="dark-input month-picker"
          />
          <span class="hint-text">例：2025年05月 → 「2025-05」を選択してください</span>
        </div>
        <button class="btn-primary" :disabled="calculating" @click="handleCalculate">
          <span class="btn-shimmer"></span>
          <el-icon v-if="calculating" class="is-loading"><Loading /></el-icon>
          <el-icon v-else><DataAnalysis /></el-icon>
          <span>{{ calculating ? '計算中...' : '計算実行' }}</span>
        </button>
      </div>

      <p v-else class="pending-note">✓ {{ targetMonth }} を選択済み</p>
    </div>

    <!-- ━━ ステップ②：計算結果確認 ━━ -->
    <div class="glass-panel step-card" :class="{ 'step-card--pending': step !== 1 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 1 }">2</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 1 }">計算結果確認</span>
      </div>

      <div v-if="step === 1" class="step-body">
        <!-- サマリー -->
        <div class="calc-summary">
          <div class="summary-chip">
            <span class="chip-label">計算対象</span>
            <span class="chip-value">{{ targetMonth }}</span>
          </div>
          <div class="summary-chip">
            <span class="chip-label">対象人数</span>
            <span class="chip-value">{{ calcResult.length }} 名</span>
          </div>
          <div class="summary-chip summary-chip--accent">
            <span class="chip-label">合計金額</span>
            <span class="chip-value">{{ totalAmount.toLocaleString() }}円</span>
          </div>
        </div>

        <el-table :data="calcResult" class="dark-table">
          <el-table-column type="index" label="No." width="60" align="center" />
          <el-table-column label="社員名" prop="employeeName" min-width="110" />
          <el-table-column label="寮名 / 部屋" min-width="150">
            <template #default="{ row }">
              <span class="cell-muted">{{ row.dormitoryName }}</span>
              <span style="color:rgba(255,255,255,0.25);margin:0 4px">/</span>
              {{ row.roomName }}
            </template>
          </el-table-column>
          <el-table-column label="入居日数" prop="days" width="88" align="center" />
          <el-table-column label="単価" prop="unitPrice" width="100" align="center">
            <template #default="{ row }">{{ Number(row.unitPrice).toLocaleString() }}円</template>
          </el-table-column>
          <el-table-column label="計算式" min-width="160">
            <template #default="{ row }">
              <span class="formula">{{ Number(row.unitPrice).toLocaleString() }}円×{{ row.days }}</span>
            </template>
          </el-table-column>
          <el-table-column label="寮費(円)" prop="amount" width="110" align="right">
            <template #default="{ row }">
              <span class="amount-cell">{{ Number(row.amount).toLocaleString() }}円</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="step2-actions">
          <button class="btn-ghost" @click="step = 0">
            <el-icon><ArrowLeft /></el-icon>
            戻る
          </button>
          <button class="btn-primary" :disabled="confirming" @click="handleConfirm">
            <span class="btn-shimmer"></span>
            <el-icon v-if="confirming" class="is-loading"><Loading /></el-icon>
            <el-icon v-else><Check /></el-icon>
            <span>{{ confirming ? '処理中...' : '確定する' }}</span>
          </button>
        </div>
      </div>

      <p v-else class="pending-note">
        {{ step < 1 ? 'ステップ1の計算実行後に表示されます' : '✓ 確認済み' }}
      </p>
    </div>

    <!-- ━━ ステップ③：確定完了 ━━ -->
    <div class="glass-panel step-card" :class="{ 'step-card--pending': step !== 2 }">
      <div class="step-card-header">
        <div class="step-num-badge" :class="{ 'badge--dim': step !== 2 }">3</div>
        <span class="step-card-title" :class="{ 'title--dim': step !== 2 }">確定完了</span>
      </div>

      <div v-if="step === 2" class="step-body">
        <div class="success-state">
          <div class="success-icon">
            <el-icon><CircleCheckFilled /></el-icon>
          </div>
          <p class="success-title">{{ targetMonth }}分 寮費 {{ calcResult.length }}件を確定しました</p>
          <p class="success-sub">データが保存されました。CSVをダウンロードするか、履歴ページで確認できます。</p>
          <div class="success-actions">
            <button class="btn-primary" @click="handleExport">
              <span class="btn-shimmer"></span>
              <el-icon><Download /></el-icon>
              <span>CSVダウンロード</span>
            </button>
            <button class="btn-ghost" @click="$router.push('/rent/history')">
              <el-icon><List /></el-icon>
              <span>寮費履歴へ</span>
            </button>
          </div>
        </div>
      </div>

      <p v-else class="pending-note">ステップ2の確認後に実行できます</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { calculateRent, confirmRent, exportRentCsv } from '@/api/rent'
import { downloadBlob } from '@/utils/export'

const router = useRouter()

const step = ref(0)
const targetMonth = ref('')
const calcResult = ref([])
const calculating = ref(false)
const confirming = ref(false)

const steps = ['対象月選択', '計算結果確認', '確定完了']
const totalAmount = computed(() => calcResult.value.reduce((s, r) => s + Number(r.amount), 0))

const handleCalculate = async () => {
  if (!targetMonth.value) return ElMessage.warning('対象月を選択してください')
  calculating.value = true
  try {
    const data = await calculateRent({ yearMonth: targetMonth.value })
    calcResult.value = data.items || []
    step.value = 1
  } finally { calculating.value = false }
}

const handleConfirm = async () => {
  await ElMessageBox.confirm(
    'この内容で確定しますか？<br>確定後の変更は「寮費履歴」画面で取消できます。',
    '確定確認',
    { type: 'warning', dangerouslyUseHTMLString: true }
  )
  confirming.value = true
  try {
    await confirmRent({ yearMonth: targetMonth.value })
    ElMessage.success('確定しました')
    router.push('/rent/history')
  } finally { confirming.value = false }
}

const handleExport = async () => {
  const blob = await exportRentCsv({ yearMonth: targetMonth.value })
  downloadBlob(blob, `寮費_${targetMonth.value}.xlsx`)
}
</script>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 100%;
}

/* ━━ 共通グラスカード ━━ */
.glass-panel {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  padding: 20px;
}

/* ━━ カスタムステッパー ━━ */
.stepper-panel { padding: 16px 24px; }
.stepper {
  display: flex;
  align-items: center;
}
.step-wrap {
  display: flex;
  align-items: center;
  flex: 1;
}
.step-wrap:last-child { flex: 0; }
.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.step-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255,255,255,0.08);
  border: 1.5px solid rgba(255,255,255,0.18);
  color: rgba(255,255,255,0.35);
  transition: all 0.3s;
}
.step-item.active .step-circle {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 0 16px rgba(79,142,247,0.5);
}
.step-item.done .step-circle {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 0 10px rgba(79,142,247,0.3);
}
.step-check { font-size: 16px; }
.step-label {
  font-size: 12px;
  color: rgba(255,255,255,0.28);
  white-space: nowrap;
  transition: color 0.3s;
}
.step-item.active .step-label { color: #ffffff; font-weight: 600; }
.step-item.done .step-label  { color: rgba(255,255,255,0.6); }

/* コネクターライン */
.step-line {
  flex: 1;
  height: 1.5px;
  background: rgba(255,255,255,0.12);
  margin: 0 8px;
  margin-bottom: 22px;
  transition: background 0.3s;
}
.step-line.done {
  background: linear-gradient(90deg, #4f8ef7, #7c3aed);
}

/* ━━ ステップカード ━━ */
.step-card { transition: border-color 0.3s, opacity 0.3s; }
.step-card--pending {
  border-style: dashed;
  border-color: rgba(255,255,255,0.06);
  opacity: 0.75;
}

.step-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}
.step-num-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 0 12px rgba(79,142,247,0.4);
  flex-shrink: 0;
  transition: all 0.3s;
}
.badge--dim {
  background: rgba(255,255,255,0.1);
  box-shadow: none;
  color: rgba(255,255,255,0.3);
}
.step-card-title {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
}
.title--dim { color: rgba(255,255,255,0.3); }

.pending-note {
  color: rgba(255,255,255,0.25);
  font-size: 13px;
  padding: 4px 0;
}
.step-body { display: flex; flex-direction: column; gap: 20px; }

/* ━━ フィールド ━━ */
.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-width: 320px;
}
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.hint-text {
  color: rgba(255,255,255,0.28);
  font-size: 12px;
  margin-top: 2px;
}
.month-picker { width: 100% !important; }

:deep(.dark-input .el-input__wrapper) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}
:deep(.dark-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,0.24) inset;
}
:deep(.dark-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4f8ef7 inset, 0 0 12px rgba(79,142,247,0.22);
}
:deep(.dark-input .el-input__inner) { color: #ffffff; caret-color: #4f8ef7; }
:deep(.dark-input .el-input__inner::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-input .el-input__prefix-icon),
:deep(.dark-input .el-input__suffix-icon) { color: rgba(255,255,255,0.38); }

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
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
  align-self: flex-start;
}
.btn-primary:hover { box-shadow: 0 6px 24px rgba(79,142,247,0.52); }
.btn-primary:disabled { opacity: 0.65; cursor: not-allowed; }
.btn-shimmer {
  position: absolute;
  top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer { 0% { left: -75%; } 60% { left: 125%; } 100% { left: 125%; } }

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  color: rgba(255,255,255,0.65);
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ ステップ2 ━━ */
.calc-summary {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.summary-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 8px;
}
.summary-chip--accent {
  background: rgba(79,142,247,0.12);
  border-color: rgba(79,142,247,0.25);
}
.chip-label { color: rgba(255,255,255,0.4); font-size: 12px; }
.chip-value { color: #ffffff; font-size: 14px; font-weight: 600; }

.dark-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-border-color: rgba(255,255,255,0.07);
  --el-table-row-hover-bg-color: rgba(255,255,255,0.05);
  --el-fill-color-lighter: transparent;
  background: transparent !important;
}
:deep(.el-table__inner-wrapper::before) { display: none; }
:deep(.el-table .el-table__header th.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.32);
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.10em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
:deep(.el-table .el-table__body td.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.78);
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-size: 13px;
}
:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255,255,255,0.05) !important;
}
.cell-muted { color: rgba(255,255,255,0.45); }
.formula { color: rgba(255,255,255,0.5); font-size: 12px; font-family: monospace; }
.amount-cell { color: #93c5fd; font-weight: 600; }

.step2-actions {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

/* ━━ ステップ3 成功 ━━ */
.success-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0 8px;
  gap: 12px;
  text-align: center;
}
.success-icon {
  font-size: 52px;
  color: #22c55e;
  filter: drop-shadow(0 0 16px rgba(34,197,94,0.5));
}
.success-title {
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
}
.success-sub {
  color: rgba(255,255,255,0.38);
  font-size: 13px;
  max-width: 420px;
  line-height: 1.6;
}
.success-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
</style>
