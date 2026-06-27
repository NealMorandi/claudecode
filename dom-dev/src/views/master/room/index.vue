<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <el-button icon="ArrowLeft" @click="$router.back()">戻る</el-button>
            <span style="margin-left:12px">部屋一覧</span>
          </div>
          <el-button type="primary" icon="Plus" @click="openDialog()">新規部屋登録</el-button>
        </div>
      </template>
      <div class="pagination-row">
        <span class="pg-label">ページ行数：</span>
        <AppSelect v-model="pageSize" :options="pageSizeOptions.map(s => ({ label: String(s), value: s }))" style="width:72px" @change="onPageSizeChange" />
        <span class="pg-total">総{{ total }}件</span>
        <button class="pg-btn" aria-label="先頭ページ" :disabled="page === 1" @click="goToPage(1)">|&lt;</button>
        <button class="pg-btn" aria-label="前ページ" :disabled="page === 1" @click="goToPage(page - 1)">&lt;</button>
        <button class="pg-btn" aria-label="次ページ" :disabled="page >= totalPages" @click="goToPage(page + 1)">&gt;</button>
        <button class="pg-btn" aria-label="末尾ページ" :disabled="page >= totalPages" @click="goToPage(totalPages)">&gt;|</button>
      </div>

      <div class="table-wrap">
      <el-table :data="loading ? [] : pagedList" height="100%" class="dark-table">
        <el-table-column type="index" label="No." width="60" align="center" />
        <el-table-column label="部屋名" prop="name" width="120" />
        <el-table-column label="面積(㎡)" prop="area" width="100" align="center" />
        <el-table-column label="定員" prop="capacity" width="80" align="center" />
        <el-table-column label="在居人数" width="95" align="center">
          <template #default="{ row }">
            <span
              class="resident-badge"
              :class="{
                'resident-full':  row.residentCount >= row.capacity,
                'resident-some':  row.residentCount > 0 && row.residentCount < row.capacity,
                'resident-empty': row.residentCount === 0,
              }"
            >{{ row.residentCount }}人</span>
          </template>
        </el-table-column>
        <el-table-column label="寮費(円)" prop="rent" width="110" align="center">
          <template #default="{ row }">
            <span v-if="row.rent != null" :class="{ 'rent-negative': row.rent < 0 }">
              {{ row.rent.toLocaleString() }}円
            </span>
            <span v-else class="rent-unset">－</span>
          </template>
        </el-table-column>
        <el-table-column label="ステータス" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'vacant' ? 'success' : 'info'">
              {{ row.status === 'vacant' ? '空き' : '入居中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">編集</el-button>
            <el-button link type="danger" @click="handleDelete(row)">削除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="loading" />
          <el-empty v-else description="データなし" :image-size="60" />
        </template>
      </el-table>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editRow ? '部屋編集' : '新規部屋登録'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="部屋名" prop="name">
          <el-input v-model="form.name" placeholder="例：101号室" />
        </el-form-item>
        <el-form-item label="面積(㎡)" prop="area">
          <el-input-number v-model="form.area" :min="1" :max="200" :precision="1" />
        </el-form-item>
        <el-form-item label="定員" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="寮費(円)">
          <el-input-number v-model="form.rent" :value-on-clear="null" :controls="true" style="width:180px" />
          <div class="form-hint">未入力の場合は共通設定を使用</div>
        </el-form-item>
        <el-form-item label="備考">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomsByDormitory, createRoom, updateRoom, deleteRoom } from '@/api/room'
import TableLoadState from '@/components/TableLoadState.vue'

const route = useRoute()
const dormitoryId = route.params.id
const loading = ref(false)
const saving = ref(false)
const list = ref([])
const page = ref(1)
const pageSize = ref(10)
const pageSizeOptions = [5, 10, 20, 50, 100]
const total = computed(() => list.value.length)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value) || 1)
const pagedList = computed(() => list.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
const goToPage = (p) => { page.value = p }
const onPageSizeChange = () => { page.value = 1 }
const dialogVisible = ref(false)
const editRow = ref(null)
const formRef = ref()

const form = reactive({ name: '', area: 20, capacity: 1, rent: 2000, remark: '' })
const rules = {
  name: [{ required: true, message: '部屋名を入力してください', trigger: 'blur' }],
  area: [{ required: true, message: '面積を入力してください', trigger: 'blur' }],
}

const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getRoomsByDormitory(dormitoryId)
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  editRow.value = row
  if (row) Object.assign(form, row)
  else Object.assign(form, { name: '', area: 20, capacity: 1, rent: 2000, remark: '' })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editRow.value) {
      await updateRoom(editRow.value.id, form, { _suppressToast: true })
    } else {
      await createRoom(dormitoryId, form)
    }
    ElMessage.success(editRow.value ? '更新しました' : '登録しました')
    dialogVisible.value = false
    fetchList()
  } catch (err) {
    if (editRow.value) {
      const msg = err.response?.data?.message || err.message || '更新できませんでした'
      ElMessageBox.alert(msg, '更新できません', { type: 'error', confirmButtonText: 'OK' })
    }
    // 新規登録エラーは request.js インターセプターで表示済み
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`「${row.name}」を削除しますか？`, '削除確認', { type: 'warning' })
  try {
    await deleteRoom(row.id, { _suppressToast: true })
    ElMessage.success('削除しました')
    fetchList()
  } catch (err) {
    const msg = err.response?.data?.message || err.message || '削除できませんでした'
    ElMessageBox.alert(msg, '削除できません', { type: 'error', confirmButtonText: 'OK' })
  }
}

onMounted(fetchList)
</script>

<style scoped>
.page-container {
  min-height: 0;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
:deep(.el-card) {
  flex: 1 !important;
  min-height: 0 !important;
  display: flex !important;
  flex-direction: column !important;
  overflow: hidden !important;
}
:deep(.el-card__body) {
  flex: 1 !important;
  min-height: 0 !important;
  overflow: hidden !important;
  display: flex !important;
  flex-direction: column !important;
}
.table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.table-wrap :deep(.el-table) {
  flex: 1;
  min-height: 0;
}
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
.pagination-row { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgba(255,255,255,0.06); width: 100%; }
.pg-label { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; }
.pg-total { color: rgba(255,255,255,0.45); font-size: 12px; white-space: nowrap; margin-left: 4px; }
.pg-btn { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; height: 28px; padding: 0 6px; background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.12); border-radius: 6px; color: rgba(255,255,255,0.65); font-size: 12px; font-weight: 600; cursor: pointer; transition: background 0.15s, color 0.15s; white-space: nowrap; }
.pg-btn:hover:not(:disabled) { background: rgba(79,142,247,0.18); color: #4f8ef7; border-color: rgba(79,142,247,0.35); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.rent-negative { color: #f87171; }
.rent-unset { color: rgba(255,255,255,0.25); }
.resident-badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}
.resident-empty { background: rgba(255,255,255,0.06); color: rgba(255,255,255,0.35); }
.resident-some  { background: rgba(56,189,248,0.15);  color: #7dd3fc; }
.resident-full  { background: rgba(251,191,36,0.18);  color: #fbbf24; }
.form-hint { font-size: 11px; color: rgba(255,255,255,0.38); margin-top: 4px; }
</style>
