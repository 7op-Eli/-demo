<template>
  <view class="tool-manage">
    <view class="card">
      <view class="section-title">工具列表</view>
      <view class="tool-item" v-for="t in tools" :key="t.id">
        <view class="tool-info">
          <text class="tool-name">{{ t.name }}</text>
          <text class="tool-stock">可用: {{ t.availableQuantity }}/{{ t.totalQuantity }}{{ t.unit }}</text>
        </view>
        <text class="tool-status tag" :class="t.status === 1 ? 'tag-green' : 'tag-red'">
          {{ t.status === 1 ? '可借用' : '停用' }}
        </text>
      </view>
    </view>

    <view class="card">
      <view class="section-title">借用中的记录</view>
      <view class="usage-item" v-for="u in activeUsages" :key="u.id">
        <text class="usage-tool">工具ID: {{ u.toolId }}</text>
        <text class="usage-borrower">借用人: {{ u.borrowerName }}</text>
        <text class="usage-time">借用: {{ u.borrowTime }}</text>
        <button class="return-btn" @click="handleReturn(u.id)">归还</button>
      </view>
      <view v-if="activeUsages.length === 0" class="empty">暂无借用记录</view>
    </view>
  </view>
</template>

<script>
import { get, put } from '../../utils/request'

export default {
  data() {
    return { tools: [], activeUsages: [] }
  },
  onShow() { this.loadData() },
  methods: {
    async loadData() {
      try {
        this.tools = await get('/convenience/tools') || []
        const res = await get('/convenience/usage-records', { page: 1, size: 50 })
        this.activeUsages = (res.list || []).filter(u => u.status === 0)
      } catch (e) { /* ignore */ }
    },
    async handleReturn(id) {
      try {
        await put(`/convenience/tools/return/${id}`)
        uni.showToast({ title: '归还成功', icon: 'success' })
        this.loadData()
      } catch (e) { /* ignore */ }
    }
  }
}
</script>

<style scoped>
.tool-manage { padding-bottom: 40rpx; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 16rpx; }
.tool-item { display: flex; align-items: center; justify-content: space-between; padding: 14rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.tool-name { font-size: 28rpx; display: block; }
.tool-stock { font-size: 22rpx; color: #999; }
.usage-item { padding: 14rpx 0; border-bottom: 1rpx solid #f5f5f5; font-size: 24rpx; color: #666; }
.usage-tool, .usage-borrower, .usage-time { display: block; margin-bottom: 2rpx; }
.return-btn { margin-top: 8rpx; background: #19BE6B; color: #fff; border: none; border-radius: 30rpx; padding: 8rpx 28rpx; font-size: 24rpx; }
.empty { text-align: center; color: #999; padding: 40rpx; font-size: 24rpx; }
</style>
