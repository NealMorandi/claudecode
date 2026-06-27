import request from './request'

export const getSettings = () => request.get('/admin/settings')
export const updateSettings = (data) => request.put('/admin/settings', data)
