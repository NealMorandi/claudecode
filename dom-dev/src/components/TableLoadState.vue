<template>
  <div class="tls-root">

    <!-- Skeleton rows -->
    <div
      v-for="(row, ri) in ROWS"
      :key="ri"
      class="tls-row"
      :style="{ '--d': `${ri * 80}ms` }"
    >
      <div
        v-for="(w, ci) in row"
        :key="ci"
        class="tls-bar"
        :style="{ width: w + '%' }"
      />
    </div>

    <!-- Bottom search status -->
    <div class="tls-status">
      <ArcSpinner :size="15" :stroke-width="2.4" />
      <span class="tls-text">検索中...</span>
    </div>

  </div>
</template>

<script setup>
import ArcSpinner from './ArcSpinner.vue'

const ROWS = [
  [7, 27, 18, 21, 11],
  [7, 22, 21, 17, 13],
  [7, 30, 15, 22, 10],
  [7, 24, 23, 15, 14],
  [7, 28, 17, 20, 12],
  [7, 21, 24, 18, 13],
  [7, 26, 19, 21, 11],
]
</script>

<style scoped>
.tls-root {
  width: 100%;
}

/* Skeleton row */
.tls-row {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 44px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255,255,255,0.04);
}

/* Shimmer bar */
.tls-bar {
  height: 11px;
  border-radius: 6px;
  background: rgba(255,255,255,0.06);
  position: relative;
  overflow: hidden;
}
.tls-bar::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(139, 92, 246, 0.16) 38%,
    rgba(196, 181, 253, 0.24) 52%,
    rgba(139, 92, 246, 0.16) 65%,
    transparent 100%
  );
  animation: tls-wave 1.5s ease-in-out infinite;
  animation-delay: var(--d, 0ms);
  transform: translateX(-100%);
}
@keyframes tls-wave {
  to { transform: translateX(100%); }
}

/* Bottom status bar */
.tls-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  border-top: 1px solid rgba(139, 92, 246, 0.22);
  background: rgba(139, 92, 246, 0.05);
}
.tls-text {
  color: rgba(196, 181, 253, 0.82);
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.05em;
}
</style>
