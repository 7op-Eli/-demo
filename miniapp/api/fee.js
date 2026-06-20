import { get, post } from '../utils/request'

export const getFeeItems = () =>
  get('/fee/items')

// For owners: ownerId derived server-side from auth token (IDOR fix)
// For employees/admins: pass ownerId as second arg
export const getBills = (page = 1, ownerId = null) => {
  const params = { page, size: 10 }
  if (ownerId != null) params.ownerId = ownerId
  return get('/fee/bills', params)
}

export const payBill = (billId, amount, payMethod) =>
  post(`/fee/bills/${billId}/pay`, { amount, payMethod })
