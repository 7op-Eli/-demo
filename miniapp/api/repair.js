import { get, post, put } from '../utils/request'

export const getMyOrders = (ownerId, page = 1) =>
  get('/repair/my-orders', { ownerId, page, size: 10 })

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
