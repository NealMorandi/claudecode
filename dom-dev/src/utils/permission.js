import { useAuthStore } from '@/store/modules/auth'

export const hasRole = (...roles) => {
  const store = useAuthStore()
  return roles.includes(store.userInfo?.role)
}

export const isAdmin = () => hasRole('admin')
