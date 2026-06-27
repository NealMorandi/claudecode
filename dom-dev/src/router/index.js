import { createRouter, createWebHistory } from 'vue-router'
import masterRoutes from './modules/master'
import residenceRoutes from './modules/residence'
import vacancyRoutes from './modules/vacancy'
import rentRoutes from './modules/rent'
import adminRoutes from './modules/admin'

export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: 'ログイン', hidden: true },
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: 'ダッシュボード', icon: 'grid', affix: true },
      },
      ...residenceRoutes,
      ...vacancyRoutes,
      ...rentRoutes,
      { path: '/items/storage', name: 'EquipmentStorage', component: () => import('@/views/equipment/storage.vue'), meta: { title: '備品管理', icon: 'clipboard', roles: ['admin', 'staff'], menuGroup: 'データ管理' } },
      ...masterRoutes,
      ...adminRoutes,
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
})

export default router
