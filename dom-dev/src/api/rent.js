import request from './request'

export const calculateRent = (data) => request.post('/rent/calculate', data)
export const confirmRent = (data) => request.post('/rent/confirm', data)
export const getRentHistory = (params) => request.get('/rent/history', { params })
export const getRentDetail = (id) => request.get(`/rent/${id}`)
export const exportRentCsv = (params) =>
  request.get('/rent/export', { params, responseType: 'blob' })
export const cancelRent = (yearMonth) => request.delete(`/rent/cancel/${yearMonth}`)
