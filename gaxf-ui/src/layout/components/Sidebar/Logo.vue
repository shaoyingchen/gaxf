<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }">
    <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
      <div class="logo-icon">
        <svg-icon icon-class="dashboard" />
      </div>
    </router-link>
    <router-link v-else key="expand" class="sidebar-logo-link" to="/">
      <div class="logo-icon">
        <svg-icon icon-class="dashboard" />
      </div>
      <div class="logo-text-area">
        <h1 class="sidebar-title">智慧·公安</h1>
        <p class="sidebar-subtitle">智能视频监控系统</p>
      </div>
    </router-link>
  </div>
</template>

<script setup lang="ts">
defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  height: 80px;
  background: transparent;
  padding: 24px 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);

  &.collapse {
    padding: 16px;
    justify-content: center;
  }

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
    height: 100%;
  }

  .logo-icon {
    background: #3B82F6;
    width: 38px;
    height: 38px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 6px 12px -6px rgba(0, 0, 0, 0.3);
    font-size: 22px;

    .svg-icon {
      width: 22px;
      height: 22px;
    }
  }

  .logo-text-area {
    flex: 1;
  }

  .sidebar-title {
    display: block;
    margin: 0;
    color: white;
    font-weight: 600;
    font-size: 18px;
    letter-spacing: 1px;
  }

  .sidebar-subtitle {
    display: block;
    margin: 4px 0 0 0;
    color: rgba(255, 255, 255, 0.8);
    font-size: 11px;
  }
}
</style>