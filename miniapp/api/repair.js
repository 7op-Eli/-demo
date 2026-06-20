import { get, post, put } from '../utils/request'

// ownerId is now derived server-side from auth token (IDOR fix)
export const getMyOrders = (page = 1) =>
  get('/repair/my-orders', { page, size: 10 })

export const getOrderDetail = (id) =>
  get(`/repair/orders/${id}`)

export const submitOrder = (data) =>
  post('/repair/orders', data)

export const getOrderLogs = (id) =>
  get(`/repair/orders/${id}/logs`)

export const getEvaluation = (id) =>
  get(`/repair/orders/${id}/evaluation`)

export const submitEvaluation = (data) =>
  post('/repair/evaluations', data)
