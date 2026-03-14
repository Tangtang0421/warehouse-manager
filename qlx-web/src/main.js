import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// 引入 Element Plus 的核心组件库
import ElementPlus from 'element-plus'
// 引入 Element Plus 的全局 CSS 样式文件
import 'element-plus/dist/index.css'

const app = createApp(App)

// 把 Element Plus 全局挂载上
app.use(router)
app.use(ElementPlus)
app.mount('#app')