<template>
  <view class="profile-page">
    <view class="header-card">
      <image class="avatar" src="/static/avatar.png" mode="aspectFill"></image>
      <text class="name">{{ userInfo.realName || '业主' }}</text>
      <text class="role-tag tag tag-blue">{{ userInfo.roleLabel || '业主' }}</text>
    </view>

    <view class="info-card card">
      <view class="info-item">
        <text class="info-label">手机号</text>
        <text class="info-value">{{ maskedPhone }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">身份</text>
        <text class="info-value">{{ userInfo.roleLabel || '业主' }}</text>
      </view>
      <view class="info-item">
        <text class="info-label">账号</text>
        <text class="info-value">{{ userInfo.username }}</text>
      </view>
    </view>

    <view class="card">
      <view class="menu-item" @click="navigate('/pages/owner/fee')">
        <text>💰 物业缴费</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/repair')">
        <text>🔧 我的报修</text>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/visitor')">
        <text>👤 访客通行</text>
        <text class="arrow">></text>
      </view>
    </view>

    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </view>
</template>

<script>
import { getUserInfo, logout } from '../../utils/auth'

export default {
  data() {
    return { userInfo: {} }
  },
  computed: {
    maskedPhone() {
      const p = this.userInfo.username || ''
      return p.length >= 11 ? p.substring(0, 3) + '****' + p.substring(7) : p
    }
  },
  onShow() { this.userInfo = getUserInfo() || {} },
  methods: {
    navigate(url) { uni.navigateTo({ url }) },
    handleLogout() {
      uni.showModal({
        title: '退出登录',
        content: '确定要退出登录吗？',
        success: (r) => {
          if (r.confirm) {
            logout()
            uni.reLaunch({ url: '/pages/login' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding-bottom: 30rpx;
}
.header-card {
  background: linear-gradient(135deg, #2B85E4, #5CADFF);
  padding: 60rpx 40rpx;
  text-align: center;
  color: #fff;
}
.header-card .avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.3);
}
.header-card .name {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-top: 16rpx;
}
.header-card .role-tag {
  margin-top: 10rpx;
  display: inline-block;
}
.info-card {
  margin-top: -20rpx;
  border-radius: 20rpx 20rpx 0 0;
}
.info-item {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.info-item .info-label {
  font-size: 26rpx;
  color: #999;
}
.info-item .info-value {
  font-size: 26rpx;
  color: #333;
}
.menu-item {
  display: flex;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  font-size: 28rpx;
}
.menu-item .arrow {
  color: #ccc;
}
.logout-btn {
  width: 90%;
  margin: 40rpx auto;
  background: #f5f5f5;
  color: #999;
  border: none;
  border-radius: 40rpx;
  padding: 20rpx;
  font-size: 28rpx;
  text-align: center;
}
</style>