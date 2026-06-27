<template>
  <div class="layout-bg">
    <!-- 背景レイヤー -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="grid-overlay"></div>

    <el-container class="layout-wrapper">
      <el-aside :width="appStore.sidebarCollapsed ? '64px' : '210px'" class="sidebar-wrapper">
        <Sidebar />
      </el-aside>
      <el-container>
        <el-header class="header-wrapper">
          <Header />
        </el-header>
        <el-main class="main-wrapper">
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <SystemAnnouncement />
  </div>
</template>

<script setup>
import { useAppStore } from '@/store/modules/app'
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import SystemAnnouncement from '@/components/SystemAnnouncement.vue'

const appStore = useAppStore()
</script>

<style scoped>
.layout-bg {
  height: 100vh;
  background: linear-gradient(135deg, #060d1f 0%, #0d1b3e 50%, #111827 100%);
  position: relative;
  overflow: hidden;
}

.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.025) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
  z-index: var(--z-bg); /* 0 */
}
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  pointer-events: none;
  z-index: var(--z-bg); /* 0 */
}
.orb-1 {
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(79,142,247,0.14) 0%, transparent 70%);
  top: -200px; left: -150px;
}
.orb-2 {
  width: 450px; height: 450px;
  background: radial-gradient(circle, rgba(124,58,237,0.12) 0%, transparent 70%);
  bottom: -120px; right: -100px;
}

.layout-wrapper {
  height: 100vh;
  position: relative;
  z-index: var(--z-layout); /* 1 */
}
.sidebar-wrapper {
  background: linear-gradient(180deg, #0c1d38 0%, #091628 50%, #07121f 100%);
  border-right: 1px solid rgba(255,255,255,0.08);
  box-shadow: 4px 0 28px rgba(0,0,0,0.42), inset -1px 0 0 rgba(255,255,255,0.04);
  transition: width 0.3s;
  overflow: hidden;
}
.header-wrapper {
  height: var(--header-height);
  padding: 0;
  position: relative;
  z-index: var(--z-header); /* 10 — backdrop-filter が stacking context を作るため明示 */
  background: rgba(255,255,255,0.03);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255,255,255,0.07);
}
.main-wrapper {
  background: transparent;
  padding: 0;
  overflow-y: auto;
}
</style>
