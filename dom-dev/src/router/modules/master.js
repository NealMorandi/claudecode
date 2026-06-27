export default [
  {
    path: '/dormitories',
    name: 'DormitoryList',
    component: () => import('@/views/master/dormitory/index.vue'),
    meta: { title: '寮マスタ', icon: 'house', roles: ['admin', 'staff'], menuGroup: 'マスタ管理', menuGroupIcon: 'Grid' },
  },
  {
    path: '/dormitories/:id',
    name: 'DormitoryDetail',
    component: () => import('@/views/master/dormitory/detail.vue'),
    meta: { title: '社員寮詳細', hidden: true },
  },
  {
    path: '/dormitories/:id/rooms',
    name: 'RoomList',
    component: () => import('@/views/master/room/index.vue'),
    meta: { title: '部屋一覧', hidden: true },
  },
  {
    path: '/rooms/:id',
    name: 'RoomDetail',
    component: () => import('@/views/master/room/detail.vue'),
    meta: { title: '部屋詳細', hidden: true },
  },
  {
    path: '/items',
    name: 'EquipmentList',
    component: () => import('@/views/master/equipment/index.vue'),
    meta: { title: '備品マスタ', icon: 'package', roles: ['admin', 'staff'], menuGroup: 'マスタ管理' },
  },
]
