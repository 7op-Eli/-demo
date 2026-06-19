/**
 * HTTP 请求封装
 */
const BASE_URL = 'http://localhost:8080/api'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const header = {
      'Content-Type': 'application/json',
    }
    if (token) {
      header['Authorization'] = 'Bearer ' + token
    }

    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header,
      success: (res) => {
        if (res.statusCode === 401) {
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.reLaunch({ url: '/pages/login' })
          reject(new Error('登录已过期'))
          return
        }
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else {
          uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url, data) => request({ url, data, method: 'GET' })
export const post = (url, data) => request({ url, data, method: 'POST' })
export const put = (url, data) => request({ url, data, method: 'PUT' })
export const del = (url) => request({ url, method: 'DELETE' })

export default { get, post, put, del }
