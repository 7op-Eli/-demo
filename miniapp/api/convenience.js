import { get, post } from '../utils/request'

export const getServices = () =>
  get('/convenience/services')

export const getTools = () =>
  get('/convenience/tools')

export const borrowTool = (data) =>
  post('/convenience/tools/borrow', data)

export const getUsageRecords = (ownerId, page = 1) =>
  get('/convenience/usage-records', { ownerId, page, size: 10 })

export const submitEvaluation = (data) =>
  post('/convenience/evaluations', data)
