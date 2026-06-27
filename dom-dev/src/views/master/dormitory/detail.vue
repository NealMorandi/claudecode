<template>
  <div class="page">

    <!-- ━━ 上段：基本情報 ＋ 備品一覧 ━━ -->
    <div class="top-row">

    <!-- 基本情報パネル -->
    <div class="glass-panel info-panel" v-loading="loading">
      <div class="panel-header">
        <div>
          <span class="panel-title">社員寮のご案内</span>
          <span class="panel-badge">DORMITORY DETAIL</span>
        </div>
        <button class="btn-ghost btn-sm" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span>戻る</span>
        </button>
      </div>

      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">寮名</span>
          <span class="info-value">{{ detail.name || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">種別</span>
          <span class="info-value">
            <span v-if="detail.type" class="badge" :class="detail.type === '男' ? 'badge-blue' : 'badge-pink'">
              {{ detail.type === '男' ? '男性寮' : '女性寮' }}
            </span>
            <span v-else>—</span>
          </span>
        </div>
        <div class="info-item">
          <span class="info-label">都道府県</span>
          <span class="info-value">{{ detail.prefecture || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">住所</span>
          <span class="info-value">{{ detail.address || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">間取り</span>
          <span class="info-value">{{ detail.layout || '—' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">総部屋数</span>
          <span class="info-value">{{ detail.totalRooms ?? '—' }} 室</span>
        </div>
        <div class="info-item">
          <span class="info-label">定員</span>
          <span class="info-value">{{ detail.totalCapacity ?? '—' }} 名</span>
        </div>
        <div class="info-item">
          <span class="info-label">入居人数</span>
          <span class="info-value">{{ detail.residentCount ?? '—' }} 名</span>
        </div>
        <div class="info-item">
          <span class="info-label">ステータス</span>
          <span class="info-value">
            <span v-if="detail.status" class="badge" :class="detail.status === 'active' ? 'badge-green' : 'badge-gray'">
              {{ detail.status === 'active' ? '稼働中' : '停止中' }}
            </span>
            <span v-else>—</span>
          </span>
        </div>
        <div class="info-item info-item--full">
          <span class="info-label">備考</span>
          <span class="info-value">{{ detail.remark || '—' }}</span>
        </div>
      </div>
    </div>

    <!-- 備品一覧パネル（右） -->
    <div class="glass-panel table-panel equip-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">備品一覧</span>
          <span class="panel-badge">EQUIPMENT LIST</span>
        </div>
      </div>

      <el-table
        :data="equipLoading ? [] : equipRows"
        row-key="historyId"
        height="100%"
        class="dark-table"
      >
        <el-table-column label="種別" prop="category" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.category" class="badge" :class="categoryBadge(row.category)">
              {{ categoryLabel(row.category) }}
            </span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="備品名" prop="equipmentName" min-width="110" />
        <el-table-column label="メーカー" prop="maker" min-width="90">
          <template #default="{ row }">
            <span v-if="row.maker">{{ row.maker }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="部屋" prop="roomName" width="100">
          <template #default="{ row }">
            <span v-if="row.roomName">{{ row.roomName }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="個数" prop="quantity" width="65" align="center">
          <template #default="{ row }">
            <span>{{ row.quantity ?? '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状態" width="90" align="center">
          <template #default="{ row }">
            <span class="badge" :class="conditionBadge(row.equipmentCondition)">
              {{ conditionLabel(row.equipmentCondition) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="備考" prop="remark" min-width="100">
          <template #default="{ row }">
            <span v-if="row.remark">{{ row.remark }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="equipLoading" />
          <el-empty v-else description="備品なし" :image-size="60" />
        </template>
      </el-table>
    </div>

    </div><!-- /.top-row -->

    <!-- ━━ 部屋×在居者テーブル ━━ -->
    <div class="glass-panel table-panel">
      <div class="panel-header">
        <div>
          <span class="panel-title">部屋・在居者一覧</span>
          <span class="panel-badge">ROOMS &amp; RESIDENTS</span>
        </div>
      </div>

      <el-table
        :data="roomLoading ? [] : roomRows"
        row-key="rowKey"
        height="100%"
        class="dark-table"
      >
        <el-table-column label="部屋名" prop="roomName" width="130" />
        <el-table-column label="定員" prop="capacity" width="70" align="center" />
        <el-table-column label="在居人数" prop="residentCount" width="90" align="center">
          <template #default="{ row }">
            <span :class="row.residentCount > row.capacity ? 'text-red' : ''">{{ row.residentCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="社員名" min-width="120">
          <template #default="{ row }">
            <span v-if="row.employeeName">{{ row.employeeName }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="区分" width="110" align="center">
          <template #default="{ row }">
            <span v-if="row.employeeType" class="badge" :class="row.employeeType === 'japan' ? 'badge-blue' : 'badge-amber'">
              {{ row.employeeType === 'japan' ? '日本社員' : '中国出張' }}
            </span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="部署" prop="department" min-width="120">
          <template #default="{ row }">
            <span v-if="row.department">{{ row.department }}</span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="当月寮費" width="120" align="right">
          <template #default="{ row }">
            <span v-if="row.currentMonthRent != null" class="rent-amount">
              ¥{{ Number(row.currentMonthRent).toLocaleString() }}
              <span class="rent-days">（{{ row.currentMonthDays }}日）</span>
            </span>
            <span v-else class="cell-empty">—</span>
          </template>
        </el-table-column>
        <el-table-column label="入居日" prop="checkinDate" width="110" align="center">
          <template #default="{ row }">
            <span :class="row.checkinDate ? '' : 'cell-empty'">{{ row.checkinDate || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="退寮日" prop="checkoutDate" width="110" align="center">
          <template #default="{ row }">
            <span :class="row.checkoutDate ? '' : 'cell-empty'">{{ row.checkoutDate || '—' }}</span>
          </template>
        </el-table-column>
        <template #empty>
          <TableLoadState v-if="roomLoading" />
          <el-empty v-else description="部屋情報なし" :image-size="60" />
        </template>
      </el-table>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getDormitoryDetail, getRoomResidents, getDormitoryEquipment } from '@/api/dormitory'
import TableLoadState from '@/components/TableLoadState.vue'

const route = useRoute()
const loading = ref(false)
const roomLoading = ref(false)
const equipLoading = ref(false)
const detail = ref({})
const roomRows = ref([])
const equipRows = ref([])

const categoryLabel = (c) => ({ appliance: '家電', furniture: '家具', bedding: '寝具', other: 'その他' }[c] ?? c)
const categoryBadge = (c) => ({ appliance: 'badge-blue', furniture: 'badge-purple', bedding: 'badge-amber', other: 'badge-gray' }[c] ?? 'badge-gray')
const conditionLabel = (c) => ({ good: '良好', needs_repair: '要修理', disposed: '廃棄', lost: '紛失' }[c] ?? '未設定')
const conditionBadge = (c) => ({ good: 'badge-green', needs_repair: 'badge-amber', disposed: 'badge-gray', lost: 'badge-red' }[c] ?? 'badge-gray')

const spanMethod = ({ row, columnIndex }) => {
  if (columnIndex <= 2) {
    if (row._firstOfRoom) return { rowspan: row._roomRowCount, colspan: 1 }
    return { rowspan: 0, colspan: 0 }
  }
}

const buildRows = (list) => {
  const result = []
  let i = 0
  while (i < list.length) {
    const roomId = list[i].roomId
    let j = i
    while (j < list.length && list[j].roomId === roomId) j++
    const count = j - i
    for (let k = i; k < j; k++) {
      result.push({
        ...list[k],
        rowKey: `${list[k].roomId}-${list[k].residenceId ?? k}`,
        _firstOfRoom: k === i,
        _roomRowCount: count,
      })
    }
    i = j
  }
  return result
}

onMounted(async () => {
  const id = route.params.id
  loading.value = true
  roomLoading.value = true
  equipLoading.value = true
  try {
    const [d, residents, equip] = await Promise.all([
      getDormitoryDetail(id),
      getRoomResidents(id),
      getDormitoryEquipment(id),
    ])
    detail.value = d
    roomRows.value = buildRows(residents)
    equipRows.value = equip
  } finally {
    loading.value = false
    roomLoading.value = false
    equipLoading.value = false
  }
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

/* ━━ 上段レイアウト ━━ */
.top-row {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 16px;
}

.info-panel {
  flex: 0 0 420px;
}

.equip-panel {
  flex: 1;
  min-width: 0;
}

.glass-panel {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.09);
  border-radius: 12px;
  padding: 20px;
}

.table-panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: rgba(255,255,255,0.92);
  letter-spacing: 0.03em;
}

.panel-badge {
  margin-left: 10px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: rgba(255,255,255,0.28);
}

/* ━━ 基本情報グリッド ━━ */
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 24px;
  margin-top: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item--full {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 11px;
  color: rgba(255,255,255,0.4);
  letter-spacing: 0.04em;
}

.info-value {
  font-size: 14px;
  color: rgba(255,255,255,0.88);
}

/* ━━ テーブル ━━ */
:deep(.dark-table) {
  background: transparent;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(255,255,255,0.06);
  --el-table-row-hover-bg-color: rgba(255,255,255,0.06);
  --el-table-border-color: rgba(255,255,255,0.08);
  --el-table-text-color: rgba(255,255,255,0.82);
  --el-table-header-text-color: rgba(255,255,255,0.5);
  --el-fill-color-lighter: transparent;
  --el-bg-color-overlay: transparent;
}

/* ━━ バッジ ━━ */
.badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.04em;
}
.badge-blue   { background: rgba(59,130,246,0.22);  color: #93c5fd; border: 1px solid rgba(59,130,246,0.35); }
.badge-pink   { background: rgba(236,72,153,0.18);  color: #f9a8d4; border: 1px solid rgba(236,72,153,0.3); }
.badge-green  { background: rgba(34,197,94,0.18);   color: #86efac; border: 1px solid rgba(34,197,94,0.3); }
.badge-amber  { background: rgba(245,158,11,0.18);  color: #fcd34d; border: 1px solid rgba(245,158,11,0.3); }
.badge-purple { background: rgba(139,92,246,0.18);  color: #c4b5fd; border: 1px solid rgba(139,92,246,0.3); }
.badge-red    { background: rgba(239,68,68,0.18);   color: #fca5a5; border: 1px solid rgba(239,68,68,0.3); }
.badge-gray   { background: rgba(148,163,184,0.15); color: #94a3b8; border: 1px solid rgba(148,163,184,0.25); }

/* ━━ ボタン ━━ */
.btn-ghost {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.18);
  background: rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.8);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}
.btn-ghost:hover { background: rgba(255,255,255,0.1); border-color: rgba(255,255,255,0.28); }
.btn-ghost.btn-sm { padding: 5px 12px; font-size: 12px; }

.cell-empty  { color: rgba(255,255,255,0.28); }
.text-red    { color: #f87171; }
.rent-amount { color: #86efac; font-weight: 500; }
.rent-days   { color: rgba(255,255,255,0.38); font-size: 11px; font-weight: 400; }
</style>
