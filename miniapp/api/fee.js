import { get, post } from '../utils/request'

export const getFeeItems = () =>
  get('/fee/items')

export const getBills = (ownerId, page = 1) =>
  get('/fee/bills', { ownerId, page, size: 10 })

export const payBill = (billId, amount, payMethod) =>
  post(`/fee/bills/${billId}/pay`, { amount, payMethod })
