<template>
  <view class="repair-page">
    <!-- 提交报修 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">提交报修</text>
      </view>
      <view class="form-item">
        <text class="label">问题描述 *</text>
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

      <!-- 图片上传 -->
      <view class="form-item">
        <text class="label">现场照片（选填）</text>
        <view class="image-grid">
          <view class="image-item" v-for="(url, idx) in uploadedImages" :key="idx">
            <image :src="url" mode="aspectFill" class="upload-img"></image>
            <view class="img-delete" @click="removeImage(idx)">✕</view>
          </view>
          <view class="image-add" @click="chooseImage" v-if="uploadedImages.length < 6">
            <text class="add-icon">+</text>
            <text class="add-text">添加照片</text>
          </view>
        </view>
      </view>

      <view class="form-item">
        <text class="label">紧急程度</text>
        <view class="urgency-row">
          <text v-for="lv in urgencyLevels" :key="lv.value"
                class="urgency-tag" :class="{ active: form.urgencyLevel === lv.value }"
                @click="form.urgencyLevel = lv.value">{{ lv.label }}</text>
        </view>
      </view>
      <button class="primary-btn submit-btn" @click="submitRepair" :loading="submitting">提交报修</button>
    </view>

    <!-- 报修记录 -->
    <view class="card">
      <view class="section-header">
        <text class="section-title">我的报修记录</text>
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
import { BASE_URL, get, post } from '../../utils/request'
import { getMyOrders, submitOrder } from '../../api/repair'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      orders: [],
      uploadedImages: [],
      uploading: false,
      submitting: false,
      form: { description: '', contactPhone: '', repairType: '', urgencyLevel: 1 },
      repairTypes: ['水电', '管道', '电气', '家电', '土建', '其他'],
      urgencyLevels: [
        { label: '普通', value: 1 }, { label: '紧急', value: 2 }, { label: '非常紧急', value: 3 }
      ]
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getMyOrders(1)
        this.orders = res.list || []
      } catch (e) {}
    },

    // 选择图片
    chooseImage() {
      uni.chooseImage({
        count: 6 - this.uploadedImages.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.uploadImages(res.tempFilePaths)
        }
      })
    },

    // 上传图片到后端
    async uploadImages(paths) {
      this.uploading = true
      for (const path of paths) {
        try {
          const url = await new Promise((resolve, reject) => {
            uni.uploadFile({
              url: BASE_URL + '/upload/image/single',
              filePath: path,
              name: 'file',
              header: { Authorization: 'Bearer ' + uni.getStorageSync('token') },
              success: (r) => {
                try {
                  const data = JSON.parse(r.data)
                  if (data.code === 200) resolve(data.data)
                  else reject(data.msg)
                } catch (e) { reject(new Error('上传响应解析失败')) }
              },
              fail: reject
            })
          })
          this.uploadedImages.push(url)
        } catch (e) {
          uni.showToast({ title: '图片上传失败', icon: 'none' })
        }
      }
      this.uploading = false
    },

    removeImage(idx) {
      this.uploadedImages.splice(idx, 1)
    },

    async submitRepair() {
      if (!this.form.description) {
        uni.showToast({ title: '请描述问题', icon: 'none' })
        return
      }
      this.submitting = true
      try {
        await submitOrder({
          ...this.form,
          ownerId: this.userInfo.ownerId,
          contactName: this.userInfo.realName,
          imageUrls: this.uploadedImages.length > 0 ? JSON.stringify(this.uploadedImages) : null
        })
        uni.showToast({ title: '提交成功', icon: 'success' })
        this.form = { description: '', contactPhone: '', repairType: '', urgencyLevel: 1 }
        this.uploadedImages = []
        this.loadOrders()
      } catch (e) {
        uni.showToast({ title: '提交失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
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
.repair-page {
  padding-bottom: 40rpx;
}
.section-header {
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: bold;
}
.form-item {
  margin-bottom: 24rpx;
}
.label {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 8rpx;
  display: block;
}
.textarea, .input {
  width: 100%;
  border: 2rpx solid #eee;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
  font-size: 28rpx;
  background: #FAFAFA;
}
.textarea {
  min-height: 140rpx;
}
.picker-text {
  color: #333;
  padding: 16rpx 0;
  font-size: 28rpx;
  display: block;
}
.urgency-row {
  display: flex;
  gap: 20rpx;
}
.urgency-tag {
  padding: 12rpx 28rpx;
  border-radius: 30rpx;
  background: #f5f5f5;
  font-size: 24rpx;
  color: #666;
}
.urgency-tag.active {
  background: #2B85E4;
  color: #fff;
}

/* 图片上传 */
.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}
.image-item {
  position: relative;
  width: 160rpx;
  height: 160rpx;
}
.upload-img {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}
.img-delete {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #ED3F14;
  color: #fff;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.image-add {
  width: 160rpx;
  height: 160rpx;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #FAFAFA;
}
.image-add .add-icon {
  font-size: 48rpx;
  color: #ccc;
}
.image-add .add-text {
  font-size: 20rpx;
  color: #ccc;
  margin-top: 6rpx;
}

.submit-btn {
  width: 100%;
  margin-top: 16rpx;
  text-align: center;
}
.order-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-no {
  font-size: 26rpx;
  font-weight: bold;
}
.order-desc {
  font-size: 26rpx;
  color: #666;
  margin-top: 8rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.order-time {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
  display: block;
}
.empty {
  text-align: center;
  color: #999;
  padding: 40rpx 0;
}
</style>