import request from './request'

export const getSummary = () => request.get('/dashboard/summary')
export const getDormitoryOccupancy = () => request.get('/dashboard/dormitory-occupancy')
export const getLongTermAlertsTop = (limit = 5) =>
  request.get('/long-term-alerts', { params: { pageSize: limit, page: 1 } })
