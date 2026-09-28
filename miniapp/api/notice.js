import { get } from '../utils/request'

export const getNotices = (category, page = 1) => {
  const params = { page, size: 10 }
  if (category) params.category = category
  return get('/notices', params)
}

export const getNoticeDetail = (id) =>
  get(`/notices/${id}`)
