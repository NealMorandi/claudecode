import request from './request'

export const getEquipmentList = (params) => request.get('/equipment', { params })
export const createEquipment = (data) => request.post('/equipment', data)
export const updateEquipment = (id, data) => request.put(`/equipment/${id}`, data)
export const deleteEquipment = (id) => request.delete(`/equipment/${id}`)
export const getStorageList = (params) => request.get('/equipment/storage', { params })
export const getEquipmentHistory = (id, params) =>
  request.get(`/equipment/${id}/history`, { params })
export const registerEquipmentStorage = (id, data) =>
  request.post(`/equipment/${id}/storage`, data)
export const getResidenceItems = (residenceId) =>
  request.get(`/residences/${residenceId}/items`)
export const updateResidenceItems = (residenceId, data) =>
  request.put(`/residences/${residenceId}/items`, data)
export const updateHistoryStorageLocation = (id, storageLocation) =>
  request.put(`/equipment/history/${id}/storage-location`, { storageLocation })
export const updateEquipmentHistory = (id, data) =>
  request.put(`/equipment/history/${id}`, data)
