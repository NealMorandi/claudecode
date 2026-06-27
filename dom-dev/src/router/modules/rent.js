export default [
  {
    path: '/rent/calculate',
    name: 'RentCalculate',
    component: () => import('@/views/rent/calculate.vue'),
    meta: { title: '寮費計算', icon: 'calculator', roles: ['admin', 'staff'], menuGroup: '寮費管理', menuGroupIcon: 'Wallet' },
  },
  {
    path: '/rent/history',
    name: 'RentHistory',
    component: () => import('@/views/rent/history.vue'),
    meta: { title: '寮費履歴', icon: 'document', roles: ['admin', 'staff', 'viewer'], menuGroup: '寮費管理' },
  },
]
