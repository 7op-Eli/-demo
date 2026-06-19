import { get, post, put, del } from '../utils/request'

// ===== 业主 =====
export const getOwners = () => get('/admin/owners')
export const getOwner = (id) => get(`/admin/owners/${id}`)
export const createOwner = (data) => post('/admin/owners', data)
export const updateOwner = (id, data) => put(`/admin/owners/${id}`, data)
export const deleteOwner = (id) => del(`/admin/owners/${id}`)

// ===== 楼栋 =====
export const getBuildings = () => get('/admin/buildings')
export const createBuilding = (data) => post('/admin/buildings', data)
export const updateBuilding = (id, data) => put(`/admin/buildings/${id}`, data)

// ===== 房间 =====
export const getRooms = (buildingId) => get(`/admin/buildings/${buildingId}/rooms`)
export const createRoom = (data) => post('/admin/rooms', data)
export const updateRoom = (id, data) => put(`/admin/rooms/${id}`, data)

// ===== 员工 =====
export const getEmployees = () => get('/admin/employees')
export const createEmployee = (data) => post('/admin/employees', data)

// ===== 通知 =====
export const getNotices = (params) => get('/notices', params)
export const createNotice = (data) => post('/notices', data)
export const updateNotice = (id, data) => put(`/notices/${id}`, data)
export const deleteNotice = (id) => del(`/notices/${id}`)
