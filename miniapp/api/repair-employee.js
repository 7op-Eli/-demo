import { get, put, post } from '../utils/request'

// 查询报修列表（按状态）
export const getOrdersByStatus = (status, page = 1) =>
  get('/repair/orders', { status, page, size: 20 })

// 查询分配给指定维修人员的工单
export const getAssignedOrders = (employeeId, page = 1) =>
  get('/repair/assigned-orders', { employeeId, page, size: 20 })

// 查询客服处理的工单
export const getCsrOrders = (csrId, page = 1) =>
  get('/repair/csr-orders', { csrId, page, size: 20 })

// 获取工单详情
export const getOrderDetail = (id) =>
  get(`/repair/orders/${id}`)

// 客服接单
export const csAccept = (id) =>
  put(`/repair/orders/${id}/cs-accept`)

// 客服派单
export const dispatchOrder = (id, assigneeId) =>
  put(`/repair/orders/${id}/dispatch`, { assigneeId })

// 维修接单
export const acceptRepair = (id) =>
  put(`/repair/orders/${id}/accept`)

// 上报进度
export const reportProgress = (id, content, imageUrls) =>
  post(`/repair/orders/${id}/progress`, { content, imageUrls })

// 维修完成
export const completeRepair = (id) =>
  put(`/repair/orders/${id}/complete`)

// 客服回访
export const followUp = (id, note) =>
  put(`/repair/orders/${id}/follow-up`, { note })

// 获取日志
export const getOrderLogs = (id) =>
  get(`/repair/orders/${id}/logs`)
