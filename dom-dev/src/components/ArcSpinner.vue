<template>
  <svg :width="size" :height="size" viewBox="0 0 36 36" class="arc-spinner">
    <defs>
      <linearGradient :id="`ag-${uid}`" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%"   stop-color="#8b5cf6"/>
        <stop offset="100%" stop-color="#c4b5fd"/>
      </linearGradient>
    </defs>
    <!-- Track -->
    <circle cx="18" cy="18" :r="r" fill="none"
      stroke="rgba(139,92,246,0.18)" :stroke-width="sw"/>
    <!-- Arc -->
    <circle cx="18" cy="18" :r="r" fill="none"
      :stroke="`url(#ag-${uid})`"
      :stroke-width="sw"
      :stroke-dasharray="`${arcLen} ${circ}`"
      stroke-linecap="round"
      transform="rotate(-90 18 18)"/>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size:        { type: Number, default: 20 },
  strokeWidth: { type: Number, default: 3  },
})

const uid    = Math.random().toString(36).slice(2, 7)
const r      = computed(() => 18 - props.strokeWidth / 2)
const circ   = computed(() => 2 * Math.PI * r.value)
const arcLen = computed(() => circ.value * 0.72)
const sw     = computed(() => props.strokeWidth)
</script>

<style scoped>
.arc-spinner {
  display: block;
  flex-shrink: 0;
  animation: arc-spin 0.9s linear infinite;
}
@keyframes arc-spin {
  to { transform: rotate(360deg); }
}
</style>
