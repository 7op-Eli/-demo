<template>
  <view class="owner-home">
    <!-- 用户信息头 -->
    <view class="header-card">
      <view class="user-info">
        <image class="avatar" src="/static/avatar.png" mode="aspectFill"></image>
        <view class="info">
          <text class="name">{{ userInfo.realName || '业主' }}</text>
          <text class="role">业主</text>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions card">
      <view class="action-grid">
        <view class="action-item" @click="navigate('/pages/owner/fee')">
          <view class="icon-box blue"><text class="iconfont">💰</text></view>
          <text class="label">物业缴费</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/repair')">
          <view class="icon-box orange"><text class="iconfont">🔧</text></view>
          <text class="label">报事报修</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/visitor')">
          <view class="icon-box green"><text class="iconfont">👤</text></view>
          <text class="label">访客通行</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/notices')">
          <view class="icon-box red"><text class="iconfont">📢</text></view>
          <text class="label">小区公告</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/messages')">
          <view class="icon-box purple"><text class="iconfont">💬</text></view>
          <text class="label">管家消息</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/convenience')">
          <view class="icon-box teal"><text class="iconfont">🛠</text></view>
          <text class="label">便民服务</text>
        </view>
      </view>
    </view>

    <!-- 公告速览 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">最新公告</text>
        <text class="more" @click="navigate('/pages/owner/notices')">更多 ></text>
      </view>
      <view class="notice-item" v-for="item in notices" :key="item.id"
            @click="viewNotice(item.id)">
        <view class="notice-tag tag tag-blue">{{ categoryLabel(item.category) }}</view>
        <text class="notice-title">{{ item.title }}</text>
        <text class="notice-time">{{ formatTime(item.publishTime) }}</text>
      </view>
      <view v-if="notices.length === 0" class="empty">暂无公告</view>
    </view>

    <!-- 待办提醒 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">待办提醒</text>
      </view>
      <view class="todo-item" v-if="pendingBills > 0" @click="navigate('/pages/owner/fee')">
        <text class="todo-icon">📋</text>
        <text class="todo-text">您有 {{ pendingBills }} 笔账单待缴费</text>
      </view>
      <view class="todo-item" v-if="pendingVisitors > 0" @click="navigate('/pages/owner/visitor')">
        <text class="todo-icon">👤</text>
        <text class="todo-text">有 {{ pendingVisitors }} 位访客待审核</text>
      </view>
      <view v-if="pendingBills === 0 && pendingVisitors === 0" class="empty">
        🎉 暂无待办事项
      </view>
    </view>
  </view>
</template>

<script>
import { getNotices } from '../../api/notice'
import { getBills } from '../../api/fee'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      notices: [],
      pendingBills: 0,
      pendingVisitors: 0
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadNotices()
    this.loadBills()
  },
  methods: {
    async loadNotices() {
      try {
        this.notices = await getNotices(null, 1) || []
      } catch (e) { /* ignore */ }
    },
    async loadBills() {
      try {
        const ownerId = this.userInfo.ownerId
        if (ownerId) {
          const bills = await getBills(1) || { list: [] }
          this.pendingBills = bills.list.filter(b => b.status === 0).length
        }
      } catch (e) { /* ignore */ }
    },
    navigate(url) { uni.navigateTo({ url }) },
    categoryLabel(cat) {
      const map = { 'government': '政府', 'law': '法规', 'community': '物业', 'emergency': '紧急' }
      return map[cat] || '通知'
    },
    formatTime(t) {
      if (!t) return ''
      return t.substring(0, 10)
    },
    viewNotice(id) {
      uni.navigateTo({ url: `/pages/owner/notices?noticeId=${id}` })
    }
  }
}
</script>

<style lang="scss" scoped>
.owner-home { padding-bottom: 120rpx; }
.header-card {
  background: linear-gradient(135deg, #2B85E4, #5CADFF);
  padding: 60rpx 40rpx 40rpx;
  color: #fff;
  .user-info {
    display: flex;
    align-items: center;
    .avatar { width: 100rpx; height: 100rpx; border-radius: 50%; background: rgba(255,255,255,0.3); }
    .info {
      margin-left: 24rpx;
      .name { font-size: 36rpx; font-weight: bold; display: block; }
      .role { font-size: 24rpx; opacity: 0.8; margin-top: 6rpx; }
    }
  }
}
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  .action-item {
    text-align: center;
    padding: 20rpx 0;
    .icon-box {
      width: 80rpx; height: 80rpx; border-radius: 20rpx;
      display: flex; align-items: center; justify-content: center;
      margin: 0 auto 12rpx;
      font-size: 36rpx;
      &.blue { background: #E8F4FD; }
      &.orange { background: #FFF3E0; }
      &.green { background: #E8F8EF; }
      &.red { background: #FDECEA; }
      &.purple { background: #F0E6FF; }
      &.teal { background: #E0F7FA; }
    }
    .label { font-size: 24rpx; color: #666; }
  }
}
.section-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20rpx;
  .section-title { font-size: 30rpx; font-weight: bold; }
  .more { font-size: 24rpx; color: $primary-color; }
}
.notice-item {
  display: flex; align-items: center; padding: 16rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
  .notice-tag { margin-right: 12rpx; }
  .notice-title { flex: 1; font-size: 26rpx; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .notice-time { font-size: 22rpx; color: #999; margin-left: 12rpx; }
}
.todo-item {
  display: flex; align-items: center; padding: 16rpx 0;
  .todo-icon { font-size: 32rpx; margin-right: 16rpx; }
  .todo-text { font-size: 26rpx; color: #666; }
}
.empty { text-align: center; color: #999; padding: 30rpx; font-size: 26rpx; }
</style>
