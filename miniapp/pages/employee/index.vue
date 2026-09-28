<template>
  <view class="emp-page">
    <!-- 顶部 -->
    <view class="header-card">
      <text class="greeting">你好，{{ userInfo.realName || '员工' }}</text>
      <text class="sub-text">物业员工工作台</text>
    </view>

    <!-- 功能导航 -->
    <view class="func-grid card">
      <view class="func-item" @click="navigate('/pages/employee/repair-list')">
        <view class="func-icon func-icon-blue"><text>📋</text></view>
        <text class="func-label">报修工单</text>
      </view>
      <view class="func-item" @click="navigate('/pages/employee/visitor-audit')">
        <view class="func-icon func-icon-green"><text>👤</text></view>
        <text class="func-label">访客审核</text>
      </view>
      <view class="func-item" @click="navigate('/pages/employee/schedule')">
        <view class="func-icon func-icon-orange"><text>📅</text></view>
        <text class="func-label">工作日程</text>
      </view>
      <view class="func-item" @click="navigate('/pages/employee/tool-manage')">
        <view class="func-icon func-icon-purple"><text>🔨</text></view>
        <text class="func-label">工具管理</text>
      </view>
    </view>

    <!-- 工单筛选 tabs -->
    <view class="tabs">
      <view class="tab" :class="{ active: tab === 'pending' }" @click="tab = 'pending'">
        <text>待接工单</text>
      </view>
      <view class="tab" :class="{ active: tab === 'mine' }" @click="tab = 'mine'">
        <text>我的工单</text>
      </view>
      <view class="tab" :class="{ active: tab === 'repair' }" @click="tab = 'repair'">
        <text>我要报修</text>
      </view>
    </view>

    <!-- 待接工单列表 -->
    <view v-if="tab === 'pending'">
      <view class="card" v-for="o in pendingOrders" :key="o.id">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="tag tag-orange">{{ o.repairType }}</text>
        </view>
        <text class="order-desc">{{ o.description }}</text>
        <view class="order-actions">
          <text class="order-time">{{ formatTime(o.createdAt) }}</text>
          <button class="accept-btn" @click="acceptOrder(o.id)" size="mini">接单</button>
        </view>
      </view>
      <view v-if="pendingOrders.length === 0" class="empty">暂无待接工单</view>
    </view>

    <!-- 我的工单 -->
    <view v-if="tab === 'mine'">
      <view class="card" v-for="o in myOrders" :key="o.id"
            @click="selectedOrder = o; showDetail = true">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="tag" :class="statusClass(o.status)">{{ statusLabel(o.status) }}</text>
        </view>
        <text class="order-desc">{{ o.description }}</text>
        <view class="owner-info">
          <text class="info-text">📞 {{ o.contactPhone || '-' }}</text>
          <text class="info-text">🏠 户号: {{ o.roomId || '-' }}</text>
        </view>
      </view>
      <view v-if="myOrders.length === 0" class="empty">暂无我的工单</view>
    </view>

    <!-- 员工报修 -->
    <view v-if="tab === 'repair'">
      <view class="card">
        <view class="section-title">提交报修</view>
        <view class="form-item">
          <text class="label">问题描述 *</text>
          <textarea class="textarea" v-model="repairForm.description" placeholder="请描述问题..." />
        </view>
        <view class="form-item">
          <text class="label">维修类型</text>
