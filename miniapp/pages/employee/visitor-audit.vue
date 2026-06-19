<template>
  <view class="visitor-audit">
    <view class="card" v-for="v in visitors" :key="v.id">
      <view class="visitor-header">
        <text class="visitor-name">{{ v.visitorName }}</text>
        <text class="visitor-status tag tag-orange" v-if="v.status === 0">待审核</text>
      </view>
      <view class="info-row"><text class="key">电话</text><text class="val">{{ v.visitorPhone }}</text></view>
      <view class="info-row" v-if="v.vehiclePlate"><text class="key">车辆</text><text class="val">{{ v.vehiclePlate }}</text></view>
      <view class="info-row"><text class="key">事由</text><text class="val">{{ v.visitPurpose }}</text></view>
      <view class="info-row"><text class="key">人数</text><text class="val">{{ v.visitCount }} 人</text></view>

      <view class="audit-actions" v-if="v.status === 0">
        <button class="approve-btn" @click="approve(v.id)">通过</button>
        <view class="reject-area">
          <input class="reject-input" v-model="rejectRemark" placeholder="拒绝原因" />
          <button class="reject-btn" @click="reject(v.id)">拒绝</button>
        </view>
      </view>
      <view v-else class="audit-result">
        <text class="result-text">已{{ v.status === 1 ? '通过' : v.status === 4 ? '拒绝' : '处理' }}</text>
      </view>
    </view>
    <view v-if="visitors.length === 0" class="empty">暂无需审核的访客</view>
  </view>
</template>

<script>
import { get, put } from '../../utils/request'

export default {
  data() {
    return { visitors: [], rejectRemark: '' }
  },
  onShow() { this.loadVisitors() },
  methods: {
    async loadVisitors() {
      try {
        const res = await get('/visitor/visitors', { status: 0, page: 1, size: 20 })
        this.visitors = res.list || []
      } catch (e) { /* ignore */ }
    },
    async approve(id) {
      try { await put(`/visitor/${id}/approve`); uni.showToast({ title: '已通过', icon: 'success' }); this.loadVisitors() } catch(e) {}
    },
    async reject(id) {
      try { await put(`/visitor/${id}/reject`, { remark: this.rejectRemark }); uni.showToast({ title: '已拒绝', icon: 'success' }); this.rejectRemark = ''; this.loadVisitors() } catch(e) {}
    }
  }
}
</script>

<style scoped>
.visitor-audit { padding: 20rpx 0 40rpx; }
.visitor-header { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.visitor-name { font-size: 28rpx; font-weight: bold; }
.info-row { display: flex; padding: 6rpx 0; font-size: 26rpx; }
.info-row .key { color: #999; width: 100rpx; }
.info-row .val { color: #333; }
.audit-actions { margin-top: 16rpx; }
.approve-btn { width: 100%; background: #19BE6B; color: #fff; border: none; border-radius: 30rpx; padding: 16rpx; font-size: 26rpx; margin-bottom: 10rpx; }
.reject-area { display: flex; gap: 12rpx; }
.reject-input { flex: 1; border: 2rpx solid #eee; border-radius: 12rpx; padding: 12rpx 16rpx; font-size: 24rpx; }
.reject-btn { background: #ED3F14; color: #fff; border: none; border-radius: 30rpx; padding: 12rpx 30rpx; font-size: 24rpx; }
.audit-result { margin-top: 12rpx; }
.result-text { font-size: 24rpx; color: #999; }
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
