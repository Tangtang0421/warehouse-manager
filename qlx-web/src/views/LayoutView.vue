<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside-transition">
      <SysAside :isCollapse="isCollapse" />
    </el-aside>

    <el-container>
      <el-header class="header-box">
        <SysHeader :isCollapse="isCollapse" @toggle="handleToggle" />
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
// 引入咱们写好的左侧菜单和顶栏组件
import SysAside from '../components/SysAside.vue'
import SysHeader from '../components/SysHeader.vue'

// 🌟 全局控制菜单折叠的变量，放在这个“大骨架”里统一管理
const isCollapse = ref(false)

// 接收顶栏传来的点击事件，切换折叠状态
const handleToggle = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<style scoped>
html, body, #app { margin: 0; padding: 0; height: 100%; }
.layout-container { height: 100vh; }
.header-box { background-color: #fff; border-bottom: 1px solid #e6e6e6; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.main-content { background-color: #f0f2f5; padding: 20px; }
.aside-transition { transition: width 0.3s ease-in-out; overflow: hidden; }
</style>