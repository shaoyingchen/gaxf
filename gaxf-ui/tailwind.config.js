/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        // 公安蓝主题色系 - 公安系统标准配色
        police: {
          primary: '#001529',    // 深蓝 - 侧边栏
          blue: '#1677FF',       // 标准蓝 - 按钮、活动状态、focus
          light: '#E6F4FF',      // 浅蓝背景
          hover: '#4096FF',      // hover状态
          bg: '#F5F5F5',         // 主背景
          bgAlt: '#FAFAFA',      // 表头背景
          card: '#FFFFFF',       // 卡片背景
          text: '#1F2937',       // 主文本
          muted: '#6B7280',      // 次文本
          border: '#D1D5DB',     // 表单边框
          borderLight: '#E5E7EB', // 表格边框
          success: '#10B981',    // 成功
          warning: '#F59E0B',    // 警告
          danger: '#EF4444',     // 危险
          cyan: '#06B6D4',       // 辅助蓝
          // Tailwind 兼容色阶
          50: '#E6F4FF',
          100: '#BAE7FF',
          200: '#91CAFF',
          300: '#69B1FF',
          400: '#4096FF',
          500: '#1677FF',
          600: '#0958D9',
          700: '#003EB3',
          800: '#002C8C',
          900: '#001529',
        }
      },
      boxShadow: {
        'police-sm': '0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06)',
        'police-md': '0 4px 6px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.06)',
        'police-lg': '0 10px 15px rgba(0, 0, 0, 0.1), 0 4px 6px rgba(0, 0, 0, 0.05)',
      },
      borderRadius: {
        'police': '12px',
      }
    }
  },
  plugins: []
}