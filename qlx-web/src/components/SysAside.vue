<template>
  <div class="aside-container">
    <div class="logo">
      <h2>🚀 OA 系统</h2>
    </div>
    <el-menu
      active-text-color="#ffd04b"
      background-color="#304156"
      text-color="#fff"
      default-active="1"
      class="border-none-menu"
    >
      <el-menu-item index="1">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="2">
        <el-icon><Setting /></el-icon>
        <span>系统设置</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { User, Setting } from '@element-plus/icons-vue'
</script>

<style scoped>

.aside-container {
  background-color: #304156;
  height: 100%;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  border-bottom: 1px solid #1f2d3d;
}
.border-none-menu {
  border-right: none;
}
</style>