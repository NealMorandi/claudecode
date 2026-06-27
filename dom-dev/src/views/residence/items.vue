<template>
  <div class="page-container">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
        <span style="margin-left:12px">備品準備チェックリスト</span>
      </template>
      <el-table :data="items">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="備品名" prop="equipmentName" />
        <el-table-column label="種別" prop="category" width="100" align="center" />
        <el-table-column label="準備状況" prop="prepared" width="160" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.prepared"
              :active-value="1"
              :inactive-value="0"
              active-text="準備済"
              inactive-text="未準備"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
      </el-table>
      <div class="flex-end mt-16">
        <el-button type="primary" @click="handleSaveAll">一括保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getResidenceItems, updateResidenceItems } from '@/api/equipment'

const route = useRoute()
const loading = ref(false)
const items = ref([])

const handleStatusChange = async (row) => {
  await updateResidenceItems(route.params.id, [{ id: row.id, prepared: row.prepared }])
}

const handleSaveAll = async () => {
  await updateResidenceItems(route.params.id, items.value)
  ElMessage.success('保存しました')
}

onMounted(async () => {
  loading.value = true
  try {
    const data = await getResidenceItems(route.params.id)
    items.value = Array.isArray(data) ? data : data.list
  } finally { loading.value = false }
})
</script>
