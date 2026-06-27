<template>
  <Teleport to="body">
    <template v-if="appStore.announcement.visible && appStore.announcement.text">
      <!-- スクリム: position:fixed; inset:0; z-index:9998 -->
      <div class="sys-scrim" @click="appStore.dismissAnnouncement" />

      <!-- カード: position:fixed; top:50%; left:50%; transform:translate(-50%,-50%); z-index:9999 -->
      <div class="sys-announcement">
        <p class="sys-announcement__text">{{ appStore.announcement.text }}</p>
        <button class="sys-announcement__close" @click="appStore.dismissAnnouncement">×</button>
      </div>
    </template>
  </Teleport>
</template>

<script setup>
import { useAppStore } from '@/store/modules/app'
const appStore = useAppStore()
</script>

<style scoped>
/* スクリム — 全画面暗転。サイドメニュー・ヘッダー等すべての親要素から独立 */
.sys-scrim {
  position: fixed;
  inset: 0;
  z-index: calc(var(--z-toast) - 1); /* 9998 */
  background: rgba(5, 10, 25, 0.72);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  cursor: pointer;
}

/* カード — ビューポートを基準に画面中央固定 */
.sys-announcement {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: var(--z-toast); /* 9999 */
  background: rgba(15, 23, 41, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 14px;
  padding: 28px 48px 24px;
  min-width: 320px;
  max-width: min(520px, calc(100vw - 32px)); /* 狭いビューポートでも画面内に収める */
  width: max-content;
  text-align: center;
  box-shadow: 0 8px 48px rgba(0, 0, 0, 0.65), 0 0 0 1px rgba(79, 142, 247, 0.08);
}

/* 狭いビューポート（320px 以下）: パディング縮小 */
@media (max-width: 400px) {
  .sys-announcement {
    min-width: 0;
    padding: 24px 24px 20px;
  }
}

.sys-announcement__text {
  color: #e8edf5;
  font-size: 15px;
  line-height: 1.7;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.sys-announcement__close {
  position: absolute;
  top: 10px;
  right: 14px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.38);
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: color 0.2s;
}

.sys-announcement__close:hover {
  color: rgba(255, 255, 255, 0.75);
}
</style>
