<template>
  <view class="login-page">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="title">智慧物业</text>
      <text class="subtitle">Smart Property Management</text>
    </view>

    <view class="login-form card">
      <view class="form-item">
        <text class="label">用户名</text>
        <input class="input" v-model="username" placeholder="请输入用户名" />
      </view>
      <view class="form-item">
        <text class="label">密码</text>
        <input class="input" v-model="password" type="password" placeholder="请输入密码" />
      </view>
      <button class="primary-btn login-btn" @click="handleLogin" :loading="loading">
        {{ loading ? '登录中...' : '登 录' }}
      </button>
    </view>

    <view class="login-footer">
      <text class="tip">默认账号: 业主手机号 / 员工手机号 / admin</text>
      <text class="tip">默认密码: 123456</text>
    </view>
  </view>
</template>

<script>
import { loginApi } from '../api/auth'
import { login } from '../utils/auth'

export default {
  data() {
    return {
      username: '',
      password: '',
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.username || !this.password) {
        uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
        return
      }
      this.loading = true
      try {
        const res = await loginApi(this.username, this.password)
        login(res.token, {
          userId: res.userId,
          username: res.username,
          realName: res.realName,
          roleType: res.roleType,
          roleLabel: res.roleLabel,
          ownerId: res.ownerId,
          employeeId: res.employeeId
        })

        uni.showToast({ title: '登录成功', icon: 'success' })

        // 根据角色跳转
        if (res.roleType === 1) {
          uni.reLaunch({ url: '/pages/owner/index' })
        } else if (res.roleType === 2) {
          uni.reLaunch({ url: '/pages/employee/index' })
        } else {
          uni.reLaunch({ url: '/pages/owner/index' })
        }
      } catch (e) {
        uni.showToast({ title: '登录失败，请检查账号密码', icon: 'none' })
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx;
}
.login-header {
  text-align: center;
  margin-bottom: 60rpx;
  .logo {
    width: 120rpx;
    height: 120rpx;
    border-radius: 24rpx;
    margin-bottom: 20rpx;
  }
  .title {
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
    display: block;
  }
  .subtitle {
    font-size: 24rpx;
    color: #999;
    margin-top: 10rpx;
  }
}
.login-form {
  width: 100%;
  .form-item {
    margin-bottom: 30rpx;
    .label {
      font-size: 26rpx;
      color: #666;
      margin-bottom: 10rpx;
      display: block;
    }
    .input {
      border: 2rpx solid #eee;
      border-radius: 12rpx;
      padding: 20rpx 24rpx;
      font-size: 28rpx;
      background: #FAFAFA;
    }
  }
  .login-btn {
    width: 100%;
    margin-top: 40rpx;
    text-align: center;
  }
}
.login-footer {
  margin-top: 60rpx;
  text-align: center;
  .tip {
    font-size: 22rpx;
    color: #bbb;
    display: block;
    line-height: 1.8;
  }
}
</style>
