import request from './request'

export const getVacancies = (params) => request.get('/vacancies', { params })
