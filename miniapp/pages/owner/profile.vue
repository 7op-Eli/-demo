<template>
  <view class="profile-page">
    <view class="header-card">
      <image class="avatar" src="/static/avatar.png" mode="aspectFill"></image>
      <text class="name">{{ userInfo.realName || '业主' }}</text>
      <text class="role-badge tag tag-blue">业主</text>
    </view>

    <view class="card">
      <view class="menu-item" @click="navigate('/pages/owner/messages')">
        <text class="menu-icon">💬</text>
        <text class="menu-text">管家消息</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/fee')">
        <text class="menu-icon">💰</text>
        <text class="menu-text">物业缴费</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/repair')">
        <text class="menu-icon">🔧</text>
        <text class="menu-text">报事报修</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/visitor')">
        <text class="menu-icon">👤</text>
        <text class="menu-text">访客通行</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/notices')">
        <text class="menu-icon">📢</text>
        <text class="menu-text">小区公告</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="navigate('/pages/owner/convenience')">
        <text class="menu-icon">🛠</text>
        <text class="menu-text">便民服务</text>
        <text class="menu-arrow">></text>
      </view>
    </view>

    <view class="card">
      <view class="menu-item" v-if="userInfo.roleType === 2" @click="switchToEmployee">
        <text class="menu-icon">🔄</text>
        <text class="menu-text">切换至员工端</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @click="handleLogout">
        <text class="menu-icon" style="color: #ED3F14">🚪</text>
        <text class="menu-text" style="color: #ED3F14">退出登录</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo, logout } from '../../utils/auth'

export default {
  data() {
    return { userInfo: getUserInfo() || {} }
  },
  methods: {
    navigate(url) { uni.navigateTo({ url }) },
    switchToEmployee() {
      uni.reLaunch({ url: '/pages/employee/index' })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定退出登录吗？',
        success: (r) => { if (r.confirm) logout() }
      })
    }
  }
}
</script>

<style scoped>
.profile-page { padding-bottom: 40rpx; }
.header-card {
  text-align: center; padding: 60rpx 40rpx;
  background: linear-gradient(135deg, #2B85E4, #5CADFF);
  color: #fff;
}
.avatar { width: 120rpx; height: 120rpx; border-radius: 50%; background: rgba(255,255,255,0.3); display: inline-block; }
.name { font-size: 36rpx; font-weight: bold; display: block; margin-top: 16rpx; }
.role-badge { margin-top: 10rpx; }
.menu-item { display: flex; align-items: center; padding: 24rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.menu-icon { font-size: 36rpx; margin-right: 20rpx; }
.menu-text { flex: 1; font-size: 28rpx; }
.menu-arrow { color: #ccc; font-size: 28rpx; }
</style>
