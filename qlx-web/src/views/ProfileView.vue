<template>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request.js' // 🌟 统一用相对路径，防止报错

const router = useRouter()
const profileFormRef = ref(null)
const form = ref({})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    form.value = userObj.data || userObj
    form.value.password = '' 
  }
})

const rules = {
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  age: [{ pattern: /^(1[89]|[2-9]\d|100)$/, message: '年龄必须在18到100岁之间', trigger: 'blur' }]
}

const updateProfile = () => {
  profileFormRef.value.validate((valid) => {
    if (valid) {
      request.post('/user/update', form.value).then(() => {
        ElMessage.success('个人信息修改成功！请重新登录。')
        localStorage.removeItem('user')
        localStorage.removeItem('menus') // 记得把菜单也清了
        router.push('/login')
      })
    }
  })
}

const goBack = () => {
  router.push('/')
}
</script>