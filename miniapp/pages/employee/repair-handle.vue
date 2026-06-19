<template>
  <view class="repair-handle">
    <view class="card" v-if="order">
      <view class="detail-header">
        <text class="order-title">{{ order.orderNo }}</text>
        <text class="order-status tag" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</text>
      </view>
      <view class="info-row"><text class="key">业主</text><text class="val">{{ order.contactName }}</text></view>
      <view class="info-row"><text class="key">电话</text><text class="val">{{ order.contactPhone }}</text></view>
      <view class="info-row"><text class="key">类型</text><text class="val">{{ order.repairType }}</text></view>
      <view class="info-row"><text class="key">描述</text><text class="val">{{ order.description }}</text></view>

      <!-- 操作按钮（根据角色和状态显示） -->
      <view class="action-btns">
        <!-- 待接单 → 客服接单 -->
        <button class="action-btn" v-if="order.status === 1" @click="csAcceptOrder">客服接单</button>
        <!-- 已接单 → 派单：需要输入维修人员 -->
        <view v-if="order.status === 2">
          <input class="input" v-model="assigneeId" type="number" placeholder="维修人员ID" />
          <button class="action-btn" @click="dispatchOrder">派单</button>
        </view>
        <!-- 已派单 → 维修接单 -->
        <button class="action-btn" v-if="order.status === 3" @click="acceptRepairOrder">维修接单</button>
        <!-- 维修中 → 上报进度 -->
        <view v-if="order.status === 4">
          <textarea class="input textarea" v-model="progressContent" placeholder="填写进度说明" />
          <button class="action-btn green" @click="reportProgressOrder">上报进度</button>
          <button class="action-btn" @click="completeRepairOrder">维修完成</button>
        </view>
        <!-- 已完成 → 回访 -->
        <view v-if="order.status === 5">
          <input class="input" v-model="followUpNote" placeholder="回访记录" />
          <button class="action-btn green" @click="followUpOrder">提交回访</button>
        </view>
      </view>
    </view>

    <!-- 进度日志 -->
    <view class="card">
      <view class="section-title">操作日志</view>
      <view class="log-item" v-for="log in logs" :key="log.id">
        <view class="log-dot"></view>
        <view class="log-body">
          <text class="log-action">{{ log.action }}</text>
          <text class="log-content">{{ log.content }}</text>
          <text class="log-time">{{ log.createdAt }}</text>
        </view>
      </view>
      <view v-if="logs.length === 0" class="empty">暂无日志</view>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, getOrderLogs, csAccept, dispatchOrder, acceptRepair, reportProgress, completeRepair, followUp } from '../../api/repair-employee'

export default {
  data() {
    return {
      orderId: '',
      order: null,
      logs: [],
      assigneeId: '',
      progressContent: '',
      followUpNote: ''
    }
  },
  onLoad(opts) { this.orderId = opts.id; this.loadData() },
  methods: {
    async loadData() {
      try {
        this.order = await getOrderDetail(this.orderId)
        this.logs = await getOrderLogs(this.orderId) || []
      } catch (e) { /* ignore */ }
    },
    async csAcceptOrder() {
      try { await csAccept(this.orderId); uni.showToast({ title: '已接单', icon: 'success' }); this.loadData() } catch(e) {}
    },
    async dispatchOrder() {
      try { await dispatchOrder(this.orderId, this.assigneeId); uni.showToast({ title: '已派单', icon: 'success' }); this.loadData() } catch(e) {}
    },
    async acceptRepairOrder() {
      try { await acceptRepair(this.orderId); uni.showToast({ title: '已接单', icon: 'success' }); this.loadData() } catch(e) {}
    },
    async reportProgressOrder() {
      try { await reportProgress(this.orderId, this.progressContent, ''); uni.showToast({ title: '已上报', icon: 'success' }); this.progressContent = ''; this.loadData() } catch(e) {}
    },
    async completeRepairOrder() {
      try { await completeRepair(this.orderId); uni.showToast({ title: '维修完成', icon: 'success' }); this.loadData() } catch(e) {}
    },
    async followUpOrder() {
      try { await followUp(this.orderId, this.followUpNote); uni.showToast({ title: '回访完成', icon: 'success' }); this.loadData() } catch(e) {}
    },
    statusClass(s) { const map={1:'tag-orange',2:'tag-blue',3:'tag-blue',4:'tag-orange',5:'tag-green',6:'tag-blue',7:'tag-gray',8:'tag-red'}; return map[s]||'tag-gray' },
    statusLabel(s) { const map={1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消'}; return map[s]||'未知' }
  }
}
</script>

<style scoped>
.repair-handle { padding-bottom: 40rpx; }
.detail-header { display: flex; justify-content: space-between; margin-bottom: 16rpx; }
.order-title { font-size: 30rpx; font-weight: bold; }
.info-row { display: flex; padding: 8rpx 0; font-size: 26rpx; }
.info-row .key { color: #999; width: 100rpx; }
.info-row .val { color: #333; flex: 1; }
.action-btns { margin-top: 24rpx; }
.action-btn { width: 100%; margin-top: 12rpx; background: #2B85E4; color: #fff; border: none; border-radius: 40rpx; padding: 18rpx; font-size: 28rpx; }
.action-btn.green { background: #19BE6B; }
.input { width: 100%; border: 2rpx solid #eee; border-radius: 12rpx; padding: 14rpx 18rpx; font-size: 26rpx; margin-bottom: 10rpx; background: #FAFAFA; }
.textarea { min-height: 100rpx; }
.section-title { font-size: 28rpx; font-weight: bold; margin-bottom: 16rpx; }
.log-item { display: flex; margin-bottom: 20rpx; }
.log-dot { width: 14rpx; height: 14rpx; border-radius: 50%; background: #2B85E4; margin-top: 6rpx; margin-right: 16rpx; }
.log-body { flex: 1; }
.log-action { font-size: 26rpx; font-weight: bold; display: block; }
.log-content { font-size: 24rpx; color: #666; display: block; }
.log-time { font-size: 20rpx; color: #bbb; display: block; }
.empty { text-align: center; color: #999; padding: 30rpx; }
</style>
