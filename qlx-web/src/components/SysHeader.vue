<template>
  <div class="header-container">
    
    <div class="header-left">
      <el-icon class="collapse-icon"><Fold /></el-icon>
    </div>

    <div class="header-center">
      <span class="system-title">欢迎来到菜单管理系统</span>
    </div>

    <div class="header-right">
      <el-dropdown trigger="hover">
        <span class="user-dropdown-link">
          超级管理员
          <el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
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

import { Fold, ArrowDown } from '@element-plus/icons-vue'
</script>

<style scoped>

.header-container {
  height: 100%;
  display: flex;
  align-items: center;
}


.header-left {
  flex: 1; 
  display: flex;
  align-items: center;
}

.header-center {
  flex: 1;
  text-align: center;
}

.header-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}


.collapse-icon {
  font-size: 22px;
  cursor: pointer;
}
.collapse-icon:hover {
  color: #409EFF;
}


.system-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  letter-spacing: 2px;
}


.user-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #333;
  font-weight: bold;
  outline: none;
}
.user-dropdown-link:hover {
  color: #409EFF;
}
</style>