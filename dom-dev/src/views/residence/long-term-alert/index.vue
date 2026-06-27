<template>
  <div class="page">
    <!-- 琥珀色オーブ（警告テーマ） -->
    <div class="amb-orb amb-orb-1"></div>
    <div class="amb-orb amb-orb-2"></div>

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">利用年数</label>
          <AppSelect
            v-model="query.minYears"
            :options="[
              { label: 'すべて',  value: null },
              { label: '1年以上', value: 1 },
              { label: '2年以上', value: 2 },
              { label: '3年以上', value: 3 },
              { label: '5年以上', value: 5 },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="field-group">
          <label class="field-label">対応状況</label>
          <AppSelect
            v-model="query.status"
            :options="[
              { label: 'すべて',       value: '' },
              { label: '未対応',       value: 'pending' },
              { label: '警告通知済',   value: 'notified' },
              { label: '値上げ適用済', value: 'applied' },
              { label: '対応完了',     value: 'done' },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="search-actions">
          <button class="btn-primary" @click="fetchList">
            <span class="btn-shimmer"></span>
            <el-icon><Search /></el-icon>
            <span>検索</span>
          </button>
          <button class="btn-amber" @click="handleExport">
            <el-icon><Download /></el-icon>
            <span>CSV出力</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#f59e0b">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Warning /></el-icon>
        </div>
        <div>
          <p class="sum-label">警告対象者（合計）</p>
          <p class="sum-value">{{ total }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#f87171">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><CircleClose /></el-icon>
        </div>
        <div>
          <p class="sum-label">未対応</p>
          <p class="sum-value">{{ pendingCount }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#4f8ef7">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><CircleCheck /></el-icon>
        </div>
        <div>
          <p class="sum-label">対応済</p>
          <p class="sum-value">{{ doneCount }}</p>
        </div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div class="header-left">
          <span class="panel-title">長期利用警告一覧</span>
          <span class="warn-badge">
            <el-icon style="font-size:11px"><Warning /></el-icon>
            {{ total }}件
          </span>
        </div>
        <span class="panel-sub">LONG-TERM ALERT</span>
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
        <el-table-column label="初回利用日" prop="firstUseDate" width="120" align="center" />
        <el-table-column label="通算利用年数" width="130" align="center">
          <template #default="{ row }">
            <span class="years-badge">{{ row.yearsOfStay }} 年</span>
          </template>
        </el-table-column>
        <el-table-column label="現在の入居先" min-width="160">
          <template #default="{ row }">
            <span v-if="row.dormitoryName" class="location-cell">
              <span class="cell-muted">{{ row.dormitoryName }}</span>
              <span style="color:rgba(255,255,255,0.25);margin:0 4px">/</span>
              {{ row.roomName }}
            </span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="対応ステータス" width="155" align="center">
          <template #default="{ row }">
            <AppSelect
              v-model="row.alertStatus"
              size="small"
              :options="[
                { label: '未対応',      value: 'pending' },
                { label: '警告通知済',  value: 'notified' },
                { label: '値上げ適用済', value: 'applied' },
                { label: '対応完了',    value: 'done' },
              ]"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="$router.push(`/residences?employeeId=${row.employeeId}`)">
              履歴確認
            </button>
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
import { ElMessage } from 'element-plus'
import { getLongTermAlerts, updateAlertStatus, exportAlertCsv } from '@/api/longTermAlert'
import TableLoadState from '@/components/TableLoadState.vue'
import { downloadBlob } from '@/utils/export'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pendingCount = ref(0)
const doneCount = ref(0)
const query = reactive({ minYears: 3, status: '', page: 1, pageSize: 10 })
const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const goToPage = (page) => { query.page = page; fetchList() }
const onPageSizeChange = () => { query.page = 1; fetchList() }

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getLongTermAlerts(query)
    list.value = data.list
    total.value = data.total
    pendingCount.value = data.pendingCount ?? 0
    doneCount.value    = data.doneCount    ?? 0
  } finally { loading.value = false }
}

