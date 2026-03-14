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
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人中心</span>
            </div>
          </template>
          
          <el-form ref="profileFormRef" :model="form" :rules="rules" label-width="100px" style="max-width: 500px; margin: 0 auto;">
            <el-form-item label="账号ID">
              <el-input v-model="form.id" disabled />
            </el-form-item>
            <el-form-item label="登录账号">
              <el-input v-model="form.no" disabled />
            </el-form-item>
            
            <el-form-item label="真实姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="修改密码" prop="password">
              <el-input v-model="form.password" show-password placeholder="不修改请留空" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="updateProfile">保存修改</el-button>
              <el-button @click="goBack">返回首页</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SysAside from '../components/SysAside.vue'
import SysHeader from '../components/SysHeader.vue'
import request from '@/utils/request.js'

// --- 布局控制 ---
const isCollapse = ref(false)
const handleToggle = () => {
  isCollapse.value = !isCollapse.value
}

// --- 表单与路由逻辑 ---
const router = useRouter()
const profileFormRef = ref(null)
const form = ref({})

// 🌟 1. 页面加载时，把浏览器保险箱里的用户信息填入表单
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    // 假设你的 user 数据在 data 层，否则去掉 .data
    form.value = userObj.data || userObj
    // 为了安全，不把加密的旧密码直接展示在框里
    form.value.password = '' 
  }
})

// 🌟 2. 前端校验规则
const rules = {
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  age: [{ pattern: /^(1[89]|[2-9]\d|100)$/, message: '年龄必须在18到100岁之间', trigger: 'blur' }]
}

// 🌟 3. 发送更新请求
const updateProfile = () => {
  profileFormRef.value.validate((valid) => {
    if (valid) {
      // 🌟 直接复用你们系统的修改用户接口！
      request.post('/user/update', form.value).then(() => {
        ElMessage.success('个人信息修改成功！请重新登录以刷新数据。')
        
        // 修改完重要信息，为了安全和数据刷新，强制用户退出重登
        localStorage.removeItem('user')
        router.push('/login')
      }).catch(() => {
        console.log("修改失败")
      })
    }
  })
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
html, body, #app { margin: 0; padding: 0; height: 100%; }
.layout-container { height: 100vh; }
.header-box { background-color: #fff; border-bottom: 1px solid #e6e6e6; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.main-content { background-color: #f0f2f5; padding: 20px; }
.aside-transition { transition: width 0.3s ease-in-out; }

.profile-card {
  margin-top: 20px;
}
.card-header {
  font-weight: bold;
  font-size: 18px;
}
</style>