<template>
  <view class="emp-home">
    <!-- 员工信息 -->
    <view class="header-card">
      <view class="user-info">
        <image class="avatar" src="/static/avatar.png" mode="aspectFill"></image>
        <view class="info">
          <text class="name">{{ userInfo.realName || '员工' }}</text>
          <text class="role">物业员工</text>
        </view>
      </view>
    </view>

    <!-- 统计数据 -->
    <view class="card stat-row">
      <view class="stat-item" @click="navigate('/pages/employee/repair-list?status=1')">
        <text class="stat-num red">{{ stats.pendingOrders }}</text>
        <text class="stat-label">待接单</text>
      </view>
      <view class="stat-item" @click="navigate('/pages/employee/repair-list?status=4')">
        <text class="stat-num orange">{{ stats.inProgressOrders }}</text>
        <text class="stat-label">维修中</text>
      </view>
      <view class="stat-item" @click="navigate('/pages/employee/visitor-audit')">
        <text class="stat-num blue">{{ stats.pendingVisitors }}</text>
        <text class="stat-label">待审核</text>
      </view>
      <view class="stat-item" @click="navigate('/pages/employee/schedule')">
        <text class="stat-num green">{{ stats.todaySchedules }}</text>
        <text class="stat-label">今日日程</text>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="card">
      <view class="section-title">快捷操作</view>
      <view class="quick-grid">
        <view class="quick-item" @click="navigate('/pages/employee/repair-list')">
          <text class="quick-icon">🔧</text>
          <text class="quick-label">报修工单</text>
        </view>
        <view class="quick-item" @click="navigate('/pages/employee/visitor-audit')">
          <text class="quick-icon">👤</text>
          <text class="quick-label">访客审核</text>
        </view>
        <view class="quick-item" @click="navigate('/pages/employee/schedule')">
          <text class="quick-icon">📅</text>
          <text class="quick-label">工作日程</text>
        </view>
        <view class="quick-item" @click="navigate('/pages/employee/tool-manage')">
          <text class="quick-icon">🔨</text>
          <text class="quick-label">工具管理</text>
        </view>
      </view>
    </view>

    <!-- 最新待处理 -->
    <view class="card">
      <view class="section-title">待处理工单</view>
      <view class="order-item" v-for="o in recentOrders" :key="o.id"
            @click="navigate(`/pages/employee/repair-handle?id=${o.id}`)">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="order-status tag tag-orange">待处理</text>
        </view>
        <text class="order-desc">{{ o.description }}</text>
        <text class="order-time">{{ o.createdAt }}</text>
      </view>
      <view v-if="recentOrders.length === 0" class="empty">暂无可处理工单</view>
    </view>
  </view>
</template>

<script>
import { getOrdersByStatus } from '../../api/repair-employee'
import { getUserInfo, logout } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: getUserInfo() || {},
      stats: { pendingOrders: 0, inProgressOrders: 0, pendingVisitors: 0, todaySchedules: 0 },
      recentOrders: []
    }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        // 这儿用简单的模拟获取 — 实际对接需员工端API
        this.stats = { pendingOrders: 3, inProgressOrders: 2, pendingVisitors: 1, todaySchedules: 5 }
      } catch (e) { /* ignore */ }
    },
    navigate(url) { uni.navigateTo({ url }) }
  }
}
</script>

<style lang="scss" scoped>
.emp-home { padding-bottom: 120rpx; }
.header-card {
  background: linear-gradient(135deg, #19BE6B, #5CDB8E);
  padding: 60rpx 40rpx 40rpx; color: #fff;
  .user-info { display: flex; align-items: center; }
  .avatar { width: 100rpx; height: 100rpx; border-radius: 50%; background: rgba(255,255,255,0.3); }
  .info { margin-left: 24rpx; }
  .name { font-size: 36rpx; font-weight: bold; display: block; }
  .role { font-size: 24rpx; opacity: 0.8; margin-top: 6rpx; }
}
.stat-row { display: flex; justify-content: space-around; padding: 30rpx; }
.stat-item { text-align: center; }
.stat-num { font-size: 40rpx; font-weight: bold; display: block; }
.stat-num.red { color: #ED3F14; }
.stat-num.orange { color: #FF9900; }
.stat-num.blue { color: #2B85E4; }
.stat-num.green { color: #19BE6B; }
.stat-label { font-size: 22rpx; color: #999; margin-top: 6rpx; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 20rpx; }
.quick-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16rpx; }
.quick-item { text-align: center; padding: 20rpx 0; }
.quick-icon { font-size: 48rpx; display: block; }
.quick-label { font-size: 24rpx; color: #666; margin-top: 8rpx; }
.order-item { padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.order-header { display: flex; justify-content: space-between; }
.order-no { font-size: 24rpx; color: #999; }
.order-desc { font-size: 26rpx; margin-top: 6rpx; display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.order-time { font-size: 22rpx; color: #bbb; margin-top: 4rpx; display: block; }
.empty { text-align: center; color: #999; padding: 30rpx; font-size: 24rpx; }
</style>
