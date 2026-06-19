<template>
  <view class="detail-page">
    <view class="card" v-if="order">
      <view class="detail-header">
        <text class="order-no">{{ order.orderNo }}</text>
        <text class="order-status tag" :class="statusClass(order.status)">
          {{ statusLabel(order.status) }}
        </text>
      </view>
      <view class="info-row"><text class="key">问题描述</text><text class="val">{{ order.description }}</text></view>
      <view class="info-row"><text class="key">联系人</text><text class="val">{{ order.contactName }}</text></view>
      <view class="info-row"><text class="key">联系电话</text><text class="val">{{ order.contactPhone }}</text></view>
      <view class="info-row"><text class="key">维修类型</text><text class="val">{{ order.repairType }}</text></view>
      <view class="info-row"><text class="key">提交时间</text><text class="val">{{ order.createdAt }}</text></view>
    </view>

    <!-- 进度日志 -->
    <view class="card">
      <view class="section-title">处理进度</view>
      <view class="log-item" v-for="log in logs" :key="log.id">
        <view class="log-dot"></view>
        <view class="log-body">
          <text class="log-action">{{ actionLabel(log.action) }}</text>
          <text class="log-content" v-if="log.content">{{ log.content }}</text>
          <text class="log-time">{{ log.createdAt }}</text>
        </view>
      </view>
      <view v-if="logs.length === 0" class="empty">暂无进度信息</view>
    </view>

    <!-- 评价 -->
    <view class="card" v-if="order && order.status >= 5 && !evaluation">
      <view class="section-title">维修评价</view>
      <view class="star-row">
        <text v-for="i in 5" :key="i" class="star" @click="evalForm.rating = i"
              :class="{ active: i <= evalForm.rating }">★</text>
      </view>
      <textarea class="eval-input" v-model="evalForm.comment" placeholder="请对本次维修服务进行评价..." />
      <button class="primary-btn" @click="submitEval">提交评价</button>
    </view>

    <view class="card" v-if="evaluation">
      <view class="section-title">我的评价</view>
      <view class="star-display">
        <text v-for="i in 5" :key="i" class="star" :class="{ active: i <= evaluation.rating }">★</text>
      </view>
      <text class="eval-comment" v-if="evaluation.comment">{{ evaluation.comment }}</text>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, getOrderLogs, getEvaluation, submitEvaluation } from '../../api/repair'

export default {
  data() {
    return {
      orderId: '',
      order: null,
      logs: [],
      evaluation: null,
      evalForm: { rating: 5, comment: '' }
    }
  },
  onLoad(options) { this.orderId = options.id; this.loadData() },
  methods: {
    async loadData() {
      try {
        this.order = await getOrderDetail(this.orderId)
        this.logs = await getOrderLogs(this.orderId) || []
        const ev = await getEvaluation(this.orderId)
        if (ev) this.evaluation = ev
      } catch (e) { /* ignore */ }
    },
    async submitEval() {
      try {
        await submitEvaluation({
          orderId: Number(this.orderId),
          ownerId: this.order.ownerId,
          rating: this.evalForm.rating,
          comment: this.evalForm.comment
        })
        uni.showToast({ title: '评价成功', icon: 'success' })
        this.evaluation = { rating: this.evalForm.rating, comment: this.evalForm.comment }
      } catch (e) { /* ignore */ }
    },
    statusClass(s) { const map={1:'tag-orange',2:'tag-blue',3:'tag-blue',4:'tag-orange',5:'tag-green',6:'tag-blue',7:'tag-gray',8:'tag-red'}; return map[s]||'tag-gray' },
    statusLabel(s) { const map={1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消'}; return map[s]||'未知' },
    actionLabel(a) {
      const map = { 'cs_accepted':'客服已接单', 'dispatched':'已派单', 'accepted':'维修已接单',
        'progress_update':'进度更新', 'completed':'维修完成', 'follow_up':'客服回访' }
      return map[a] || a
    }
  }
}
</script>

<style scoped>
.detail-page { padding: 20rpx 0 40rpx; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20rpx; }
.order-no { font-size: 28rpx; font-weight: bold; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 20rpx; }
.info-row { display: flex; padding: 10rpx 0; font-size: 26rpx; }
.info-row .key { color: #999; width: 140rpx; }
.info-row .val { color: #333; flex: 1; }
.log-item { display: flex; margin-bottom: 24rpx; }
.log-dot { width: 16rpx; height: 16rpx; border-radius: 50%; background: #2B85E4; margin-top: 8rpx; margin-right: 20rpx; }
.log-body { flex: 1; }
.log-action { font-size: 26rpx; font-weight: bold; display: block; }
.log-content { font-size: 24rpx; color: #666; display: block; margin-top: 4rpx; }
.log-time { font-size: 20rpx; color: #bbb; display: block; margin-top: 4rpx; }
.star-row, .star-display { margin-bottom: 16rpx; }
.star { font-size: 50rpx; color: #ddd; margin-right: 10rpx; }
.star.active { color: #FF9900; }
.eval-input { width: 100%; min-height: 120rpx; border: 2rpx solid #eee; border-radius: 12rpx; padding: 16rpx; font-size: 26rpx; margin-bottom: 20rpx; }
.eval-comment { font-size: 26rpx; color: #666; }
.empty { text-align: center; color: #999; padding: 30rpx 0; font-size: 24rpx; }
</style>
