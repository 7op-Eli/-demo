import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '控制台' } },
      { path: 'owners', name: 'Owners', component: () => import('../views/Owners.vue'), meta: { title: '业主管理' } },
      { path: 'buildings', name: 'Buildings', component: () => import('../views/Buildings.vue'), meta: { title: '楼栋管理' } },
      { path: 'employees', name: 'Employees', component: () => import('../views/Employees.vue'), meta: { title: '员工管理' } },
      { path: 'fee-items', name: 'FeeItems', component: () => import('../views/FeeItems.vue'), meta: { title: '费用项目' } },
      { path: 'fee-bills', name: 'FeeBills', component: () => import('../views/FeeBills.vue'), meta: { title: '缴费账单' } },
      { path: 'repairs', name: 'Repairs', component: () => import('../views/Repairs.vue'), meta: { title: '报修管理' } },
      { path: 'notices', name: 'Notices', component: () => import('../views/Notices.vue'), meta: { title: '公告管理' } },
      { path: 'convenience', name: 'Convenience', component: () => import('../views/Convenience.vue'), meta: { title: '便民服务' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.noAuth) return next()
  const token = localStorage.getItem('admin-token')
  if (!token) return next('/login')
  next()
})

export default router
