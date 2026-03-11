<template>
  <div class="aside-container">
    
    <div class="logo">
      <h2 v-show="!isCollapse">🚀 OA 系统</h2>
      <h2 v-show="isCollapse">🚀</h2>
    </div>

    <el-menu
      active-text-color="#ffd04b"
      background-color="#304156"
      text-color="#fff"
      default-active="1"
      class="border-none-menu"
      :collapse="isCollapse"
      :collapse-transition="false" 
    >
      <el-menu-item index="1"><el-icon><House /></el-icon><template #title><span>首页</span></template></el-menu-item>
      <el-menu-item index="2"><el-icon><Location /></el-icon><template #title><span>导航一</span></template></el-menu-item>
      <el-menu-item index="3"><el-icon><Document /></el-icon><template #title><span>导航二</span></template></el-menu-item>
    </el-menu>
    
  </div>
</template>

<script setup>
/* eslint-disable no-undef */
import { House, Location, Document } from '@element-plus/icons-vue'

// 接收老大传过来的折叠状态
defineProps({
  isCollapse: Boolean
})
</script>

<style scoped>
/* 这里的样式也完全不用动 */
.aside-container { background-color: #304156; height: 100%; overflow: hidden;}
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; border-bottom: 1px solid #1f2d3d; transition: all 0.3s;}
.border-none-menu { border-right: none; }
</style>