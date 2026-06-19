<template>
  <view class="repair-page">
    <!-- 提交报修 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">提交报修</text>
      </view>
      <view class="form-item">
        <text class="label">问题描述</text>
        <textarea class="textarea" v-model="form.description" placeholder="请详细描述您的问题..." />
      </view>
      <view class="form-item">
        <text class="label">联系电话</text>
        <input class="input" v-model="form.contactPhone" placeholder="请输入联系电话" />
      </view>
      <view class="form-item">
        <text class="label">维修类型</text>
        <picker :range="repairTypes" @change="e => form.repairType = repairTypes[e.detail.value]">
          <text class="picker-text">{{ form.repairType || '请选择维修类型' }}</text>
        </picker>
      </view>
      <view class="form-item">
        <text class="label">紧急程度</text>
        <view class="urgency-row">
          <text v-for="lv in urgencyLevels" :key="lv.value"
                class="urgency-tag" :class="{ active: form.urgencyLevel === lv.value }"
                @click="form.urgencyLevel = lv.value">{{ lv.label }}</text>
        </view>
      </view>
      <button class="primary-btn submit-btn" @click="submitRepair">提交报修</button>
    </view>

    <!-- 报修记录 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">报修记录</text>
      </view>
      <view class="order-item" v-for="order in orders" :key="order.id"
            @click="viewDetail(order.id)">
        <view class="order-header">
          <text class="order-no">{{ order.orderNo }}</text>
          <text class="order-status tag" :class="orderStatusClass(order.status)">
            {{ orderStatusLabel(order.status) }}
          </text>
        </view>
        <text class="order-desc">{{ order.description }}</text>
        <text class="order-time">{{ formatTime(order.createdAt) }}</text>
      </view>
      <view v-if="orders.length === 0" class="empty">暂无报修记录</view>
    </view>
  </view>
</template>

<script>
import { getMyOrders, submitOrder } from '../../api/repair'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      orders: [],
      form: {
        description: '',
        contactPhone: '',
        repairType: '',
        urgencyLevel: 1
      },
      repairTypes: ['水电', '管道', '电气', '家电', '土建', '其他'],
      urgencyLevels: [{ label: '普通', value: 1 }, { label: '紧急', value: 2 }, { label: '非常紧急', value: 3 }]
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getMyOrders(this.userInfo.ownerId, 1)
        this.orders = res.list || []
      } catch (e) { /* ignore */ }
    },
    async submitRepair() {
      if (!this.form.description) {
        uni.showToast({ title: '请描述问题', icon: 'none' })
        return
      }
      try {
        await submitOrder({
          ...this.form,
          ownerId: this.userInfo.ownerId,
          contactName: this.userInfo.realName
        })
        uni.showToast({ title: '提交成功', icon: 'success' })
        this.form = { description: '', contactPhone: '', repairType: '', urgencyLevel: 1 }
        this.loadOrders()
      } catch (e) { /* ignore */ }
    },
    viewDetail(id) {
      uni.navigateTo({ url: `/pages/owner/repair-detail?id=${id}` })
    },
    orderStatusClass(s) {
      const map = {1:'tag-orange',2:'tag-blue',3:'tag-blue',4:'tag-orange',5:'tag-green',6:'tag-blue',7:'tag-gray',8:'tag-red'}
      return map[s] || 'tag-gray'
    },
    orderStatusLabel(s) {
      const map = {1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消'}
      return map[s] || '未知'
    },
    formatTime(t) { return t ? t.substring(0, 16) : '' }
  }
}
</script>

<style scoped>
.repair-page { padding-bottom: 40rpx; }
.section-header { margin-bottom: 20rpx; }
.section-title { font-size: 30rpx; font-weight: bold; }
.form-item { margin-bottom: 24rpx; }
.label { font-size: 26rpx; color: #666; margin-bottom: 8rpx; display: block; }
.textarea, .input {
  width: 100%; border: 2rpx solid #eee; border-radius: 12rpx;
  padding: 16rpx 20rpx; font-size: 28rpx; background: #FAFAFA;
}
.textarea { min-height: 140rpx; }
.picker-text { color: #333; padding: 16rpx 0; font-size: 28rpx; display: block; }
.urgency-row { display: flex; gap: 20rpx; }
.urgency-tag {
  padding: 12rpx 28rpx; border-radius: 30rpx; background: #f5f5f5;
  font-size: 24rpx; color: #666;
}
.urgency-tag.active { background: #2B85E4; color: #fff; }
.submit-btn { width: 100%; margin-top: 16rpx; text-align: center; }
.order-item {
  padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5;
}
.order-header { display: flex; justify-content: space-between; align-items: center; }
.order-no { font-size: 26rpx; font-weight: bold; }
.order-desc { font-size: 26rpx; color: #666; margin-top: 8rpx; display: block;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.order-time { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.empty { text-align: center; color: #999; padding: 40rpx 0; }
</style>
