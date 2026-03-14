import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue') // 指向刚才搬家的 Login.vue
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')  // 指向刚才新建的 Home.vue
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 简单的路由守卫：没登录就踢回登录页
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (to.path === '/login') {
    next()
  } else {
    if (!user) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router