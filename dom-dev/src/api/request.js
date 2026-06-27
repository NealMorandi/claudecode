import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken, removeUser } from '@/utils/auth'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') return response.data
    const { code, data, message } = response.data
    if (code === 200 || code === 0) return data
    if (!response.config._suppressToast) {
      ElMessage.error(message || 'リクエストエラー')
    }
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response?.status === 401) {
      removeToken()
      removeUser()
      router.push('/login')
      ElMessage.error('セッションが切れました。再ログインしてください。')
    } else if (!error.config?._suppressToast) {
      ElMessage.error(error.response?.data?.message || 'サーバーエラー')
    }
    return Promise.reject(error)
  },
)

export default request
