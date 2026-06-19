import { post, get } from '../utils/request'

export const login = (username, password) =>
  post('/auth/login', { username, password })

export const getCurrentUser = () =>
  get('/auth/me')
