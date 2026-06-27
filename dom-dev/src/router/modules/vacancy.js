export default [
  {
    path: '/vacancies',
    name: 'VacancyList',
    component: () => import('@/views/vacancy/index.vue'),
    meta: { title: '空き室一覧', icon: 'door', roles: ['admin', 'staff', 'viewer'], menuGroup: '社員寮管理' },
  },
  {
    path: '/long-term-alerts',
    name: 'LongTermAlerts',
    component: () => import('@/views/longTermAlert/index.vue'),
    meta: { title: '長期利用警告', icon: 'alert-circle', roles: ['admin', 'staff'], menuGroup: '社員寮管理', menuGroupIcon: 'OfficeBuilding' },
  },
]
