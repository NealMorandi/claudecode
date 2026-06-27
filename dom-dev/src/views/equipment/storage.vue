<template>
  <div class="page">

    <!-- ━━ 検索パネル ━━ -->
    <div class="glass-panel search-panel">
      <div class="search-form">
        <div class="field-group">
          <label class="field-label">備品名</label>
          <AppSelect
            v-model="query.equipmentId"
            :options="equipmentOptions"
            placeholder="備品名で絞り込み"
            clearable
            @change="query.page = 1"
          />
        </div>
        <div class="field-group">
          <label class="field-label">種別</label>
          <AppSelect
            v-model="query.category"
            :options="categoryOptions"
            placeholder="種別で絞り込み"
            clearable
            @change="query.page = 1"
          />
        </div>
        <div class="field-group">
          <label class="field-label">保管場所</label>
          <AppSelect
            v-model="query.location"
            :options="dormitoryOptions"
            placeholder="寮名で絞り込み"
            clearable
            @change="query.page = 1"
          />
        </div>
        <div class="field-group">
          <label class="field-label">状態</label>
          <AppSelect
            v-model="query.condition"
            :options="conditionOptions"
            placeholder="状態で絞り込み"
            clearable
            @change="query.page = 1"
          />
        </div>
        <div class="search-actions">
          <button class="btn-primary" @click="fetchList">
            <span class="btn-shimmer"></span>
            <el-icon><Search /></el-icon>
            <span>検索</span>
          </button>
        </div>
      </div>
    </div>

    <!-- ━━ サマリーカード ━━ -->
    <div class="summary-grid">
      <div class="sum-card" style="--accent:#4f8ef7">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Box /></el-icon>
        </div>
        <div>
          <p class="sum-label">総備品数</p>
          <p class="sum-value">{{ totalQuantity }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#22d3ee">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><OfficeBuilding /></el-icon>
        </div>
        <div>
          <p class="sum-label">保管場所数</p>
          <p class="sum-value">{{ locationCount }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#a78bfa">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Grid /></el-icon>
        </div>
        <div>
          <p class="sum-label">種別数</p>
          <p class="sum-value">{{ categoryCount }}</p>
        </div>
      </div>
      <div class="sum-card" style="--accent:#f59e0b">
        <div class="sum-icon-wrap">
          <div class="sum-halo"></div>
          <el-icon class="sum-icon"><Warning /></el-icon>
        </div>
        <div>
          <p class="sum-label">要確認</p>
          <p class="sum-value">{{ needCheckCount }}</p>
        </div>
      </div>
    </div>

    <!-- ━━ テーブルパネル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">備品保管一覧</span>
          <span class="panel-badge">EQUIPMENT STORAGE</span>
        </div>
        <button class="btn-primary btn-sm" @click="openRegister">
          <span class="btn-shimmer"></span>
          <el-icon><Plus /></el-icon>
          <span>新規備品登録</span>
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

      <el-table :data="loading ? [] : pagedList" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center"
          :index="(i) => (query.page - 1) * query.pageSize + i + 1" />
        <el-table-column label="備品名" prop="equipmentName" min-width="140" />
        <el-table-column label="種別" prop="category" width="100" align="center">
          <template #default="{ row }">
            <span class="category-text">{{ categoryLabel(row.category) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="保管場所" prop="storageLocation" min-width="140">
          <template #default="{ row }">
            <span :class="row.storageLocation ? '' : 'cell-empty'">
              {{ row.storageLocation || '未設定' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="部屋" prop="roomName" min-width="110">
          <template #default="{ row }">
            <span :class="row.roomName ? '' : 'cell-empty'">{{ row.roomName || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="個数" prop="quantity" width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.quantity ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="関連社員" prop="employeeName" min-width="120">
          <template #default="{ row }">
            <span :class="row.employeeName ? '' : 'cell-empty'">{{ row.employeeName || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状態" width="110" align="center">
          <template #default="{ row }">
            <span class="badge" :class="statusClass(row)">{{ statusLabel(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center" fixed="right">
          <template #default="{ row }">
            <button class="link-btn" @click="openEdit(row)">編集</button>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="loading" />
          <el-empty v-else description="データなし" :image-size="60" />
        </template>
      </el-table>

    </div>

    <!-- ━━ 備品登録 / 備品状態管理ダイアログ ━━ -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="460px"
      destroy-on-close
      class="dark-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="備品名" prop="equipmentId">
          <AppSelect
            v-model="form.equipmentId"
            :options="equipmentOptions"
            placeholder="備品マスタから選択"
            @change="onEquipmentChange"
          />
        </el-form-item>
        <el-form-item label="メーカー">
          <AppSelect
            v-model="form.maker"
            :options="makerOptions"
            clearable
            placeholder="メーカーを選択"
          />
        </el-form-item>
        <el-form-item label="種別">
          <div class="radio-group">
            <button
              v-for="opt in categoryOptions"
              :key="opt.value"
              class="radio-btn"
              :class="{ 'radio-btn--active': form.category === opt.value }"
              type="button"
              @click="form.category = opt.value"
            >{{ opt.label }}</button>
          </div>
        </el-form-item>
        <el-form-item label="状態">
          <div class="radio-group">
            <button
              v-for="opt in conditionOptions"
              :key="opt.value"
              class="radio-btn"
              :class="{ 'radio-btn--active': form.equipmentCondition === opt.value }"
              type="button"
              @click="form.equipmentCondition = opt.value"
            >{{ opt.label }}</button>
          </div>
        </el-form-item>
        <el-form-item label="保管場所">
          <AppSelect
            v-model="form.storageLocation"
            :options="dormitoryOptions"
            clearable
            placeholder="寮名を選択"
            @change="onDormitoryChange"
          />
        </el-form-item>
        <el-form-item label="部屋">
          <AppSelect
            v-model="form.roomId"
            :options="roomOptions"
            clearable
            placeholder="部屋を選択"
            @change="onRoomChange"
          />
        </el-form-item>
        <el-form-item label="個数" prop="quantity">
          <el-input-number
            v-model="form.quantity"
            :min="1"
            :max="999"
            class="dark-input"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="社員">
          <AppSelect
            v-model="form.employeeId"
            :options="employeeOptions"
            clearable
            placeholder="保管場所を選択すると入居者が表示されます"
          />
        </el-form-item>
        <el-form-item label="備考">
          <el-input v-model="form.remark" type="textarea" :rows="2" class="dark-input" />
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
import { ElMessage } from 'element-plus'
import { getStorageList, getEquipmentList, registerEquipmentStorage, updateEquipmentHistory } from '@/api/equipment'
import { getDormitoryList, getRoomResidents } from '@/api/dormitory'
import { getAllRooms } from '@/api/room'
import AppSelect from '@/components/AppSelect.vue'
import TableLoadState from '@/components/TableLoadState.vue'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const query = reactive({ equipmentId: null, category: '', condition: '', location: '', page: 1, pageSize: 10 })
const pageSizeOptions = [5, 10, 20, 50, 100]

const filteredList = computed(() => list.value.filter(i => {
  if (query.equipmentId && i.equipmentId !== query.equipmentId) return false
  if (query.category  && i.category !== query.category) return false
  if (query.condition && i.equipmentCondition !== query.condition) return false
  if (query.location  && i.storageLocation !== query.location) return false
  return true
}))

const total     = computed(() => filteredList.value.length)
const totalPages = computed(() => Math.ceil(total.value / query.pageSize) || 1)
const pagedList  = computed(() => filteredList.value.slice((query.page - 1) * query.pageSize, query.page * query.pageSize))
const goToPage = (page) => { query.page = page }
const onPageSizeChange = () => { query.page = 1 }

const totalQuantity = computed(() =>
  filteredList.value.reduce((sum, i) => sum + (i.quantity ?? 0), 0)
)
const locationCount = computed(() =>
  new Set(filteredList.value.map(i => i.storageLocation).filter(Boolean)).size
)
const categoryCount = computed(() =>
  new Set(filteredList.value.map(i => i.category).filter(Boolean)).size
)
const needCheckCount = computed(() =>
  filteredList.value.filter(i => !i.equipmentCondition).length
)

const CATEGORY_LABELS = { appliance: '家電', furniture: '家具', bedding: '寝具', other: 'その他' }
const categoryLabel = (val) => CATEGORY_LABELS[val] || val || '—'

const CONDITION_LABELS = { good: '良好', needs_repair: '要修理', disposed: '廃棄', lost: '紛失' }
const CONDITION_CLASS  = { good: 'badge-teal', needs_repair: 'badge-amber', disposed: 'badge-gray', lost: 'badge-purple' }
const statusLabel = (row) => CONDITION_LABELS[row.equipmentCondition] ?? '未設定'
const statusClass = (row) => CONDITION_CLASS[row.equipmentCondition]  ?? 'badge-gray'

const fetchList = async () => {
  loading.value = true
  query.page = 1
  try {
    const data = await getStorageList({})
    list.value = data.list || data
  } finally { loading.value = false }
}

// ━━ ダイアログ共通 ━━
const dialogMode = ref('create')
const editingHistoryId = ref(null)
const dialogTitle = computed(() => dialogMode.value === 'edit' ? '備品状態管理' : '新規備品登録')
const dialogVisible = ref(false)
const formRef = ref()
const allEquipment        = ref([])
const allDormitories      = ref([])
const allRooms            = ref([])
const dormitoryResidents  = ref([])
const form = reactive({ equipmentId: null, category: '', maker: '', equipmentCondition: 'good', storageLocation: '', roomId: null, employeeId: null, quantity: 1, remark: '' })

const categoryOptions = [
  { label: '家電', value: 'appliance' },
  { label: '家具', value: 'furniture' },
  { label: '寝具', value: 'bedding' },
  { label: 'その他', value: 'other' },
]

const conditionOptions = [
  { label: '良好', value: 'good' },
  { label: '要修理', value: 'needs_repair' },
  { label: '廃棄', value: 'disposed' },
  { label: '紛失', value: 'lost' },
]

const equipmentOptions = computed(() =>
  allEquipment.value.map(e => ({ label: e.name, value: e.id }))
)

const dormitoryOptions = computed(() =>
  allDormitories.value.map(d => ({ label: d.name, value: d.name }))
)

const roomOptions = computed(() => {
  const src = form.storageLocation
    ? allRooms.value.filter(r => r.dormitoryName === form.storageLocation)
    : allRooms.value
  return src.map(r => ({ label: r.name, value: r.id }))
})

const makerOptions = computed(() => {
  const makers = [...new Set(allEquipment.value.map(e => e.maker).filter(Boolean))]
  return makers.map(m => ({ label: m, value: m }))
})

const onEquipmentChange = (id) => {
  const eq = allEquipment.value.find(e => e.id === id)
  form.category = eq ? eq.category : ''
  form.maker    = eq ? (eq.maker || '') : ''
}

const employeeOptions = computed(() => {
  let residents = dormitoryResidents.value.filter(r => r.employeeId != null)
  if (form.roomId) residents = residents.filter(r => r.roomId === form.roomId)
  return residents.map(r => ({ label: r.employeeName, value: r.employeeId }))
})

const onDormitoryChange = async () => {
  form.roomId = null
  form.employeeId = null
  dormitoryResidents.value = []
  const dorm = allDormitories.value.find(d => d.name === form.storageLocation)
  if (dorm) {
    dormitoryResidents.value = await getRoomResidents(dorm.id).catch(() => [])
  }
}

const onRoomChange = (id) => {
  const room = allRooms.value.find(r => r.id === id)
  if (room) form.storageLocation = room.dormitoryName
  form.employeeId = null
}

const rules = {
  equipmentId: [{ required: true, message: '備品を選択してください', trigger: 'change' }],
}

const resetForm = () => {
  Object.assign(form, { equipmentId: null, category: '', maker: '', equipmentCondition: 'good', storageLocation: '', roomId: null, employeeId: null, quantity: 1, remark: '' })
  dormitoryResidents.value = []
}

const openRegister = () => {
  dialogMode.value = 'create'
  editingHistoryId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  dialogMode.value = 'edit'
  editingHistoryId.value = row.historyId
  // 保管場所が選択されたら入居者リストを取得
  dormitoryResidents.value = []
  if (row.storageLocation) {
    const dorm = allDormitories.value.find(d => d.name === row.storageLocation)
    if (dorm) dormitoryResidents.value = await getRoomResidents(dorm.id).catch(() => [])
  }
  Object.assign(form, {
    equipmentId:        row.equipmentId  ?? null,
    category:           row.category     ?? '',
    maker:              row.maker        ?? '',
    equipmentCondition: row.equipmentCondition ?? '',
    storageLocation:    row.storageLocation    ?? '',
    roomId:             row.roomId       ?? null,
    employeeId:         row.employeeId   ?? null,
    quantity:           row.quantity     ?? 1,
    remark:             row.remark       ?? '',
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    const payload = {
      equipmentId:        form.equipmentId,
      roomId:             form.roomId,
      employeeId:         form.employeeId,
      equipmentCondition: form.equipmentCondition,
      quantity:           form.quantity,
      storageLocation:    form.storageLocation,
      remark:             form.remark,
    }
    if (dialogMode.value === 'edit') {
      await updateEquipmentHistory(editingHistoryId.value, payload)
      ElMessage.success('更新しました')
    } else {
      await registerEquipmentStorage(form.equipmentId, payload)
      ElMessage.success('登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } finally { saving.value = false }
}

onMounted(async () => {
  fetchList()
  const [equip, dorm, rooms] = await Promise.all([
    getEquipmentList({ pageSize: 1000 }).then(d => d.list || []).catch(() => []),
    getDormitoryList({ pageSize: 1000 }).then(d => d.list || []).catch(() => []),
    getAllRooms().catch(() => []),
  ])
  allEquipment.value   = equip
  allDormitories.value = dorm
  allRooms.value       = rooms
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
  min-width: 180px;
}
.field-label {
  color: rgba(255,255,255,0.38);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}
.search-actions { display: flex; gap: 8px; align-items: center; }

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
:deep(.dark-input .el-input__suffix-icon) { color: rgba(255,255,255,0.38); }

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
.panel-badge {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}

.btn-sm { padding: 7px 14px; font-size: 12px; }

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

/* 保管場所インライン編集 */
.location-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}
.inline-edit { display: flex; align-items: center; }
.inline-input { width: 130px; }
:deep(.inline-input.el-input--small .el-input__wrapper) {
  background: rgba(255,255,255,0.08);
  box-shadow: 0 0 0 1px #4f8ef7 inset;
  border-radius: 6px;
}
:deep(.inline-input.el-input--small .el-input__inner) { color: #fff; font-size: 12px; }

.icon-btn {
  background: none;
  border: none;
  color: rgba(255,255,255,0.3);
  cursor: pointer;
  padding: 2px 4px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  transition: color 0.15s, background 0.15s;
}
.icon-btn:hover { color: #4f8ef7; background: rgba(79,142,247,0.1); }

.category-text { color: rgba(255,255,255,0.55); }
.cell-empty { color: rgba(255,255,255,0.25); }

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
}
.badge-teal   { background: rgba(34,211,238,0.18);  color: #67e8f9; border: 1px solid rgba(34,211,238,0.3); }
.badge-purple { background: rgba(167,139,250,0.18); color: #c4b5fd; border: 1px solid rgba(167,139,250,0.3); }
.badge-amber  { background: rgba(245,158,11,0.18);  color: #fcd34d; border: 1px solid rgba(245,158,11,0.3); }
.badge-blue   { background: rgba(59,130,246,0.22);  color: #93c5fd; border: 1px solid rgba(59,130,246,0.35); }
.badge-gray   { background: rgba(148,163,184,0.15); color: #94a3b8; border: 1px solid rgba(148,163,184,0.25); }

/* ━━ 操作ボタン ━━ */
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

/* ━━ ゴーストボタン ━━ */
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

/* ━━ ダークダイアログ ━━ */
:deep(.dark-dialog .el-dialog) {
  background: #0f1d3d;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
}
:deep(.dark-dialog .el-dialog__header) { border-bottom: 1px solid rgba(255,255,255,0.08); padding: 18px 20px 14px; }
:deep(.dark-dialog .el-dialog__title) { color: #ffffff; font-size: 15px; font-weight: 600; }
:deep(.dark-dialog .el-dialog__headerbtn .el-dialog__close) { color: rgba(255,255,255,0.45); }
:deep(.dark-dialog .el-dialog__body) { padding: 20px; }
:deep(.dark-dialog .el-dialog__footer) { border-top: 1px solid rgba(255,255,255,0.08); padding: 14px 20px; }
:deep(.dark-dialog .el-form-item__label) { color: rgba(255,255,255,0.55); font-size: 13px; }
:deep(.dark-dialog .el-form-item__error) { color: #f87171; }
:deep(.dark-input .el-input__wrapper),
:deep(.dark-input.el-textarea .el-textarea__inner) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}
:deep(.dark-input.el-textarea .el-textarea__inner) { color: #ffffff; caret-color: #4f8ef7; resize: none; }
:deep(.dark-input.el-textarea .el-textarea__inner::placeholder) { color: rgba(255,255,255,0.28); }

.dialog-footer { display: flex; justify-content: flex-end; gap: 10px; }

/* ━━ 個数入力 ━━ */
:deep(.dark-input.el-input-number .el-input__wrapper) {
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 1px rgba(255,255,255,0.12) inset;
  border-radius: 8px;
}
:deep(.dark-input.el-input-number .el-input__inner) { color: #ffffff; text-align: left; }
:deep(.dark-input.el-input-number .el-input-number__decrease),
:deep(.dark-input.el-input-number .el-input-number__increase) {
  background: rgba(255,255,255,0.06);
  border-color: rgba(255,255,255,0.12);
  color: rgba(255,255,255,0.6);
}

/* ━━ 種別・状態ラジオボタン ━━ */
.radio-group { display: flex; gap: 8px; flex-wrap: wrap; }
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
