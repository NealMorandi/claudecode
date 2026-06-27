export default [
  {
    path: '/dorm-overview',
    name: 'DormOverview',
    component: () => import('@/views/dormitory/index.vue'),
    meta: { title: '社員寮一覧', icon: 'building', roles: ['admin', 'staff', 'viewer'], menuGroup: '社員寮管理', menuGroupIcon: 'OfficeBuilding' },
  },
  {
    path: '/residences',
    name: 'ResidenceList',
    component: () => import('@/views/residence/index.vue'),
    meta: { title: '入居履歴一覧', icon: 'users', roles: ['admin', 'staff'], menuGroup: '社員寮管理', menuGroupIcon: 'OfficeBuilding' },
  },
  {
    path: '/residences/new',
    name: 'ResidenceNew',
    component: () => import('@/views/residence/new.vue'),
    meta: { title: '入居登録', hidden: true },
  },
  {
    path: '/residences/:id',
    name: 'ResidenceDetail',
    component: () => import('@/views/residence/detail.vue'),
    meta: { title: '入居詳細', hidden: true },
  },
  {
    path: '/residences/:id/checkout',
    name: 'ResidenceCheckout',
    component: () => import('@/views/residence/checkout.vue'),
    meta: { title: '退寮処理', hidden: true },
  },
  {
    path: '/residences/:id/items',
    name: 'ResidenceItems',
    component: () => import('@/views/residence/items.vue'),
    meta: { title: '備品チェックリスト', hidden: true },
  },
]
