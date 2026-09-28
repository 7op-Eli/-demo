<template>
  <view class="login-page">
    <view class="login-header">
      <image class="logo" src="/static/avatar.png" mode="aspectFit"></image>
      <text class="title">智慧物业</text>
      <text class="subtitle">Smart Property Management</text>
    </view>

    <view class="login-body">
      <text class="welcome">欢迎使用智慧物业管理系统</text>
      <text class="desc">微信一键授权登录，无需输入手机号</text>

      <!-- 微信授权登录按钮：点击后调用 uni.login() 拿 code -->
      <button class="wechat-btn" @click="handleWechatLogin" :loading="loading" :disabled="loading">
        <text class="wechat-icon">💬</text>
        <text>{{ loading ? '授权登录中...' : '微信一键登录' }}</text>
      </button>

      <!-- Demo 模式：无正式 AppID 时使用，创建独立 demo 账号 -->
      <view class="demo-section">
        <view class="demo-divider">
          <view class="demo-divider-line"></view>
          <text class="demo-divider-text">Demo 模式（开发自测）</text>
          <view class="demo-divider-line"></view>
        </view>
        <input class="phone-input" v-model="demoKey" type="number" maxlength="11"
               placeholder="输入已登记的手机号（如业主/员工/政府手机号）" />
        <button class="demo-btn" @click="handleDemoLogin" :loading="loading" :disabled="loading">
          模拟登录
        </button>
        <text class="demo-tip">后端根据手机号自动匹配角色：业主/员工/政府</text>
      </view>
    </view>

    <view class="login-footer">
      <text class="tip">授权即表示同意《用户协议》和《隐私政策》</text>
    </view>
  </view>
</template>

<script>
import { post } from '../utils/request'

export default {
  data() {
    return {
      demoKey: '',
      loading: false
    }
  },
  methods: {
    // 真实微信授权登录：uni.login 拿 code → 后端用 code 调微信换 openid
    async handleWechatLogin() {
      if (this.loading) return
      this.loading = true
      try {
        // 1. 调微信拿临时 code（5 分钟有效）
        const loginRes = await new Promise((resolve, reject) => {
          uni.login({
            provider: 'weixin',
            success: resolve,
            fail: reject
          })
        })
        if (!loginRes.code) {
          uni.showToast({ title: '微信授权失败，请重试', icon: 'none' })
          return
        }
        // 2. 把 code 发给后端，后端用 code 换 openid（不传手机号）
        await this.doLogin({ code: loginRes.code })
      } catch (e) {
        uni.showToast({ title: '微信授权失败，请重试', icon: 'none' })
        console.error('Wechat login failed:', e)
      } finally {
        this.loading = false
      }
    },

    // Demo 模式：用 demoKey 生成独立体验账号（不碰真实数据）
    async handleDemoLogin() {
      if (this.loading) return
      const key = this.demoKey.trim()
      if (!key) {
        uni.showToast({ title: '请输入标识', icon: 'none' })
        return
      }
      this.loading = true
      try {
        await this.doLogin({ demoKey: key })
      } catch (e) {
        const msg = (e && e.msg) || '登录失败'
        uni.showToast({ title: msg, icon: 'none', duration: 2500 })
        console.error('Demo login failed:', e)
      } finally {
        this.loading = false
      }
    },

    // 统一登录请求：后端接收 code 或 demoKey，返回 token
    async doLogin(payload) {
      let latitude = null, longitude = null
      try {
        const loc = await this.getLocation()
        latitude = loc.latitude
        longitude = loc.longitude
      } catch (e) { /* 位置获取失败不阻塞 */ }

      const res = await post('/auth/wechat-login', { ...payload, latitude, longitude })

      uni.setStorageSync('token', res.token)
      uni.setStorageSync('userInfo', JSON.stringify({
        userId: res.userId, username: res.username, realName: res.realName,
        roleType: res.roleType, roleLabel: res.roleLabel,
        ownerId: res.ownerId, employeeId: res.employeeId
      }))

      uni.showToast({ title: '登录成功', icon: 'success' })

      setTimeout(() => {
        const routes = { 1: '/pages/owner/index', 2: '/pages/employee/index', 4: '/pages/government/index' }
        uni.reLaunch({ url: routes[res.roleType] || '/pages/owner/index' })
      }, 500)
    },

    getLocation() {
      return new Promise((resolve, reject) => {
        uni.getLocation({ type: 'gcj02', success: resolve, fail: reject })
      })
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 60rpx;
  background: linear-gradient(180deg, #E8F4FD 0%, #FFFFFF 40%);
}

.login-header {
  text-align: center;
  margin-bottom: 80rpx;
}
.login-header .logo {
  width: 140rpx;
  height: 140rpx;
  border-radius: 30rpx;
  background: #fff;
  box-shadow: 0 8rpx 24rpx rgba(43,133,228,0.15);
}
.login-header .title {
  font-size: 44rpx;
  font-weight: bold;
  color: #2B85E4;
  display: block;
  margin-top: 24rpx;
}
.login-header .subtitle {
  font-size: 24rpx;
  color: #999;
  margin-top: 8rpx;
}

.login-body {
  width: 100%;
  text-align: center;
}
.login-body .welcome {
  font-size: 32rpx;
  color: #333;
  font-weight: bold;
  display: block;
}
.login-body .desc {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-top: 12rpx;
  margin-bottom: 60rpx;
}

.wechat-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #07C160, #06AD56);
  color: #fff;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(7,193,96,0.3);
}
.wechat-btn .wechat-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
}

.demo-section {
  margin-top: 50rpx;
}
.demo-divider {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}
.demo-divider-line {
  flex: 1;
  height: 1rpx;
  background: #eee;
}
.demo-divider-text {
  padding: 0 24rpx;
  font-size: 22rpx;
  color: #ccc;
}
.demo-section .phone-input {
  width: 100%;
  border: 2rpx solid #eee;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 30rpx;
  text-align: center;
  background: #FAFAFA;
  margin-bottom: 24rpx;
}
.demo-section .demo-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: #2B85E4;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
  border: none;
}
.demo-section .demo-tip {
  font-size: 20rpx;
  color: #ccc;
  display: block;
  margin-top: 16rpx;
}

.login-footer {
  position: fixed;
  bottom: 60rpx;
}
.login-footer .tip {
  font-size: 22rpx;
  color: #bbb;
}
</style>