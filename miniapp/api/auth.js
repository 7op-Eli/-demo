import { post, get } from '../utils/request'

export const loginApi = (username, password) =>
  post('/auth/login', { username, password })

export const getCurrentUser = () =>
  get('/auth/me')
