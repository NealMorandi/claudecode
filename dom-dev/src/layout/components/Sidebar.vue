<template>
  <div class="sidebar" :class="{ 'sidebar--collapsed': appStore.sidebarCollapsed }">

    <!-- ━━ ブランドエリア ━━ -->
    <div class="brand-area">
      <div class="brand-glow-line"></div>
      <div class="brand-content">
        <div class="logo-icon">
          <svg width="34" height="34" viewBox="0 0 34 34" fill="none">
            <rect width="34" height="34" rx="10" fill="url(#sb-grad)"/>
            <path d="M17 6.5L28 12.5V21.5L17 27.5L6 21.5V12.5L17 6.5Z"
              stroke="white" stroke-width="1.4" fill="none" opacity="0.7"/>
            <circle cx="17" cy="17" r="3.5" fill="white" opacity="0.92"/>
            <defs>
              <linearGradient id="sb-grad" x1="0" y1="0" x2="34" y2="34">
                <stop offset="0%" stop-color="#4f8ef7"/>
                <stop offset="100%" stop-color="#7c3aed"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div v-if="!appStore.sidebarCollapsed" class="logo-texts">
          <p class="logo-title">寮管理</p>
          <p class="logo-sub">DORMITORY SYSTEM</p>
        </div>
      </div>
    </div>

    <!-- ━━ ナビゲーション ━━ -->
    <nav class="nav-body">
      <template v-for="item in menuItems" :key="item.key">

        <!-- グループ -->
        <div v-if="item.type === 'group'" class="nav-group">
          <div
            v-if="!appStore.sidebarCollapsed"
            class="nav-section"
            @click="toggleGroup(item.key)"
          >
            <span class="section-name">{{ item.title }}</span>
            <span class="section-line"></span>
            <el-icon class="section-chevron" :class="{ 'section-chevron--up': !isGroupCollapsed(item.key) }">
              <ArrowRight />
            </el-icon>
          </div>
          <div v-else class="section-divider"></div>

          <div class="group-children" :class="{ 'group-children--hidden': isGroupCollapsed(item.key) }">
            <div
              v-for="child in item.children"
              :key="child.path"
              class="nav-item"
              :class="{ 'nav-item--active': isActive(child.path) }"
              @click="navigate(child.path)"
            >
              <div class="nav-item-inner">
                <NavIcon :name="child.meta.icon" class="nav-icon" />
                <span v-if="!appStore.sidebarCollapsed" class="nav-label">{{ child.meta.title }}</span>
                <span
                  v-if="!appStore.sidebarCollapsed && badges[child.path]"
                  class="nav-badge"
                  :class="`nav-badge--${badges[child.path].type}`"
                >{{ badges[child.path].text }}</span>
              </div>
              <span v-if="isActive(child.path) && !appStore.sidebarCollapsed" class="nav-glow-dot"></span>
            </div>
          </div>
        </div>

        <!-- 単体アイテム -->
        <div
          v-else
          class="nav-item"
          :class="{ 'nav-item--active': isActive(item.path) }"
          @click="navigate(item.path)"
        >
          <div class="nav-item-inner">
            <NavIcon :name="item.meta?.icon" class="nav-icon" />
            <span v-if="!appStore.sidebarCollapsed" class="nav-label">{{ item.meta?.title }}</span>
          </div>
          <span v-if="isActive(item.path) && !appStore.sidebarCollapsed" class="nav-glow-dot"></span>
        </div>

      </template>
    </nav>

    <!-- ━━ フッター ━━ -->
    <div class="sidebar-footer">
      <!-- ユーザーカード -->
      <div v-if="!appStore.sidebarCollapsed" class="user-card">
        <div class="user-avatar">{{ avatarChar }}</div>
        <div class="user-info">
          <p class="user-name">{{ authStore.userInfo?.username || 'admin' }}</p>
          <p class="user-role">{{ roleLabel }}</p>
        </div>
        <el-icon class="user-arrow"><ArrowDown /></el-icon>
      </div>
      <div v-else class="user-avatar-center">{{ avatarChar }}</div>

      <!-- システム状態 -->
      <div class="status-bar" :class="{ 'status-bar--collapsed': appStore.sidebarCollapsed }">
        <span class="status-dot"></span>
        <span v-if="!appStore.sidebarCollapsed" class="status-text">システム正常稼働中</span>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { useAuthStore } from '@/store/modules/auth'
