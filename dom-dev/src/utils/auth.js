const TOKEN_KEY = 'dom_token'
const USER_KEY = 'dom_user'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token)
export const removeToken = () => localStorage.removeItem(TOKEN_KEY)

export const getUser = () => {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}
export const setUser = (user) => localStorage.setItem(USER_KEY, JSON.stringify(user))
export const removeUser = () => localStorage.removeItem(USER_KEY)
