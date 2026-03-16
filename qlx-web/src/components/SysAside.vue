<template>
  <div class="aside-container">
    <div class="aside-logo" :class="{ 'collapse-logo': isCollapse }">
      <span v-show="!isCollapse">仓储系统</span>
      <span v-show="isCollapse">WMS</span>
    </div>
    
    <el-menu
      :default-active="route.path"
      class="el-menu-vertical"
      :collapse="isCollapse"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      :collapse-transition="false"
      router
    >
      <el-menu-item v-for="item in menuList" :key="item.id" :index="item.menuComponent">
        <el-icon><component :is="item.menuIcon" /></el-icon>
        <template #title>{{ item.menuName }}</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const menuList = ref([])

/* global defineProps */
defineProps({
  isCollapse: Boolean
})

onMounted(() => {
  const menusStr = localStorage.getItem('menus')
  if (menusStr) {
    menuList.value = JSON.parse(menusStr)
  }
})
</script>

<style scoped>
/* 🌟 核心修复 1：让整个侧边栏容器撑满 100vh 屏幕高度，并统一深色背景 */
.aside-container {
  height: 100vh;
  background-color: #304156; 
  display: flex;
  flex-direction: column;
}

/* 顶部 Logo 样式 */
.aside-logo {
  height: 50px;
  line-height: 50px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3643;
  flex-shrink: 0; /* 防止被下方菜单挤压 */
  overflow: hidden;
  white-space: nowrap;
}

/* 🌟 核心修复 2：让菜单自动填满下方剩余空间，并去掉右侧丑陋的白边 */
.el-menu-vertical {
  flex: 1;
  border-right: none; 
}

/* 🌟 核心修复 3：严格控制展开和折叠时的宽度，确保图标完美居中露出 */
.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}
.el-menu--collapse {
  width: 64px;
}
</style>