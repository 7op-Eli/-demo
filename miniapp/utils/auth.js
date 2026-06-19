/**
 * 登录态管理
 */
export const login = (token, userInfo) => {
  uni.setStorageSync('token', token)
  uni.setStorageSync('userInfo', JSON.stringify(userInfo))
}

export const logout = () => {
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
  uni.reLaunch({ url: '/pages/login' })
}

export const getToken = () => uni.getStorageSync('token')

export const getUserInfo = () => {
  const raw = uni.getStorageSync('userInfo')
  return raw ? JSON.parse(raw) : null
}

export const isLoggedIn = () => !!getToken()

export const getRoleType = () => {
  const user = getUserInfo()
  return user ? user.roleType : null
}

export const isOwner = () => getRoleType() === 1
export const isEmployee = () => getRoleType() === 2
export const isAdmin = () => getRoleType() === 3
