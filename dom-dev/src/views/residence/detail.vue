<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
            <span style="margin-left:12px">入居詳細</span>
          </div>
          <el-space>
            <el-button
              v-if="detail.status === 'active'"
              type="warning"
              @click="$router.push(`/residences/${route.params.id}/checkout`)"
            >
              退寮処理
            </el-button>
            <el-button
              v-if="detail.status === 'active'"
              @click="$router.push(`/residences/${route.params.id}/items`)"
            >
              備品チェックリスト
            </el-button>
          </el-space>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="社員名">{{ detail.employeeName }}</el-descriptions-item>
        <el-descriptions-item label="社員番号">{{ detail.employeeNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="社員区分">
          <el-tag :type="detail.employeeType === 'japan' ? '' : 'warning'" size="small">
            {{ detail.employeeType === 'japan' ? '日本社員' : '中国出張社員' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="初回寮利用日" v-if="detail.employeeType === 'japan'">
          {{ detail.firstUseDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="寮名">{{ detail.dormitoryName }}</el-descriptions-item>
        <el-descriptions-item label="部屋名">
          {{ detail.roomName }}
          <span v-if="detail.roomArea" style="color:rgba(255,255,255,0.45);font-size:12px;margin-left:6px">{{ detail.roomArea }} ㎡</span>
        </el-descriptions-item>
        <el-descriptions-item label="入居日">{{ detail.checkinDate }}</el-descriptions-item>
        <el-descriptions-item label="退寮予定日">{{ detail.expectedCheckoutDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退寮日">{{ detail.checkoutDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退寮理由">{{ checkoutReasonLabel(detail.checkoutReason) }}</el-descriptions-item>
        <el-descriptions-item label="ステータス">
          <el-tag :type="detail.status === 'active' ? 'success' : 'info'">
            {{ detail.status === 'active' ? '入居中' : '退寮済' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getResidenceDetail } from '@/api/residence'

const route = useRoute()
const loading = ref(false)
const detail = ref({})

const CHECKOUT_REASON_LABELS = {
  relocation: '転居',
  retirement: '退職',
  trip_end:   '出張終了',
  other:      'その他',
}
const checkoutReasonLabel = (code) => CHECKOUT_REASON_LABELS[code] || code || '-'

onMounted(async () => {
  loading.value = true
  try { detail.value = await getResidenceDetail(route.params.id) }
  finally { loading.value = false }
})
</script>
