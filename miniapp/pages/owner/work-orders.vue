<template>
  <view class="work-orders-page">
    <!-- 顶部报修入口 -->
    <view class="repair-banner" @click="navigate('/pages/owner/repair')">
      <text class="repair-icon">🔧</text>
      <text class="repair-text">我要报修</text>
      <text class="repair-arrow">></text>
    </view>

    <!-- 工单列表 -->
    <view class="card" v-for="order in orders" :key="order.id"
          @click="viewDetail(order.id)">
      <view class="order-header">
        <text class="order-no">{{ order.orderNo }}</text>
        <text class="order-time">{{ formatDate(order.completeTime) }}</text>
      </view>
      <text class="order-desc">{{ order.description }}</text>
      <view class="order-tags">
        <text class="tag tag-blue">{{ order.repairType }}</text>
      </view>
      <!-- 评价信息 -->
      <view class="eval-section" v-if="order._evaluation">
        <view class="eval-stars">
          <text v-for="i in 5" :key="i"
                :style="{ color: i <= order._evaluation.rating ? '#FF9900' : '#eee' }">★</text>
        </view>
        <text class="eval-comment" v-if="order._evaluation.comment">{{ order._evaluation.comment }}</text>
      </view>
    </view>

    <view v-if="orders.length === 0" class="empty">暂无工单展示</view>
    <view v-if="loading" class="loading">加载中...</view>
  </view>
</template>

<script>
import { get } from '../../utils/request'

export default {
  data() {
    return {
      orders: [],
      loading: false,
      page: 1,
      hasMore: true
    }
  },
  onShow() { this.page = 1; this.hasMore = true; this.orders = []; this.loadOrders() },
  onReachBottom() { if (this.hasMore) this.loadOrders() },
  methods: {
    async loadOrders() {
      if (this.loading || !this.hasMore) return
      this.loading = true
      try {
        const res = await get('/repair/orders/public-feed', { page: this.page, size: 10 })
        const list = res.list || []
        for (const o of list) {
          try {
            o._evaluation = await get('/repair/orders/' + o.id + '/evaluation')
          } catch (e) { o._evaluation = null }
        }
        this.orders = this.page === 1 ? list : [...this.orders, ...list]
        this.hasMore = list.length >= 10
        this.page++
      } catch (e) { console.error('loadOrders:', e) }
      finally { this.loading = false }
    },
    navigate(url) { uni.navigateTo({ url }) },
    viewDetail(id) { uni.navigateTo({ url: '/pages/owner/repair-detail?id=' + id }) },
    formatDate(t) { return t ? t.substring(0, 10) : '' }
  }
}
</script>

<style scoped>
.work-orders-page {
  padding-bottom: 30rpx;
}
.repair-banner {
  margin: 20rpx;
  padding: 24rpx 30rpx;
  background: linear-gradient(135deg, #FF6B35, #FF8C5A);
  border-radius: 16rpx;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.repair-banner .repair-icon {
  font-size: 40rpx;
}
.repair-banner .repair-text {
  font-size: 30rpx;
  font-weight: bold;
  flex: 1;
}
.repair-banner .repair-arrow {
  font-size: 28rpx;
  opacity: 0.8;
}
.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8rpx;
}
.order-no {
  font-size: 24rpx;
  color: #999;
}
.order-time {
  font-size: 22rpx;
  color: #bbb;
}
.order-desc {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  display: block;
}
.order-tags {
  margin-bottom: 10rpx;
}
.eval-section {
  background: #FFF8E1;
  border-radius: 10rpx;
  padding: 12rpx 16rpx;
}
.eval-stars {
  font-size: 28rpx;
  letter-spacing: 4rpx;
}
.eval-comment {
  font-size: 24rpx;
  color: #666;
  margin-top: 6rpx;
  display: block;
}
.empty, .loading {
  text-align: center;
  color: #999;
  padding: 60rpx 0;
  font-size: 26rpx;
}
</style>