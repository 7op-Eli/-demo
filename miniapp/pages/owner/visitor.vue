<template>
  <view class="visitor-page">
    <!-- 登记访客 -->
    <view class="card">
      <view class="section-title">登记访客</view>
      <view class="form-item">
        <text class="label">访客姓名</text>
        <input class="input" v-model="form.visitorName" placeholder="请输入访客姓名" />
      </view>
      <view class="form-item">
        <text class="label">访客电话</text>
        <input class="input" v-model="form.visitorPhone" placeholder="请输入访客电话" />
      </view>
      <view class="form-item">
        <text class="label">来访事由</text>
        <input class="input" v-model="form.visitPurpose" placeholder="请输入来访事由" />
      </view>
      <view class="form-item">
        <text class="label">车辆牌号（选填）</text>
        <input class="input" v-model="form.vehiclePlate" placeholder="如：京A88888" />
      </view>
      <view class="form-item">
        <text class="label">访客人数</text>
        <picker :range="[1,2,3,4,5,6,7,8,9,10]" @change="e => form.visitCount = Number(e.detail.value) + 1">
          <text class="picker-text">{{ form.visitCount }} 人</text>
        </picker>
      </view>
      <button class="primary-btn submit-btn" @click="registerVisitor">提交登记</button>
    </view>

    <!-- 访客记录 -->
    <view class="card">
      <view class="section-title">访客记录</view>
      <view class="record-item" v-for="v in visitors" :key="v.id">
        <view class="record-header">
          <text class="visitor-name">{{ v.visitorName }}</text>
          <text class="visitor-status tag" :class="visitorStatusClass(v.status)">
            {{ visitorStatusLabel(v.status) }}
          </text>
        </view>
        <text class="visitor-info">电话: {{ v.visitorPhone }}</text>
        <text class="visitor-info" v-if="v.vehiclePlate">车辆: {{ v.vehiclePlate }}</text>
        <text class="visitor-info">来访: {{ v.visitPurpose }}</text>
        <text class="visitor-time">{{ v.createdAt }}</text>
      </view>
      <view v-if="visitors.length === 0" class="empty">暂无访客记录</view>
    </view>
  </view>
</template>

<script>
import { getMyVisitors, registerVisitor } from '../../api/visitor'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      visitors: [],
      form: { visitorName: '', visitorPhone: '', visitPurpose: '', vehiclePlate: '', visitCount: 1 }
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadVisitors()
  },
  methods: {
    async loadVisitors() {
      try {
        const res = await getMyVisitors(1)
        this.visitors = res.list || []
      } catch (e) { /* ignore */ }
    },
    async registerVisitor() {
      if (!this.form.visitorName || !this.form.visitorPhone) {
        uni.showToast({ title: '请填写必填信息', icon: 'none' })
        return
      }
      try {
        await registerVisitor({ ...this.form, ownerId: this.userInfo.ownerId, roomId: this.userInfo.ownerId })
        uni.showToast({ title: '登记成功', icon: 'success' })
        this.form = { visitorName: '', visitorPhone: '', visitPurpose: '', vehiclePlate: '', visitCount: 1 }
        this.loadVisitors()
      } catch (e) { /* ignore */ }
    },
    visitorStatusClass(s) {
      return ['tag-orange', 'tag-green', 'tag-blue', 'tag-gray', 'tag-red'][s] || 'tag-gray'
    },
    visitorStatusLabel(s) {
      return ['待审核', '已通过', '已进入', '已离开', '已拒绝'][s] || '未知'
    }
  }
}
</script>

<style scoped>
.visitor-page { padding-bottom: 40rpx; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 20rpx; }
.form-item { margin-bottom: 24rpx; }
.label { font-size: 26rpx; color: #666; margin-bottom: 8rpx; display: block; }
.input {
  width: 100%; border: 2rpx solid #eee; border-radius: 12rpx;
  padding: 16rpx 20rpx; font-size: 28rpx; background: #FAFAFA;
}
.picker-text { color: #333; padding: 16rpx 0; font-size: 28rpx; display: block; }
.submit-btn { width: 100%; margin-top: 10rpx; text-align: center; }
.record-item { padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.record-header { display: flex; justify-content: space-between; align-items: center; }
.visitor-name { font-size: 28rpx; font-weight: bold; }
.visitor-info { font-size: 24rpx; color: #666; display: block; margin-top: 4rpx; }
.visitor-time { font-size: 22rpx; color: #bbb; display: block; margin-top: 8rpx; }
.empty { text-align: center; color: #999; padding: 40rpx 0; }
</style>