import { constantRoutes } from '@/router'
import { getSummary } from '@/api/dashboard'
import NavIcon from '@/components/NavIcon.vue'

const appStore  = useAppStore()
const authStore = useAuthStore()
const route  = useRoute()
const router = useRouter()

const collapsedGroups = ref(new Set())
const toggleGroup = (key) => {
  const next = new Set(collapsedGroups.value)
  next.has(key) ? next.delete(key) : next.add(key)
  collapsedGroups.value = next
}
const isGroupCollapsed = (key) => collapsedGroups.value.has(key)

const badges = reactive({})

onMounted(async () => {
  try {
    const summary = await getSummary()
    if (summary.alertCount > 0) {
      badges['/long-term-alerts'] = { text: String(summary.alertCount), type: 'amber' }
    }
    if (summary.unconfirmedRent > 0) {
      badges['/rent/history'] = { text: String(summary.unconfirmedRent), type: 'red' }
    }
  } catch {
    // バッジ取得失敗時は非表示のまま
  }
})

const isActive = (path) => route.path === path

const navigate = (path) => router.push(path.startsWith('/') ? path : `/${path}`)

const avatarChar = computed(() => {
  const src = authStore.userInfo?.name || authStore.userInfo?.username || 'A'
  return src.charAt(0).toUpperCase()
})

const roleLabel = computed(() => ({
  admin:  'システム管理者',
  staff:  'スタッフ',
  viewer: '閲覧者',
}[authStore.role] || 'ゲスト'))

const menuItems = computed(() => {
  const children = constantRoutes.find((r) => r.path === '/')?.children || []
  const visible = children.filter((r) => {
    if (r.meta?.hidden) return false
    if (!r.meta?.roles) return true
    return r.meta.roles.includes(authStore.role)
  })

  const result = []
  const groups = {}

  for (const r of visible) {
    const groupName = r.meta?.menuGroup
    if (groupName) {
      if (!groups[groupName]) {
        groups[groupName] = {
          type: 'group',
          key: `group-${groupName}`,
          title: groupName,
          children: [],
        }
        result.push(groups[groupName])
      }
      groups[groupName].children.push(r)
    } else {
      result.push({ type: 'item', key: r.path, ...r })
    }
  }

  return result
})
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  user-select: none;
}

/* ━━ ブランドエリア ━━ */
.brand-area {
  flex-shrink: 0;
  position: relative;
  border-bottom: 1px solid rgba(255,255,255,0.07);
}
.brand-glow-line {
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #3b82f6 35%, #8b5cf6 65%, transparent 100%);
  opacity: 1;
}
.brand-content {
  display: flex;
  align-items: center;
  gap: 11px;
  height: 62px;
  padding: 0 14px;
}
.logo-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  filter: drop-shadow(0 0 10px rgba(79,142,247,0.55));
}
.sidebar--collapsed .brand-content {
  justify-content: center;
  padding: 0;
}
.logo-texts { min-width: 0; }
.logo-title {
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.04em;
  line-height: 1.2;
}
.logo-sub {
  color: rgba(255,255,255,0.3);
  font-size: 9px;
  font-weight: 600;
  letter-spacing: 0.18em;
  margin-top: 2px;
}

/* ━━ ナビゲーション ━━ */
.nav-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 8px 0 4px;
}
.nav-body::-webkit-scrollbar { width: 0; }

/* セクションヘッダー */
.nav-group { margin-bottom: 2px; }
.nav-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px 5px;
}
.nav-section {
  cursor: pointer;
  user-select: none;
  border-radius: 6px;
  transition: background 0.15s;
}
.nav-section:hover { background: rgba(255,255,255,0.03); }

.section-name {
  color: rgba(255,255,255,0.5);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.04em;
  white-space: nowrap;
  flex-shrink: 0;
  transition: color 0.15s;
}
.nav-section:hover .section-name { color: rgba(255,255,255,0.7); }
.section-line {
  flex: 1;
  height: 1px;
  background: rgba(255,255,255,0.06);
  display: block;
}
.section-chevron {
  color: rgba(255,255,255,0.28);
  font-size: 11px;
  flex-shrink: 0;
  transform: rotate(90deg);
  transition: transform 0.22s ease, color 0.15s;
}
.section-chevron--up {
  transform: rotate(270deg);
}
.nav-section:hover .section-chevron { color: rgba(255,255,255,0.5); }

