<template>
  <div class="index-container">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="text-info">
          <h2 class="greeting">下午好，{{ user.name || '管理员' }}，开启高效工作的一天！</h2>
          <p class="sub-text">身份：<el-tag size="small" type="success">{{ userRole }}</el-tag> | 登录时间：{{ loginTime }}</p>
        </div>
      </div>
    </el-card>

   <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="系统累计注册用户" :value="totalUsers">
            <template #suffix>
              <el-icon style="vertical-align: -0.125em"><User /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="custom-stat">
            <div class="stat-title">当前系统时间</div>
            <div class="stat-value">{{ currentTime }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="custom-stat">
            <div class="stat-title">后端服务状态</div>
            <div class="stat-value" style="color: #67c23a;">
              在线 <el-icon style="vertical-align: -0.125em"><Connection /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <el-icon><Postcard /></el-icon>
          <span style="margin-left: 8px">个人档案</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账号ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="登录账号">{{ user.no }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ user.age }} 岁</el-descriptions-item>
        <el-descriptions-item label="性别">
          <el-tag :type="user.sex === 1 ? '' : 'danger'">
            {{ user.sex === 1 ? '男' : '女' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="权限角色">{{ userRole }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { User, Connection, Postcard } from '@element-plus/icons-vue'
import request from '../utils/request.js' // 🌟 记得引入你的请求工具

const user = ref({})
const loginTime = ref(new Date().toLocaleString())
const currentTime = ref(new Date().toLocaleTimeString())
const totalUsers = ref(0) // 🌟 用来存放真实的注册总人数
let timer = null

// 角色转换逻辑
const userRole = computed(() => {
  const roles = { 0: '超级管理员', 1: '管理员', 2: '普通用户' }
  return roles[user.value.roleId] || '未知角色'
})

// 🌟 核心：通过现有的分页接口“骗”出总人数
const fetchSystemStats = () => {
  request.post('/user/list/page', {
    pageNum: 1,
    pageSize: 1, // 我们只需要总数，所以每页查1条就行，节省流量
    roleId: 0    // 传0表示查询所有角色的用户
  }).then(res => {
    // res 里的 total 就是数据库 user 表的真实行数
    totalUsers.value = res.total || 0
  }).catch(() => {
    console.error("首页获取统计数据失败")
  })
}

onMounted(() => {
  // 1. 获取登录用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    user.value = userObj.data || userObj
  }

  // 2. 调用接口获取真实总人数
  fetchSystemStats()

  // 3. 开启定时器，让时间每秒跳动一次
  timer = setInterval(() => {
    currentTime.value = new Date().toLocaleTimeString()
  }, 1000)
})

// 🌟 养成好习惯：组件销毁时清理定时器，防止内存泄漏
onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.index-container { padding: 10px; }
.welcome-card { margin-bottom: 20px; border-radius: 12px; background: linear-gradient(to right, #ffffff, #f0f9ff); border: none; }
.welcome-content { display: flex; align-items: center; gap: 20px; }
.greeting { margin: 0 0 10px 0; color: #303133; }
.sub-text { margin: 0; color: #909399; font-size: 14px; }
.stats-row { margin-bottom: 20px; }
.info-card { border-radius: 12px; }
.card-header { font-weight: bold; color: #409eff; display: flex; align-items: center; }
/* 🌟 手写统计卡片的样式，跟 Element Plus 原生一模一样 */
.stat-card { text-align: center; border-radius: 12px; height: 100%; }
.custom-stat { display: flex; flex-direction: column; justify-content: center; }
.stat-title { color: #606266; font-size: 14px; margin-bottom: 10px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; display: flex; align-items: center; justify-content: center; gap: 5px; }
/* 让统计数字稍微大一点，更有视觉冲击力 */
:deep(.el-statistic__content) {
  font-weight: bold;
}
</style>