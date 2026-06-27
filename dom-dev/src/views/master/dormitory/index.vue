<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">寮名</label>
          <el-input v-model="query.name" placeholder="寮名で検索" clearable class="dark-input" />
        </div>
        <div class="field-group">
          <label class="field-label">種別</label>
          <AppSelect
            v-model="query.type"
            :options="[
              { label: 'すべて', value: '' },
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
        <div class="field-group">
          <label class="field-label">状態</label>
          <AppSelect
            v-model="query.status"
            :options="[
              { label: 'すべて',  value: '' },
              { label: '有効',    value: 'active' },
              { label: '退却',    value: 'inactive' },
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
          <button class="btn-ghost" @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            <span>リセット</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#4f8ef7">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><OfficeBuilding /></el-icon></div>
        <div><p class="sum-label">総寮数</p><p class="sum-value">{{ activeTotal }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#38bdf8">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><HomeFilled /></el-icon></div>
        <div><p class="sum-label">総部屋数</p><p class="sum-value">{{ totalRooms }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#22c55e">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><UserFilled /></el-icon></div>
        <div><p class="sum-label">男性寮数</p><p class="sum-value">{{ maleCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#a78bfa">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><UserFilled /></el-icon></div>
        <div><p class="sum-label">女性寮数</p><p class="sum-value">{{ femaleCount }}</p></div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">寮一覧</span>
          <span class="panel-badge">DORMITORY MASTER</span>
        </div>
        <button class="btn-primary btn-sm" @click="openDialog()">
          <span class="btn-shimmer"></span>
          <el-icon><Plus /></el-icon>
          <span>新規登録</span>
        </button>
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
        <el-table-column label="寮名" prop="name" min-width="130" />
        <el-table-column label="状態" prop="status" width="90" align="center">
          <template #default="{ row }">
            <span class="badge" :class="row.status === 'inactive' ? 'badge-inactive' : 'badge-active'">
              {{ row.status === 'inactive' ? '退却' : '有効' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="種別" prop="type" width="100" align="center">
          <template #default="{ row }">
            <span class="badge" :class="typeClass(row.type)">{{ typeLabel(row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="都道府県" prop="prefecture" width="110" align="center">
          <template #default="{ row }">
            <span v-if="row.prefecture" class="badge badge-pref">{{ row.prefecture }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="住所" prop="address" min-width="200">
          <template #default="{ row }">
            <span :class="row.address ? '' : 'cell-empty'">{{ row.address || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="総部屋数" prop="totalRooms" width="100" align="center" />
        <el-table-column label="部屋別寮費" min-width="240">
          <template #default="{ row }">
            <div v-if="row.roomRents && row.roomRents.length > 0" class="room-rents-cell">
              <span
                v-for="r in row.roomRents"
                :key="r.name"
                class="room-rent-chip"
              >{{ r.name }}：{{ r.rent != null ? r.rent.toLocaleString() + '円' : '－' }}</span>
            </div>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="備考" prop="remark" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span :class="row.remark ? 'cell-muted' : 'cell-empty'">{{ row.remark || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="$router.push(`/dormitories/${row.id}/rooms`)">部屋一覧</button>
            <button class="link-btn" @click="openDialog(row)">編集</button>
            <button
              class="link-btn link-btn--danger"
              :disabled="row.status === 'inactive'"
              @click="handleDelete(row)"
            >退却</button>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="loading" />
          <el-empty v-else description="データなし" :image-size="60" />
        </template>
      </el-table>

    </div>

    <!-- ━━ 登録・編集ダイアログ ━━ -->
    <el-dialog
      v-model="dialogVisible"
      :title="editRow ? '寮編集' : '新規寮登録'"
      width="520px"
      destroy-on-close
      class="dark-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="寮名" prop="name">
          <el-input v-model="form.name" placeholder="例：東京第1寮" class="dark-input" />
        </el-form-item>
        <el-form-item label="種別" prop="type">
          <div class="radio-group">
            <button
              v-for="opt in typeOptions"
              :key="opt.value"
              class="radio-btn"
              :class="{ 'radio-btn--active': form.type === opt.value }"
              @click="form.type = opt.value"
              type="button"
            >{{ opt.label }}</button>
          </div>
        </el-form-item>
        <el-form-item label="都道府県" prop="prefecture">
          <AppSelect
            v-model="form.prefecture"
            :options="prefectureOptions"
            placeholder="都道府県を選択"
            filterable
            clearable
          />
        </el-form-item>
        <el-form-item label="住所" prop="address">
          <el-input v-model="form.address" placeholder="例：渋谷区道玄坂1-1-1" class="dark-input" />
        </el-form-item>
        <el-form-item label="間取り" prop="layout">
          <AppSelect
            v-model="form.layout"
            :options="layoutOptions"
            placeholder="間取りを選択"
            clearable
          />
        </el-form-item>
        <el-form-item label="備考">
          <el-input v-model="form.remark" type="textarea" :rows="3" class="dark-input" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-ghost" @click="dialogVisible = false">キャンセル</button>
          <button class="btn-primary" :disabled="saving" @click="handleSave">
            <span class="btn-shimmer"></span>
            <el-icon v-if="saving" class="is-loading"><Loading /></el-icon>
            <span>{{ saving ? '保存中...' : '保存' }}</span>
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDormitoryList, createDormitory, updateDormitory, deleteDormitory } from '@/api/dormitory'
import { getCodeList } from '@/api/codeMaster'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editRow = ref(null)
const formRef = ref()

const query = reactive({ name: '', type: '', prefecture: '', status: '', page: 1, pageSize: 10 })

const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const goToPage = (page) => { query.page = page; fetchList() }
const onPageSizeChange = () => { query.page = 1; fetchList() }
const form = reactive({ name: '', type: '男', prefecture: '', address: '', layout: '', remark: '' })

const typeOptions = [
  { label: '男性寮', value: '男' },
  { label: '女性寮', value: '女' },
]

const prefectureOptions = ref([])
const layoutOptions = ref([])

const activeList  = computed(() => list.value.filter(r => r.status !== 'inactive'))
const activeTotal = computed(() => activeList.value.length)
const totalRooms  = computed(() => activeList.value.reduce((s, r) => s + (r.totalRooms || 0), 0))
const maleCount   = computed(() => activeList.value.filter(r => r.type === '男').length)
const femaleCount = computed(() => activeList.value.filter(r => r.type === '女').length)

const typeLabel = (t) => ({ '男': '男性寮', '女': '女性寮' }[t] || t)
const typeClass  = (t) => ({ '男': 'badge-blue', '女': 'badge-pink' }[t] || 'badge-gray')

const rules = {
  name:       [{ required: true, message: '寮名を入力してください',       trigger: 'blur' }],
  type:       [{ required: true, message: '種別を選択してください',       trigger: 'change' }],
  prefecture: [{ required: true, message: '都道府県を選択してください',   trigger: 'change' }],
  address:    [{ required: true, message: '住所を入力してください',       trigger: 'blur' }],
}

const fetchList = async () => {
  loading.value = true
  try {
    const data = await getDormitoryList(query)
    list.value = data.list
    total.value = data.total
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { name: '', type: '', prefecture: '', status: '', page: 1 })
  fetchList()
}

const openDialog = (row = null) => {
  editRow.value = row
  Object.assign(form, row ? { ...row } : { name: '', type: '男', prefecture: '', address: '', layout: '', remark: '' })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editRow.value) {
      await updateDormitory(editRow.value.id, form)
      ElMessage.success('更新しました')
    } else {
      await createDormitory(form)
      ElMessage.success('登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(
    `「${row.name}」を退却状態に変更しますか？<br>退却後も検索で確認できます。`,
    '退却確認',
    { type: 'warning', dangerouslyUseHTMLString: true }
  )
  try {
    await deleteDormitory(row.id, { _suppressToast: true })
    ElMessage.success('退却状態に変更しました')
    fetchList()
  } catch (err) {
    const msg = err.response?.data?.message || err.message || '退却状態に変更できませんでした'
    ElMessageBox.alert(msg, '変更できません', { type: 'error', confirmButtonText: 'OK' })
  }
}

onMounted(() => {
  fetchList()
  getCodeList('PREFECTURE').then(data => {
    prefectureOptions.value = data.map(d => ({ label: d.name, value: d.code }))
  })
  getCodeList('LAYOUT').then(data => {
    layoutOptions.value = data.map(d => ({ label: d.name, value: d.code }))
  })
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
.field-group { display: flex; flex-direction: column; gap: 6px; min-width: 180px; }
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.search-actions { display: flex; gap: 8px; align-items: center; }

/* 入力欄 */
:deep(.dark-input .el-input__wrapper),
:deep(.dark-input.el-textarea .el-textarea__inner) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px;
  transition: box-shadow 0.2s;
  color: #ffffff;
}
:deep(.dark-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(255,255,255,0.24) inset;
}
:deep(.dark-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4f8ef7 inset, 0 0 10px rgba(79,142,247,0.2);
}
:deep(.dark-input .el-input__inner) { color: #ffffff; caret-color: #4f8ef7; }
:deep(.dark-input .el-input__inner::placeholder),
:deep(.dark-input.el-textarea .el-textarea__inner::placeholder) { color: rgba(255,255,255,0.28); }
:deep(.dark-input .el-input__prefix-icon),
:deep(.dark-input .el-input__suffix-icon),
:deep(.dark-input .el-select__caret) { color: rgba(255,255,255,0.38); }
:deep(.dark-input.el-textarea .el-textarea__inner) {
  caret-color: #4f8ef7;
  resize: none;
}

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
  transition: box-shadow 0.2s, opacity 0.2s;
  white-space: nowrap;
}
.btn-primary:hover { box-shadow: 0 6px 20px rgba(79,142,247,0.5); }
.btn-primary:disabled { opacity: 0.65; cursor: not-allowed; }
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

/* ━━ パネルヘッダー ━━ */
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-title { color: #ffffff; font-size: 14px; font-weight: 600; margin-right: 10px; }
.panel-badge { color: rgba(255,255,255,0.28); font-size: 10px; font-weight: 600; letter-spacing: 0.12em; }

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

/* ━━ 部屋別寮費 ━━ */
.room-rents-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 2px 0;
}
.room-rent-chip {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(79, 142, 247, 0.12);
  border: 1px solid rgba(79, 142, 247, 0.28);
  border-radius: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.78);
  white-space: nowrap;
}

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-blue     { background: rgba(79,142,247,0.2);  color: #93c5fd; }
.badge-pink     { background: rgba(244,114,182,0.2); color: #f9a8d4; }
.badge-purple   { background: rgba(167,139,250,0.2); color: #c4b5fd; }
.badge-gray     { background: rgba(255,255,255,0.08); color: rgba(255,255,255,0.45); }
.badge-pref     { background: rgba(34,197,94,0.12); color: #4ade80; border: 1px solid rgba(34,197,94,0.25); }
.badge-active   { background: rgba(34,197,94,0.15); color: #4ade80; border: 1px solid rgba(34,197,94,0.25); }
.badge-inactive { background: rgba(248,113,113,0.15); color: #fca5a5; border: 1px solid rgba(248,113,113,0.25); }

/* ━━ 操作ボタン ━━ */
.link-btn {
  background: none;
  border: none;
  color: #4f8ef7;
  font-size: 12px;
  cursor: pointer;
  padding: 0 5px;
  transition: opacity 0.15s;
}
.link-btn:hover:not(:disabled) { opacity: 0.7; }
.link-btn--danger { color: #f87171; }
.link-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ━━ ページネーション ━━ */
.pagination-row {
  display: flex; align-items: center; justify-content: flex-end;
  gap: 8px; margin-bottom: 12px; padding-bottom: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%;
}
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 28px; height: 28px; padding: 0 6px;
  background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12);
  border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600;
  cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap;
}
.pg-btn:hover:not(:disabled) { background: rgba(79,142,247,0.18); color: #4f8ef7; border-color: rgba(79,142,247,0.35); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ━━ ダークダイアログ ━━ */
:deep(.dark-dialog .el-dialog) {
  background: #0f1d3d;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
}
:deep(.dark-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(255,255,255,0.08);
  padding: 18px 20px 14px;
}
:deep(.dark-dialog .el-dialog__title) {
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
}
:deep(.dark-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: rgba(255,255,255,0.45);
}
:deep(.dark-dialog .el-dialog__body) { padding: 20px; }
:deep(.dark-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255,255,255,0.08);
  padding: 14px 20px;
}
:deep(.dark-dialog .el-form-item__label) {
  color: rgba(255,255,255,0.55);
  font-size: 13px;
}
:deep(.dark-dialog .el-form-item__error) { color: #f87171; }

/* ダイアログフッター */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 種別ラジオボタン */
.radio-group {
  display: flex;
  gap: 8px;
}
.radio-btn {
  padding: 6px 14px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.14);
  border-radius: 6px;
  color: rgba(255,255,255,0.5);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.15s;
}
.radio-btn:hover { color: rgba(255,255,255,0.8); background: rgba(255,255,255,0.08); }
.radio-btn--active {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent;
  color: #ffffff;
  font-weight: 500;
}
</style>
