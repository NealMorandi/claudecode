import request from './request'

export const getRoomsByDormitory = (dormitoryId, params) =>
  request.get(`/dormitories/${dormitoryId}/rooms`, { params })
export const getRoomDetail = (id) => request.get(`/rooms/${id}`)
export const createRoom = (dormitoryId, data) =>
  request.post(`/dormitories/${dormitoryId}/rooms`, data)
export const updateRoom = (id, data, config = {}) => request.put(`/rooms/${id}`, data, config)
export const deleteRoom = (id, config = {}) => request.delete(`/rooms/${id}`, config)
export const getAvailableRooms = (params) => request.get('/rooms/available', { params })
export const getAllRooms = () => request.get('/rooms')