const handleStatusChange = async (row) => {
  await updateAlertStatus(row.employeeId, { alertStatus: row.alertStatus })
  ElMessage.success('ステータスを更新しました')
}

const handleExport = async () => {
  const blob = await exportAlertCsv(query)
  downloadBlob(blob, '長期利用警告一覧.csv')
}

onMounted(fetchList)
</script>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow: hidden;
  position: relative;
}

/* ━━ 琥珀オーブ（ページ専用） ━━ */
.amb-orb {
  position: fixed;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
  filter: blur(90px);
}
.amb-orb-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(245,158,11,0.14) 0%, transparent 70%);
  top: -180px; right: 0;
}
.amb-orb-2 {
  width: 380px; height: 380px;
  background: radial-gradient(circle, rgba(248,113,113,0.12) 0%, transparent 70%);
  bottom: -100px; left: 200px;
}

/* ━━ 共通グラスカード ━━ */
.glass-panel {
  position: relative;
  z-index: 1;
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
  box-shadow: 0 0 0 1px rgba(255,255,255,0.22) inset;
}
:deep(.dark-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #f59e0b inset, 0 0 10px rgba(245,158,11,0.2);
}
:deep(.dark-input .el-input__inner) { color: #ffffff; caret-color: #f59e0b; }
:deep(.dark-input .el-input__inner::placeholder) { color: rgba(255,255,255,0.28); }
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

/* 琥珀色CSV出力ボタン */
.btn-amber {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  background: transparent;
  border: 1px solid rgba(245,158,11,0.5);
  border-radius: 8px;
  color: #fcd34d;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.btn-amber:hover {
  background: rgba(245,158,11,0.12);
  border-color: #f59e0b;
}

/* ━━ サマリーカード ━━ */
.summary-grid {
  position: relative;
  z-index: 1;
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
  opacity: 0.6;
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

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.header-left { display: flex; align-items: center; gap: 12px; }
.panel-title { color: #ffffff; font-size: 14px; font-weight: 600; }
.panel-sub {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}

/* 琥珀色バッジ */
.warn-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 3px 10px;
  background: rgba(245,158,11,0.18);
  border: 1px solid rgba(245,158,11,0.35);
  border-radius: 20px;
  color: #fcd34d;
  font-size: 12px;
  font-weight: 500;
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

/* 利用年数バッジ */
.years-badge {
  display: inline-block;
  padding: 2px 10px;
  background: rgba(245,158,11,0.2);
  border: 1px solid rgba(245,158,11,0.35);
  border-radius: 20px;
  color: #fcd34d;
  font-size: 12px;
  font-weight: 500;
}

.location-cell { display: inline-flex; align-items: center; }
.cell-muted { color: rgba(255,255,255,0.45); }
.cell-empty { color: rgba(255,255,255,0.25); }

/* ステータスセレクト */
.status-select {
  width: 138px;
}
:deep(.status-select .el-input__wrapper) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.10) inset;
  border-radius: 6px;
}
:deep(.status-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(245,158,11,0.4) inset;
}
:deep(.status-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #f59e0b inset;
}
:deep(.status-select .el-input__inner) { color: rgba(255,255,255,0.8); font-size: 12px; }
:deep(.status-select .el-select__caret) { color: rgba(255,255,255,0.38); }

/* リンクボタン */
.link-btn {
  background: none;
  border: none;
  color: #f59e0b;
  font-size: 12px;
  cursor: pointer;
  padding: 0 4px;
  transition: opacity 0.15s;
}
.link-btn:hover { opacity: 0.7; }

/* ━━ ページネーション ━━ */
.pagination-row { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%; }
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; height: 28px; padding: 0 6px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
.pg-btn:hover:not(:disabled) { background: rgba(79,142,247,0.18); color: #4f8ef7; border-color: rgba(79,142,247,0.35); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
