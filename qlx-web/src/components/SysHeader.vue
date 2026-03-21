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
const userName = ref('未知用户')

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    const userObj = JSON.parse(userStr)
    if (userObj.data && userObj.data.name) {
      userName.value = userObj.data.name
    } else if (userObj.name) {
      userName.value = userObj.name
    } else {
      userName.value = '管理员'
    }
  }
})

const handleCommand = (command) => {
  if (command === 'logout') {
    logout()
  } else if (command === 'profile') {
    router.push('/profile') 
  }
}

// 🌟 3. 退出登录的终极闭环（彻底秒杀白屏 Bug）
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
    // 🌟 核心修复 1：必须把动态菜单的缓存也一并清空！
    localStorage.removeItem('menus') 
    
    // 💖 友好的离别提示
    ElMessage.success('已安全退出系统，期待您的再次使用！')

    // 🚀 第二步：一脚踢回登录页
    // 🌟 核心修复 2：用 window.location.href 强制刷新浏览器！
    // 加上 500 毫秒的延迟，是为了让上面那句绿色的 ElMessage 能弹出来亮个相
    setTimeout(() => {
      window.location.href = '/login'
    }, 500)
    
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
  outline: none; 
}
</style>