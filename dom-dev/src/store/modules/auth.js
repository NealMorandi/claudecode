import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getToken, setToken, removeToken, getUser, setUser, removeUser } from '@/utils/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || null,
    userInfo: getUser() || null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    role: (state) => state.userInfo?.role || '',
  },
  actions: {
    async login(credentials) {
      const data = await loginApi(credentials)
      const { token, ...user } = data
      this.token = token
      this.userInfo = user
      setToken(token)
      setUser(user)
    },
    async logout() {
      await logoutApi().catch(() => {})
      this.token = null
      this.userInfo = null
      removeToken()
      removeUser()
    },
  },
})
