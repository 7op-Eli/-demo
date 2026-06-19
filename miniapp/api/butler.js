import { get, post, put } from '../utils/request'

export const getMessages = (roomId, page = 1) =>
  get('/butler/messages', { roomId, page, size: 20 })

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
