<template>
  <view class="convenience-page">
    <!-- 便民服务列表 -->
    <view class="card">
      <view class="section-title">便民服务</view>
      <view class="service-item" v-for="svc in services" :key="svc.id"
            @click="showServiceInfo(svc)">
        <text class="service-icon">{{ getIcon(svc.serviceType) }}</text>
        <view class="service-info">
          <text class="service-name">{{ svc.name }}</text>
          <text class="service-desc">{{ svc.description }}</text>
        </view>
        <text class="service-arrow">&gt;</text>
      </view>
      <view v-if="services.length === 0" class="empty">暂无便民服务</view>
    </view>

    <!-- 工具借用 -->
    <view class="card">
      <view class="section-title">工具借用</view>
      <view class="tool-item" v-for="tool in tools" :key="tool.id">
        <view class="tool-info">
          <text class="tool-name">{{ tool.name }}</text>
          <text class="tool-stock">可用: {{ tool.availableQuantity }}/{{ tool.totalQuantity }}{{ tool.unit }}</text>
        </view>
        <button class="borrow-btn" v-if="tool.availableQuantity > 0"
                @click="showBorrowDialog(tool)">借用</button>
        <text class="no-stock" v-else>暂无库存</text>
      </view>
      <view v-if="tools.length === 0" class="empty">暂无可用工具</view>
    </view>

    <!-- 借用记录 -->
    <view class="card">
      <view class="section-title">我的借用记录</view>
      <view class="usage-item" v-for="u in usageRecords" :key="u.id">
        <text class="usage-name">{{ u.borrowerName }}</text>
        <text class="usage-status tag" :class="usageStatusClass(u.status)">
          {{ usageStatusLabel(u.status) }}
        </text>
        <text class="usage-time">{{ u.borrowTime }}</text>
      </view>
      <view v-if="usageRecords.length === 0" class="empty">暂无借用记录</view>
    </view>

    <!-- 借用弹窗 -->
    <uni-popup ref="borrowPopup" type="center">
      <view class="borrow-modal">
        <text class="borrow-title">借用 {{ borrowTool&&borrowTool.name }}</text>
        <input class="input" v-model="borrowForm.quantity" type="number" placeholder="借用数量" />
        <input class="input" v-model="borrowForm.purpose" placeholder="借用用途" />
        <view class="borrow-actions">
          <button class="cancel-btn" @click="$refs.borrowPopup.close()">取消</button>
          <button class="confirm-btn" @click="confirmBorrow">确认</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { getServices, getTools, borrowTool, getUsageRecords } from '../../api/convenience'
import { getUserInfo } from '../../utils/auth'
import uniPopup from
'@dcloudio/uni-ui/lib/uni-popup/uni-popup'

export default {
  components:{uniPopup},
  data() {
    return {
      userInfo: {},
      services: [],
      tools: [],
      usageRecords: [],
      borrowTool: null,
      borrowForm: { quantity: 1, purpose: '' }
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        this.services = await getServices() || []
        this.tools = await getTools() || []
        const res = await getUsageRecords(this.userInfo.ownerId, 1)
        this.usageRecords = res.list || []
      } catch (e) { /* ignore */ }
    },
    showServiceInfo(svc) {
      uni.showModal({ title: svc.name, content: `${svc.description}\n电话: ${svc.contactPhone || '无'}\n时间: ${svc.serviceTime || '无'}`, showCancel: false })
    },
    showBorrowDialog(tool) {
      this.borrowTool = tool
      this.borrowForm = { quantity: 1, purpose: '' }
      this.$refs.borrowPopup.open()
    },
    async confirmBorrow() {
      try {
        await borrowTool({
          toolId: this.borrowTool.id,
          ownerId: this.userInfo.ownerId,
          borrowerName: this.userInfo.realName,
          borrowerPhone: this.userInfo.phone || '',
          quantity: Number(this.borrowForm.quantity),
          purpose: this.borrowForm.purpose
        })
        uni.showToast({ title: '借用成功', icon: 'success' })
        this.$refs.borrowPopup.close()
        this.loadData()
      } catch (e) { /* ignore */ }
    },
    getIcon(type) {
      return { 'info':'📋', 'risk':'⚠️', 'tool':'🔧', 'other':'📌' }[type] || '📌'
    },
    usageStatusClass(s) { return ['tag-orange', 'tag-green', 'tag-red', 'tag-red'][s] || 'tag-gray' },
    usageStatusLabel(s) { return ['借用中', '已归还', '逾期未还', '损坏'][s] || '未知' }
  }
}
</script>

<style scoped>
.convenience-page { padding-bottom: 40rpx; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 20rpx; }
.service-item { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.service-icon { font-size: 40rpx; margin-right: 20rpx; }
.service-info { flex: 1; }
.service-name { font-size: 28rpx; font-weight: bold; display: block; }
.service-desc { font-size: 24rpx; color: #999; margin-top: 4rpx; }
.service-arrow { color: #ccc; font-size: 28rpx; }
.tool-item { display: flex; align-items: center; justify-content: space-between; padding: 16rpx 0; border-bottom: 1rpx solid #f5f5f5; }
.tool-name { font-size: 28rpx; display: block; }
.tool-stock { font-size: 22rpx; color: #999; }
.borrow-btn { background: #2B85E4; color: #fff; border: none; border-radius: 30rpx; padding: 10rpx 30rpx; font-size: 24rpx; }
.no-stock { color: #ccc; font-size: 24rpx; }
.usage-item { display: flex; align-items: center; justify-content: space-between; padding: 12rpx 0; }
.usage-name { font-size: 26rpx; }
.usage-time { font-size: 22rpx; color: #bbb; }
.empty { text-align: center; color: #999; padding: 40rpx 0; font-size: 24rpx; }
.borrow-modal { width: 560rpx; background: #fff; border-radius: 20rpx; padding: 40rpx; }
.borrow-title { font-size: 30rpx; font-weight: bold; display: block; margin-bottom: 24rpx; text-align: center; }
.borrow-modal .input { width: 100%; border: 2rpx solid #eee; border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 28rpx; margin-bottom: 16rpx; }
.borrow-actions { display: flex; justify-content: space-between; margin-top: 20rpx; }
.cancel-btn { flex: 1; margin-right: 16rpx; background: #f5f5f5; border: none; border-radius: 40rpx; padding: 16rpx; }
.confirm-btn { flex: 1; background: #2B85E4; color: #fff; border: none; border-radius: 40rpx; padding: 16rpx; }
</style>
