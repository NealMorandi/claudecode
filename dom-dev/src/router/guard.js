import router from './index'
import { useAuthStore } from '@/store/modules/auth'
import { getToken, removeToken, removeUser } from '@/utils/auth'

const whiteList = ['/login']

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  if (whiteList.includes(to.path)) {
    if (authStore.isLoggedIn) return next('/dashboard')
    return next()
  }

  if (!authStore.isLoggedIn) return next(`/login?redirect=${to.path}`)

  const roles = to.meta?.roles
  if (roles && !roles.includes(authStore.role)) return next('/dashboard')

  next()
})
