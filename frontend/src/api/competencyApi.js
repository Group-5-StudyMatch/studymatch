import axiosClient from './axiosClient'

export const getCompetencies = (studentId) =>
  axiosClient.get(`/students/${studentId}/competencies`)