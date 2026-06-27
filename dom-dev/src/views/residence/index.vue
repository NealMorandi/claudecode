<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">社員名 / 寮名</label>
          <el-input
            v-model="query.keyword"
            placeholder="社員名・社員番号・寮名で検索"
            clearable
            class="dark-input"
          />
        </div>
        <div class="field-group">
          <label class="field-label">ステータス</label>
          <AppSelect
            v-model="query.status"
            :options="[
              { label: '入居中', value: 'active' },
              { label: '退寮済', value: 'checked_out' },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="field-group">
          <label class="field-label">入居日（開始）</label>
          <el-date-picker
            v-model="query.checkinFrom"
            type="date"
            placeholder="開始日"
            value-format="YYYY-MM-DD"
            clearable
            class="dark-input"
          />
        </div>
        <div class="field-group">
          <label class="field-label">入居日（終了）</label>
          <el-date-picker
            v-model="query.checkinTo"
            type="date"
            placeholder="終了日"
            value-format="YYYY-MM-DD"
            clearable
            class="dark-input"
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
          <span class="panel-title">入居履歴一覧</span>
          <span class="panel-badge">RESIDENCE HISTORY</span>
        </div>
        <button class="btn-primary btn-sm" @click="$router.push('/residences/new')">
          <span class="btn-shimmer"></span>
          <el-icon><Plus /></el-icon>
          <span>新規入居登録</span>
        </button>
      </div>

      <!-- ページネーション（テーブル直上・右揃え） -->
      <div class="pagination-row">
        <span class="pg-label">ページ行数：</span>
        <AppSelect v-model="query.pageSize" :options="pageSizeOptions.map(s => ({ label: String(s), value: s }))" style="width:72px" @change="onPageSizeChange" />

        <span class="pg-total">総{{ total }}件</span>

        <button
          class="pg-btn"
          aria-label="先頭ページ"
          :disabled="query.page === 1"
          @click="goToPage(1)"
        >|&lt;</button>
        <button
          class="pg-btn"
          aria-label="前ページ"
          :disabled="query.page === 1"
          @click="goToPage(query.page - 1)"
        >&lt;</button>
        <button
          class="pg-btn"
          aria-label="次ページ"
          :disabled="query.page >= totalPages"
          @click="goToPage(query.page + 1)"
        >&gt;</button>
        <button
          class="pg-btn"
          aria-label="末尾ページ"
          :disabled="query.page >= totalPages"
          @click="goToPage(totalPages)"
        >&gt;|</button>
      </div>

      <el-table
        :data="loading ? [] : list"
        row-key="id"
        height="100%"
        class="dark-table"
      >
        <el-table-column type="index" label="No." width="60" align="center"
          :index="(i) => (query.page - 1) * query.pageSize + i + 1" />
        <el-table-column label="社員名" prop="employeeName" min-width="120" />
        <el-table-column label="区分" prop="employeeType" width="110" align="center">
          <template #default="{ row }">
            <span class="badge" :class="row.employeeType === 'japan' ? 'badge-blue' : 'badge-amber'">
              {{ row.employeeType === 'japan' ? '日本社員' : '中国出張' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="寮名 / 部屋" min-width="160">
          <template #default="{ row }">
            <span class="cell-muted">{{ row.dormitoryName }}</span>
            <span style="color:rgba(255,255,255,0.3);margin:0 4px">/</span>
            <span>{{ row.roomName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="入居日" prop="checkinDate" width="110" align="center" />
        <el-table-column label="退寮日" prop="checkoutDate" width="110" align="center">
          <template #default="{ row }">
            <span :class="row.checkoutDate ? '' : 'cell-empty'">{{ row.checkoutDate || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="ステータス" width="100" align="center">
          <template #default="{ row }">
            <span class="badge" :class="row.status === 'active' ? 'badge-green' : 'badge-gray'">
              {{ row.status === 'active' ? '入居中' : '退寮済' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="$router.push(`/residences/${row.id}`)">詳細</button>
            <button
              v-if="row.status === 'active'"
              class="link-btn link-btn--warn"
              @click="$router.push(`/residences/${row.id}/checkout`)"
            >退寮処理</button>
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
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResidenceList } from '@/api/residence'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({
  keyword: '', status: '', checkinFrom: '', checkinTo: '', page: 1, pageSize: 10,
})

const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)

const goToPage = (page) => {
  query.page = page
  fetchList()
}

const onPageSizeChange = () => {
  query.page = 1
  fetchList()
}

const fetchList = async () => {
  if (query.checkinFrom && query.checkinTo && query.checkinFrom > query.checkinTo) {
    ElMessage.warning('入居日の開始日は終了日以前に設定してください')
    return
  }
  loading.value = true
  try {
    const data = await getResidenceList({ ...query })
    list.value = data.list
    total.value = data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { keyword: '', status: '', checkinFrom: '', checkinTo: '', page: 1 })
  fetchList()
}

const route = useRoute()
onMounted(() => {
  if (route.query.keyword) query.keyword = route.query.keyword
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
.field-wide { min-width: 260px; }
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
:deep(.dark-input .el-input__wrapper:hover),
:deep(.dark-input.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,0.24) inset;
}
:deep(.dark-input .el-input__wrapper.is-focus),
:deep(.dark-input.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4f8ef7 inset, 0 0 10px rgba(79,142,247,0.2);
}
:deep(.dark-input .el-input__inner) { color: #ffffff; caret-color: #4f8ef7; }
:deep(.dark-input .el-input__inner::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-input .el-input__prefix-icon),
:deep(.dark-input .el-input__suffix-icon),
:deep(.dark-input .el-input__clear) { color: rgba(255,255,255,0.38); }

/* セレクト */
:deep(.el-select .dark-input .el-input__wrapper) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
}

/* デートピッカー幅 */
:deep(.dark-input.el-date-editor) { width: 100%; }
:deep(.dark-input .el-range-input) {
  color: rgba(255,255,255,0.75);
  background: transparent;
}
:deep(.dark-input .el-range-input::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-input .el-range-separator) { color: rgba(255,255,255,0.28); }

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
.btn-sm { padding: 7px 14px; font-size: 12px; }
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

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-blue   { background: rgba(79,142,247,0.2);  color: #93c5fd; }
.badge-amber  { background: rgba(245,158,11,0.2);  color: #fcd34d; }
.badge-green  { background: rgba(34,197,94,0.2);   color: #86efac; }
.badge-gray   { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.45); }

.cell-muted { color: rgba(255,255,255,0.5); }
.cell-empty { color: rgba(255,255,255,0.25); }

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
.link-btn--warn { color: #f59e0b; }

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
.pg-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
