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
      <!-- 报修入口（显眼位置） -->
      <view class="repair-entry" @click="navigate('/pages/owner/repair')">
        <text class="repair-icon">🔧</text>
        <text class="repair-text">我要报修</text>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions card">
      <view class="action-grid">
        <view class="action-item" @click="navigate('/pages/owner/fee')">
          <view class="icon-box icon-box-blue"><text>💰</text></view>
          <text class="label">物业缴费</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/repair')">
          <view class="icon-box icon-box-orange"><text>🔧</text></view>
          <text class="label">报事报修</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/visitor')">
          <view class="icon-box icon-box-green"><text>👤</text></view>
          <text class="label">访客通行</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/notices')">
          <view class="icon-box icon-box-red"><text>📢</text></view>
          <text class="label">小区公告</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/messages')">
          <view class="icon-box icon-box-purple"><text>💬</text></view>
          <text class="label">管家消息</text>
        </view>
        <view class="action-item" @click="navigate('/pages/owner/convenience')">
          <view class="icon-box icon-box-teal"><text>🛒</text></view>
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
        <view class="notice-tag tag tag-blue">{{ item.category }}</view>
        <text class="notice-title">{{ item.title }}</text>
        <text class="notice-time">{{ formatDate(item.publishTime) }}</text>
      </view>
      <view v-if="notices.length === 0" class="empty">暂无公告</view>
    </view>

    <!-- 待办提醒 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">待办提醒</text>
      </view>
      <view class="todo-item" v-if="pendingBills > 0" @click="navigate('/pages/owner/fee')">
        <text>📋 您有 {{ pendingBills }} 笔账单待缴费</text>
      </view>
      <view v-if="pendingBills === 0" class="empty">🎉 暂无待办事项</view>
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
      pendingBills: 0
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
        const res = await getNotices(null, 1)
        this.notices = (res && res.list) || []
      } catch (e) { console.error('loadNotices failed:', e) }
    },
    async loadBills() {
      try {
        const res = await getBills(1)
        this.pendingBills = (res?.list || []).filter(b => b.status === 0).length
      } catch (e) {}
    },
    navigate(url) { uni.navigateTo({ url }) },
    formatDate(t) { return t ? t.substring(0, 10) : '' },
    viewNotice(id) { uni.navigateTo({ url: `/pages/owner/notices?noticeId=${id}` }) }
  }
}
</script>

<style scoped>
.owner-home { padding-bottom: 30rpx; }

.header-card {
  background: linear-gradient(135deg, #2B85E4, #5CADFF);
  padding: 40rpx 40rpx 30rpx;
  color: #fff;
}
.header-card .user-info { display: flex; align-items: center; }
.header-card .avatar { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(255,255,255,0.3); }
.header-card .info { margin-left: 20rpx; }
.header-card .name { font-size: 32rpx; font-weight: bold; display: block; }
.header-card .role { font-size: 22rpx; opacity: 0.8; }
.header-card .repair-entry {
  margin-top: 20rpx; background: rgba(255,255,255,0.2);
  border-radius: 16rpx; padding: 18rpx;
  display: flex; align-items: center; justify-content: center;
}
.header-card .repair-icon { font-size: 36rpx; margin-right: 10rpx; }
.header-card .repair-text { font-size: 28rpx; font-weight: bold; }

/* 快捷功能 - flexbox 代替 grid */
.action-grid {
  display: flex;
  flex-wrap: wrap;
}
.action-item {
  width: 33.33%;
  text-align: center;
  padding: 20rpx 0;
  box-sizing: border-box;
}
.icon-box {
  width: 80rpx; height: 80rpx; border-radius: 20rpx;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12rpx; font-size: 36rpx;
}
.icon-box-blue { background: #E8F4FD; }
.icon-box-orange { background: #FFF3E0; }
.icon-box-green { background: #E8F8EF; }
.icon-box-red { background: #FDECEA; }
.icon-box-purple { background: #F0E6FF; }
.icon-box-teal { background: #E0F7FA; }
.action-item .label { font-size: 24rpx; color: #666; }

.section-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 16rpx;
}
.section-title { font-size: 30rpx; font-weight: bold; }
.section-header .more { font-size: 24rpx; color: #2B85E4; }

.notice-item {
  display: flex; align-items: center; padding: 14rpx 0; border-bottom: 1rpx solid #f5f5f5;
}
.notice-tag { margin-right: 12rpx; }
.notice-title { flex: 1; font-size: 26rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.notice-time { font-size: 22rpx; color: #999; margin-left: 12rpx; }

.todo-item { padding: 16rpx 0; font-size: 26rpx; color: #666; }
.empty { text-align: center; color: #999; padding: 30rpx; font-size: 24rpx; }
</style>
