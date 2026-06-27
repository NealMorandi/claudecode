<template>
  <div class="page-container">
    <el-card shadow="never" class="form-card">
      <template #header>
        <div class="card-header-inner">
          <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
          <span class="card-title">新規入居登録</span>
        </div>
      </template>
      <el-row :gutter="24">
        <el-col :span="14">
          <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
            <el-form-item label="社員" prop="employeeId">
              <div class="field-with-btn">
                <AppSelect
                  v-model="form.employeeId"
                  :options="employeeSelectOptions"
                  filterable
                  remote
                  :remoteMethod="searchEmployee"
                  :loading="employeeLoading"
                  placeholder="社員名または社員IDで検索"
                  @change="onEmployeeChange"
                />
                <el-button type="primary" @click="openNewEmpDialog">新規社員</el-button>
              </div>
            </el-form-item>

            <el-form-item label="入居日" prop="checkinDate">
              <el-date-picker
                v-model="form.checkinDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="入居日を選択"
                @change="loadAvailableRooms"
              />
            </el-form-item>

            <el-form-item label="退寮予定日">
              <el-date-picker
                v-model="form.expectedCheckoutDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="入力任意（中国出張社員は入力推奨）"
              />
              <div v-if="selectedEmployee?.type !== 'japan'" class="tip">
                <el-icon><InfoFilled /></el-icon> 中国出張社員は退寮予定日の入力を推奨します
              </div>
            </el-form-item>

            <el-form-item label="部屋" prop="roomId">
              <AppSelect
                v-model="form.roomId"
                :options="roomSelectOptions"
                placeholder="部屋を選択"
                @change="onRoomChange"
              />
              <div v-if="roomsLoaded && !roomOptions.length" class="tip danger">
                入居可能な部屋がありません
              </div>
            </el-form-item>

            <el-form-item>
              <el-alert
                v-if="firstUseDateInfo"
                :title="firstUseDateInfo"
                type="info"
                show-icon
                :closable="false"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSubmit">登録</el-button>
              <el-button @click="$router.back()">キャンセル</el-button>
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="10">
          <div class="preview-card" v-if="selectedRoom">
            <div class="preview-header">
              <div class="preview-header-accent"></div>
              <el-icon class="preview-header-icon"><HomeFilled /></el-icon>
              <span class="preview-header-title">選択中の部屋情報</span>
            </div>
            <el-descriptions :column="1" size="small">
              <el-descriptions-item label="寮名">{{ selectedRoom.dormitoryName }}</el-descriptions-item>
              <el-descriptions-item label="部屋名">{{ selectedRoom.name }}</el-descriptions-item>
              <el-descriptions-item label="種別">
                <el-tag :type="selectedRoom.dormitoryType === '男' ? 'primary' : 'danger'">
                  {{ selectedRoom.dormitoryType === '男' ? '男性寮' : '女性寮' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="面積">{{ selectedRoom.area }} ㎡</el-descriptions-item>
              <el-descriptions-item label="定員">{{ selectedRoom.capacity }} 名</el-descriptions-item>
            </el-descriptions>
          </div>
          <div v-else class="preview-empty">
            <div class="preview-empty-icon">
              <el-icon><HomeFilled /></el-icon>
            </div>
            <p class="preview-empty-title">部屋を選択してください</p>
            <p class="preview-empty-sub">Select a room to preview</p>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>

  <!-- 新規社員登録ダイアログ -->
  <el-dialog
    v-model="newEmpDialogVisible"
    title="新規社員登録"
    width="480px"
    :close-on-click-modal="false"
    @open="onNewEmpDialogOpen"
  >
    <el-form
      ref="newEmpFormRef"
      :model="newEmpForm"
      :rules="newEmpRules"
      label-width="100px"
    >
      <el-form-item label="社員番号" prop="employeeNo">
        <el-input v-model="newEmpForm.employeeNo" placeholder="例: EMP001" @input="employeeNoServerError = ''" />
      </el-form-item>
      <el-form-item label="氏名" prop="name">
        <el-input v-model="newEmpForm.name" placeholder="例: 山田 太郎" />
      </el-form-item>
      <el-form-item label="性別" prop="gender">
        <el-radio-group v-model="newEmpForm.gender">
          <el-radio value="男">男性</el-radio>
          <el-radio value="女">女性</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="社員区分" prop="type">
        <el-radio-group v-model="newEmpForm.type">
          <el-radio value="japan">日本社員</el-radio>
          <el-radio value="china">中国出張</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="部署">
        <el-input v-model="newEmpForm.department" placeholder="例: 営業部（任意）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="newEmpDialogVisible = false">キャンセル</el-button>
      <el-button type="primary" :loading="newEmpSaving" @click="submitNewEmployee">登録</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { searchEmployees, createEmployee } from '@/api/employee'
import { getAvailableRooms } from '@/api/room'
import { createResidence } from '@/api/residence'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const employeeLoading = ref(false)
const employeeOptions = ref([])
const roomOptions = ref([])
const roomsLoaded = ref(false)
const selectedEmployee = ref(null)
const selectedRoom = ref(null)

// ── 新規社員登録ダイアログ ──────────────────────────────
const newEmpDialogVisible = ref(false)
const newEmpSaving = ref(false)
const newEmpFormRef = ref()
const newEmpForm = reactive({ employeeNo: '', name: '', gender: '男', type: 'japan', department: '' })
const employeeNoServerError = ref('')
const newEmpRules = {
  employeeNo: [
    { required: true, message: '社員番号を入力してください', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (employeeNoServerError.value) {
          callback(new Error(employeeNoServerError.value))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
  name:   [{ required: true, message: '氏名を入力してください',       trigger: 'blur' }],
  gender: [{ required: true, message: '性別を選択してください',       trigger: 'change' }],
  type:   [{ required: true, message: '社員区分を選択してください',   trigger: 'change' }],
}

const openNewEmpDialog = () => {
  newEmpDialogVisible.value = true
}

const onNewEmpDialogOpen = () => {
  Object.assign(newEmpForm, { employeeNo: '', name: '', gender: '男', type: 'japan', department: '' })
  employeeNoServerError.value = ''
  newEmpFormRef.value?.clearValidate()
}

const submitNewEmployee = async () => {
  try {
    await newEmpFormRef.value.validate()
  } catch {
    return
  }
  newEmpSaving.value = true
  try {
    const created = await createEmployee({ ...newEmpForm })
    employeeOptions.value = [created, ...employeeOptions.value]
    form.employeeId = created.id
    onEmployeeChange(created.id)
    ElMessage.success('社員を登録しました')
    newEmpDialogVisible.value = false
  } catch {
    employeeNoServerError.value = 'この社員番号はすでに登録されています'
    await nextTick()
    newEmpFormRef.value.validateField('employeeNo')
  } finally {
    newEmpSaving.value = false
  }
}

const employeeSelectOptions = computed(() =>
  employeeOptions.value.map((e) => ({
    label: `${e.name}（${e.type === 'japan' ? '日本' : '中国出張'}）${e.residenceStatus === 'active' ? ' [入居中]' : ''}`,
    value: e.id,
    disabled: e.residenceStatus === 'active',
  }))
)

const roomSelectOptions = computed(() =>
  roomOptions.value.map((r) => ({
    label: `${r.dormitoryName} / ${r.name}（${r.area}㎡）`,
    value: r.id,
  }))
)

const form = reactive({
  employeeId: null,
  checkinDate: '',
  expectedCheckoutDate: '',
  roomId: route.query.roomId ? Number(route.query.roomId) : null,
})

const rules = {
  employeeId: [{ required: true, message: '社員を選択してください', trigger: 'change' }],
  checkinDate: [{ required: true, message: '入居日を選択してください', trigger: 'change' }],
  roomId: [{ required: true, message: '部屋を選択してください', trigger: 'change' }],
}

const resetForm = () => {
  Object.assign(form, { employeeId: null, checkinDate: '', expectedCheckoutDate: '', roomId: null })
  employeeOptions.value = []
  roomOptions.value = []
  roomsLoaded.value = false
  selectedEmployee.value = null
  selectedRoom.value = null
  nextTick(() => formRef.value?.clearValidate())
}

onMounted(() => {
  if (route.query.roomId) {
    form.roomId = Number(route.query.roomId)
  }
  loadAvailableRooms()
})

const firstUseDateInfo = computed(() => {
  if (!selectedEmployee.value || selectedEmployee.value.type !== 'japan') return null
  if (selectedEmployee.value.firstUseDate) {
    return `初回寮利用日 ${selectedEmployee.value.firstUseDate} は保持されます`
  }
  return form.checkinDate
    ? `初回寮利用日として ${form.checkinDate} が設定されます`
    : null
})

const searchEmployee = async (keyword) => {
  employeeLoading.value = true
  try {
    const data = await searchEmployees(keyword || '')
    employeeOptions.value = Array.isArray(data) ? data : (data?.list || [])
  } finally {
    employeeLoading.value = false
  }
}

const onEmployeeChange = (id) => {
  selectedEmployee.value = employeeOptions.value.find((e) => e.id === id) || null
  loadAvailableRooms()
}

const loadAvailableRooms = async () => {
  try {
    const params = {}
    if (form.checkinDate) params.date = form.checkinDate
    if (selectedEmployee.value?.gender) params.gender = selectedEmployee.value.gender
    const data = await getAvailableRooms(params)
    const rooms = Array.isArray(data) ? data : (data?.list || [])
    roomOptions.value = rooms
    if (form.roomId) {
      const found = rooms.find((r) => r.id === form.roomId)
      if (found) {
        selectedRoom.value = found
      } else {
        form.roomId = null
        selectedRoom.value = null
      }
    }
  } catch {
    roomOptions.value = []
  } finally {
    roomsLoaded.value = true
  }
}

const onRoomChange = (id) => {
  selectedRoom.value = roomOptions.value.find((r) => r.id === id) || null
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if (selectedEmployee.value?.residenceStatus === 'active') {
    ElMessageBox.alert('この社員はすでに入居中です。退寮処理後に再登録してください。', '入居エラー', { type: 'error', confirmButtonText: 'OK' })
    return
  }
  if (selectedEmployee.value && selectedRoom.value) {
    const empGender = selectedEmployee.value.gender
    const dormType  = selectedRoom.value.dormitoryType
    if (empGender !== dormType) {
      ElMessageBox.alert(
        `社員の性別（${empGender === '男' ? '男性' : '女性'}）と寮種別（${dormType === '男' ? '男性寮' : '女性寮'}）が一致しません。`,
        '性別不一致',
        { type: 'error', confirmButtonText: 'OK' }
      )
      return
    }
  }
  saving.value = true
  try {
    const payload = {
      employeeId: Number(form.employeeId),
      checkinDate: form.checkinDate,
      expectedCheckoutDate: form.expectedCheckoutDate || null,
      roomId: Number(form.roomId),
    }
    const result = await createResidence(payload)
    ElMessage.success('入居登録しました')
    router.push('/residences')
  } catch {
    // エラーは request.js のインターセプターで表示済み
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
/* ── employee field + new button ─── */
.field-with-btn {
  display: flex;
  gap: 8px;
  width: 100%;
}
.field-with-btn > :first-child {
  flex: 1;
  min-width: 0;
}

/* ── form card header ─── */
.card-header-inner {
  display: flex;
  align-items: center;
  gap: 12px;
}
.card-title {
  color: var(--text-primary);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

/* ── tip messages ─── */
.tip {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.tip.danger { color: #f87171; }

/* ── preview card ─── */
.preview-card {
  background: var(--navy-mid);
  border: 1px solid var(--navy-border);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0,0,0,0.3);
}
.preview-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  background: rgba(99,102,241,0.08);
  border-bottom: 1px solid var(--navy-border);
}
.preview-header-accent {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, #6366f1 0%, #7c3aed 60%, transparent 100%);
}
.preview-header-icon {
  color: #818cf8;
  font-size: 16px;
}
.preview-header-title {
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.02em;
}
.preview-card :deep(.el-descriptions__body) {
  padding: 4px 0;
}

/* ── empty state ─── */
.preview-empty {
  background: var(--navy-mid);
  border: 1px solid var(--navy-border);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 52px 20px;
  gap: 10px;
}
.preview-empty-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: rgba(99,102,241,0.14);
  border: 1px solid rgba(99,102,241,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
}
.preview-empty-icon .el-icon {
  font-size: 26px;
  color: #6366f1;
  opacity: 0.75;
}
.preview-empty-title {
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 500;
}
.preview-empty-sub {
  color: var(--text-muted);
  font-size: 11px;
  letter-spacing: 0.06em;
}
</style>
