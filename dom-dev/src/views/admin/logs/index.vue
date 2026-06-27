<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">操作者</label>
          <el-input v-model="query.keyword" placeholder="ユーザー名で検索" clearable class="dark-input" />
        </div>
        <div class="field-group">
          <label class="field-label">操作種別</label>
          <AppSelect
            v-model="query.action"
            :options="[
              { label: 'すべて',       value: '' },
              { label: '作成',         value: 'create' },
              { label: '更新',         value: 'update' },
              { label: '削除',         value: 'delete' },
              { label: 'ログイン',     value: 'login' },
              { label: 'エクスポート', value: 'export' },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="field-group field-group--wide">
          <label class="field-label">日時範囲</label>
          <el-date-picker
            v-model="query.dateRange"
            type="datetimerange"
            start-placeholder="開始日時"
            end-placeholder="終了日時"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="dark-range"
          />
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
          <button class="btn-purple" @click="handleExport">
            <span class="btn-shimmer"></span>
            <el-icon><Download /></el-icon>
            <span>Excel出力</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#4f8ef7">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><List /></el-icon></div>
        <div><p class="sum-label">総ログ件数</p><p class="sum-value">{{ total }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#22c55e">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><EditPen /></el-icon></div>
        <div><p class="sum-label">作成・更新</p><p class="sum-value">{{ createUpdateCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#f87171">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Delete /></el-icon></div>
        <div><p class="sum-label">削除</p><p class="sum-value">{{ deleteCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#f59e0b">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Upload /></el-icon></div>
        <div><p class="sum-label">エクスポート</p><p class="sum-value">{{ exportCount }}</p></div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div class="panel-header-left">
          <span class="panel-title">操作ログ一覧</span>
          <span class="panel-badge">OPERATION LOG</span>
        </div>
        <div class="realtime-badge">
          <span class="pulse-dot"></span>
          リアルタイム記録中
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

      <el-table :data="loading ? [] : list" row-key="id" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="操作日時" prop="createdAt" width="170" align="center">
          <template #default="{ row }">
            <span class="datetime-cell">{{ row.createdAt }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作者" prop="operatorName" width="120" />
        <el-table-column label="操作種別" prop="action" width="130" align="center">
          <template #default="{ row }">
            <span class="badge" :class="actionClass(row.action)">{{ actionLabel(row.action) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="対象" prop="targetDesc" min-width="160">
          <template #default="{ row }">
            <span class="cell-muted">{{ row.targetDesc || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="対象ID" prop="targetId" width="90" align="center">
          <template #default="{ row }">
            <span class="id-cell">{{ row.targetId || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="詳細" prop="detail" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="detail-cell">{{ row.detail || '—' }}</span>
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
import { getLogList, exportLogs } from '@/api/operationLog'
import { downloadBlob } from '@/utils/export'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const list    = ref([])
const total   = ref(0)
const createUpdateCount = ref(0)
const deleteCount       = ref(0)
const exportCount       = ref(0)

const query = reactive({
  keyword: '', action: '', dateRange: null, page: 1, pageSize: 10,
})
const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const goToPage = (page) => { query.page = page; fetchList() }
const onPageSizeChange = () => { query.page = 1; fetchList() }

const ACTION_MAP = {
  create:   { label: '作成',         cls: 'badge-green'  },
  update:   { label: '更新',         cls: 'badge-blue'   },
  delete:   { label: '削除',         cls: 'badge-red'    },
  login:    { label: 'ログイン',     cls: 'badge-purple' },
  export:   { label: 'エクスポート', cls: 'badge-amber'  },
  checkout: { label: '退寮',         cls: 'badge-amber'  },
}

// compound action（例: "dormitory_delete"）に対応：末尾の種別でマッピング
const resolveAction = (a) => {
  if (!a) return null
  if (ACTION_MAP[a]) return ACTION_MAP[a]
  const suffix = a.split('_').pop()
  return ACTION_MAP[suffix] ?? null
}
const actionLabel = (a) => resolveAction(a)?.label ?? a
const actionClass = (a) => resolveAction(a)?.cls  ?? 'badge-gray'

const buildParams = () => {
  const params = { keyword: query.keyword, action: query.action, page: query.page, pageSize: query.pageSize }
  if (query.dateRange) {
    params.from = query.dateRange[0]
    params.to   = query.dateRange[1]
  }
  return params
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getLogList(buildParams())
    list.value              = data.list  ?? data
    total.value             = data.total ?? data.length ?? 0
    createUpdateCount.value = data.pendingCount    || 0
    deleteCount.value       = data.doneCount       || 0
    exportCount.value       = data.confirmedCount  || 0
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { keyword: '', action: '', dateRange: null, page: 1 })
  fetchList()
}

const handleExport = async () => {
  const blob = await exportLogs(buildParams())
  downloadBlob(blob, '操作ログ.xlsx')
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
.field-group { display: flex; flex-direction: column; gap: 6px; min-width: 170px; }
.field-group--wide { min-width: 360px; }
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px; font-weight: 600;
  letter-spacing: 0.12em; text-transform: uppercase;
}
.search-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }

/* ━━ ダーク入力 ━━ */
:deep(.dark-input .el-input__wrapper) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px; transition: box-shadow 0.2s;
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

/* ━━ 日時範囲ピッカー ━━ */
:deep(.dark-range.el-date-editor) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px;
  border: none;
  width: 100%;
}
:deep(.dark-range.el-date-editor:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,0.24) inset;
}
:deep(.dark-range .el-range-input) {
  background: transparent;
  color: #ffffff;
}
:deep(.dark-range .el-range-input::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-range .el-range-separator) { color: rgba(255,255,255,0.35); }
:deep(.dark-range .el-range__icon),
:deep(.dark-range .el-range__close-icon) { color: rgba(255,255,255,0.38); }

/* ━━ ボタン ━━ */
.btn-primary,
.btn-purple {
  position: relative;
  display: inline-flex; align-items: center; gap: 6px;
  padding: 9px 18px;
  border: none; border-radius: 8px;
  color: #ffffff; font-size: 13px; font-weight: 500;
  cursor: pointer; overflow: hidden;
  transition: box-shadow 0.2s, opacity 0.2s;
  white-space: nowrap;
}
.btn-primary {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  box-shadow: 0 4px 14px rgba(79,142,247,0.35);
}
.btn-primary:hover { box-shadow: 0 6px 20px rgba(79,142,247,0.5); }
.btn-purple {
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  box-shadow: 0 4px 14px rgba(124,58,237,0.35);
}
.btn-purple:hover { box-shadow: 0 6px 20px rgba(124,58,237,0.5); }
.btn-shimmer {
  position: absolute; top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer { 0% { left: -75%; } 60% { left: 125%; } 100% { left: 125%; } }

.btn-ghost {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 9px 16px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  color: rgba(255,255,255,0.65); font-size: 13px;
  cursor: pointer; transition: background 0.15s, color 0.15s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ サマリーカード ━━ */
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.sum-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px; padding: 20px;
  display: flex; align-items: center; gap: 16px;
  position: relative; overflow: hidden;
}
.sum-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  opacity: 0.65;
}
.sum-icon-wrap { position: relative; flex-shrink: 0; }
.sum-halo {
  position: absolute; inset: -8px; border-radius: 50%;
  background: radial-gradient(circle, var(--accent) 0%, transparent 70%);
  opacity: 0.25; filter: blur(6px);
}
.sum-icon { font-size: 28px; color: var(--accent); position: relative; }
.sum-label { color: rgba(255,255,255,0.45); font-size: 12px; margin-bottom: 6px; }
.sum-value { color: #ffffff; font-size: 30px; font-weight: 700; line-height: 1; }

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex; align-items: center;
  justify-content: space-between; margin-bottom: 16px;
}
.panel-header-left { display: flex; align-items: center; gap: 10px; }
.panel-title  { color: #ffffff; font-size: 14px; font-weight: 600; }
.panel-badge  { color: rgba(255,255,255,0.28); font-size: 10px; font-weight: 600; letter-spacing: 0.12em; }

/* ━━ リアルタイムバッジ ━━ */
.realtime-badge {
  display: flex; align-items: center; gap: 7px;
  padding: 5px 12px;
  background: rgba(34,197,94,0.10);
  border: 1px solid rgba(34,197,94,0.28);
  border-radius: 20px;
  color: #4ade80; font-size: 11px; font-weight: 600;
  letter-spacing: 0.04em;
}
.pulse-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #22c55e;
  box-shadow: 0 0 0 0 rgba(34,197,94,0.7);
  animation: pulse-ring 1.8s ease-in-out infinite;
  flex-shrink: 0;
}
@keyframes pulse-ring {
  0%   { box-shadow: 0 0 0 0   rgba(34,197,94,0.7); }
  70%  { box-shadow: 0 0 0 7px rgba(34,197,94,0);   }
  100% { box-shadow: 0 0 0 0   rgba(34,197,94,0);   }
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
  color: rgba(255,255,255,0.32); font-size: 10px; font-weight: 600;
  text-transform: uppercase; letter-spacing: 0.10em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
:deep(.el-table .el-table__body td.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.78);
  border-bottom: 1px solid rgba(255,255,255,0.05); font-size: 13px;
}
:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255,255,255,0.05) !important;
}

/* セル */
.datetime-cell { color: rgba(255,255,255,0.55); font-size: 12px; font-family: monospace; }
.id-cell       { color: rgba(255,255,255,0.4);  font-size: 12px; font-family: monospace; }
.cell-muted    { color: rgba(255,255,255,0.55); }
.detail-cell   { color: rgba(255,255,255,0.45); font-size: 12px; }

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px; font-weight: 600; letter-spacing: 0.04em;
}
.badge-green  { background: rgba(34,197,94,0.15);   color: #86efac; border: 1px solid rgba(34,197,94,0.3);   }
.badge-blue   { background: rgba(79,142,247,0.18);  color: #93c5fd; border: 1px solid rgba(79,142,247,0.3);  }
.badge-red    { background: rgba(248,113,113,0.15); color: #fca5a5; border: 1px solid rgba(248,113,113,0.3); }
.badge-purple { background: rgba(167,139,250,0.18); color: #c4b5fd; border: 1px solid rgba(167,139,250,0.3); }
.badge-amber  { background: rgba(245,158,11,0.15);  color: #fcd34d; border: 1px solid rgba(245,158,11,0.3);  }
.badge-gray   { background: rgba(255,255,255,0.07); color: rgba(255,255,255,0.45); border: 1px solid rgba(255,255,255,0.12); }

/* ━━ ページネーション ━━ */
.pagination-row { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%; }
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; height: 28px; padding: 0 6px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
.pg-btn:hover:not(:disabled) { background: rgba(79,142,247,0.18); color: #4f8ef7; border-color: rgba(79,142,247,0.35); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
