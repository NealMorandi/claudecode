<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <!-- 寮種別トグル -->
        <div class="field-group">
          <label class="field-label">寮種別</label>
          <div class="toggle-group">
            <button
              v-for="opt in genderOptions"
              :key="opt.value"
              class="toggle-btn"
              :class="{ 'toggle-btn--active': query.gender === opt.value }"
              @click="query.gender = opt.value"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- 入居予定日 -->
        <div class="field-group">
          <label class="field-label">入居予定日</label>
          <el-date-picker
            v-model="query.date"
            type="date"
            placeholder="日付を選択"
            value-format="YYYY-MM-DD"
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

    <!-- ━━ メインパネル ━━ -->
    <div class="glass-panel main-panel">
      <div class="panel-header">
        <div class="header-left">
          <span class="panel-title">空き室一覧</span>
          <span class="vacant-badge">
            <span class="badge-dot"></span>
            {{ total }}室空き
          </span>
        </div>
        <!-- カード / テーブル切り替え -->
        <div class="view-toggle">
          <button
            class="view-btn"
            :class="{ 'view-btn--active': viewMode === 'card' }"
            @click="viewMode = 'card'"
          >
            <el-icon><Grid /></el-icon>
            カード
          </button>
          <button
            class="view-btn"
            :class="{ 'view-btn--active': viewMode === 'table' }"
            @click="viewMode = 'table'"
          >
            <el-icon><List /></el-icon>
            テーブル
          </button>
        </div>
      </div>

      <!-- ページネーション（テーブル直上・右揃え） -->
      <div class="pagination-row">
        <span class="pg-label">ページ行数：</span>
        <AppSelect v-model="pageSize" :options="pageSizeOptions.map(s => ({ label: String(s), value: s }))" style="width:72px" @change="onPageSizeChange" />
        <span class="pg-total">総{{ total }}件</span>
        <button class="pg-btn" aria-label="先頭ページ" :disabled="page === 1" @click="goToPage(1)">|&lt;</button>
        <button class="pg-btn" aria-label="前ページ" :disabled="page === 1" @click="goToPage(page - 1)">&lt;</button>
        <button class="pg-btn" aria-label="次ページ" :disabled="page >= totalPages" @click="goToPage(page + 1)">&gt;</button>
        <button class="pg-btn" aria-label="末尾ページ" :disabled="page >= totalPages" @click="goToPage(totalPages)">&gt;|</button>
      </div>

      <!-- カード表示 -->
      <div v-if="viewMode === 'card'" class="card-scroll-wrap">

        <!-- スケルトンカード -->
        <div v-if="loading" class="room-grid">
          <div
            v-for="i in 8"
            :key="i"
            class="room-card room-card--skel"
            :style="{ '--d': `${(i - 1) * 55}ms` }"
          >
            <div class="room-card-top">
              <div class="room-card-header">
                <div class="sk sk--name" />
                <div class="sk sk--badge" />
              </div>
              <div class="sk sk--sub" />
            </div>
            <div class="room-card-body">
              <div v-for="j in 4" :key="j" class="room-meta">
                <div class="sk sk--label" />
                <div class="sk sk--val" />
              </div>
            </div>
            <div class="sk sk--btn" />
          </div>
        </div>

        <!-- 通常コンテンツ -->
        <template v-else>
          <div v-if="!list.length" class="empty-state">
            <div class="empty-icon">
              <el-icon><House /></el-icon>
            </div>
            <p class="empty-title">空き室なし</p>
            <p class="empty-sub">検索条件を変更するか、後ほどご確認ください</p>
          </div>

          <div v-else class="room-grid">
            <div v-for="room in pagedList" :key="room.id" class="room-card">
              <div class="room-card-top">
                <div class="room-card-header">
                  <span class="room-name">{{ room.name }}</span>
                  <span class="badge" :class="room.dormitoryType === '男' ? 'badge-blue' : 'badge-red'">
                    {{ room.dormitoryType === '男' ? '男性寮' : '女性寮' }}
                  </span>
                </div>
                <p class="dormitory-name">{{ room.dormitoryName }}</p>
              </div>
              <div class="room-card-body">
                <div class="room-meta">
                  <span class="meta-label">面積</span>
                  <span class="meta-value">{{ room.area }} ㎡</span>
                </div>
                <div class="room-meta">
                  <span class="meta-label">定員 / 在居</span>
                  <span class="meta-value">
                    {{ room.capacity }} 名
                    <span class="occ-badge">在居 {{ room.currentOccupancy }} 名</span>
                  </span>
                </div>
                <div class="room-meta">
                  <span class="meta-label">最終退寮</span>
                  <span class="meta-value" :class="!room.lastCheckout ? 'meta-empty' : ''">
                    {{ room.lastCheckout || '—' }}
                  </span>
                </div>
              </div>
              <button
                class="btn-primary btn-block"
                @click="$router.push({ path: '/residences/new', query: { roomId: room.id } })"
              >
                <span class="btn-shimmer"></span>
                <el-icon><Plus /></el-icon>
                <span>入居登録</span>
              </button>
            </div>
          </div>
        </template>

        <!-- 検索中バー -->
        <div v-if="loading" class="card-search-bar">
          <ArcSpinner :size="15" :stroke-width="2.4" />
          <span class="card-search-text">検索中...</span>
        </div>

      </div>

      <!-- テーブル表示 -->
      <div v-else class="table-scroll-wrap">
      <el-table :data="loading ? [] : pagedList" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="寮名" prop="dormitoryName" />
        <el-table-column label="部屋名" prop="name" />
        <el-table-column label="種別" width="90" align="center">
          <template #default="{ row }">
            <span class="badge" :class="row.dormitoryType === '男' ? 'badge-blue' : 'badge-red'">
              {{ row.dormitoryType === '男' ? '男性寮' : '女性寮' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="面積(㎡)" prop="area" width="100" align="center" />
        <el-table-column label="定員 / 在居" width="110" align="center">
          <template #default="{ row }">
            {{ row.capacity }} / {{ row.currentOccupancy }} 名
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center">
          <template #default="{ row }">
            <button
              class="link-btn"
              @click="$router.push({ path: '/residences/new', query: { roomId: row.id } })"
            >入居登録</button>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="loading" />
          <el-empty v-else description="データなし" :image-size="60" />
        </template>
      </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getVacancies } from '@/api/vacancy'
import TableLoadState from '@/components/TableLoadState.vue'
import ArcSpinner from '@/components/ArcSpinner.vue'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const viewMode = ref('card')
const query = reactive({ gender: '', date: '' })
const page = ref(1)
const pageSize = ref(10)
const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const pagedList = computed(() => list.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
const goToPage = (p) => { page.value = p }
const onPageSizeChange = () => { page.value = 1 }

const genderOptions = [
  { label: 'すべて', value: '' },
  { label: '男性寮', value: '男' },
  { label: '女性寮', value: '女' },
]

const fetchList = async () => {
  loading.value = true
  page.value = 1
  try {
    const data = await getVacancies(query)
    list.value = Array.isArray(data) ? data : data.list
    total.value = Array.isArray(data) ? data.length : data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { gender: '', date: '' })
  fetchList()
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
.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
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

/* ━━ 寮種別トグルボタン ━━ */
.toggle-group {
  display: flex;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.10);
  border-radius: 8px;
  overflow: hidden;
}
.toggle-btn {
  padding: 8px 16px;
  background: transparent;
  border: none;
  color: rgba(255,255,255,0.5);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  white-space: nowrap;
}
.toggle-btn:hover { color: rgba(255,255,255,0.85); background: rgba(255,255,255,0.06); }
.toggle-btn--active {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  color: #ffffff;
  font-weight: 500;
}

/* デートピッカー */
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
:deep(.dark-input .el-input__suffix-icon) { color: rgba(255,255,255,0.38); }

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.panel-title {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
}
.vacant-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px 10px;
  background: rgba(34,197,94,0.18);
  border: 1px solid rgba(34,197,94,0.3);
  border-radius: 20px;
  color: #86efac;
  font-size: 12px;
  font-weight: 500;
}
.badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #22c55e;
  box-shadow: 0 0 5px #22c55e;
  animation: breathe 2s ease-in-out infinite;
}
@keyframes breathe {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.4; }
}

