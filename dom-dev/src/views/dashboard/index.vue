<template>
  <div class="dash">
    <!-- ━━ 統計カード ━━ -->
    <div class="stat-grid">
      <div
        v-for="card in summaryCards"
        :key="card.label"
        class="stat-card"
        :style="{ '--accent': card.color }"
      >
        <div class="stat-icon-wrap">
          <div class="stat-icon-halo"></div>
          <el-icon class="stat-icon"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-body">
          <p class="stat-label">{{ card.label }}</p>
          <!-- 当月寮費カード: 未計算バッジ表示 (P1-A) -->
          <template v-if="card.isRent">
            <div v-if="summary.rentStatus === 'uncalculated'" class="rent-uncalculated">
              <span class="rent-badge rent-badge--amber">未計算</span>
              <button class="rent-calc-link" @click="$router.push('/rent/calculate')">計算へ →</button>
            </div>
            <p v-else class="stat-value">¥{{ Number(card.value || 0).toLocaleString() }}</p>
          </template>
          <p v-else class="stat-value">{{ card.value }}</p>
        </div>
      </div>
    </div>

    <!-- ━━ 中段：テーブル ＋ 警告 ━━ -->
    <div class="mid-row">
      <!-- 寮別稼働状況 -->
      <div class="glass-panel panel-table">
        <div class="panel-header">
          <span class="panel-title">寮別稼働状況</span>
          <span class="panel-badge">OCCUPANCY</span>
        </div>

        <!-- サマリーカード (P2-A: residentCount を使用して入居中人数を統一) -->
        <div class="occ-summary">
          <div class="occ-sum-item">
            <span class="occ-sum-val">{{ occupancySummary.totalRooms }}</span>
            <span class="occ-sum-label">総部屋数</span>
          </div>
          <div class="occ-sum-divider"></div>
          <div class="occ-sum-item">
            <span class="occ-sum-val" style="color:#4f8ef7">{{ occupancySummary.residentCount }}</span>
            <span class="occ-sum-label">入居者数</span>
          </div>
          <div class="occ-sum-divider"></div>
          <div class="occ-sum-item">
            <span class="occ-sum-val" style="color:#22c55e">{{ occupancySummary.available }}</span>
            <span class="occ-sum-label">空き室</span>
          </div>
          <div class="occ-sum-divider"></div>
          <div class="occ-sum-item">
            <span class="occ-sum-val" :style="{ color: rateColor(occupancySummary.rate) }">
              {{ (occupancySummary.rate * 100).toFixed(0) }}%
            </span>
            <span class="occ-sum-label">全体稼働率</span>
          </div>
        </div>

        <!-- テーブル (P2-B: スクロールインジケーター付き) -->
        <div class="table-wrap">
          <el-table
            :data="occupancyList"
            size="small"
            class="dark-table"
            :show-header="true"
            max-height="252"
          >
            <el-table-column label="寮名" min-width="100">
              <template #default="{ row }">
                <span class="dorm-name-cell">
                  <span class="dorm-dot" :style="{ background: rateColor(row.occupancyRate) }"></span>
                  {{ row.name }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="総部屋数" prop="totalRooms" width="82" align="center" />
            <el-table-column label="入居中" prop="occupiedRooms" width="72" align="center" />
            <el-table-column label="空き" prop="availableRooms" width="64" align="center" />
            <el-table-column label="入居人数" prop="residentCount" width="78" align="center">
              <template #default="{ row }">
                <span class="resident-count">{{ row.residentCount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="入居可能" prop="availableCapacity" width="78" align="center">
              <template #default="{ row }">
                <span
                  class="avail-capacity"
                  :class="{
                    'avail-zero': row.availableCapacity === 0,
                    'avail-negative': row.availableCapacity < 0
                  }"
                >
                  {{ row.availableCapacity }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="稼働率" prop="occupancyRate" width="130" align="center">
              <template #default="{ row }">
                <div class="rate-wrap">
                  <div class="rate-bar">
                    <div
                      class="rate-fill"
                      :style="{
                        width: (row.occupancyRate * 100).toFixed(0) + '%',
                        background: rateColor(row.occupancyRate)
                      }"
                    ></div>
                  </div>
                  <span class="rate-pct" :style="{ color: rateColor(row.occupancyRate) }">
                    {{ (row.occupancyRate * 100).toFixed(0) }}%
                  </span>
                </div>
              </template>
            </el-table-column>
            <el-table-column width="88" align="center">
              <template #default="{ row }">
                <button class="occ-detail-btn" @click="$router.push({ path: '/residences', query: { keyword: row.name } })">
                  入居状況
                </button>
              </template>
            </el-table-column>
          </el-table>

        </div>

        <!-- 稼働率凡例 + スクロールインジケーター (P3-B / P2-B) -->
        <div class="rate-legend">
          <span class="legend-item"><span class="legend-dot" style="background:#22c55e"></span>80%以上 良好</span>
          <span class="legend-item"><span class="legend-dot" style="background:#f59e0b"></span>50〜79% 注意</span>
          <span class="legend-item"><span class="legend-dot" style="background:#f87171"></span>49%以下 警告</span>
          <span v-if="occupancyList.length > 5" class="scroll-hint-text">▼ さらに {{ occupancyList.length - 5 }} 件</span>
        </div>
      </div>

      <!-- 長期利用警告 (P1-B / P3-A) -->
      <div class="glass-panel panel-alert">
        <div class="panel-header">
          <span class="panel-title">
            長期利用警告
            <span v-if="alertTotal > 0" class="alert-count-badge">{{ alertTotal }}件</span>
          </span>
          <button class="link-btn" @click="$router.push('/long-term-alerts')">全件表示</button>
        </div>
        <div v-if="!alerts.length" class="empty-state">
          <div class="empty-icon">
            <el-icon><CircleCheckFilled /></el-icon>
          </div>
          <p class="empty-title">警告対象者なし</p>
          <p class="empty-sub">All clear</p>
        </div>
        <div v-else>
          <div v-for="item in alerts" :key="item.employeeId" class="alert-row">
            <div class="alert-avatar">{{ item.employeeName?.[0] }}</div>
            <div class="alert-info">
              <div class="alert-name-row">
                <span class="alert-name">{{ item.employeeName }}</span>
                <span class="alert-tag">{{ item.yearsOfStay }}年超</span>
              </div>
              <div class="alert-detail-row">
                <span class="alert-location">{{ item.dormitoryName }} {{ item.roomName }}</span>
                <span v-if="item.overdueDays != null" class="alert-overdue">{{ item.overdueDays }}日超過</span>
              </div>
            </div>
            <div class="alert-actions">
              <button class="alert-btn alert-btn--detail" @click="$router.push('/long-term-alerts')">詳細</button>
              <button class="alert-btn alert-btn--action" @click="openActionDialog(item)">対応する</button>
            </div>
          </div>
          <div v-if="alertTotal > alerts.length" class="alert-more">
            他 {{ alertTotal - alerts.length }} 件を表示
          </div>
        </div>
      </div>
    </div>

    <!-- ━━ クイックアクション (P3-C: 長期利用対応追加) ━━ -->
    <div class="glass-panel action-panel">
      <div class="panel-header">
        <span class="panel-title">クイックアクション</span>
        <span class="panel-badge">ACTIONS</span>
      </div>
      <div class="action-row">
        <button class="action-btn action-primary" @click="$router.push('/residences/new')">
          <span class="btn-shimmer"></span>
          <el-icon><Plus /></el-icon>
          <span>新規入居登録</span>
        </button>
        <button class="action-btn action-ghost" @click="$router.push('/vacancies')">
          <el-icon><Key /></el-icon>
          <span>空き室確認</span>
        </button>
        <button class="action-btn action-ghost" @click="$router.push('/rent/calculate')">
          <el-icon><Money /></el-icon>
          <span>寮費計算</span>
        </button>
        <button class="action-btn action-ghost action-alert-btn" @click="$router.push('/long-term-alerts')">
          <el-icon><Warning /></el-icon>
          <span>長期利用対応</span>
          <span v-if="summary.alertCount > 0" class="action-badge">{{ summary.alertCount }}</span>
        </button>
        <button class="action-btn action-ghost" @click="$router.push('/import')">
          <el-icon><Upload /></el-icon>
          <span>データ取り込み</span>
        </button>
      </div>
    </div>

    <!-- ━━ 対応モーダル (P1-B) ━━ -->
    <el-dialog
      v-model="actionDialog.visible"
      title="長期利用対応記録"
      width="400px"
      :close-on-click-modal="false"
    >
      <div v-if="actionDialog.item" class="action-modal-body">
        <p class="modal-employee">{{ actionDialog.item.employeeName }}</p>
        <p class="modal-location">{{ actionDialog.item.dormitoryName }} {{ actionDialog.item.roomName }}</p>
        <el-form label-width="80px" style="margin-top:16px">
          <el-form-item label="対応状況">
            <el-select v-model="actionDialog.status" style="width:100%">
              <el-option label="未対応" value="pending" />
              <el-option label="連絡済み" value="notified" />
              <el-option label="延長申請" value="applied" />
              <el-option label="退去予定" value="done" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="actionDialog.visible = false">キャンセル</el-button>
        <el-button type="primary" :loading="actionDialog.loading" @click="submitAction">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSummary, getDormitoryOccupancy, getLongTermAlertsTop } from '@/api/dashboard'
import { updateAlertStatus } from '@/api/longTermAlert'

const summary = ref({
  occupiedEmployees: 0, availableRooms: 0, alertCount: 0,
  monthlyRent: 0, rentStatus: 'uncalculated', rentCount: 0
})
const occupancyList = ref([])
const alerts = ref([])
const alertTotal = ref(0)

// P2-A: residentCount（人数）で統一
const occupancySummary = computed(() => {
  if (!occupancyList.value.length) return { totalRooms: 0, residentCount: 0, available: 0, rate: 0 }
  const totalRooms    = occupancyList.value.reduce((s, d) => s + d.totalRooms, 0)
  const residentCount = occupancyList.value.reduce((s, d) => s + d.residentCount, 0)
  const available     = occupancyList.value.reduce((s, d) => s + d.availableRooms, 0)
  const totalCapacity = occupancyList.value.reduce((s, d) => s + (d.totalCapacity || 0), 0)
  return { totalRooms, residentCount, available, rate: totalCapacity > 0 ? residentCount / totalCapacity : 0 }
})

// P3-B: 閾値に応じたカラー（緑≥0.8 / 黄0.5〜0.79 / 赤<0.5）
function rateColor(rate) {
  const r = Math.max(0, Math.min(1, rate))
  if (r >= 0.8) return '#22c55e'
  if (r >= 0.5) return '#f59e0b'
  return '#f87171'
}

const summaryCards = computed(() => [
  { label: '入居中',      value: summary.value.occupiedEmployees, icon: 'UserFilled', color: '#4f8ef7', isRent: false },
  { label: '空き室',      value: summary.value.availableRooms,    icon: 'Key',        color: '#22c55e', isRent: false },
  { label: '長期利用警告', value: summary.value.alertCount,        icon: 'Warning',    color: '#f59e0b', isRent: false },
  { label: '当月寮費',    value: summary.value.monthlyRent,       icon: 'Money',      color: '#f87171', isRent: true  },
])

// 対応モーダル (P1-B)
const actionDialog = ref({ visible: false, item: null, status: 'pending', loading: false })

function openActionDialog(item) {
  actionDialog.value = { visible: true, item, status: item.alertStatus || 'pending', loading: false }
}

async function submitAction() {
  actionDialog.value.loading = true
  try {
    await updateAlertStatus(actionDialog.value.item.employeeId, { alertStatus: actionDialog.value.status })
    ElMessage.success('対応状況を更新しました')
    actionDialog.value.visible = false
    // 警告リストを再取得
    const al = await getLongTermAlertsTop(5)
    alerts.value = al.list || al
    alertTotal.value = al.total ?? alerts.value.length
  } catch {
    // エラーは request.js のインターセプターで表示済み
  } finally {
    actionDialog.value.loading = false
  }
}

onMounted(async () => {
  const [s, occ, al] = await Promise.all([
    getSummary(),
    getDormitoryOccupancy(),
    getLongTermAlertsTop(5),
  ])
  summary.value = s
  occupancyList.value = occ
  alerts.value = al.list || al
  alertTotal.value = al.total ?? alerts.value.length
})
</script>

<style scoped>
.dash {
  padding: 14px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
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
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-title {
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  gap: 8px;
}
.panel-badge {
  color: rgba(255,255,255,0.28);
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.12em;
}

/* ━━ 統計カード ━━ */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  flex-shrink: 0;
}
.stat-card {
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
.stat-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent), transparent);
  opacity: 0.6;
}
.stat-icon-wrap { position: relative; flex-shrink: 0; }
.stat-icon-halo {
  position: absolute;
  inset: -8px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--accent) 0%, transparent 70%);
  opacity: 0.25;
  filter: blur(6px);
}
.stat-icon { font-size: 28px; color: var(--accent); position: relative; }
.stat-label { color: rgba(255,255,255,0.45); font-size: 12px; margin-bottom: 6px; }
.stat-value { color: #ffffff; font-size: 30px; font-weight: 700; line-height: 1; }

/* P1-A: 当月寮費 未計算状態 */
.rent-uncalculated {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 4px;
}
.rent-badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  line-height: 1.4;
}
.rent-badge--amber {
  background: rgba(245,158,11,0.22);
  border: 1px solid rgba(245,158,11,0.45);
  color: #fbbf24;
}
.rent-calc-link {
  background: none;
  border: none;
  color: #f59e0b;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
  text-decoration: underline;
  text-underline-offset: 2px;
}
.rent-calc-link:hover { opacity: 0.75; }

