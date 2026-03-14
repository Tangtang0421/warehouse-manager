<template>
  <div class="header-container">
    <div class="left">
       <el-icon style="font-size: 22px; cursor: pointer;" @click="$emit('toggle')">
         <Fold v-if="!isCollapse" />
         <Expand v-else />
       </el-icon>
    </div>

    <div class="right">
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          欢迎您，<span style="color: #409EFF;">{{ userName }}</span>
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </span>
        
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided style="color: #f56c6c; font-weight: bold;">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Fold, Expand } from '@element-plus/icons-vue'

/* global defineProps */

defineProps({
  isCollapse: Boolean
})

const router = useRouter()

// 🌟 1. 动态获取用户名的核心逻辑
const userName = ref('未知用户')

onMounted(() => {
  // 从浏览器的保险箱里把刚才存的 user 字符串拿出来
  const userStr = localStorage.getItem('user')
  if (userStr) {
    // 把它转换回 JSON 对象
    const userObj = JSON.parse(userStr)
    
    // 【注意这里】：如果你的后端 login 接口返回的是 Result.success(user)，
    // 那么真实的数据其实是包在 data 属性里的！所以取 userObj.data.name
    // 如果没有 data 包裹，就直接取 userObj.name
    if (userObj.data && userObj.data.name) {
      userName.value = userObj.data.name
    } else if (userObj.name) {
      userName.value = userObj.name
    } else {
      userName.value = '管理员'
    }
  }
})

// 🌟 2. 处理下拉菜单的点击事件
const handleCommand = (command) => {
  if (command === 'logout') {
    logout()
  } else if (command === 'profile') {
    // 🌟 原来是弹窗，现在改成跳转到个人中心路由
    router.push('/profile') 
  }
}

// 🌟 3. 退出登录的终极闭环
const logout = () => {
  ElMessageBox.confirm(
    '您确定要退出当前系统吗？',
    '退出警告',
    {
      confirmButtonText: '狠心退出',
      cancelButtonText: '再逛逛',
      type: 'warning',
    }
  ).then(() => {
    // 🔪 第一步：撕毁门票（清空所有的缓存数据）
    localStorage.removeItem('user')
    // 如果你以后还有 token 什么的，都在这里一起 remove 掉
    // localStorage.removeItem('token')
    
    // 🚀 第二步：一脚踢回登录页
    router.push('/login')
    
    // 💖 第三步：友好的离别提示
    ElMessage.success('已安全退出系统，期待您的再次使用！')
  }).catch(() => {
    // 点了取消，就假装无事发生
  })
}
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}
.left {
  display: flex;
  align-items: center;
}
.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #333;
  font-weight: bold;
  font-size: 16px;
  outline: none; /* 去除点击时的黑框 */
}
</style>