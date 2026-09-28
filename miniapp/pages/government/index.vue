<template>
  <view class="gov-page">
    <!-- 顶部 -->
    <view class="header-card">
      <text class="greeting">{{ userInfo.realName || '政府人员' }}</text>
      <text class="sub-text">政府工作台</text>
    </view>

    <!-- 功能 tabs -->
    <view class="tabs">
      <view class="tab" :class="{ active: tab === 'notice' }" @click="tab = 'notice'">
        <text>发布公告</text>
      </view>
      <view class="tab" :class="{ active: tab === 'feedback' }" @click="tab = 'feedback'">
        <text>问题反馈</text>
      </view>
      <view class="tab" :class="{ active: tab === 'orders' }" @click="tab = 'orders'; loadOrders()">
        <text>工单展示</text>
      </view>
    </view>

    <!-- 发布公告 -->
    <view v-if="tab === 'notice'" class="card">
      <view class="section-title">📢 发布政府公告</view>
      <view class="form-item">
        <text class="label">公告标题 *</text>
        <input class="input" v-model="noticeForm.title" placeholder="请输入公告标题" />
      </view>
      <view class="form-item">
        <text class="label">公告内容 *</text>
        <textarea class="textarea" v-model="noticeForm.content" placeholder="请输入公告内容..." />
      </view>
      <view class="form-item">
        <text class="label">图片（选填）</text>
        <view class="image-grid">
          <view class="image-item" v-for="(url, idx) in noticeImages" :key="idx">
            <image :src="url" mode="aspectFill" class="upload-img"></image>
            <view class="img-delete" @click="noticeImages.splice(idx,1)">✕</view>
          </view>
          <view class="image-add" @click="chooseNoticeImage" v-if="noticeImages.length < 4">
            <text class="add-icon">+</text>
          </view>
        </view>
      </view>
      <button class="primary-btn" @click="publishNotice" :loading="pubLoading">发布公告</button>
      <text class="tip">公告将对所有业主可见</text>
    </view>

    <!-- 问题反馈 -->
    <view v-if="tab === 'feedback'">
      <view class="card">
        <view class="section-title">📝 提交反馈给物业</view>
        <view class="form-item">
          <text class="label">反馈标题 *</text>
          <input class="input" v-model="fbForm.title" placeholder="请输入反馈标题" />
        </view>
        <view class="form-item">
          <text class="label">反馈内容 *</text>
          <textarea class="textarea" v-model="fbForm.content" placeholder="请输入反馈内容..." />
        </view>
        <view class="form-item">
          <text class="label">图片（选填）</text>
          <view class="image-grid">
            <view class="image-item" v-for="(url, idx) in fbImages" :key="idx">
              <image :src="url" mode="aspectFill" class="upload-img"></image>
              <view class="img-delete" @click="fbImages.splice(idx,1)">✕</view>
            </view>
            <view class="image-add" @click="chooseFbImage" v-if="fbImages.length < 4">
              <text class="add-icon">+</text>
            </view>
          </view>
        </view>
        <button class="primary-btn" @click="submitFeedback" :loading="fbLoading">提交反馈</button>
      </view>

      <view class="card" v-for="fb in feedbacks" :key="fb.id">
        <view class="fb-header">
          <text class="fb-title">{{ fb.title }}</text>
          <text class="tag" :class="fbStatusClass(fb.status)">{{ fbStatusLabel(fb.status) }}</text>
        </view>
        <text class="fb-content">{{ fb.content }}</text>
        <view class="fb-reply" v-if="fb.reply">
          <text class="reply-label">物业回复：</text>
          <text class="reply-text">{{ fb.reply }}</text>
        </view>
        <text class="fb-time">{{ formatTime(fb.createdAt) }}</text>
      </view>
      <view v-if="feedbacks.length === 0" class="empty">暂无反馈记录</view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="handleLogout">退出登录</button>

    <!-- 工单展示 -->
    <view v-if="tab === 'orders'">
      <view class="card" v-for="o in orders" :key="o.id">
        <view class="order-header">
          <text class="order-no">{{ o.orderNo }}</text>
          <text class="order-time">{{ formatDate(o.completeTime) }}</text>
        </view>
        <text class="order-desc">{{ o.description }}</text>
        <view class="order-tags">
          <text class="tag tag-blue">{{ o.repairType }}</text>
        </view>
        <view class="eval-section" v-if="o._evaluation">
          <view class="eval-stars">
            <text v-for="i in 5" :key="i"
                  :style="{ color: i <= o._evaluation.rating ? '#FF9900' : '#eee' }">★</text>
          </view>
          <text class="eval-comment" v-if="o._evaluation.comment">{{ o._evaluation.comment }}</text>
        </view>
      </view>
      <view v-if="orders.length === 0" class="empty">暂无工单展示</view>
    </view>
  </view>
