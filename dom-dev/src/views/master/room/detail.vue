<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
        <span style="margin-left:12px">部屋詳細</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="部屋名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="ステータス">
          <el-tag :type="detail.status === 'vacant' ? 'success' : 'info'">
            {{ detail.status === 'vacant' ? '空き' : '入居中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="面積">{{ detail.area }} ㎡</el-descriptions-item>
        <el-descriptions-item label="定員">{{ detail.capacity }} 名</el-descriptions-item>
        <el-descriptions-item label="所属寮">{{ detail.dormitoryName }}</el-descriptions-item>
        <el-descriptions-item label="備考">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getRoomDetail } from '@/api/room'

const route = useRoute()
const loading = ref(false)
const detail = ref({})

onMounted(async () => {
  loading.value = true
  try { detail.value = await getRoomDetail(route.params.id) }
  finally { loading.value = false }
})
</script>
