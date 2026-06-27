import request from './request'

export const getResidenceList = (params) => request.get('/residences', { params })
export const getResidenceDetail = (id) => request.get(`/residences/${id}`)
export const createResidence = (data) => request.post('/residences', data)
export const updateResidence = (id, data) => request.put(`/residences/${id}`, data)
export const checkoutResidence = (id, data) =>
  request.put(`/residences/${id}/checkout`, data)
