<template>
  <view class="fee-page">
    <!-- 缴费概览 -->
    <view class="card summary-card">
      <text class="sum-title">缴费概览</text>
      <view class="sum-row">
        <view class="sum-item">
          <text class="sum-num blue">{{ unpaidCount }}</text>
          <text class="sum-label">待缴费</text>
        </view>
        <view class="sum-item">
          <text class="sum-num green">{{ paidCount }}</text>
          <text class="sum-label">已缴清</text>
        </view>
        <view class="sum-item">
          <text class="sum-num red">{{ overdueCount }}</text>
          <text class="sum-label">已逾期</text>
        </view>
      </view>
    </view>

    <!-- 账单列表 -->
    <view class="card" v-for="bill in bills" :key="bill.id">
      <view class="bill-header">
        <text class="bill-no">{{ bill.billNo }}</text>
        <text class="bill-status tag" :class="statusClass(bill.status)">
          {{ statusLabel(bill.status) }}
        </text>
      </view>
      <view class="bill-body">
        <view class="bill-row">
          <text class="bill-key">费用项目</text>
          <text class="bill-val">{{ bill.feeItemId }}</text>
        </view>
        <view class="bill-row">
          <text class="bill-key">金额</text>
          <text class="bill-val price">¥{{ bill.amount }}</text>
        </view>
        <view class="bill-row">
          <text class="bill-key">已付</text>
          <text class="bill-val">¥{{ bill.paidAmount }}</text>
        </view>
        <view class="bill-row">
          <text class="bill-key">周期</text>
          <text class="bill-val">{{ bill.billPeriodStart }} ~ {{ bill.billPeriodEnd }}</text>
        </view>
        <view class="bill-row">
          <text class="bill-key">截止日</text>
          <text class="bill-val" :class="{ red: bill.status === 3 }">{{ bill.dueDate }}</text>
        </view>
      </view>
      <button class="pay-btn" v-if="bill.status === 0 || bill.status === 3"
              @click="handlePay(bill)">立即缴费</button>
    </view>
    <view v-if="bills.length === 0" class="empty">暂无账单</view>
  </view>
</template>

<script>
import { getBills, payBill } from '../../api/fee'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      bills: [],
      unpaidCount: 0,
      paidCount: 0,
      overdueCount: 0
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadBills()
  },
  methods: {
    async loadBills() {
      try {
        const res = await getBills(this.userInfo.ownerId, 1)
        this.bills = res.list || []
        this.unpaidCount = this.bills.filter(b => b.status === 0).length
        this.paidCount = this.bills.filter(b => b.status === 2).length
        this.overdueCount = this.bills.filter(b => b.status === 3).length
      } catch (e) { /* ignore */ }
    },
    async handlePay(bill) {
      uni.showModal({
        title: '确认缴费',
        content: `确认缴纳 ¥${bill.amount - bill.paidAmount} 吗？`,
        success: async (r) => {
          if (r.confirm) {
            try {
              const remain = bill.amount - bill.paidAmount
              await payBill(bill.id, remain, 1)
              uni.showToast({ title: '缴费成功', icon: 'success' })
              this.loadBills()
            } catch (e) { /* ignore */ }
          }
        }
      })
    },
    statusClass(s) {
      return ['', 'tag-orange', 'tag-green', 'tag-red'][s] || 'tag-gray'
    },
    statusLabel(s) {
      return ['未缴费', '部分缴纳', '已缴清', '已逾期'][s] || '未知'
    }
  }
}
</script>

<style scoped>
.fee-page { padding-bottom: 40rpx; }
.summary-card { text-align: center; }
.sum-title { font-size: 28rpx; color: #666; display: block; margin-bottom: 20rpx; }
.sum-row { display: flex; justify-content: space-around; }
.sum-item { text-align: center; }
.sum-num { font-size: 40rpx; font-weight: bold; display: block; }
.sum-num.blue { color: #2B85E4; }
.sum-num.green { color: #19BE6B; }
.sum-num.red { color: #ED3F14; }
.sum-label { font-size: 24rpx; color: #999; }
.bill-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16rpx;
}
.bill-no { font-size: 26rpx; font-weight: bold; }
.bill-row {
  display: flex; justify-content: space-between; padding: 10rpx 0;
  font-size: 26rpx;
}
.bill-key { color: #999; }
.bill-val { color: #333; }
.price { color: #ED3F14; font-weight: bold; font-size: 30rpx; }
.pay-btn {
  margin-top: 20rpx; width: 100%; background: linear-gradient(135deg, #2B85E4, #5CADFF);
  color: #fff; border: none; border-radius: 40rpx; padding: 20rpx;
}
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
