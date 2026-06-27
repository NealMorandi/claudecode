<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group" style="min-width:180px">
          <label class="field-label">社員名</label>
          <AppSelect
            v-model="query.employeeId"
            :options="employeeOptions"
            placeholder="全社員"
          />
        </div>
        <div class="field-group">
          <label class="field-label">対象月</label>
          <el-date-picker
            v-model="query.yearMonth"
            type="month"
            value-format="YYYY-MM"
            placeholder="年月を選択"
            class="dark-input"
          />
        </div>
        <div class="field-group">
          <label class="field-label">確定状況</label>
          <AppSelect
            v-model="query.status"
            :options="[
              { label: 'すべて', value: '' },
              { label: '確定済', value: 'confirmed' },
              { label: '下書き', value: 'draft' },
            ]"
            placeholder="すべて"
          />
        </div>
        <div class="search-actions">
          <button class="btn-primary" @click="fetchList">
            <span class="btn-shimmer"></span>
            <el-icon><Search /></el-icon>
            <span>検索</span>
          </button>
          <button class="btn-green" @click="handleExport">
            <el-icon><Download /></el-icon>
            <span>CSV出力</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#4f8ef7">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Document /></el-icon>
        </div>
        <div>
          <p class="sum-label">総件数</p>
          <p class="sum-value">{{ total }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#22c55e">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><CircleCheckFilled /></el-icon>
        </div>
        <div>
          <p class="sum-label">確定済</p>
          <p class="sum-value">{{ confirmedCount }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#a78bfa">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Money /></el-icon>
        </div>
        <div>
          <p class="sum-label">合計寮費</p>
          <p class="sum-value sum-value--sm">{{ totalAmount.toLocaleString() }}円</p>
        </div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div class="panel-title-group">
          <span class="panel-title">寮費履歴一覧</span>
          <span class="panel-badge">RENT HISTORY</span>
        </div>
        <div class="panel-header-right">
          <button
            v-if="query.yearMonth"
            class="btn-danger"
            :disabled="!list.length || !!cancelingMonth"
            @click="handleCancel(query.yearMonth)"
          >
            <span class="btn-shimmer"></span>
            {{ cancelingMonth ? '処理中...' : `${query.yearMonth}分 確定取消` }}
          </button>
        </div>
      </div>

      <!-- ページネーション（テーブル直上・右揃え） -->
      <div class="pagination-row">
        <span class="pg-label">ページ行数：</span>
        <AppSelect v-model="query.pageSize" :options="pageSizeOptions.map(s => ({ label: String(s), value: s }))" style="width:72px" @change="onPageSizeChange" />
        <span class="pg-total">総{{ total }}件</span>
        <button class="pg-btn" aria-label="先頭ページ" :disabled="query.page === 1" @click="goToPage(1)">|&lt;</button>
        <button class="pg-btn" aria-label="前ページ" :disabled="query.page === 1" @click="goToPage(query.page - 1)">&lt;</button>
        <button class="pg-btn" aria-label="次ページ" :disabled="query.page >= totalPages" @click="goToPage(query.page + 1)">&gt;</button>
        <button class="pg-btn" aria-label="末尾ページ" :disabled="query.page >= totalPages" @click="goToPage(totalPages)">&gt;|</button>
      </div>

      <el-table :data="loading ? [] : list" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="社員名" prop="employeeName" min-width="120" />
        <el-table-column label="対象月" prop="yearMonth" width="100" align="center" />
        <el-table-column label="寮名 / 部屋" min-width="160">
          <template #default="{ row }">
            <span class="cell-muted">{{ row.dormitoryName }}</span>
            <span style="color:rgba(255,255,255,0.25);margin:0 4px">/</span>
            {{ row.roomName }}
          </template>
        </el-table-column>
        <el-table-column label="入居日数" prop="days" width="88" align="center" />
        <el-table-column label="寮費(円)" prop="amount" width="120" align="right">
          <template #default="{ row }">
            <span class="amount-cell">{{ row.amount?.toLocaleString() }}円</span>
          </template>
        </el-table-column>
        <el-table-column label="確定状況" width="100" align="center">
          <template #default="{ row }">
            <span class="badge" :class="statusClass(row)">{{ statusLabel(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="確定日時" prop="confirmedAt" width="155" align="center">
          <template #default="{ row }">
            <span :class="row.confirmedAt ? '' : 'cell-empty'">
              {{ row.confirmedAt ? row.confirmedAt.replace('T', ' ') : '—' }}
            </span>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="loading" />
          <el-empty v-else description="データなし" :image-size="60" />
        </template>
      </el-table>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRentHistory, exportRentCsv, cancelRent } from '@/api/rent'
import { searchEmployees } from '@/api/employee'
import TableLoadState from '@/components/TableLoadState.vue'
import { downloadBlob } from '@/utils/export'

const router = useRouter()

const now = new Date()
const currentYearMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`

const loading = ref(false)
const list = ref([])
const total = ref(0)
const confirmedCount = ref(0)
const totalAmount = ref(0)
const employees = ref([])
const employeeOptions = computed(() => [
  { label: '全社員', value: null },
  ...employees.value.map(e => ({ label: e.name, value: e.id })),
])

const query = reactive({ employeeId: null, yearMonth: currentYearMonth, status: '', page: 1, pageSize: 10 })
const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const goToPage = (page) => { query.page = page; fetchList() }
const onPageSizeChange = () => { query.page = 1; fetchList() }

const statusLabel = (row) => {
  if (row.status === 'confirmed') return '確定済'
  if (row.status === 'draft')     return '下書き'
  return '未確定'
}
const statusClass = (row) => {
  if (row.status === 'confirmed') return 'badge-green'
  if (row.status === 'draft')     return 'badge-gray'
  return 'badge-amber'
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getRentHistory(query)
    list.value = data.list
    total.value = data.total
    confirmedCount.value = data.confirmedCount ?? 0
    totalAmount.value = data.totalAmount ?? 0
  } finally { loading.value = false }
}

const handleExport = async () => {
  const blob = await exportRentCsv(query)
  downloadBlob(blob, `寮費履歴_${query.yearMonth || 'all'}.xlsx`)
}

const cancelingMonth = ref('')
const handleCancel = async (yearMonth) => {
  await ElMessageBox.confirm(
    `${yearMonth}分の寮費確定を取消しますか？<br>対象月の全データが削除され、再計算が必要になります。`,
    '確定取消の確認',
    { type: 'warning', dangerouslyUseHTMLString: true, confirmButtonText: '取消する', cancelButtonText: 'キャンセル' }
  )
  cancelingMonth.value = yearMonth
  try {
    await cancelRent(yearMonth)
    ElMessage.success(`${yearMonth}分の確定を取消しました`)
    router.push('/rent/calculate')
  } finally { cancelingMonth.value = '' }
}

onMounted(async () => {
  const list = await searchEmployees()
  employees.value = Array.isArray(list) ? list : (list?.list ?? [])
  fetchList()
})
</script>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow: hidden;
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

/* ━━ 検索フォーム ━━ */
.search-form {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 16px;
}
.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 160px;
}
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.search-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 入力欄 */
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
  box-shadow: 0 0 0 1px #4f8ef7 inset, 0 0 10px rgba(79,142,247,0.2);
}
:deep(.dark-input .el-input__inner) { color: #ffffff; caret-color: #4f8ef7; }
:deep(.dark-input .el-input__inner::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-input .el-input__prefix-icon),
:deep(.dark-input .el-input__suffix-icon),
:deep(.dark-input .el-select__caret) { color: rgba(255,255,255,0.38); }

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 4px 14px rgba(79,142,247,0.35);
  transition: box-shadow 0.2s;
}
.btn-primary:hover { box-shadow: 0 6px 20px rgba(79,142,247,0.5); }
.btn-shimmer {
  position: absolute;
  top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer { 0% { left: -75%; } 60% { left: 125%; } 100% { left: 125%; } }

/* 緑色CSVボタン */
.btn-green {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  background: transparent;
  border: 1px solid rgba(34,197,94,0.45);
  border-radius: 8px;
  color: #86efac;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.btn-green:hover { background: rgba(34,197,94,0.12); border-color: #22c55e; }

/* ━━ サマリーカード ━━ */
.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.sum-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
}
.sum-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  opacity: 0.65;
}
.sum-icon-wrap { position: relative; flex-shrink: 0; }
.sum-halo {
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--accent) 0%, transparent 70%);
  opacity: 0.25;
  filter: blur(6px);
}
.sum-icon { font-size: 28px; color: var(--accent); position: relative; }
.sum-label { color: rgba(255,255,255,0.45); font-size: 12px; margin-bottom: 6px; }
.sum-value { color: #ffffff; font-size: 30px; font-weight: 700; line-height: 1; }
.sum-value--sm { font-size: 22px; }

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.panel-title-group { display: flex; align-items: center; gap: 8px; }
.panel-title { color: #ffffff; font-size: 14px; font-weight: 600; }
.panel-badge {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}

/* ━━ ダークテーブル ━━ */
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
.cell-empty { color: rgba(255,255,255,0.25); }
.amount-cell { color: #a78bfa; font-weight: 600; }

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-green { background: rgba(34,197,94,0.18);  color: #86efac; }
.badge-amber { background: rgba(245,158,11,0.18); color: #fcd34d; }
.badge-gray  { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.42); }

.btn-danger {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 4px 14px rgba(79,142,247,0.35);
  transition: box-shadow 0.2s, filter 0.2s, opacity 0.2s;
}
.btn-danger:hover:not(:disabled) { box-shadow: 0 6px 20px rgba(79,142,247,0.5); }
.btn-danger:disabled { filter: grayscale(1); opacity: 0.45; cursor: not-allowed; }

/* ━━ ページネーション ━━ */
.pagination-row { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%; }
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; height: 28px; padding: 0 6px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
.pg-btn:hover:not(:disabled) { background: rgba(79,142,247,0.18); color: #4f8ef7; border-color: rgba(79,142,247,0.35); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