<picker :range="repairTypes" @change="handleTypeChange">
                      <text class="picker-text">{{ repairForm.repairType || '请选择' }}</text>
          </picker>
        </view>
        <button class="primary-btn" @click="submitRepair" :loading="subRepairing">提交报修</button>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- 工单详情弹窗 -->
    <view class="detail-modal" v-if="showDetail && selectedOrder" @click="showDetail = false">
      <view class="detail-card" @click.stop>
        <text class="detail-title">工单 #{{ selectedOrder.orderNo }}</text>

        <view class="detail-section">
          <text class="detail-label">业主信息</text>
          <text class="detail-value">📞 {{ selectedOrder.contactPhone || '-' }}</text>
          <text class="detail-value">🏠 户号: {{ selectedOrder.roomId || '-' }}</text>
          <text class="detail-value">👤 {{ selectedOrder.contactName || '-' }}</text>
        </view>

        <view class="detail-section">
          <text class="detail-label">工单信息</text>
          <text class="detail-value">类型: {{ selectedOrder.repairType }}</text>
          <text class="detail-value">描述: {{ selectedOrder.description }}</text>
          <text class="detail-value">状态: {{ statusLabel(selectedOrder.status) }}</text>
        </view>

        <!-- 进度上报（维修中工单） -->
        <view v-if="selectedOrder.status === 4" class="detail-section">
          <text class="detail-label">上报进度</text>
          <textarea class="textarea" v-model="progressText" placeholder="输入进度说明..." />
          <view class="image-grid">
            <view class="image-item" v-for="(url, idx) in progressImages" :key="idx">
              <image :src="url" mode="aspectFill" class="upload-img"></image>
            </view>
            <view class="image-add" @click="chooseProgressImage" v-if="progressImages.length < 3">
              <text class="add-icon">+</text>
            </view>
          </view>
          <button class="primary-btn" @click="submitProgress" :loading="subProgressing" style="margin-top:16rpx">
            上报进度
          </button>
        </view>

        <!-- 完成工单 -->
        <view v-if="selectedOrder.status === 4" class="detail-section">
          <text class="detail-label">完成工单</text>
          <view class="image-grid">
            <view class="image-item" v-for="(url, idx) in completeImages" :key="idx">
              <image :src="url" mode="aspectFill" class="upload-img"></image>
            </view>
            <view class="image-add" @click="chooseCompleteImage" v-if="completeImages.length < 6">
              <text class="add-icon">+</text>
            </view>
          </view>
          <button class="primary-btn success-btn" @click="completeOrder" :loading="subCompleting" style="margin-top:16rpx">
            完成维修
          </button>
        </view>

        <button class="close-btn" @click="showDetail = false">关闭</button>
      </view>
    </view>
  </view>
</template>

