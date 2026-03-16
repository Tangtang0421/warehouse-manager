import { defineStore } from 'pinia'
import { ref } from 'vue'

// 定义一个叫 'user' 的全局仓库
export const useUserStore = defineStore('user', () => {
  // 🌟 这里就是全局共享的数据（状态）
  const userInfo = ref({})

  // 🌟 这里是修改全局数据的方法
  const setUserInfo = (newInfo) => {
    userInfo.value = newInfo
  }

  // 把数据和方法暴露出去给所有组件用
  return { userInfo, setUserInfo }
})