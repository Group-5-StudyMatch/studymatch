import axiosClient from './axiosClient'

export const getStudents = () => axiosClient.get('/students')
export const getStudent = (id) => axiosClient.get(`/students/${id}`)
export const createStudent = (student) => axiosClient.post('/students', student)
export const updateStudent = (id, student) => axiosClient.put(`/students/${id}`, student)