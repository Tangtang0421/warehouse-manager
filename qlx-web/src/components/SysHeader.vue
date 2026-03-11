<template>
  <div class="header-container">
    
    <div class="header-left">
      <el-icon class="collapse-icon" @click="toggleMenu">
        <component :is="isCollapse ? Expand : Fold" />
      </el-icon>
    </div>

    <div class="header-center">
      <span class="system-title">欢迎来到菜单管理系统</span>
    </div>
    <div class="header-right">
      <el-dropdown trigger="hover">
        <span class="user-dropdown-link">超级管理员<el-icon class="el-icon--right"><ArrowDown /></el-icon></span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人中心</el-dropdown-item>
            <el-dropdown-item divided style="color: red;">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    
  </div>
</template>

<script setup>
/* eslint-disable no-undef */
import { defineProps, defineEmits } from 'vue'
// 记得引入 Expand(展开) 图标
import { Fold, Expand, ArrowDown } from '@element-plus/icons-vue'

// 1. 接收老大传过来的变量
defineProps({
  isCollapse: Boolean
})

// 2. 准备向上级发送指令的喇叭
const emit = defineEmits(['toggle'])

// 3. 点击事件：吹响喇叭，通知老大切换
const toggleMenu = () => {
  emit('toggle')
}
</script>

<style scoped>
/* 这里的样式跟你之前的一模一样，完全不用动 */
.header-container { height: 100%; display: flex; align-items: center; }
.header-left { flex: 1; display: flex; align-items: center; }
.header-center { flex: 1; text-align: center; }
.header-right { flex: 1; display: flex; justify-content: flex-end; align-items: center; }
.collapse-icon { font-size: 22px; cursor: pointer; }
.collapse-icon:hover { color: #409EFF; }
.system-title { font-size: 18px; font-weight: bold; color: #333; letter-spacing: 2px; }
.user-dropdown-link { cursor: pointer; display: flex; align-items: center; color: #333; font-weight: bold; outline: none; }
.user-dropdown-link:hover { color: #409EFF; }
</style>