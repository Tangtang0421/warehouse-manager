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
        <el-card>
          
          <el-button type="primary" @click="loadData">点击拉取后端数据库数据！</el-button>
          
          <el-table :data="tableData" style="width: 100%; margin-top: 20px;" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="no" label="账号" width="120" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="phone" label="电话" />
          </el-table>

        </el-card>
      </el-main>
    </el-container>

  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import SysAside from './components/SysAside.vue'
import SysHeader from './components/SysHeader.vue'

// 🌟 变化 3：引入咱们刚写好的 Axios 拦截器 (注意路径)
import request from './utils/request.js' 

// ====== 控制菜单折叠的代码（保持原样） ======
const isCollapse = ref(false)
const handleToggle = () => {
  isCollapse.value = !isCollapse.value
}

// ====== 🌟 核心：联调后端数据的代码 ======
// 准备一个空数组盒子，一会儿把后端查到的数据塞进这里
const tableData = ref([])

// 点击按钮时执行的方法
const loadData = () => {
  // 1. 组装发给后端的 JSON 参数（跟你 Apifox 里填的一模一样）
  const queryParam = {
    pageNum: 1,
    pageSize: 10,
    param: {} // 空条件代表查所有
  }

  // 2. 用 request 向你的分页接口发 POST 请求
  request.post('/user/list/page', queryParam).then(res => {
    // 打印在控制台给你自己看
    console.log("后端传回来的数据：", res) 
    
    // 3. 把后端返回的 records (用户列表) 强行塞进咱们前端的表格格子里！
    tableData.value = res.records
  }).catch(error => {
    console.error("请求报错了：", error)
  })
}
</script>

<style>
/* 全局基础样式（保持原样） */
html, body, #app { margin: 0; padding: 0; height: 100%; }
.layout-container { height: 100vh; }
.header-box { background-color: #fff; border-bottom: 1px solid #e6e6e6; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.main-content { background-color: #f0f2f5; padding: 20px; }
.aside-transition { transition: width 0.3s ease-in-out; }
</style>