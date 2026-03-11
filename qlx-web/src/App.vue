<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <SysAside /> 
    </el-aside>
    <el-container>
      <el-header class="header-box">
        <SysHeader /> 
      </el-header>
      <el-main class="main-content">
        <el-card><h3>结构</h3></el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>

import SysAside from './components/SysAside.vue'
import SysHeader from './components/SysHeader.vue'
</script>