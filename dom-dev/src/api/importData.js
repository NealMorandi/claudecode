import request from './request'

export const uploadFile = (formData) =>
  request.post('/import/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
export const validateImport = (data) => request.post('/import/validate', data)
export const executeImport = (data) => request.post('/import/execute', data)
export const getImportReport = (jobId) => request.get(`/import/report/${jobId}`)
