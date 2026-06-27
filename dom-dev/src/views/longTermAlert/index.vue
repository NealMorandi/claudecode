<template>
  <div class="page">
    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">最低在居年数</label>
          <el-select v-model="query.minYears" clearable placeholder="全て" class="dark-input" style="width:140px">
            <el-option label="全て" :value="null" />
            <el-option label="1年以上" :value="1" />
            <el-option label="2年以上" :value="2" />
            <el-option label="3年以上" :value="3" />
            <el-option label="5年以上" :value="5" />
          </el-select>
        </div>
        <div class="field-group">
          <label class="field-label">対応状況</label>
          <el-select v-model="query.status" clearable placeholder="全て" class="dark-input" style="width:140px">
            <el-option label="全て" value="" />
            <el-option label="未対応" value="pending" />
            <el-option label="連絡済み" value="notified" />
            <el-option label="延長申請" value="applied" />
            <el-option label="退去予定" value="done" />
          </el-select>
        </div>
        <div class="search-actions">
          <button class="btn-primary" @click="fetchList">
            <span class="btn-shimmer"></span>
            <el-icon><Search /></el-icon>
            <span>検索</span>
          </button>
          <button class="btn-ghost" @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            <span>リセット</span>
          </button>
          <button class="btn-ghost" @click="exportExcel">
            <el-icon><Download /></el-icon>
            <span>エクスポート</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#f87171">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Warning /></el-icon></div>
        <div><p class="sum-label">総警告件数</p><p class="sum-value">{{ total }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#f59e0b">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Bell /></el-icon></div>
        <div><p class="sum-label">未対応</p><p class="sum-value">{{ pendingCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#22c55e">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><CircleCheck /></el-icon></div>
        <div><p class="sum-label">対応済み</p><p class="sum-value">{{ doneCount }}</p></div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">長期利用警告一覧</span>
          <span class="panel-badge">LONG-TERM ALERT</span>
        </div>
        <div class="header-right">
          <span class="pg-total">総{{ total }}件</span>
          <el-pagination
            v-model:current-page="query.page"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="sizes, prev, pager, next"
            small
            class="dark-pager"
            @current-change="fetchList"
            @size-change="onSizeChange"
          />
        </div>
      </div>

      <el-table :data="list" row-key="employeeId" height="100%" class="dark-table" v-loading="loading">
        <el-table-column type="index" label="No." width="55" align="center" />
        <el-table-column label="社員名" min-width="120">
          <template #default="{ row }">
            <div class="employee-cell">
              <div class="emp-avatar">{{ row.employeeName?.[0] }}</div>
              <div>
                <div class="emp-name">{{ row.employeeName }}</div>
                <div class="emp-no">{{ row.employeeNo }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="部署" prop="department" width="120" />
        <el-table-column label="寮 / 部屋" min-width="150">
          <template #default="{ row }">
            <div class="location-cell">
              <span class="loc-dorm">{{ row.dormitoryName }}</span>
              <span class="loc-sep">›</span>
              <span class="loc-room">{{ row.roomName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="初回入居日" width="110" align="center">
          <template #default="{ row }">{{ row.firstUseDate }}</template>
        </el-table-column>
        <el-table-column label="在居年数" width="90" align="center">
          <template #default="{ row }">
            <span class="years-badge" :class="{ 'years-danger': row.yearsOfStay >= 5 }">
              {{ row.yearsOfStay }}年
            </span>
          </template>
        </el-table-column>
        <el-table-column label="超過日数" width="90" align="center">
          <template #default="{ row }">
            <span class="overdue-days">{{ row.overdueDays }}日</span>
          </template>
        </el-table-column>
        <el-table-column label="対応状況" width="110" align="center">
          <template #default="{ row }">
            <span class="status-badge" :class="statusClass(row.alertStatus)">
              {{ statusLabel(row.alertStatus) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center" fixed="right">
          <template #default="{ row }">
            <button class="act-btn" @click="openDialog(row)">対応する</button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ━━ 対応ダイアログ ━━ -->
    <el-dialog
      v-model="dialog.visible"
      title="長期利用対応記録"
      width="420px"
      :close-on-click-modal="false"
    >
      <div v-if="dialog.item" class="modal-body">
        <div class="modal-employee">
          <div class="modal-avatar">{{ dialog.item.employeeName?.[0] }}</div>
          <div>
            <div class="modal-name">{{ dialog.item.employeeName }}</div>
            <div class="modal-location">{{ dialog.item.dormitoryName }} {{ dialog.item.roomName }}</div>
          </div>
        </div>
        <el-form label-width="90px" style="margin-top:20px">
          <el-form-item label="対応状況">
            <el-select v-model="dialog.status" style="width:100%">
              <el-option label="未対応" value="pending" />
              <el-option label="連絡済み" value="notified" />
              <el-option label="延長申請" value="applied" />
              <el-option label="退去予定" value="done" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="dialog.visible = false">キャンセル</el-button>
        <el-button type="primary" :loading="dialog.loading" @click="submitStatus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getLongTermAlerts, updateAlertStatus, exportAlertCsv } from '@/api/longTermAlert'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pendingCount = ref(0)
const doneCount = ref(0)

const query = ref({ minYears: null, status: '', page: 1, pageSize: 20 })

const STATUS_LABELS = { pending: '未対応', notified: '連絡済み', applied: '延長申請', done: '退去予定' }
const statusLabel = (s) => STATUS_LABELS[s] || s || '未対応'
const statusClass = (s) => ({
  'status-pending':  s === 'pending' || !s,
  'status-notified': s === 'notified',
  'status-applied':  s === 'applied',
  'status-done':     s === 'done',
})

async function fetchList() {
  loading.value = true
  try {
    const params = { page: query.value.page, pageSize: query.value.pageSize, status: query.value.status }
    if (query.value.minYears != null) params.minYears = query.value.minYears
    const res = await getLongTermAlerts(params)
    list.value = res.list || []
    total.value = res.total || 0
    pendingCount.value = res.pendingCount || 0
    doneCount.value = res.doneCount || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.value = { minYears: null, status: '', page: 1, pageSize: 20 }
  fetchList()
}

function onSizeChange() {
  query.value.page = 1
  fetchList()
}

async function exportExcel() {
  try {
    const params = {}
    if (query.value.minYears != null) params.minYears = query.value.minYears
    if (query.value.status) params.status = query.value.status
    const blob = await exportAlertCsv(params)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'long_term_alerts.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('エクスポートに失敗しました')
  }
}

const dialog = ref({ visible: false, item: null, status: 'pending', loading: false })

function openDialog(item) {
  dialog.value = { visible: true, item, status: item.alertStatus || 'pending', loading: false }
}

async function submitStatus() {
  dialog.value.loading = true
  try {
    await updateAlertStatus(dialog.value.item.employeeId, { alertStatus: dialog.value.status })
    ElMessage.success('対応状況を更新しました')
    dialog.value.visible = false
    fetchList()
  } catch {
    // エラーはインターセプターで表示済み
  } finally {
    dialog.value.loading = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.page {
  padding: 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
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
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.panel-title {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.02em;
  margin-right: 10px;
}
.panel-badge {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.pg-total {
  color: rgba(255,255,255,0.4);
  font-size: 12px;
}

/* ━━ 検索パネル ━━ */
.search-panel { flex-shrink: 0; }
.search-form { display: flex; align-items: flex-end; gap: 16px; flex-wrap: wrap; }
.field-group { display: flex; flex-direction: column; gap: 6px; }
.field-label { color: rgba(255,255,255,0.45); font-size: 11px; font-weight: 500; letter-spacing: 0.05em; }
.search-actions { display: flex; gap: 8px; align-items: center; }

/* ━━ サマリーグリッド ━━ */
.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  flex-shrink: 0;
}
.sum-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 14px;
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
  inset: -6px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--accent) 0%, transparent 70%);
  opacity: 0.25;
  filter: blur(5px);
}
.sum-icon { font-size: 24px; color: var(--accent); position: relative; }
.sum-label { color: rgba(255,255,255,0.45); font-size: 11px; margin-bottom: 4px; }
.sum-value { color: #ffffff; font-size: 26px; font-weight: 700; line-height: 1; }

/* ━━ テーブルパネル ━━ */
.table-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.dark-table {
  flex: 1;
  min-height: 0;
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
  color: rgba(255,255,255,0.35);
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
:deep(.el-table .el-table__body td.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.78);
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-size: 13px;
}
:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255,255,255,0.08) !important;
  color: rgba(255,255,255,0.95) !important;
}
:deep(.el-table__fixed-right) { background: transparent !important; }
:deep(.el-table__fixed-right .el-table__cell) { background: rgba(18,18,30,0.92) !important; }

/* ━━ セル内コンテンツ ━━ */
.employee-cell { display: flex; align-items: center; gap: 10px; }
.emp-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; color: white; font-weight: 600; flex-shrink: 0;
}
.emp-name { color: rgba(255,255,255,0.85); font-size: 13px; font-weight: 500; }
.emp-no { color: rgba(255,255,255,0.35); font-size: 11px; }
.location-cell { display: flex; align-items: center; gap: 4px; font-size: 12px; }
.loc-dorm { color: rgba(255,255,255,0.65); }
.loc-sep { color: rgba(255,255,255,0.25); }
.loc-room { color: rgba(255,255,255,0.5); }

.years-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(245,158,11,0.15);
  border: 1px solid rgba(245,158,11,0.35);
  color: #fbbf24;
}
.years-danger {
  background: rgba(248,113,113,0.15);
  border-color: rgba(248,113,113,0.4);
  color: #fca5a5;
}
.overdue-days { color: #f87171; font-size: 12px; font-weight: 600; }

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}
.status-pending  { background: rgba(248,113,113,0.15); border: 1px solid rgba(248,113,113,0.3); color: #fca5a5; }
.status-notified { background: rgba(79,142,247,0.15);  border: 1px solid rgba(79,142,247,0.3);  color: #7bb3fb; }
.status-applied  { background: rgba(245,158,11,0.15);  border: 1px solid rgba(245,158,11,0.3);  color: #fbbf24; }
.status-done     { background: rgba(34,197,94,0.15);   border: 1px solid rgba(34,197,94,0.3);   color: #86efac; }

.act-btn {
  padding: 3px 10px;
  font-size: 11px;
  font-weight: 500;
  color: #fbbf24;
  background: rgba(245,158,11,0.1);
  border: 1px solid rgba(245,158,11,0.3);
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s;
}
.act-btn:hover { background: rgba(245,158,11,0.22); border-color: rgba(245,158,11,0.6); }

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #4f8ef7 0%, #7c3aed 100%);
  border: none;
  border-radius: 8px;
  color: #ffffff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(79,142,247,0.35);
  overflow: hidden;
}
.btn-primary:hover { box-shadow: 0 6px 24px rgba(79,142,247,0.5); }
.btn-shimmer {
  position: absolute; top: 0; left: -75%;
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
  padding: 8px 14px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.07); color: #fff; border-color: rgba(255,255,255,0.3); }

/* ━━ ページネーション ━━ */
:deep(.dark-pager .el-pagination__sizes .el-input__wrapper) {
  background: rgba(255,255,255,0.07);
  box-shadow: none;
  border: 1px solid rgba(255,255,255,0.12);
}
:deep(.dark-pager .el-pagination__sizes .el-input__inner) { color: rgba(255,255,255,0.7); }
:deep(.dark-pager button),
:deep(.dark-pager .el-pager li) {
  background: transparent;
  color: rgba(255,255,255,0.45);
  border: none;
}
:deep(.dark-pager .el-pager li.is-active) { color: #4f8ef7; font-weight: 700; }
:deep(.dark-pager button:not(:disabled):hover),
:deep(.dark-pager .el-pager li:hover) { color: #fff; }

/* ━━ 対応モーダル ━━ */
.modal-body { padding: 4px 0; }
.modal-employee { display: flex; align-items: center; gap: 12px; }
.modal-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b, #ef4444);
  display: flex; align-items: center; justify-content: center;
  font-size: 15px; color: white; font-weight: 700; flex-shrink: 0;
}
.modal-name { color: #ffffff; font-size: 15px; font-weight: 600; margin-bottom: 2px; }
.modal-location { color: rgba(255,255,255,0.45); font-size: 12px; }
</style>
