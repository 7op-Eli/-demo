import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器
request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => Promise.reject(error))

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.msg || '请求失败')
    if (res.code === 401) {
      localStorage.removeItem('admin-token')
      localStorage.removeItem('admin-user')
      router.push('/login')
    }
    return Promise.reject(new Error(res.msg))
  },
  error => {
    const status = error.response && error.response.status
    const res = error.response && error.response.data

    // 401 → 认证失败：清除登录态并跳转登录页
    if (status === 401) {
      localStorage.removeItem('admin-token')
      localStorage.removeItem('admin-user')
      router.push('/login')
      ElMessage.error((res && res.msg) || '登录已过期，请重新登录')
      return Promise.reject(error)
    }

    // 403 → 授权失败：已认证但无权限，不清 token、不跳转
    if (status === 403) {
      ElMessage.error((res && res.msg) || '无操作权限')
      return Promise.reject(error)
    }

    // 优先展示后端返回的业务提示，否则回退到通用文案
    ElMessage.error((res && res.msg) || error.message || '网络异常')
    return Promise.reject(error)
  }
)

export const get = (url, params) => request.get(url, { params })
export const post = (url, data) => request.post(url, data)
export const put = (url, data) => request.put(url, data)
export const del = (url) => request.delete(url)

export default request
