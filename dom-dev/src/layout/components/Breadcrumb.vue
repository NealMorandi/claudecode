<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/dashboard' }">ホーム</el-breadcrumb-item>
    <el-breadcrumb-item v-if="route.meta?.menuGroup">
      {{ route.meta.menuGroup }}
    </el-breadcrumb-item>
    <el-breadcrumb-item v-if="route.meta?.title">{{ route.meta.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
:deep(.el-breadcrumb__item .el-breadcrumb__inner) {
  color: rgba(255,255,255,0.45);
  font-size: 13px;
  transition: color 0.15s;
}
:deep(.el-breadcrumb__item .el-breadcrumb__inner a:hover),
:deep(.el-breadcrumb__item .el-breadcrumb__inner:hover) {
  color: rgba(255,255,255,0.85);
}
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: rgba(255,255,255,0.75);
  font-weight: 500;
}
:deep(.el-breadcrumb__separator) {
  color: rgba(255,255,255,0.2);
}
</style>
