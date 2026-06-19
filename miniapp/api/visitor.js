import { get, post } from '../utils/request'

export const getMyVisitors = (ownerId, page = 1) =>
  get('/visitor/my-visitors', { ownerId, page, size: 10 })

export const registerVisitor = (data) =>
  post('/visitor/register', data)
