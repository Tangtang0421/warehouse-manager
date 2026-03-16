import { createRouter, createWebHistory } from 'vue-router'

// 1. 静态路由：只留登录页，千万别在这里写 '/' 的 HomeView！
const routes = [
  { 
    path: '/login', 
    name: 'Login', 
    component: () => import('../views/LoginView.vue') 
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

const componentMap = {
  '/': () => import('../views/HomeView.vue'),    // 🌟 现在这里是高颜值个人信息页了
  '/user': () => import('../views/UserView.vue'), // 🌟 这里是你的用户表格页
  '/profile': () => import('../views/ProfileView.vue'),
  '/admin': () => import('../views/AdminView.vue')
}

let hasAddedRoutes = false

router.beforeEach((to, from, next) => {
  if (to.path === '/login') return next()

  const userStr = localStorage.getItem('user')
  if (!userStr) return next('/login')

  if (!hasAddedRoutes) {
    const menusStr = localStorage.getItem('menus')
    if (menusStr) {
      const menus = JSON.parse(menusStr)
      
      // 🌟 核心点 1：手动先添加 Layout 骨架，并起个固定的名字 'MainLayout'
      router.addRoute({
        path: '/',
        name: 'MainLayout',
        component: () => import('../views/LayoutView.vue'),
        children: [] // 先留空
      })

      // 🌟 核心点 2：把所有菜单都塞进这个 'MainLayout' 的 children 里
      menus.forEach(menu => {
        if (componentMap[menu.menuComponent]) {
          let childPath = menu.menuComponent
          
          // 如果路径是 '/'，在子路由里必须写成空字符串 ''，否则会跳出骨架
          if (childPath === '/') {
            childPath = '' 
          } else {
            // 如果是 '/admin'，子路由路径要去掉前面的斜杠变成 'admin'
            childPath = childPath.replace('/', '')
          }

          router.addRoute('MainLayout', {
            path: childPath,
            name: menu.menuName,
            component: componentMap[menu.menuComponent]
          })
        }
      })
      
      hasAddedRoutes = true
      // 🌟 核心点 3：重要！addRoute 后必须用这一句重定向，否则第一次加载会白屏或出错
      return next({ ...to, replace: true })
    }
  }
  next()
})

export default router