import request from './request'

export const getUserList = (params) => request.get('/admin/users', { params })
export const createUser = (data) => request.post('/admin/users', data)
export const updateUser = (id, data) => request.put(`/admin/users/${id}`, data)
export const deleteUser = (id) => request.delete(`/admin/users/${id}`)
export const resetPassword = (id, data) =>
  request.put(`/admin/users/${id}/password`, data)
