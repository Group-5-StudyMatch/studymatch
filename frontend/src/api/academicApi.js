import axiosClient from './axiosClient'

export const getCourseUnits = () => axiosClient.get('/course-units')

export const getAcademicRecords = (studentId) =>
  axiosClient.get(`/students/${studentId}/academic-records`)

export const addAcademicRecord = (studentId, record) =>
  axiosClient.post(`/students/${studentId}/academic-records`, record)