</template>

<script>
import { BASE_URL, get, post } from '../../utils/request'
import { getUserInfo, logout } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      tab: 'notice',
      noticeForm: { title: '', content: '' },
      noticeImages: [],
      pubLoading: false,
      fbForm: { title: '', content: '' },
      fbImages: [],
      fbLoading: false,
      feedbacks: [],
      orders: []
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
  },
  methods: {
    chooseNoticeImage() {
      uni.chooseImage({
        count: 4 - this.noticeImages.length, sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => this.uploadFiles(res.tempFilePaths, 'notice')
      })
    },
    chooseFbImage() {
      uni.chooseImage({
        count: 4 - this.fbImages.length, sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => this.uploadFiles(res.tempFilePaths, 'fb')
      })
    },
    async uploadFiles(paths, type) {
      for (const p of paths) {
        try {
          const url = await new Promise((resolve, reject) => {
            uni.uploadFile({
              url: BASE_URL + '/upload/image/single', filePath: p, name: 'file',
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
          if (type === 'notice') this.noticeImages.push(url)
          else this.fbImages.push(url)
        } catch (e) { console.error('upload error:', e) }
      }
    },
    async publishNotice() {
      if (!this.noticeForm.title || !this.noticeForm.content) {
        uni.showToast({ title: '请填写标题和内容', icon: 'none' }); return
      }
      this.pubLoading = true
      try {
        await post('/notices', {
          ...this.noticeForm, category: 'government',
          imageUrls: this.noticeImages.length > 0 ? JSON.stringify(this.noticeImages) : null
        })
        uni.showToast({ title: '发布成功', icon: 'success' })
        this.noticeForm = { title: '', content: '' }
        this.noticeImages = []
      } catch (e) { uni.showToast({ title: '发布失败', icon: 'none' }); console.error(e) }
      finally { this.pubLoading = false }
    },
    async loadFeedbacks() {
      try {
        const res = await get('/government/feedbacks', { page: 1, size: 50 })
        this.feedbacks = res.list || []
      } catch (e) { console.error('loadFeedbacks:', e) }
    },
    async submitFeedback() {
      if (!this.fbForm.title || !this.fbForm.content) {
        uni.showToast({ title: '请填写标题和内容', icon: 'none' }); return
      }
      this.fbLoading = true
      try {
        await post('/government/feedbacks', {
          ...this.fbForm,
          imageUrls: this.fbImages.length > 0 ? JSON.stringify(this.fbImages) : null
        })
        uni.showToast({ title: '提交成功', icon: 'success' })
        this.fbForm = { title: '', content: '' }
        this.fbImages = []
        this.loadFeedbacks()
      } catch (e) { uni.showToast({ title: '提交失败', icon: 'none' }); console.error(e) }
      finally { this.fbLoading = false }
    },
    async loadOrders() {
      try {
        const res = await get('/repair/orders/public-feed', { page: 1, size: 20 })
        const list = res.list || []
        for (const o of list) {
          try { o._evaluation = await get('/repair/orders/' + o.id + '/evaluation') }
          catch (e) { o._evaluation = null }
        }
        this.orders = list
      } catch (e) { console.error('loadOrders:', e) }
    },
    handleLogout() {
      uni.showModal({
        title: '退出登录', content: '确定退出吗？',
        success: (r) => { if (r.confirm) { logout(); uni.reLaunch({ url: '/pages/login' }) } }
      })
    },
    fbStatusClass(s) { const m={0:'tag-orange',1:'tag-blue',2:'tag-green',3:'tag-gray'}; return m[s]||'tag-gray' },
    fbStatusLabel(s) { const m={0:'待处理',1:'处理中',2:'已回复',3:'已关闭'}; return m[s]||'未知' },
    formatTime(t) { return t ? t.substring(0, 16) : '' },
    formatDate(t) { return t ? t.substring(0, 10) : '' }
  },
  watch: {
    tab(v) {
      if (v === 'feedback') this.loadFeedbacks()
    }
  }
}
</script>

<style scoped>
.gov-page { min-height: 100vh; background: #F5F6FA; padding-bottom: 30rpx; }
.header-card {
  background: linear-gradient(135deg, #ED3F14, #FF6B35);
  padding: 50rpx 30rpx 30rpx; color: #fff;
}
.header-card .greeting { font-size: 36rpx; font-weight: bold; display: block; }
.header-card .sub-text { font-size: 24rpx; opacity: 0.8; }
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
.tabs { display: flex; background: #fff; margin-bottom: 10rpx; }
.tab { flex: 1; text-align: center; padding: 24rpx 0; font-size: 28rpx; color: #666; border-bottom: 3rpx solid transparent; }
.tab.active { color: #ED3F14; border-bottom-color: #ED3F14; font-weight: bold; }
.section-title { font-size: 30rpx; font-weight: bold; margin-bottom: 16rpx; }
.form-item { margin-bottom: 20rpx; }
.label { font-size: 26rpx; color: #666; margin-bottom: 8rpx; display: block; }
.input, .textarea {
  width: 100%; border: 2rpx solid #eee; border-radius: 12rpx;
  padding: 16rpx; font-size: 28rpx; background: #FAFAFA; box-sizing: border-box;
}
.textarea { min-height: 120rpx; }
.image-grid { display: flex; flex-wrap: wrap; }
.image-item { position: relative; width: 140rpx; height: 140rpx; margin-right: 12rpx; margin-bottom: 12rpx; }
.upload-img { width: 100%; height: 100%; border-radius: 10rpx; }
.img-delete {
  position: absolute; top: -8rpx; right: -8rpx;
  width: 36rpx; height: 36rpx; border-radius: 50%;
  background: #ED3F14; color: #fff; font-size: 20rpx;
  display: flex; align-items: center; justify-content: center;
}
.image-add {
  width: 140rpx; height: 140rpx; border: 2rpx dashed #ddd;
  border-radius: 10rpx; display: flex; align-items: center; justify-content: center; background: #FAFAFA;
}
.image-add .add-icon { font-size: 48rpx; color: #ccc; }
.primary-btn {
  width: 100%; background: #ED3F14; color: #fff; border-radius: 40rpx;
  padding: 20rpx; text-align: center; border: none; font-size: 28rpx; margin-top: 10rpx;
}
.tip { font-size: 22rpx; color: #ccc; display: block; text-align: center; margin-top: 12rpx; }
.fb-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.fb-title { font-size: 28rpx; font-weight: bold; }
.fb-content { font-size: 26rpx; color: #333; display: block; margin-bottom: 8rpx; }
.fb-reply { background: #F0F9FF; padding: 12rpx 16rpx; border-radius: 8rpx; margin-bottom: 8rpx; }
.reply-label { font-size: 24rpx; color: #2B85E4; font-weight: bold; }
.reply-text { font-size: 24rpx; color: #333; display: block; margin-top: 4rpx; }
.fb-time { font-size: 22rpx; color: #bbb; display: block; }
.order-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.order-no { font-size: 24rpx; color: #999; }
.order-time { font-size: 22rpx; color: #bbb; }
.order-desc { font-size: 28rpx; color: #333; margin-bottom: 10rpx; display: block; }
.order-tags { margin-bottom: 10rpx; }
.eval-section { background: #FFF8E1; border-radius: 10rpx; padding: 12rpx 16rpx; }
.eval-stars { font-size: 28rpx; letter-spacing: 4rpx; }
.eval-comment { font-size: 24rpx; color: #666; margin-top: 6rpx; display: block; }
.empty { text-align: center; color: #999; padding: 80rpx 0; font-size: 26rpx; }
</style>
