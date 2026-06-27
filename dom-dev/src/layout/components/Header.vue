<template>
  <div class="header">
    <div class="left">
      <el-icon class="toggle-btn" @click="appStore.toggleSidebar()">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <Breadcrumb />
    </div>
    <div class="right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-avatar size="small" icon="UserFilled" class="avatar" />
          <span class="username">{{ authStore.userInfo?.username }}</span>
          <el-icon class="arrow"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">ログアウト</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAppStore } from '@/store/modules/app'
import { useAuthStore } from '@/store/modules/auth'
import Breadcrumb from './Breadcrumb.vue'

const appStore = useAppStore()
const authStore = useAuthStore()
const router = useRouter()

const handleCommand = async (cmd) => {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('ログアウトしますか？', '確認', { type: 'warning' })
    await authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.left { display: flex; align-items: center; gap: 12px; }
.right { display: flex; align-items: center; }

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: rgba(255,255,255,0.5);
  transition: color 0.15s;
}
.toggle-btn:hover { color: rgba(255,255,255,0.9); }

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: rgba(255,255,255,0.7);
  transition: color 0.15s;
}
.user-info:hover { color: #ffffff; }

.avatar {
  background: linear-gradient(135deg, #4f8ef7, #7c3aed);
  border: 1px solid rgba(255,255,255,0.15);
}
.username { font-size: 13px; }
.arrow { font-size: 12px; opacity: 0.6; }
</style>
