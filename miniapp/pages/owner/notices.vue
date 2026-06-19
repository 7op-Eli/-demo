<template>
  <view class="notice-page">
    <!-- 分类Tabs -->
    <view class="tabs card">
      <view class="tab-item" v-for="tab in tabs" :key="tab.key"
            :class="{ active: currentTab === tab.key }"
            @click="switchTab(tab.key)">
        <text>{{ tab.label }}</text>
      </view>
    </view>

    <!-- 列表 -->
    <view class="card" v-for="item in notices" :key="item.id" @click="showDetail(item)">
      <view class="notice-header">
        <text class="notice-tag tag" :class="catClass(item.category)">{{ catLabel(item.category) }}</text>
        <text class="notice-top" v-if="item.topStatus === 1">置顶</text>
      </view>
      <text class="notice-title">{{ item.title }}</text>
      <text class="notice-desc">{{ item.content }}</text>
      <view class="notice-footer">
        <text class="notice-time">{{ formatTime(item.publishTime) }}</text>
        <text class="notice-views">阅读 {{ item.viewCount || 0 }}</text>
      </view>
    </view>
    <view v-if="notices.length === 0" class="empty">暂无公告</view>

    <!-- 详情弹窗 -->
    <uni-popup ref="popup" type="center">
      <view class="detail-modal">
        <text class="detail-title">{{ detail.title }}</text>
        <text class="detail-content">{{ detail.content }}</text>
        <button class="close-btn" @click="$refs.popup.close()">关闭</button>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getNotices } from '../../api/notice'

export default {
  data() {
    return {
      currentTab: 'all',
      tabs: [
        { key: 'all', label: '全部' },
        { key: 'community', label: '物业通知' },
        { key: 'government', label: '政府公告' },
        { key: 'law', label: '法律法规' },
        { key: 'emergency', label: '紧急通知' }
      ],
      notices: [],
      detail: {}
    }
  },
  onShow() { this.loadNotices() },
  methods: {
    async loadNotices() {
      try {
        const cat = this.currentTab === 'all' ? null : this.currentTab
        const res = await getNotices(cat, 1)
        this.notices = res.list || []
      } catch (e) { /* ignore */ }
    },
    switchTab(key) { this.currentTab = key; this.loadNotices() },
    showDetail(item) {
      this.detail = item
      this.$refs.popup.open()
    },
    catClass(c) {
      const map = { 'government':'tag-red', 'law':'tag-orange', 'community':'tag-blue', 'emergency':'tag-red' }
      return map[c] || 'tag-blue'
    },
    catLabel(c) {
      const map = { 'government':'政府', 'law':'法规', 'community':'物业', 'emergency':'紧急', 'other':'其他' }
      return map[c] || '通知'
    },
    formatTime(t) { return t ? t.substring(0, 16) : '' }
  }
}
</script>

<style scoped>
.notice-page { padding-bottom: 40rpx; }
.tabs { display: flex; overflow-x: auto; padding: 20rpx 0; }
.tab-item { padding: 12rpx 28rpx; margin-right: 16rpx; border-radius: 30rpx; font-size: 26rpx; background: #f5f5f5; color: #666; white-space: nowrap; }
.tab-item.active { background: #2B85E4; color: #fff; }
.notice-header { display: flex; align-items: center; margin-bottom: 12rpx; }
.notice-top { margin-left: 12rpx; font-size: 22rpx; color: #ED3F14; font-weight: bold; }
.notice-title { font-size: 30rpx; font-weight: bold; display: block; margin-bottom: 10rpx; }
.notice-desc { font-size: 26rpx; color: #666; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; margin-bottom: 12rpx; }
.notice-footer { display: flex; justify-content: space-between; }
.notice-time, .notice-views { font-size: 22rpx; color: #bbb; }
.empty { text-align: center; color: #999; padding: 60rpx 0; }
.detail-modal {
  width: 600rpx; max-height: 700rpx; background: #fff; border-radius: 20rpx;
  padding: 40rpx; overflow-y: auto;
}
.detail-title { font-size: 34rpx; font-weight: bold; display: block; margin-bottom: 24rpx; }
.detail-content { font-size: 28rpx; color: #333; line-height: 1.7; display: block; }
.close-btn { margin-top: 30rpx; width: 100%; background: #2B85E4; color: #fff; border: none; border-radius: 40rpx; padding: 20rpx; text-align: center; }
</style>