/* 折りたたみアニメーション */
.group-children {
  overflow: hidden;
  max-height: 500px;
  transition: max-height 0.28s ease;
}
.group-children--hidden {
  max-height: 0;
}
.section-divider {
  height: 1px;
  margin: 8px 10px;
  background: rgba(255,255,255,0.07);
}

/* ━━ ナビゲーションアイテム ━━ */
.nav-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 38px;
  margin: 1px 8px 1px 2px;
  padding: 0 8px 0 14px;
  border-left: 2px solid transparent;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.nav-item:hover:not(.nav-item--active) {
  background: rgba(255,255,255,0.045);
  border-left-color: rgba(255,255,255,0.2);
}
.nav-item--active {
  background: linear-gradient(90deg, rgba(59,130,246,0.20) 0%, rgba(59,130,246,0.05) 65%, transparent 100%);
  border-left-color: #3b82f6;
}

/* collapsed */
.sidebar--collapsed .nav-item {
  justify-content: center;
  margin: 1px 4px;
  padding: 0;
  border-left: none;
  border-radius: 8px;
}
.sidebar--collapsed .nav-item--active {
  background: rgba(59,130,246,0.2);
}

.nav-item-inner {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.nav-icon {
  color: rgba(255,255,255,0.45);
  flex-shrink: 0;
  transition: color 0.15s;
}
.nav-item:hover .nav-icon,
.nav-item--active .nav-icon { color: #ffffff; }

.nav-label {
  color: rgba(255,255,255,0.52);
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.15s;
}
.nav-item:hover .nav-label { color: rgba(255,255,255,0.72); }
.nav-item--active .nav-label { color: #ffffff; font-weight: 500; }

/* 通知バッジ */
.nav-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 16px;
  padding: 2px 6px;
  border-radius: 9px;
  font-size: 9.5px;
  font-weight: 700;
  flex-shrink: 0;
  line-height: 1;
}
.nav-badge--amber {
  background: rgba(245,158,11,0.22);
  color: #fbbf24;
  border: 1px solid rgba(245,158,11,0.4);
}
.nav-badge--red {
  background: rgba(248,113,113,0.2);
  color: #fca5a5;
  border: 1px solid rgba(248,113,113,0.38);
}

/* アクティブ右端光点 */
.nav-glow-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 6px rgba(59,130,246,0.85), 0 0 14px rgba(59,130,246,0.35);
  flex-shrink: 0;
  margin-right: 3px;
}

/* ━━ フッター ━━ */
.sidebar-footer {
  flex-shrink: 0;
  border-top: 1px solid rgba(255,255,255,0.07);
  padding: 10px 0 0;
}

/* ユーザーカード */
.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  margin: 0 8px 6px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
}
.user-card:hover { background: rgba(255,255,255,0.07); }

.user-avatar {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  color: #ffffff; font-size: 13px; font-weight: 700;
  box-shadow: 0 2px 8px rgba(37,99,235,0.35);
  flex-shrink: 0;
}
.user-avatar-center {
  width: 32px; height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  display: flex; align-items: center; justify-content: center;
  color: #ffffff; font-size: 13px; font-weight: 700;
  box-shadow: 0 2px 8px rgba(37,99,235,0.35);
  margin: 0 auto 8px;
  cursor: pointer;
}

.user-info { flex: 1; min-width: 0; }
.user-name {
  color: rgba(255,255,255,0.82);
  font-size: 11.5px; font-weight: 600;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.user-role {
  color: rgba(255,255,255,0.32);
  font-size: 9.5px; margin-top: 1px;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.user-arrow {
  color: rgba(255,255,255,0.3);
  font-size: 12px; flex-shrink: 0;
}

/* システム状態 */
.status-bar {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 8px 14px 12px;
}
.status-bar--collapsed { justify-content: center; padding: 8px 0 12px; }
.status-dot {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: #22c55e;
  box-shadow: 0 0 6px rgba(34,197,94,0.8);
  flex-shrink: 0;
  animation: breathe 2s ease-in-out infinite;
}
@keyframes breathe {
  0%, 100% { opacity: 1;   box-shadow: 0 0 6px rgba(34,197,94,0.8); }
  50%       { opacity: 0.4; box-shadow: 0 0 2px rgba(34,197,94,0.4); }
}
.status-text {
  color: rgba(255,255,255,0.28);
  font-size: 10.5px;
  white-space: nowrap;
}
</style>
