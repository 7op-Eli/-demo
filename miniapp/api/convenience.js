import { get, post } from '../utils/request'

export const getServices = () =>
  get('/convenience/services')

export const getTools = () =>
  get('/convenience/tools')

export const borrowTool = (data) =>
  post('/convenience/tools/borrow', data)

// For owners: ownerId derived server-side from auth token (IDOR fix)
// For employees/admins: pass ownerId as second arg
export const getUsageRecords = (page = 1, ownerId = null) => {
  const params = { page, size: 10 }
  if (ownerId != null) params.ownerId = ownerId
  return get('/convenience/usage-records', params)
}

export const submitEvaluation = (data) =>
  post('/convenience/evaluations', data)
