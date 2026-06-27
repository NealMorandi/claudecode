import request from './request'

export const getLogList = (params) => request.get('/admin/logs', { params })
export const exportLogs = (params) =>
  request.get('/admin/logs/export', { params, responseType: 'blob' })
