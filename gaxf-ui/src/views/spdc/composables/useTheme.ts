import { ref, computed } from 'vue'

export interface ThemeConfig {
  name: string
  bg: string
  panel: string
  accent: string
  accentBg: string
  accentBorder: string
  glow: string
  gradient: string
  chart: string
  btnHover: string
  cssClass?: string
}

const themes: Record<string, ThemeConfig> = {
  default: {
    name: '经典蓝',
    bg: 'bg-[#020617]',
    panel: 'bg-slate-900/40 border-blue-500/20',
    accent: 'text-blue-400',
    accentBg: 'bg-blue-600',
    accentBorder: 'border-blue-500/30',
    glow: 'bg-blue-600/5',
    gradient: 'from-blue-400 to-indigo-300',
    chart: 'bg-blue-500/30',
    btnHover: 'hover:bg-blue-500/20',
    cssClass: 'default-theme'
  },
  police: {
    name: '公安蓝',
    bg: 'bg-police-bg',              // #F5F7FA 浅灰背景
    panel: 'bg-white border-police-border',  // 白色卡片
    accent: 'text-police-primary',   // #1E3A8A 深蓝文本
    accentBg: 'bg-police-blue',      // #2F54EB 标准蓝按钮
    accentBorder: 'border-police-border',
    glow: 'bg-police-light',         // #E6F0FF 浅蓝光晕
    gradient: 'from-police-blue to-police-cyan',  // 标准蓝到青蓝渐变
    chart: 'bg-police-blue/30',
    btnHover: 'hover:bg-police-hover',
    cssClass: 'police-theme'
  },
  crimson: {
    name: '威严红',
    bg: 'bg-[#1a0505]',
    panel: 'bg-red-950/20 border-red-500/20',
    accent: 'text-red-400',
    accentBg: 'bg-red-600',
    accentBorder: 'border-red-500/30',
    glow: 'bg-red-600/5',
    gradient: 'from-red-400 to-orange-300',
    chart: 'bg-red-500/30',
    btnHover: 'hover:bg-red-500/20',
    cssClass: 'crimson-theme'
  },
  midnight: {
    name: '极客黑',
    bg: 'bg-[#0a0a0c]',
    panel: 'bg-zinc-900/60 border-zinc-700/50',
    accent: 'text-zinc-100',
    accentBg: 'bg-zinc-700',
    accentBorder: 'border-zinc-600/50',
    glow: 'bg-zinc-400/5',
    gradient: 'from-zinc-100 to-zinc-500',
    chart: 'bg-zinc-600/30',
    btnHover: 'hover:bg-zinc-800',
    cssClass: 'midnight-theme'
  }
}

const getInitialTheme = () => {
  const saved = localStorage.getItem('spdc-theme')
  return saved && themes[saved] ? saved : 'police'  // 默认公安蓝
}

const currentTheme = ref(getInitialTheme())

const themeConfig = computed<ThemeConfig>(() => themes[currentTheme.value])

const cycleTheme = () => {
  const keys = Object.keys(themes)
  const currentIndex = keys.indexOf(currentTheme.value)
  const nextIndex = (currentIndex + 1) % keys.length
  setTheme(keys[nextIndex])
}

const setTheme = (themeKey: string) => {
  if (!themes[themeKey]) return

  currentTheme.value = themeKey

  const html = document.documentElement
  Object.values(themes).forEach(t => {
    if (t.cssClass) html.classList.remove(t.cssClass)
  })
  if (themes[themeKey].cssClass) {
    html.classList.add(themes[themeKey].cssClass)
  }

  localStorage.setItem('spdc-theme', themeKey)
}

const initTheme = () => {
  setTheme(currentTheme.value)
}

export function useTheme() {
  return {
    currentTheme,
    themeConfig,
    cycleTheme,
    setTheme,
    initTheme,
    themes
  }
}