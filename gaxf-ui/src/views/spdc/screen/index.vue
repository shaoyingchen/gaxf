<template>
  <div class="flex items-center justify-center h-full animate-in fade-in duration-500">
    <div :class="`${theme.panel} border rounded-2xl p-12 text-center`">
      <Monitor :size="48" :class="`${theme.accent} mx-auto mb-4`" />
      <h2 class="text-xl font-bold text-slate-200 mb-2">大屏设计</h2>
      <p class="text-sm text-slate-400">大屏展示模块开发中，敬请期待...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Monitor } from 'lucide-vue-next'
import { useTheme } from '../composables/useTheme'

const { themeConfig } = useTheme()
const theme = computed(() => themeConfig.value)
</script>