/* ━━ 中段レイアウト ━━ */
.mid-row {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 12px;
  flex: 1;
  min-height: 0;
}

/* ━━ テーブル ━━ */
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
:deep(.el-scrollbar__bar.is-vertical) { width: 4px; right: 0; }
:deep(.el-scrollbar__thumb) {
  background: rgba(255,255,255,0.2);
  border-radius: 2px;
  transition: background 0.2s;
}
:deep(.el-scrollbar__thumb:hover) { background: rgba(255,255,255,0.38); }
:deep(.el-table .el-table__header th.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.35);
  font-size: 10px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}
:deep(.el-table .el-table__body td.el-table__cell) {
  background: transparent;
  color: rgba(255,255,255,0.78);
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-size: 13px;
  padding-top: 11px;
  padding-bottom: 11px;
  transition: background 0.15s;
}
:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255,255,255,0.09) !important;
  color: rgba(255,255,255,0.95) !important;
}

/* P2-B: テーブルラッパー */
.table-wrap { position: relative; }

/* P3-B: 稼働率凡例 + スクロールインジケーター */
.rate-legend {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid rgba(255,255,255,0.06);
}
.scroll-hint-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 11px;
  color: rgba(255,255,255,0.35);
  letter-spacing: 0.05em;
  white-space: nowrap;
  pointer-events: none;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 10px;
  color: rgba(255,255,255,0.38);
}
.legend-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
}

