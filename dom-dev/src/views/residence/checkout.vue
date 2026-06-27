<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
        <span style="margin-left:12px">退寮処理</span>
      </template>

      <el-descriptions :column="2" border class="mb-16" v-if="residence.id">
        <el-descriptions-item label="社員名">{{ residence.employeeName }}</el-descriptions-item>
        <el-descriptions-item label="部屋">{{ residence.dormitoryName }} / {{ residence.roomName }}</el-descriptions-item>
        <el-descriptions-item label="入居日">{{ residence.checkinDate }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="退寮日" prop="checkoutDate">
          <el-date-picker
            v-model="form.checkoutDate"
            type="date"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledCheckoutDate"
          />
        </el-form-item>
        <el-form-item label="退寮理由" prop="checkoutReason">
          <el-select v-model="form.checkoutReason" placeholder="理由を選択" style="width:240px">
            <el-option label="転居" value="relocation" />
            <el-option label="退職" value="retirement" />
            <el-option label="出張終了" value="trip_end" />
            <el-option label="その他" value="other" />
          </el-select>
        </el-form-item>

        <el-divider>備品処理</el-divider>
        <el-table :data="items" v-loading="itemsLoading" class="mb-16">
          <el-table-column label="備品名" prop="equipmentName" />
          <el-table-column label="処理区分" prop="disposition" width="200" align="center">
            <template #default="{ row }">
              <el-select v-model="row.disposition" size="small">
                <el-option label="廃棄" value="discard" />
                <el-option label="保管" value="storage" />
                <el-option label="次回利用" value="reuse" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="保管場所" prop="storageLocation" width="200">
            <template #default="{ row }">
              <el-input
                v-if="row.disposition === 'storage'"
                v-model="row.storageLocation"
                size="small"
                placeholder="例：倉庫A"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>

        <el-form-item>
          <el-button type="warning" :loading="saving" @click="handleSubmit">退寮処理を完了する</el-button>
          <el-button @click="$router.back()">キャンセル</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResidenceDetail, checkoutResidence } from '@/api/residence'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)
const itemsLoading = ref(false)
const residence = ref({})
const items = ref([])

const form = reactive({ checkoutDate: '', checkoutReason: '' })
const rules = {
  checkoutDate: [{ required: true, message: '退寮日を選択してください', trigger: 'change' }],
  checkoutReason: [{ required: true, message: '退寮理由を選択してください', trigger: 'change' }],
}

const disabledCheckoutDate = (time) => {
  if (!residence.value.checkinDate) return false
  const [y, m, d] = residence.value.checkinDate.toString().split('-').map(Number)
  return time.getTime() < new Date(y, m - 1, d).getTime()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  const hasUnfilledStorage = items.value.some(
    (i) => i.disposition === 'storage' && !i.storageLocation?.trim()
  )
  if (hasUnfilledStorage) {
    ElMessage.warning('「保管」を選択した備品の保管場所を入力してください')
    return
  }
  try {
    await ElMessageBox.confirm('退寮処理を完了します。よろしいですか？', '確認', { type: 'warning' })
  } catch {
    return
  }
  saving.value = true
  try {
    await checkoutResidence(route.params.id, { ...form, items: items.value })
    ElMessage.success('退寮処理が完了しました')
    router.push(`/residences/${route.params.id}`)
  } catch {
    // エラーは request.js のインターセプターで表示済み
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  itemsLoading.value = true
  try {
    const res = await getResidenceDetail(route.params.id)
    if (res.status !== 'active') {
      ElMessage.warning('この入居はすでに退寮済みです')
      router.replace(`/residences/${route.params.id}`)
      return
    }
    residence.value = res
    items.value = (Array.isArray(res.items) ? res.items : []).map((i) => ({
      ...i,
      disposition: i.disposition || 'reuse',
      storageLocation: i.storageLocation || '',
    }))
  } finally {
    itemsLoading.value = false
  }
})
</script>
