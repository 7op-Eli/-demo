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
    // 优先展示后端返回的业务提示（如「用户名或密码错误」），否则回退到通用文案
    const res = error.response && error.response.data
    ElMessage.error((res && res.msg) || error.message || '网络异常')
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('admin-token')
      localStorage.removeItem('admin-user')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export const get = (url, params) => request.get(url, { params })
export const post = (url, data) => request.post(url, data)
export const put = (url, data) => request.put(url, data)
export const del = (url) => request.delete(url)

export default request
