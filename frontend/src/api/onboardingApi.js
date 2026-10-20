import axiosClient from './axiosClient'

export const getSkills = () => axiosClient.get('/skills')

export const submitOnboarding = (studentId, skills) =>
  axiosClient.post(`/students/${studentId}/onboarding`, { skills })