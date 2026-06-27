<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">寮名</label>
          <AppSelect
            v-model="query.name"
            :options="dormitoryNameOptions"
            placeholder="寮名で絞り込み"
            clearable
          />
        </div>
        <div class="field-group">
          <label class="field-label">種別</label>
          <AppSelect
            v-model="query.type"
            :options="[
              { label: 'すべて',  value: '' },
              { label: '男性寮', value: '男' },
              { label: '女性寮', value: '女' },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="field-group">
          <label class="field-label">都道府県</label>
          <AppSelect
            v-model="query.prefecture"
            :options="prefectureOptions"
            clearable
            filterable
            placeholder="すべて"
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
        </div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">社員寮一覧</span>
          <span class="panel-badge">DORMITORY LIST</span>
        </div>
      </div>

      <!-- ページネーション（テーブル直上・右揃え） -->
      <div class="pagination-row">
        <span class="pg-label">ページ行数：</span>
        <AppSelect
          v-model="query.pageSize"
          :options="pageSizeOptions.map(s => ({ label: String(s), value: s }))"
          style="width:72px"
          @change="onPageSizeChange"
        />
        <span class="pg-total">総{{ total }}件</span>
        <button class="pg-btn" aria-label="先頭ページ" :disabled="query.page === 1" @click="goToPage(1)">|&lt;</button>
        <button class="pg-btn" aria-label="前ページ"   :disabled="query.page === 1" @click="goToPage(query.page - 1)">&lt;</button>
        <button class="pg-btn" aria-label="次ページ"   :disabled="query.page >= totalPages" @click="goToPage(query.page + 1)">&gt;</button>
        <button class="pg-btn" aria-label="末尾ページ" :disabled="query.page >= totalPages" @click="goToPage(totalPages)">&gt;|</button>
      </div>

      <el-table
        :data="loading ? [] : list"
        row-key="id"
        height="100%"
        class="dark-table"
      >
        <el-table-column type="index" label="No." width="60" align="center" />

        <el-table-column label="寮名" prop="name" min-width="130" />

        <el-table-column label="都道府県" width="100" align="center">
          <template #default="{ row }">
            <span :class="row.prefecture ? '' : 'cell-empty'">{{ row.prefecture || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="住所" min-width="180">
          <template #default="{ row }">
            <span :class="row.address ? 'cell-muted' : 'cell-empty'">{{ row.address || '—' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="種別" width="90" align="center">
          <template #default="{ row }">
            <span class="badge" :class="typeClass(row.type)">{{ typeLabel(row.type) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="総部屋数" width="84" align="center">
          <template #default="{ row }">
            {{ row.totalRooms }}
          </template>
        </el-table-column>

        <el-table-column label="定員" width="70" align="center">
          <template #default="{ row }">
            {{ row.totalCapacity }}
          </template>
        </el-table-column>

        <el-table-column label="入居人数" width="84" align="center">
          <template #default="{ row }">
            <span class="cell-resident">{{ row.residentCount }}</span>
          </template>
        </el-table-column>

        <el-table-column label="入居可能" width="84" align="center">
          <template #default="{ row }">
            <span :class="availClass(row)">{{ row.availableCapacity }}</span>
          </template>
        </el-table-column>

        <el-table-column label="ステータス" width="96" align="center">
          <template #default="{ row }">
            <span class="badge" :class="occStatus(row).cls">{{ occStatus(row).label }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="80" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="$router.push(`/dormitories/${row.id}`)">詳細</button>
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
import { Search, Refresh } from '@element-plus/icons-vue'
import { getDormitoryList } from '@/api/dormitory'
import AppSelect from '@/components/AppSelect.vue'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const list    = ref([])
const total   = ref(0)
const allDormitoryNames = ref([])

const query = reactive({
  name:       '',
  prefecture: '',
  type:       '',
  status:     'active',
  page:       1,
  pageSize:   10,
})

const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)

const goToPage = (page) => { query.page = page; fetchList() }
const onPageSizeChange = () => { query.page = 1; fetchList() }

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getDormitoryList({ ...query })
    list.value  = data.list
    total.value = data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { name: '', prefecture: '', type: '', page: 1 })
  fetchList()
}

onMounted(async () => {
  fetchList()
  const all = await getDormitoryList({ pageSize: 1000, status: '' }).catch(() => ({ list: [] }))
  allDormitoryNames.value = (all.list || []).map(d => d.name).filter(Boolean).sort()
})

const dormitoryNameOptions = computed(() =>
  allDormitoryNames.value.map(n => ({ label: n, value: n }))
)

const prefectureOptions = computed(() => {
  const set = new Set(list.value.map(d => d.prefecture).filter(Boolean))
  return [...set].sort().map(p => ({ label: p, value: p }))
})

function typeLabel(type) {
  return { 男: '男性寮', 女: '女性寮', mixed: '混合寮' }[type] ?? type ?? '—'
}
function typeClass(type) {
  return { 男: 'badge-blue', 女: 'badge-pink', mixed: 'badge-purple' }[type] ?? 'badge-gray'
}

function occStatus(row) {
  const cap   = row.totalCapacity    ?? 0
  const res   = row.residentCount    ?? 0
  const avail = row.availableCapacity ?? 0
  if (res > cap)   return { label: '定員超', cls: 'badge-red' }
  if (avail === 0) return { label: '満室',   cls: 'badge-amber' }
  return              { label: '入居可',   cls: 'badge-green' }
}

function availClass(row) {
  const res = row.residentCount    ?? 0
  const cap = row.totalCapacity    ?? 0
  if (res > cap)                return 'cell-over'
  if (row.availableCapacity > 0) return 'cell-avail'
  return 'cell-empty'
}
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

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-title {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  margin-right: 10px;
}
.panel-badge {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}

/* ━━ 検索フォーム ━━ */
.search-form {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 12px;
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
  padding-bottom: 1px;
}

/* ━━ 入力欄ダークオーバーライド ━━ */
.dark-input :deep(.el-input__wrapper),
.dark-input.el-input :deep(.el-input__wrapper),
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
:deep(.dark-input .el-input__clear) { color: rgba(255,255,255,0.38); }

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
  white-space: nowrap;
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

.btn-ghost {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  border-radius: 8px;
  color: rgba(255,255,255,0.65);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  white-space: nowrap;
}
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ テーブルパネル（flex で高さ追従） ━━ */
.table-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

/* ━━ テーブル ━━ */
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
:deep(.el-loading-mask) { background: rgba(6,13,31,0.7); }

/* ━━ バッジ（入居履歴一覧と統一） ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-blue   { background: rgba(79,142,247,0.2);   color: #93c5fd; }
.badge-pink   { background: rgba(244,114,182,0.2);  color: #f9a8d4; }
.badge-purple { background: rgba(167,139,250,0.2);  color: #c4b5fd; }
.badge-green  { background: rgba(34,197,94,0.2);    color: #86efac; }
.badge-amber  { background: rgba(245,158,11,0.2);   color: #fcd34d; }
.badge-red    { background: rgba(248,113,113,0.2);  color: #fca5a5; }
.badge-gray   { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.45); }

/* ━━ セル ━━ */
.cell-muted    { color: rgba(255,255,255,0.5); }
.cell-empty    { color: rgba(255,255,255,0.25); }
.cell-resident { color: #93c5fd; }
.cell-avail    { color: #86efac; }
.cell-over     { color: #fca5a5; }

/* ━━ テーブル内リンクボタン ━━ */
.link-btn {
  background: none;
  border: none;
  color: #4f8ef7;
  font-size: 12px;
  cursor: pointer;
  padding: 0 6px;
  transition: opacity 0.15s;
}
.link-btn:hover { opacity: 0.7; }

/* ━━ ページネーション ━━ */
.pagination-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  width: 100%;
}
.pg-label {
  color: rgba(255,255,255,0.45);
  font-size: 12px;
  white-space: nowrap;
}
.pg-total {
  color: rgba(255,255,255,0.45);
  font-size: 12px;
  white-space: nowrap;
  margin-left: 4px;
}
.pg-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  height: 28px;
  padding: 0 6px;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 6px;
  color: rgba(255,255,255,0.65);
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  white-space: nowrap;
}
.pg-btn:hover:not(:disabled) {
  background: rgba(79,142,247,0.18);
  color: #4f8ef7;
  border-color: rgba(79,142,247,0.35);
}
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
