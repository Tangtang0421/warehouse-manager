<template>
  <div class="index-container">
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="text-info">
          <h2 class="greeting">欢迎回来，{{ user.name || '管理员' }}！</h2>
          <p class="sub-text">身份：<el-tag type="success" size="small">{{ userRole }}</el-tag> | 登录时间：{{ loginTime }}</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span style="margin-left: 10px; font-weight: bold;">个人档案中心</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="登录账号">{{ user.no }}</el-descriptions-item>
            <el-descriptions-item label="真实姓名">{{ user.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号码">{{ user.phone }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ user.age }} 岁</el-descriptions-item>
            <el-descriptions-item label="性别">
              <el-tag :type="user.sex === 1 ? 'primary' : 'danger'">{{ user.sex === 1 ? '男' : '女' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="所属角色">{{ userRole }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <el-statistic title="系统活跃用户" :value="125" />
          <el-divider />
          <el-statistic title="今日处理单据" :value="48" />
          <el-divider />
          <el-statistic title="服务运行状态" value="Normal" value-style="color: #67c23a" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { User } from '@element-plus/icons-vue'

const user = ref({})
const loginTime = ref(new Date().toLocaleString())

const userRole = computed(() => {
  const roles = { 0: '超级管理员', 1: '管理员', 2: '普通用户' }
  return roles[user.value.roleId] || '访问者'
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    user.value = userObj.data || userObj // 自动兼容后端包裹格式
  }
})
</script>

<style scoped>
.index-container { padding: 5px; }
.welcome-card { background: linear-gradient(to right, #ffffff, #f0f7ff); border-radius: 12px; }
.welcome-content { display: flex; align-items: center; gap: 20px; }
.greeting { margin: 0 0 10px 0; color: #2c3e50; }
.sub-text { margin: 0; color: #7f8c8d; font-size: 14px; }
.card-header { color: #409eff; display: flex; align-items: center; }
.stat-card { text-align: center; border-radius: 12px; height: 100%; }
</style>