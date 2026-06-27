<template>
  <div
    ref="rootRef"
    class="asel"
    :class="[
      `asel--${size || 'default'}`,
      {
        'asel--open':     isOpen,
        'asel--disabled': disabled,
        'asel--focus':    isOpen,
      },
    ]"
    @keydown="onKeydown"
  >
    <!-- ── Trigger ── -->
    <div class="asel__trigger" @click="toggleOpen" tabindex="0">
      <input
        v-if="filterable && isOpen"
        ref="filterRef"
        v-model="filterQuery"
        class="asel__filter-input"
        :placeholder="displayLabel || placeholder"
        @input="onFilterInput"
        @click.stop
      />
      <span v-else class="asel__label" :class="{ 'asel__label--placeholder': !hasValue }">
        {{ displayLabel || placeholder }}
      </span>

      <span class="asel__icons">
        <el-icon v-if="loading" class="asel__icon asel__icon--spin"><Loading /></el-icon>
        <el-icon
          v-else-if="clearable && hasValue && !disabled"
          class="asel__icon asel__icon--clear"
          @mousedown.prevent="handleClear"
        ><CircleClose /></el-icon>
        <el-icon class="asel__icon asel__icon--arrow" :class="{ 'asel__icon--up': isOpen }">
          <ArrowDown />
        </el-icon>
      </span>
    </div>

    <!-- ── Dropdown (teleported) ── -->
    <Teleport to="body">
      <Transition name="asel-drop">
        <div
          v-if="isOpen"
          ref="popperRef"
          class="asel__popper"
          :style="popperStyle"
        >
          <ul class="asel__list">
            <li v-if="loading" class="asel__state">読み込み中…</li>
            <li v-else-if="filteredOptions.length === 0" class="asel__state">データなし</li>
            <li
              v-for="opt in filteredOptions"
              :key="String(opt.value)"
              class="asel__item"
              :class="{
                'asel__item--selected': opt.value === modelValue,
                'asel__item--disabled': opt.disabled,
              }"
              @mousedown.prevent="pickOption(opt)"
            >
              <span class="asel__item-label">{{ opt.label }}</span>
              <el-icon v-if="opt.value === modelValue" class="asel__item-check"><Check /></el-icon>
            </li>
          </ul>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelValue:   { default: null },
  options:      { type: Array, default: () => [] },
  placeholder:  { type: String, default: '選択してください' },
  clearable:    { type: Boolean, default: false },
  disabled:     { type: Boolean, default: false },
  size:         { type: String, default: 'default' },
  filterable:   { type: Boolean, default: false },
  remote:       { type: Boolean, default: false },
  remoteMethod: { type: Function, default: null },
  loading:      { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue', 'change'])

const rootRef   = ref(null)
const popperRef = ref(null)
const filterRef = ref(null)

const isOpen      = ref(false)
const filterQuery = ref('')
const popperStyle = ref({})

const hasValue = computed(() =>
  props.modelValue !== null && props.modelValue !== undefined && props.modelValue !== ''
)

const displayLabel = computed(() => {
  if (!hasValue.value) return ''
  const found = props.options.find((o) => o.value === props.modelValue)
  return found ? found.label : String(props.modelValue)
})

const filteredOptions = computed(() => {
  if (!props.filterable || props.remote || !filterQuery.value) return props.options
  const q = filterQuery.value.toLowerCase()
  return props.options.filter((o) => o.label.toLowerCase().includes(q))
})

// ── positioning ──────────────────────────────────────────
const updatePopper = () => {
  if (!rootRef.value) return
  const rect            = rootRef.value.getBoundingClientRect()
  const viewH           = window.innerHeight
  const dropH           = Math.min(filteredOptions.value.length * 34 + 12, 228)
  const spaceBelow      = viewH - rect.bottom - 6
  const placeAbove      = spaceBelow < dropH && rect.top > dropH

  popperStyle.value = {
    position: 'fixed',
    width:    rect.width + 'px',
    left:     rect.left  + 'px',
    zIndex:   2500,
    ...(placeAbove
      ? { bottom: (viewH - rect.top + 4) + 'px' }
      : { top:    (rect.bottom + 4)      + 'px' }),
  }
}

// ── open / close ─────────────────────────────────────────
const open = async () => {
  if (props.disabled) return
  isOpen.value = true
  filterQuery.value = ''
  await nextTick()
  updatePopper()
  if (props.filterable) filterRef.value?.focus()
  if (props.remote && props.remoteMethod) props.remoteMethod('')
}

const close = () => {
  isOpen.value    = false
  filterQuery.value = ''
}

const toggleOpen = () => (isOpen.value ? close() : open())

// ── selection ────────────────────────────────────────────
const pickOption = (opt) => {
  if (opt.disabled) return
  emit('update:modelValue', opt.value)
  emit('change', opt.value)
  close()
}

const handleClear = () => {
  emit('update:modelValue', null)
  emit('change', null)
}

// ── filter input ─────────────────────────────────────────
const onFilterInput = () => {
  if (props.remote && props.remoteMethod) {
    props.remoteMethod(filterQuery.value)
  }
  nextTick(updatePopper)
}

// ── keyboard ─────────────────────────────────────────────
const onKeydown = (e) => {
  if (e.key === 'Escape') close()
  if (e.key === 'Enter' && !isOpen.value) open()
}

// ── outside click ────────────────────────────────────────
const onOutsideClick = (e) => {
  if (
    isOpen.value &&
    !rootRef.value?.contains(e.target) &&
    !popperRef.value?.contains(e.target)
  ) {
    close()
  }
}

onMounted(()      => document.addEventListener('mousedown', onOutsideClick))
onBeforeUnmount(() => document.removeEventListener('mousedown', onOutsideClick))

// scroll / resize も追従
watch(isOpen, (v) => {
  if (v) {
    window.addEventListener('scroll', updatePopper, true)
    window.addEventListener('resize', updatePopper)
  } else {
    window.removeEventListener('scroll', updatePopper, true)
    window.removeEventListener('resize', updatePopper)
  }
})
</script>

<style scoped>
/* ── Root ───────────────────────────────── */
.asel {
  position: relative;
  display: inline-block;
  width: 100%;
  font-size: 14px;
  cursor: pointer;
  outline: none;
}
.asel--disabled { pointer-events: none; opacity: 0.5; }

/* ── Trigger ────────────────────────────── */
.asel__trigger {
  display: flex;
  align-items: center;
  height: 32px;
  padding: 0 10px;
  background: #0f1729;
  border-radius: 8px;
  box-shadow: 0 0 0 1px rgba(99,132,255,0.22) inset;
  transition: box-shadow 0.18s;
  user-select: none;
}
.asel--small .asel__trigger  { height: 24px; padding: 0 8px; font-size: 12px; border-radius: 6px; }
.asel--open  .asel__trigger,
.asel--focus .asel__trigger  {
  box-shadow: 0 0 0 1px #6366f1 inset, 0 0 0 3px rgba(99,102,241,0.18);
}
.asel__trigger:hover:not(.asel--open .asel__trigger) {
  box-shadow: 0 0 0 1px rgba(99,132,255,0.45) inset;
}

/* ── Label ──────────────────────────────── */
.asel__label {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: #c8d6f0;
}
.asel__label--placeholder { color: #4a6480; }

/* ── Filter input ───────────────────────── */
.asel__filter-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: #c8d6f0;
  font-size: inherit;
  padding: 0;
}
.asel__filter-input::placeholder { color: #4a6480; }

/* ── Icons ──────────────────────────────── */
.asel__icons {
  display: flex;
  align-items: center;
  gap: 2px;
  flex-shrink: 0;
  margin-left: 6px;
}
.asel__icon {
  font-size: 13px;
  color: #4a6480;
  transition: color 0.15s, transform 0.2s;
}
.asel__icon--arrow.asel__icon--up { transform: rotate(180deg); }
.asel__icon--clear:hover { color: #8ba3c0; }
.asel__icon--spin {
  animation: asel-spin 1s linear infinite;
}
@keyframes asel-spin { to { transform: rotate(360deg); } }

/* ── Popper ─────────────────────────────── */
.asel__popper {
  background: #0f1729;
  border: 1px solid rgba(99,132,255,0.22);
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.55);
  padding: 4px 0;
  overflow: hidden;
}

/* ── List ───────────────────────────────── */
.asel__list {
  list-style: none;
  max-height: 228px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: rgba(99,132,255,0.25) transparent;
}
.asel__list::-webkit-scrollbar { width: 4px; }
.asel__list::-webkit-scrollbar-thumb {
  background: rgba(99,132,255,0.25);
  border-radius: 2px;
}

.asel__state {
  padding: 10px 14px;
  color: #4a6480;
  font-size: 13px;
}

/* ── Item ───────────────────────────────── */
.asel__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  height: 34px;
  cursor: pointer;
  transition: background 0.12s;
  color: #8ba3c0;
  font-size: 13px;
}
.asel__item:hover,
.asel__item--selected {
  background: rgba(99,102,241,0.14);
}
.asel__item--selected { color: #818cf8; font-weight: 500; }
.asel__item--disabled { opacity: 0.45; pointer-events: none; }

.asel__item-label {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.asel__item-check {
  font-size: 12px;
  color: #818cf8;
  flex-shrink: 0;
  margin-left: 6px;
}

/* ── Transition ─────────────────────────── */
.asel-drop-enter-active { transition: opacity 0.14s, transform 0.14s; }
.asel-drop-leave-active { transition: opacity 0.10s, transform 0.10s; }
.asel-drop-enter-from,
.asel-drop-leave-to    { opacity: 0; transform: translateY(-6px) scaleY(0.96); }
</style>
