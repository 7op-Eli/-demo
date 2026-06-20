import { get, post } from '../utils/request'

// ownerId is now derived server-side from auth token (IDOR fix)
export const getMyVisitors = (page = 1) =>
  get('/visitor/my-visitors', { page, size: 10 })

export const registerVisitor = (data) =>
  post('/visitor/register', data)