/* ━━ サマリーバー ━━ */
.occ-summary {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.04);
  border-radius: 8px;
  padding: 14px 0;
  margin-bottom: 16px;
}
.occ-sum-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}
.occ-sum-divider {
  width: 1px;
  height: 32px;
  background: rgba(255,255,255,0.09);
  flex-shrink: 0;
}
.occ-sum-val { font-size: 24px; font-weight: 700; color: #ffffff; line-height: 1; }
.occ-sum-label {
  font-size: 10px;
  color: rgba(255,255,255,0.38);
  letter-spacing: 0.07em;
  font-weight: 500;
}

/* ━━ 入居人数 / 入居可能人数 ━━ */
.resident-count { font-weight: 600; color: #7bb3fb; }
.avail-capacity { font-weight: 600; color: #86efac; }
.avail-zero { color: rgba(255,255,255,0.25); }
.avail-negative { color: #f87171; font-weight: 700; }

/* ━━ 入居状況ボタン ━━ */
.occ-detail-btn {
  padding: 3px 10px;
  font-size: 11px;
  font-weight: 500;
  color: #4f8ef7;
  background: rgba(79,142,247,0.12);
  border: 1px solid rgba(79,142,247,0.3);
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.occ-detail-btn:hover {
  background: rgba(79,142,247,0.25);
  border-color: rgba(79,142,247,0.6);
  color: #7bb3fb;
}

/* ━━ ドット指標 ━━ */
.dorm-name-cell { display: flex; align-items: center; gap: 8px; }
.dorm-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  flex-shrink: 0;
  filter: brightness(1.2);
  box-shadow: 0 0 5px currentColor;
}

/* 稼働率バー */
.rate-wrap { display: flex; align-items: center; gap: 8px; }
.rate-bar { flex: 1; height: 4px; background: rgba(255,255,255,0.1); border-radius: 2px; overflow: hidden; }
.rate-fill { height: 100%; border-radius: 2px; transition: width 0.4s ease, background 0.4s ease; }
.rate-pct { font-size: 11px; color: rgba(255,255,255,0.55); min-width: 30px; }

/* ━━ P1-B: 警告パネル改修 ━━ */
.alert-count-badge {
  display: inline-block;
  background: rgba(248,113,113,0.2);
  border: 1px solid rgba(248,113,113,0.4);
  color: #fca5a5;
  font-size: 10px;
  font-weight: 600;
  padding: 1px 7px;
  border-radius: 20px;
}
.link-btn {
  background: none;
  border: none;
  color: #4f8ef7;
  font-size: 12px;
  cursor: pointer;
  padding: 0;
  transition: opacity 0.15s;
}
.link-btn:hover { opacity: 0.7; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 0;
  gap: 8px;
}
.empty-icon { font-size: 40px; color: #22c55e; opacity: 0.6; }
.empty-title { color: rgba(255,255,255,0.5); font-size: 13px; }
.empty-sub { color: rgba(255,255,255,0.22); font-size: 11px; letter-spacing: 0.08em; }

.alert-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.alert-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: white;
  font-weight: 600;
  flex-shrink: 0;
  margin-top: 2px;
}
.alert-info { flex: 1; min-width: 0; }
.alert-name-row { display: flex; align-items: center; gap: 6px; margin-bottom: 3px; }
.alert-name { color: rgba(255,255,255,0.8); font-size: 13px; }
.alert-tag {
  background: rgba(248,113,113,0.2);
  color: #fca5a5;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 20px;
  flex-shrink: 0;
}
.alert-detail-row { display: flex; align-items: center; gap: 8px; }
.alert-location { color: rgba(255,255,255,0.38); font-size: 11px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.alert-overdue { color: #fbbf24; font-size: 10px; flex-shrink: 0; }
.alert-actions { display: flex; flex-direction: column; gap: 4px; flex-shrink: 0; }
.alert-btn {
  padding: 3px 8px;
  font-size: 10px;
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s, border-color 0.15s;
}
.alert-btn--detail {
  background: rgba(79,142,247,0.1);
  border: 1px solid rgba(79,142,247,0.25);
  color: #7bb3fb;
}
.alert-btn--detail:hover { background: rgba(79,142,247,0.2); border-color: rgba(79,142,247,0.5); }
.alert-btn--action {
  background: rgba(245,158,11,0.1);
  border: 1px solid rgba(245,158,11,0.3);
  color: #fbbf24;
}
.alert-btn--action:hover { background: rgba(245,158,11,0.2); border-color: rgba(245,158,11,0.6); }
.alert-more {
  text-align: center;
  padding: 8px 0 2px;
  font-size: 11px;
  color: rgba(255,255,255,0.28);
}

/* ━━ 対応モーダル ━━ */
.action-modal-body { padding: 4px 0; }
.modal-employee { color: #ffffff; font-size: 15px; font-weight: 600; margin-bottom: 4px; }
.modal-location { color: rgba(255,255,255,0.45); font-size: 12px; margin-bottom: 8px; }

/* ━━ クイックアクション (高さ最小化) ━━ */
.action-panel {
  flex-shrink: 0;
  padding: 12px 20px;
}
.action-panel .panel-header {
  margin-bottom: 10px;
}
.action-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.action-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: box-shadow 0.2s, opacity 0.2s;
  overflow: hidden;
}
.action-primary {
  background: linear-gradient(135deg, #4f8ef7 0%, #7c3aed 100%);
  border: none;
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(79,142,247,0.35);
}
.action-primary:hover { box-shadow: 0 6px 24px rgba(79,142,247,0.5); }
.btn-shimmer {
  position: absolute;
  top: 0; left: -75%;
  width: 50%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.22), transparent);
  transform: skewX(-20deg);
  animation: shimmer 2.8s infinite;
}
@keyframes shimmer {
  0%   { left: -75%; }
  60%  { left: 125%; }
  100% { left: 125%; }
}
.action-ghost {
  background: transparent;
  border: 1px solid rgba(255,255,255,0.18);
  color: rgba(255,255,255,0.7);
}
.action-ghost:hover {
  background: rgba(255,255,255,0.07);
  color: #ffffff;
  border-color: rgba(255,255,255,0.3);
}

/* P3-C: 長期利用対応ボタンのバッジ */
.action-alert-btn { position: relative; }
.action-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #f87171;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}
</style>
