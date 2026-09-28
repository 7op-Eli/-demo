<template>
  <view class="convenience-page">
    <!-- 社区团购（placeholder） -->
    <view class="card">
      <view class="section-title">🛒 社区团购</view>
      <text class="coming-soon">功能开发中，敬请期待...</text>
    </view>

    <!-- 便民服务 -->
    <view class="card" v-for="svc in services" :key="svc.id">
      <view class="svc-header">
        <text class="svc-name">{{ svc.name }}</text>
        <text class="svc-tag tag tag-green" v-if="svc.status === 1">营业中</text>
      </view>
      <text class="svc-desc">{{ svc.description }}</text>
      <view class="svc-info" v-if="svc.contactPhone">
        <text>📞 {{ svc.contactPhone }}</text>
      </view>
      <view class="svc-info" v-if="svc.serviceTime">
        <text>🕐 {{ svc.serviceTime }}</text>
      </view>
    </view>

    <!-- 工具借用 -->
    <view class="card">
      <view class="section-title">🔨 工具借用</view>
      <view class="tool-item" v-for="tool in tools" :key="tool.id">
        <view class="tool-info">
          <text class="tool-name">{{ tool.name }}</text>
          <text class="tool-stock">可用: {{ tool.availableQuantity }}/{{ tool.totalQuantity }}{{ tool.unit }}</text>
        </view>
        <button class="borrow-btn" v-if="tool.availableQuantity > 0"
                @click="borrowTool(tool)" size="mini">借用</button>
        <text class="tag tag-red" v-else>已借完</text>
      </view>
    </view>

    <view v-if="services.length === 0" class="empty">暂无便民服务</view>
  </view>
</template>

<script>
import { getServices, getTools, borrowTool } from '../../api/convenience'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return { userInfo: {}, services: [], tools: [] }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadData()
  },
  methods: {
    async loadData() {
      try { this.services = await getServices() || [] } catch (e) {}
      try { this.tools = await getTools() || [] } catch (e) {}
    },
    async borrowTool(tool) {
      uni.showModal({
        title: '借用工具',
        content: `确认借用「${tool.name}」吗？`,
        success: async (r) => {
          if (r.confirm) {
            try {
              await borrowTool({
                toolId: tool.id,
                ownerId: this.userInfo.ownerId,
                borrowerName: this.userInfo.realName,
                borrowerPhone: this.userInfo.username,
                quantity: 1,
                borrowTime: new Date().toISOString()
              })
              uni.showToast({ title: '借用成功', icon: 'success' })
              this.loadData()
            } catch (e) { uni.showToast({ title: '借用失败', icon: 'none' }) }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.convenience-page { padding-bottom: 30rpx; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 16rpx; }
.coming-soon { font-size: 26rpx; color: #999; display: block; text-align: center; padding: 40rpx 0; }
.svc-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.svc-name { font-size: 28rpx; font-weight: bold; }
.svc-desc { font-size: 24rpx; color: #666; display: block; margin-bottom: 8rpx; }
.svc-info { font-size: 24rpx; color: #999; margin-top: 4rpx; }
.tool-item { display: flex; justify-content: space-between; align-items: center; padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.tool-name { font-size: 26rpx; font-weight: bold; display: block; }
.tool-stock { font-size: 22rpx; color: #999; }
.borrow-btn { background: #2B85E4; color: #fff; border: none; border-radius: 20rpx; padding: 8rpx 24rpx; font-size: 22rpx; }
.empty { text-align: center; color: #999; padding: 40rpx; }
</style>
