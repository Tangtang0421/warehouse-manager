<template>
  <div class="index-container">
    <el-card class="welcome-card">
      <div class="welcome-content">
        <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="text-info">
          <h2 class="greeting">下午好，{{ user.name }}，开启高效工作的一天！</h2>
          <p class="sub-text">身份：<el-tag size="small">{{ userRole }}</el-tag> | 登录时间：{{ loginTime }}</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="今日出库单据" :value="125" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="库存预警数量" :value="12" value-style="color: #f56c6c" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <el-statistic title="系统运行状态" value="Normal" value-style="color: #67c23a" />
        </el-card>
      </el-col>
    </el-row>

    <el-card class="info-card">
      <template #header>
        <div class="card-header"><span>个人档案</span></div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账号ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="登录账号">{{ user.no }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ user.age }} 岁</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ user.sex === 1 ? '男' : '女' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属机构">研发中心 / 仓储管理部</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

const user = ref({})
const loginTime = ref(new Date().toLocaleString())

const userRole = computed(() => {
  const roles = { 0: '超级管理员', 1: '管理员', 2: '普通用户' }
  return roles[user.value.roleId] || '未知角色'
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    user.value = userObj.data || userObj
  }
})
</script>

<style scoped>
.index-container { padding: 10px; }
.welcome-card { margin-bottom: 20px; border-radius: 12px; background: linear-gradient(to right, #ffffff, #f0f9ff); }
.welcome-content { display: flex; align-items: center; gap: 20px; }
.greeting { margin: 0 0 10px 0; color: #303133; }
.sub-text { margin: 0; color: #909399; font-size: 14px; }
.stats-row { margin-bottom: 20px; }
.info-card { border-radius: 12px; }
.card-header { font-weight: bold; color: #409eff; }
</style>