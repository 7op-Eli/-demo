<template>
  <view class="repair-list">
    <!-- 状态筛选 -->
    <view class="filter-bar card">
      <view class="filter-item" v-for="f in filters" :key="f.key"
            :class="{ active: currentFilter === f.key }"
            @click="switchFilter(f.key)">
        <text>{{ f.label }}</text>
      </view>
    </view>

    <!-- 工单列表 -->
    <view class="card" v-for="o in orders" :key="o.id"
          @click="navigate(`/pages/employee/repair-handle?id=${o.id}`)">
      <view class="order-header">
        <text class="order-no">{{ o.orderNo }}</text>
        <text class="order-status tag" :class="statusClass(o.status)">{{ statusLabel(o.status) }}</text>
      </view>
      <text class="order-desc">{{ o.description }}</text>
      <view class="order-meta">
        <text>联系人: {{ o.contactName }}</text>
        <text>类型: {{ o.repairType }}</text>
      </view>
      <text class="order-time">{{ o.createdAt }}</text>
    </view>
    <view v-if="orders.length === 0" class="empty">暂无工单</view>
  </view>
</template>

<script>
import { getOrdersByStatus } from '../../api/repair-employee'

export default {
  data() {
    return {
      currentFilter: 'all',
      filters: [
        { key: 'all', label: '全部' },
        { key: '1', label: '待接单' },
        { key: '4', label: '维修中' },
        { key: '5', label: '已完成' },
        { key: '7', label: '已结束' }
      ],
      orders: []
    }
  },
  onShow() { this.loadOrders() },
  methods: {
    async loadOrders() {
      try {
        const status = this.currentFilter === 'all' ? null : Number(this.currentFilter)
        const res = await getOrdersByStatus(status, 1)
        this.orders = res.list || []
      } catch (e) { /* ignore */ }
    },
    switchFilter(key) { this.currentFilter = key; this.loadOrders() },
    navigate(url) { uni.navigateTo({ url }) },
    statusClass(s) { const map={1:'tag-orange',2:'tag-blue',3:'tag-blue',4:'tag-orange',5:'tag-green',6:'tag-blue',7:'tag-gray',8:'tag-red'}; return map[s]||'tag-gray' },
    statusLabel(s) { const map={1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消'}; return map[s]||'未知' }
  }
}
</script>

<style scoped>
.repair-list { padding-bottom: 40rpx; }
.filter-bar { display: flex; overflow-x: auto; padding: 20rpx 0; }
.filter-item { padding: 10rpx 24rpx; margin-right: 12rpx; border-radius: 30rpx; font-size: 24rpx; background: #f5f5f5; color: #666; white-space: nowrap; }
.filter-item.active { background: #2B85E4; color: #fff; }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10rpx; }
.order-no { font-size: 26rpx; font-weight: bold; }
.order-desc { font-size: 26rpx; color: #333; display: block; margin-bottom: 8rpx; }
.order-meta { display: flex; justify-content: space-between; font-size: 22rpx; color: #999; margin-bottom: 4rpx; }
.order-time { font-size: 22rpx; color: #bbb; }
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