/* ━━ ビュー切り替え ━━ */
.view-toggle {
  display: flex;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.10);
  border-radius: 8px;
  overflow: hidden;
}
.view-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  background: transparent;
  border: none;
  color: rgba(255,255,255,0.45);
  font-size: 12px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.view-btn:hover { color: rgba(255,255,255,0.8); }
.view-btn--active {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  color: #ffffff;
  font-weight: 500;
}

/* ━━ 空状態 ━━ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 12px;
}
.empty-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: rgba(255,255,255,0.3);
  margin-bottom: 4px;
}
.empty-title {
  color: rgba(255,255,255,0.55);
  font-size: 15px;
  font-weight: 500;
}
.empty-sub {
  color: rgba(255,255,255,0.25);
  font-size: 12px;
}

/* ━━ カードビュースクロール ━━ */
.card-scroll-wrap {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(163,166,173,0.3) transparent;
}
.card-scroll-wrap::-webkit-scrollbar { width: 6px; }
.card-scroll-wrap::-webkit-scrollbar-track { background: transparent; }
.card-scroll-wrap::-webkit-scrollbar-thumb { background: rgba(163,166,173,0.3); border-radius: 4px; }
.card-scroll-wrap::-webkit-scrollbar-thumb:hover { background: rgba(163,166,173,0.5); }

