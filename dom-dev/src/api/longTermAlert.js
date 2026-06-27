import request from './request'

export const getLongTermAlerts = (params) =>
  request.get('/long-term-alerts', { params })
export const updateAlertStatus = (employeeId, data) =>
  request.patch(`/long-term-alerts/${employeeId}/status`, data)
export const exportAlertCsv = (params) =>
  request.get('/long-term-alerts/export', { params, responseType: 'blob' })