<script>
import { BASE_URL, get, post, put } from '../../utils/request'
import { getUserInfo, logout } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      tab: 'pending',
      pendingOrders: [],
      myOrders: [],
      showDetail: false,
      selectedOrder: null,
      progressText: '',
      progressImages: [],
      completeImages: [],
      subProgressing: false,
      subCompleting: false,
      subRepairing: false,
      repairForm: { description: '', repairType: '' },
      repairTypes: ['水电', '管道', '电气', '家电', '土建', '其他']
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadPendingOrders()
    this.loadMyOrders()
  },
  methods: {
    async loadPendingOrders() {
      try {
        const res = await get('/repair/orders', { status: 1, page: 1, size: 50 })
        this.pendingOrders = res.list || []
      } catch (e) { console.error('loadPendingOrders:', e) }
    },
    async loadMyOrders() {
      try {
        const res = await get('/repair/assigned-orders', { employeeId: this.userInfo.employeeId, page: 1, size: 50 })
        this.myOrders = res.list || []
      } catch (e) { console.error('loadMyOrders:', e) }
    },
    async acceptOrder(orderId) {
      try {
        await put(`/repair/orders/${orderId}/cs-accept`)
        uni.showToast({ title: '接单成功', icon: 'success' })
        this.loadPendingOrders()
        this.loadMyOrders()
      } catch (e) { uni.showToast({ title: '接单失败', icon: 'none' }); console.error(e) }
    },
    // 上传进度图片
    chooseProgressImage() {
      uni.chooseImage({
        count: 3 - this.progressImages.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => this.uploadFiles(res.tempFilePaths, 'progress')
      })
    },
    chooseCompleteImage() {
      uni.chooseImage({
        count: 6 - this.completeImages.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => this.uploadFiles(res.tempFilePaths, 'complete')
      })
    },
    async uploadFiles(paths, type) {
      for (const path of paths) {
        try {
          const url = await this.uploadFile(path)
          if (type === 'progress') this.progressImages.push(url)
          else this.completeImages.push(url)
        } catch (e) { console.error('uploadFiles error:', e) }
      }
    },
    uploadFile(path) {
      return new Promise((resolve, reject) => {
        uni.uploadFile({
          url: BASE_URL + '/upload/image/single',
          filePath: path, name: 'file',
          header: { Authorization: 'Bearer ' + uni.getStorageSync('token') },
          success: (r) => {
            try {
              const d = JSON.parse(r.data)
              d.code === 200 ? resolve(d.data) : reject(d.msg)
            } catch (e) { reject(new Error('上传响应解析失败')) }
          },
          fail: reject
        })
      })
    },
    async submitProgress() {
      if (!this.progressText) { uni.showToast({ title: '请输入进度说明', icon: 'none' }); return }
      this.subProgressing = true
      try {
        await post(`/repair/orders/${this.selectedOrder.id}/progress`, {
          content: this.progressText,
          imageUrls: this.progressImages.length > 0 ? JSON.stringify(this.progressImages) : null
        })
        uni.showToast({ title: '上报成功', icon: 'success' })
        this.progressText = ''
        this.progressImages = []
      } catch (e) { uni.showToast({ title: '上报失败', icon: 'none' }); console.error(e) }
      finally { this.subProgressing = false }
    },
    async completeOrder() {
      uni.showModal({
        title: '确认完成', content: '确定已完成维修吗？',
        success: async (r) => {
          if (!r.confirm) return
          this.subCompleting = true
          try {
            const qs = this.completeImages.length > 0
              ? '?imageUrls=' + encodeURIComponent(JSON.stringify(this.completeImages)) : ''
            await put('/repair/orders/' + this.selectedOrder.id + '/complete' + qs)
            uni.showToast({ title: '维修完成', icon: 'success' })
            this.showDetail = false; this.completeImages = []
            this.loadMyOrders(); this.loadPendingOrders()
          } catch (e) { uni.showToast({ title: '操作失败', icon: 'none' }); console.error(e) }
          finally { this.subCompleting = false }
        }
      })
    },
    async submitRepair() {
      if (!this.repairForm.description) { uni.showToast({ title: '请描述问题', icon: 'none' }); return }
      this.subRepairing = true
      try {
        await post('/repair/orders', { ...this.repairForm, contactName: this.userInfo.realName, contactPhone: this.userInfo.username })
        uni.showToast({ title: '提交成功', icon: 'success' })
        this.repairForm = { description: '', repairType: '' }
      } catch (e) { uni.showToast({ title: '提交失败', icon: 'none' }); console.error(e) }
      finally { this.subRepairing = false }
    },

    navigate(url) { uni.navigateTo({ url }) },
    handleLogout() {
      uni.showModal({
        title: '退出登录',
        content: '确定退出吗？',
        success: (r) => {
          if (r.confirm) { logout(); uni.reLaunch({ url: '/pages/login' }) }
        }
      })
    },

    statusClass(s) { const m={1:'tag-orange',2:'tag-blue',3:'tag-blue',4:'tag-orange',5:'tag-green',6:'tag-blue',7:'tag-gray',8:'tag-red'}; return m[s]||'tag-gray' },
    statusLabel(s) { const m={1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消'}; return m[s]||'未知' },
    formatTime(t) { return t ? t.substring(0, 16) : '' },
    handleTypeChange(e) {
      this.repairForm.repairType = this.repairTypes[e.detail.value]
    }
  }
}
</script>

<style scoped>
.emp-page {
  min-height: 100vh;
  background: #F5F6FA;
  padding-bottom: 30rpx;
}
.header-card {
  background: linear-gradient(135deg, #19BE6B, #5CDB8E);
  padding: 50rpx 30rpx 30rpx;
  color: #fff;
}
.header-card .greeting {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
}
.header-card .sub-text {
  font-size: 24rpx;
  opacity: 0.8;
}
/* 功能导航 */
.func-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 10rpx 0;
}
.func-item {
  width: 25%;
  text-align: center;
  padding: 16rpx 0;
  box-sizing: border-box;
}
.func-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10rpx;
  font-size: 36rpx;
}
.func-icon-blue { background: #E8F4FD; }
.func-icon-green { background: #E8F8EF; }
.func-icon-orange { background: #FFF3E0; }
.func-icon-purple { background: #F0E6FF; }
.func-label {
  font-size: 22rpx;
  color: #666;
}
/* 退出登录按钮 */
.logout-btn {
  width: 90%;
  margin: 30rpx auto;
  background: #f5f5f5;
  color: #999;
  border: none;
  border-radius: 40rpx;
  padding: 20rpx;
  font-size: 28rpx;
  text-align: center;
}
.tabs {
  display: flex;
  background: #fff;
  margin-bottom: 10rpx;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #666;
  border-bottom: 3rpx solid transparent;
}
.tab.active {
  color: #19BE6B;
  border-bottom-color: #19BE6B;
  font-weight: bold;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}
.order-no {
  font-size: 26rpx;
  font-weight: bold;
}
.order-desc {
  font-size: 26rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}
.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-time {
  font-size: 22rpx;
  color: #bbb;
}
.accept-btn {
  background: #19BE6B;
  color: #fff;
  border: none;
  border-radius: 20rpx;
  padding: 8rpx 28rpx;
  font-size: 24rpx;
}
.owner-info {
  background: #F0F9FF;
  padding: 12rpx 16rpx;
  border-radius: 8rpx;
}
.info-text {
  font-size: 24rpx;
  color: #666;
  margin-right: 20rpx;
}
/* 工单详情弹窗 */
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}
.detail-card {
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  padding: 30rpx;
}
.detail-title {
  font-size: 32rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 20rpx;
}
.detail-section {
  margin-bottom: 24rpx;
}
.detail-label {
  font-size: 26rpx;
  color: #999;
  display: block;
  margin-bottom: 8rpx;
}
.detail-value {
  font-size: 26rpx;
  color: #333;
  display: block;
  line-height: 1.6;
}
.form-item {
  margin-bottom: 20rpx;
}
.label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 8rpx;
  display: block;
}
.textarea {
  width: 100%;
  border: 2rpx solid #eee;
  border-radius: 12rpx;
  padding: 16rpx;
  font-size: 26rpx;
  min-height: 100rpx;
  background: #FAFAFA;
}
.picker-text {
  color: #333;
  padding: 16rpx 0;
  font-size: 28rpx;
  display: block;
}
.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}
.image-item {
  width: 140rpx;
  height: 140rpx;
}
.upload-img {
  width: 100%;
  height: 100%;
  border-radius: 10rpx;
}
.image-add {
  width: 140rpx;
  height: 140rpx;
  border: 2rpx dashed #ddd;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FAFAFA;
}
.image-add .add-icon {
  font-size: 48rpx;
  color: #ccc;
}
.primary-btn {
  width: 100%;
  background: #19BE6B;
  color: #fff;
  border-radius: 40rpx;
  padding: 20rpx;
  text-align: center;
  border: none;
  font-size: 28rpx;
}
.success-btn {
  background: #2B85E4;
}
.close-btn {
  width: 100%;
  background: #f5f5f5;
  color: #999;
  border-radius: 40rpx;
  padding: 20rpx;
  text-align: center;
  border: none;
  font-size: 28rpx;
  margin-top: 16rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
}
.empty {
  text-align: center;
  color: #999;
  padding: 80rpx 0;
  font-size: 26rpx;
}
</style>
