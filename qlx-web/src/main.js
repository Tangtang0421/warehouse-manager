import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// 引入 Element Plus 的核心组件库
import ElementPlus from 'element-plus'
// 引入 Element Plus 的全局 CSS 样式文件
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
const app = createApp(App)
const pinia = createPinia()
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
// 把 Element Plus 全局挂载上
app.use(router)
app.use(ElementPlus)
app.mount('#app')
app.use(pinia)