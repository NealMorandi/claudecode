import request from './request'

export const searchEmployees  = (keyword) => request.get('/employees', { params: { keyword } })
export const getEmployeeList  = (params)  => request.get('/employees', { params })
export const createEmployee   = (data)    => request.post('/employees', data, { _suppressToast: true })
export const updateEmployee   = (id, data) => request.put(`/employees/${id}`, data)
export const deleteEmployee   = (id)      => request.delete(`/employees/${id}`)