/* ━━ テーブルビュースクロール ━━ */
.table-scroll-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.table-scroll-wrap :deep(.el-table) {
  flex: 1;
  min-height: 0;
}

/* ━━ ルームカードグリッド ━━ */
.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}
.room-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 10px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: border-color 0.2s, background 0.2s;
}
.room-card:hover {
  border-color: rgba(79,142,247,0.3);
  background: rgba(79,142,247,0.05);
}

/* ━━ スケルトンカード ━━ */
.room-card--skel { pointer-events: none; }

.sk {
  border-radius: 6px;
  background: rgba(255,255,255,0.06);
  position: relative;
  overflow: hidden;
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
  animation-delay: var(--d, 0ms);
  transform: translateX(-100%);
}
@keyframes sk-wave { to { transform: translateX(100%); } }

.sk--name  { width: 65%; height: 15px; }
.sk--badge { width: 24%; height: 20px; border-radius: 9px; }
.sk--sub   { width: 50%; height: 10px; margin-top: 4px; }
.sk--label { width: 32%; height: 10px; }
.sk--val   { width: 38%; height: 10px; }
.sk--btn   { width: 100%; height: 36px; border-radius: 8px; margin-top: 4px; }

/* ━━ 検索中バー ━━ */
.card-search-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  margin-top: 12px;
  border: 1px solid rgba(139,92,246,0.22);
  border-radius: 8px;
  background: rgba(139,92,246,0.05);
}
.card-search-text {
  color: rgba(196,181,253,0.82);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.05em;
}
.room-card-top { display: flex; flex-direction: column; gap: 4px; }
.room-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.room-name { color: #ffffff; font-size: 15px; font-weight: 600; }
.dormitory-name { color: rgba(255,255,255,0.42); font-size: 12px; }

.room-card-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 10px 0;
  border-top: 1px solid rgba(255,255,255,0.06);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.room-meta { display: flex; justify-content: space-between; align-items: center; }
.meta-label { color: rgba(255,255,255,0.35); font-size: 11px; }
.meta-value { color: rgba(255,255,255,0.75); font-size: 12px; font-weight: 500; }
.meta-empty { color: rgba(255,255,255,0.25); }
.occ-badge {
  margin-left: 6px;
  padding: 1px 7px;
  border-radius: 10px;
  background: rgba(79,142,247,0.18);
  color: #6fa8f7;
  font-size: 11px;
}

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
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
.btn-block { width: 100%; }
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
}
.btn-ghost:hover { background: rgba(255,255,255,0.08); color: #ffffff; }

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-blue { background: rgba(79,142,247,0.2);  color: #93c5fd; }
.badge-red  { background: rgba(248,113,113,0.2); color: #fca5a5; }

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
.link-btn {
  background: none;
  border: none;
  color: #4f8ef7;
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
