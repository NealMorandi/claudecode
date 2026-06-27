export default [
  {
    path: '/admin/users',
    name: 'UserManage',
    component: () => import('@/views/admin/users/index.vue'),
    meta: { title: 'ユーザー管理', icon: 'user', roles: ['admin'], menuGroup: 'データ管理' },
  },
  {
    path: '/employees',
    name: 'EmployeeManage',
    component: () => import('@/views/employee/index.vue'),
    meta: { title: '社員管理', icon: 'users', roles: ['admin', 'staff'], menuGroup: 'データ管理' },
  },
  {
    path: '/import',
    name: 'ImportData',
    component: () => import('@/views/import/index.vue'),
    meta: { title: 'データ取り込み', icon: 'upload', roles: ['admin', 'staff'], menuGroup: 'データ管理', menuGroupIcon: 'DataLine' },
  },
  {
    path: '/admin/settings',
    name: 'SystemSettings',
    component: () => import('@/views/admin/settings/index.vue'),
    meta: { title: '基準値マスタ', icon: 'sliders', roles: ['admin'], menuGroup: 'マスタ管理' },
  },
  {
    path: '/admin/logs',
    name: 'OperationLogs',
    component: () => import('@/views/admin/logs/index.vue'),
    meta: { title: '操作ログ', icon: 'log-file', roles: ['admin'], menuGroup: 'システム設定' },
  },
]
