<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 20 20"
    fill="none"
    stroke="currentColor"
    :stroke-width="strokeWidth"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="nav-svg"
  >

    <!-- grid: 4-square dashboard -->
    <template v-if="name === 'grid'">
      <rect x="2" y="2" width="7" height="7" rx="1.5"/>
      <rect x="11" y="2" width="7" height="7" rx="1.5"/>
      <rect x="2" y="11" width="7" height="7" rx="1.5"/>
      <rect x="11" y="11" width="7" height="7" rx="1.5"/>
    </template>

    <!-- users: 2 people -->
    <template v-else-if="name === 'users'">
      <circle cx="7.5" cy="6.5" r="2.5"/>
      <path d="M2 17c0-2.5 2.4-4.5 5.5-4.5s5.5 2 5.5 4.5"/>
      <circle cx="14.5" cy="7" r="2"/>
      <path d="M17.5 17c0-2-1.7-3.5-3.5-3.8"/>
    </template>

    <!-- door -->
    <template v-else-if="name === 'door'">
      <path d="M5 2h10a1 1 0 0 1 1 1v15a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1Z"/>
      <circle cx="13" cy="10.5" r="0.9" fill="currentColor" stroke="none"/>
    </template>

    <!-- alert-circle -->
    <template v-else-if="name === 'alert-circle'">
      <circle cx="10" cy="10" r="8"/>
      <line x1="10" y1="6.5" x2="10" y2="11.5"/>
      <circle cx="10" cy="13.8" r="0.8" fill="currentColor" stroke="none"/>
    </template>

    <!-- calculator -->
    <template v-else-if="name === 'calculator'">
      <rect x="3.5" y="2" width="13" height="16.5" rx="1.5"/>
      <rect x="5.5" y="4" width="9" height="3.5" rx="0.8"/>
      <rect x="5.5" y="10.5" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
      <rect x="8.75" y="10.5" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
      <rect x="12" y="10.5" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
      <rect x="5.5" y="14" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
      <rect x="8.75" y="14" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
      <rect x="12" y="14" width="2.5" height="2.5" rx="0.6" fill="currentColor" stroke="none"/>
    </template>

    <!-- document -->
    <template v-else-if="name === 'document'">
      <path d="M5 2h8l4 4v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1Z"/>
      <path d="M13 2v4h4"/>
      <line x1="7" y1="10" x2="13" y2="10"/>
      <line x1="7" y1="13" x2="13" y2="13"/>
      <line x1="7" y1="16" x2="10" y2="16"/>
    </template>

    <!-- house -->
    <template v-else-if="name === 'house'">
      <path d="M3 9.5L10 3l7 6.5"/>
      <path d="M5 8.2V17h4v-4.5h2V17h4V8.2"/>
    </template>

    <!-- package: 3D box -->
    <template v-else-if="name === 'package'">
      <path d="M10 2L2 6.5v7L10 18l8-4.5v-7L10 2Z"/>
      <polyline points="2,6.5 10,11 18,6.5"/>
      <line x1="10" y1="11" x2="10" y2="18"/>
      <line x1="6" y1="4.2" x2="14" y2="8.7"/>
    </template>

    <!-- sliders: 3 tracks with knobs -->
    <template v-else-if="name === 'sliders'">
      <line x1="3" y1="5.5" x2="17" y2="5.5"/>
      <line x1="3" y1="10" x2="17" y2="10"/>
      <line x1="3" y1="14.5" x2="17" y2="14.5"/>
      <circle cx="7.5" cy="5.5" r="2"/>
      <circle cx="13" cy="10" r="2"/>
      <circle cx="7.5" cy="14.5" r="2"/>
    </template>

    <!-- clipboard -->
    <template v-else-if="name === 'clipboard'">
      <rect x="7.5" y="1.5" width="5" height="3" rx="0.8"/>
      <path d="M6.5 2.5H4.5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-14a1 1 0 0 0-1-1h-2"/>
      <line x1="7" y1="9.5" x2="13" y2="9.5"/>
      <line x1="7" y1="12.5" x2="13" y2="12.5"/>
      <line x1="7" y1="15.5" x2="10" y2="15.5"/>
    </template>

    <!-- upload -->
    <template v-else-if="name === 'upload'">
      <path d="M10 12.5V3"/>
      <path d="M6 7L10 3l4 4"/>
      <path d="M3 16.5h14"/>
    </template>

    <!-- wrench -->
    <template v-else-if="name === 'wrench'">
      <path d="M14.5 3c-1.9 0-3.5 1.6-3.5 3.5 0 .4.1.8.2 1.2L4.6 14.8a1.5 1.5 0 1 0 2.1 2.1l6.6-6.6c.4.1.8.2 1.2.2 1.9 0 3.5-1.6 3.5-3.5 0-.5-.1-1-.3-1.5L15.5 7.7 13.8 6l2.2-2.2C15.5 3.3 15 3 14.5 3Z"/>
    </template>

    <!-- user: single person -->
    <template v-else-if="name === 'user'">
      <circle cx="10" cy="7" r="3.5"/>
      <path d="M3 18c0-3.3 3.1-6 7-6s7 2.7 7 6"/>
    </template>

    <!-- building: office building -->
    <template v-else-if="name === 'building'">
      <rect x="3" y="4" width="14" height="14" rx="1"/>
      <rect x="5.5" y="7" width="3" height="3" rx="0.5"/>
      <rect x="11.5" y="7" width="3" height="3" rx="0.5"/>
      <rect x="5.5" y="12" width="3" height="3" rx="0.5"/>
      <rect x="11.5" y="12" width="3" height="3" rx="0.5"/>
      <path d="M8 18v-3.5h4V18"/>
      <line x1="3" y1="4" x2="17" y2="4"/>
    </template>

    <!-- log-file: document with bullet list -->
    <template v-else-if="name === 'log-file'">
      <path d="M5 2h8l4 4v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1Z"/>
      <path d="M13 2v4h4"/>
      <circle cx="7.5" cy="9.5" r="0.7" fill="currentColor" stroke="none"/>
      <line x1="9.5" y1="9.5" x2="13" y2="9.5"/>
      <circle cx="7.5" cy="12.5" r="0.7" fill="currentColor" stroke="none"/>
      <line x1="9.5" y1="12.5" x2="13" y2="12.5"/>
      <circle cx="7.5" cy="15.5" r="0.7" fill="currentColor" stroke="none"/>
      <line x1="9.5" y1="15.5" x2="11.5" y2="15.5"/>
    </template>

  </svg>
</template>

<script setup>
defineProps({
  name:        { type: String, required: true },
  size:        { type: Number, default: 16    },
  strokeWidth: { type: Number, default: 1.6   },
})
</script>

<style scoped>
.nav-svg {
  display: block;
  flex-shrink: 0;
}
</style>
