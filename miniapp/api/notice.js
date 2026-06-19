import { get } from '../utils/request'

export const getNotices = (category, page = 1) =>
  get('/notices', { category, page, size: 10 })

export const getNoticeDetail = (id) =>
  get(`/notices/${id}`)
