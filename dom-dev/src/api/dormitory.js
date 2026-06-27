import request from './request'

export const getDormitoryList = (params) => request.get('/dormitories', { params })
export const getDormitoryDetail = (id) => request.get(`/dormitories/${id}`)
export const createDormitory = (data) => request.post('/dormitories', data)
export const updateDormitory = (id, data) => request.put(`/dormitories/${id}`, data)
export const deleteDormitory = (id, config = {}) => request.delete(`/dormitories/${id}`, config)
export const getRoomResidents = (id) => request.get(`/dormitories/${id}/room-residents`)
export const getDormitoryEquipment = (id) => request.get(`/dormitories/${id}/equipment`)
