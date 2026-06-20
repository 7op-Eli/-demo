import { get, post, put } from '../utils/request'

// For owners: roomId derived server-side from auth token (IDOR fix)
// For employees/admins: pass roomId as second arg
export const getMessages = (page = 1, roomId = null) => {
  const params = { page, size: 20 }
  if (roomId != null) params.roomId = roomId
  return get('/butler/messages', params)
}

export const sendMessage = (data) =>
  post('/butler/messages', data)

export const getUnreadCount = (receiverId) =>
  get('/butler/messages/unread-count', { receiverId })

export const markMessageRead = (id) =>
  put(`/butler/messages/${id}/read`)

export const getSchedules = (date) =>
  get('/butler/schedules', { date })

export const getNotifications = (page = 1) =>
  get('/butler/notifications', { page, size: 10 })

export const getAnnouncements = (page = 1) =>
  get('/butler/announcements', { page, size: 10 })
