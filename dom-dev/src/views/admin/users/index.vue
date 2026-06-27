<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">ユーザー名 / 氏名</label>
          <el-input v-model="query.keyword" placeholder="ユーザー名・氏名で検索" clearable class="dark-input" />
        </div>
        <div class="field-group">
          <label class="field-label">ロール</label>
          <AppSelect
            v-model="query.role"
            :options="[
              { label: 'すべて',   value: '' },
              { label: '管理者',   value: 'admin' },
              { label: 'スタッフ', value: 'staff' },
              { label: '閲覧者',   value: 'viewer' },
            ]"
            clearable
            placeholder="すべて"
          />
        </div>
        <div class="field-group">
          <label class="field-label">状態</label>
          <AppSelect
            v-model="query.active"
            :options="[
              { label: 'すべて', value: '' },
              { label: '有効',   value: 'true' },
              { label: '無効',   value: 'false' },
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
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><UserFilled /></el-icon></div>
        <div><p class="sum-label">総ユーザー数</p><p class="sum-value">{{ total }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#22c55e">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><CircleCheckFilled /></el-icon></div>
        <div><p class="sum-label">有効</p><p class="sum-value">{{ activeCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#a78bfa">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Lock /></el-icon></div>
        <div><p class="sum-label">管理者</p><p class="sum-value">{{ adminCount }}</p></div>
      </div>
      <div class="sum-card" style="--accent:#f59e0b">
        <div class="sum-icon-wrap"><div class="sum-halo"></div><el-icon class="sum-icon"><Warning /></el-icon></div>
        <div><p class="sum-label">無効</p><p class="sum-value">{{ inactiveCount }}</p></div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">ユーザー一覧</span>
          <span class="panel-badge">USER MANAGEMENT</span>
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

      <el-table :data="loading ? [] : pagedList" row-key="id" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="ユーザー名" prop="username" min-width="130" />
        <el-table-column label="氏名" prop="name" min-width="120" />
        <el-table-column label="ロール" prop="role" width="120" align="center">
          <template #default="{ row }">
            <span class="badge" :class="roleClass(row.role)">{{ roleLabel(row.role) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状態" prop="active" width="90" align="center">
          <template #default="{ row }">
            <span class="badge" :class="row.active ? 'badge-green' : 'badge-red'">
              {{ row.active ? '有効' : '無効' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="openDialog(row)">編集</button>
            <button class="link-btn link-btn--danger" @click="handleDelete(row)">削除</button>
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
      :title="editRow ? 'ユーザー編集' : '新規ユーザー登録'"
      width="480px"
      destroy-on-close
      class="dark-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="dialog-form">
        <el-form-item label="ユーザー名" prop="username">
          <el-input v-model="form.username" :disabled="!!editRow" class="dark-input" />
        </el-form-item>
        <el-form-item label="氏名" prop="name">
          <el-input v-model="form.name" class="dark-input" />
        </el-form-item>
        <el-form-item v-if="!editRow" label="パスワード" prop="password">
          <el-input v-model="form.password" type="password" show-password class="dark-input" />
        </el-form-item>
        <el-form-item label="ロール" prop="role">
          <div class="radio-group">
            <button
              v-for="opt in roleOptions"
              :key="opt.value"
              class="radio-btn"
              :class="{ 'radio-btn--active': form.role === opt.value }"
              type="button"
              @click="form.role = opt.value"
            >{{ opt.label }}</button>
          </div>
        </el-form-item>
        <el-form-item v-if="editRow" label="状態">
          <div class="radio-group">
            <button
              class="radio-btn"
              :class="[{ 'radio-btn--active': form.active }, { 'radio-btn--green': form.active }]"
              type="button"
              @click="form.active = true"
            >有効</button>
            <button
              class="radio-btn"
              :class="[{ 'radio-btn--active': !form.active }, { 'radio-btn--red': !form.active }]"
              type="button"
              @click="form.active = false"
            >無効</button>
          </div>
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
import { Loading } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const saving  = ref(false)
const list    = ref([])
const total   = computed(() => list.value.length)
const dialogVisible = ref(false)
const editRow = ref(null)
const formRef = ref()

const query = reactive({ keyword: '', role: '', active: '', page: 1, pageSize: 10 })
const pageSizeOptions = [5, 10, 20, 50, 100]
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const pagedList  = computed(() => list.value.slice((query.page - 1) * query.pageSize, query.page * query.pageSize))
const goToPage = (page) => { query.page = page }
const onPageSizeChange = () => { query.page = 1 }
const form  = reactive({ username: '', name: '', password: '', role: 'staff', active: true })

const roleOptions = [
  { label: '管理者',   value: 'admin'  },
  { label: 'スタッフ', value: 'staff'  },
  { label: '閲覧者',   value: 'viewer' },
]

const activeCount   = computed(() => list.value.filter(u => u.active).length)
const inactiveCount = computed(() => list.value.filter(u => !u.active).length)
const adminCount    = computed(() => list.value.filter(u => u.role === 'admin').length)

const roleLabel = (r) => ({ admin: '管理者', staff: 'スタッフ', viewer: '閲覧者' }[r] || r)
const roleClass = (r) => ({ admin: 'badge-purple', staff: 'badge-blue', viewer: 'badge-gray' }[r] || 'badge-gray')

const rules = {
  username: [{ required: true, message: 'ユーザー名を入力してください', trigger: 'blur' }],
  name:     [{ required: true, message: '氏名を入力してください',       trigger: 'blur' }],
  password: [{
    required: true, trigger: 'blur',
    validator: (_, v, cb) => !editRow.value && !v ? cb(new Error('パスワードを入力してください')) : cb(),
  }],
  role: [{ required: true, message: 'ロールを選択してください', trigger: 'change' }],
}

const fetchList = async () => {
  loading.value = true
  query.page = 1
  try {
    const { keyword, role, active } = query
    const data = await getUserList({ keyword, role, active })
    list.value = Array.isArray(data) ? data : (data.list || [])
  } finally { loading.value = false }
}

const resetQuery = () => {
  Object.assign(query, { keyword: '', role: '', active: '', page: 1 })
  fetchList()
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) Object.assign(form, { ...row, password: '' })
  else Object.assign(form, { username: '', name: '', password: '', role: 'staff', active: true })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editRow.value) {
      await updateUser(editRow.value.id, form)
      ElMessage.success('更新しました')
    } else {
      await createUser(form)
      ElMessage.success('登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } catch {
    // エラーは request.js のインターセプターで表示済み
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`「${row.name}」を削除しますか？`, '削除確認', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteUser(row.id)
    ElMessage.success('削除しました')
    fetchList()
  } catch {
    // エラーは request.js のインターセプターで表示済み
  }
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
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px; font-weight: 600;
  letter-spacing: 0.12em; text-transform: uppercase;
}
.search-actions { display: flex; gap: 8px; align-items: center; }

/* ━━ ダーク入力 ━━ */
:deep(.dark-input .el-input__wrapper),
:deep(.dark-input.el-textarea .el-textarea__inner) {
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
:deep(.dark-input.el-textarea .el-textarea__inner) { caret-color: #4f8ef7; resize: none; color: #ffffff; }
:deep(.dark-input.el-textarea .el-textarea__inner::placeholder) { color: rgba(255,255,255,0.28); }

/* ━━ ボタン ━━ */
.btn-primary {
  position: relative;
  display: inline-flex; align-items: center; gap: 6px;
  padding: 9px 18px;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: none; border-radius: 8px;
  color: #ffffff; font-size: 13px; font-weight: 500;
  cursor: pointer; overflow: hidden;
  box-shadow: 0 4px 14px rgba(79,142,247,0.35);
  transition: box-shadow 0.2s, opacity 0.2s;
  white-space: nowrap;
}
.btn-primary:hover { box-shadow: 0 6px 20px rgba(79,142,247,0.5); }
.btn-primary:disabled { opacity: 0.65; cursor: not-allowed; }
.btn-sm { padding: 7px 14px; font-size: 12px; }
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
  border-radius: 12px;
  padding: 20px;
  display: flex; align-items: center; gap: 16px;
  position: relative; overflow: hidden;
}
.sum-card::before {
  content: '';
  position: absolute; top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  opacity: 0.65;
}
.sum-icon-wrap { position: relative; flex-shrink: 0; }
.sum-halo {
  position: absolute; inset: -8px;
  border-radius: 50%;
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

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px; font-weight: 600;
  letter-spacing: 0.04em;
}
.badge-purple { background: rgba(167,139,250,0.18); color: #c4b5fd; border: 1px solid rgba(167,139,250,0.3); }
.badge-blue   { background: rgba(79,142,247,0.18);  color: #93c5fd; border: 1px solid rgba(79,142,247,0.3);  }
.badge-gray   { background: rgba(255,255,255,0.07); color: rgba(255,255,255,0.45); border: 1px solid rgba(255,255,255,0.12); }
.badge-green  { background: rgba(34,197,94,0.15);   color: #86efac; border: 1px solid rgba(34,197,94,0.3);   }
.badge-red    { background: rgba(248,113,113,0.15); color: #fca5a5; border: 1px solid rgba(248,113,113,0.3); }

/* ━━ 操作ボタン ━━ */
.link-btn {
  background: none; border: none;
  color: #4f8ef7; font-size: 12px;
  cursor: pointer; padding: 0 5px;
  transition: opacity 0.15s;
}
.link-btn:hover { opacity: 0.7; }
.link-btn--danger { color: #f87171; }

/* ━━ ページネーション ━━ */
.pagination-row { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%; }
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; height: 28px; padding: 0 6px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
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
:deep(.dark-dialog .el-dialog__title) { color: #ffffff; font-size: 15px; font-weight: 600; }
:deep(.dark-dialog .el-dialog__headerbtn .el-dialog__close) { color: rgba(255,255,255,0.45); }
:deep(.dark-dialog .el-dialog__body) { padding: 20px; }
:deep(.dark-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255,255,255,0.08); padding: 14px 20px;
}
:deep(.dark-dialog .el-form-item__label) { color: rgba(255,255,255,0.55); font-size: 13px; }
:deep(.dark-dialog .el-form-item__error) { color: #f87171; }

.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }

/* ━━ ラジオボタン ━━ */
.radio-group { display: flex; gap: 8px; flex-wrap: wrap; }
.radio-btn {
  padding: 6px 14px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.14);
  border-radius: 6px;
  color: rgba(255,255,255,0.5); font-size: 13px;
  cursor: pointer; transition: all 0.15s;
}
.radio-btn:hover { color: rgba(255,255,255,0.8); background: rgba(255,255,255,0.08); }
.radio-btn--active {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border-color: transparent; color: #ffffff; font-weight: 500;
}
.radio-btn--active.radio-btn--green {
  background: linear-gradient(135deg, #16a34a, #22c55e);
}
.radio-btn--active.radio-btn--red {
  background: linear-gradient(135deg, #b91c1c, #ef4444);
}
</style>
