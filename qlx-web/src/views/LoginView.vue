<template>
  <div class="login-container">
    <el-card class="login-box">
      <div class="login-title">系统登录</div>
      
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="no">
          <el-input 
            v-model="loginForm.no" 
            placeholder="请输入账号" 
            prefix-icon="User"
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            placeholder="请输入密码" 
            prefix-icon="Lock" 
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            style="width: 100%; font-size: 16px; margin-top: 10px;" 
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
// 如果你用了 Vue Router，需要引入 useRouter 来实现跳转
// import { useRouter } from 'vue-router' 
import request from '../utils/request.js' 

// const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

// 🌟 登录表单数据（前端只负责把这两个发给后端）
const loginForm = ref({
  no: '',
  password: ''
})

// 🌟 前端基础校验：不填不让发请求
const rules = {
  no: [
    { required: true, message: '账号不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true // 开启按钮转圈加载状态
      
      // 🌟 1. 向后端发送 POST 请求验证账号密码
      request.post('/user/login', loginForm.value).then(res => {
        ElMessage.success('登录成功！正在加载系统权限...')
        
        // 提取token、用户信息和菜单并存入localStorage
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res.user));
        localStorage.setItem('menus', JSON.stringify(res.menus));
        
        // 🌟 2. 直接跳转，不需要再次请求菜单
        window.location.href = '/'
        
      }).catch(() => {
        // 登录失败，拦截器会自动提示，这里只需要关闭 loading
        console.log("登录拦截，账号或密码错误")
      }).finally(() => {
        loading.value = false
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #2d3a4b; /* 经典大厂后台深色背景 */
}

.login-box {
  width: 400px;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}
</style>