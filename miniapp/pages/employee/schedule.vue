<template>
  <view class="schedule-page">
    <!-- 日期选择 -->
    <view class="card date-row">
      <button class="date-nav" @click="changeDay(-1)">前一天</button>
      <text class="current-date">{{ currentDate }}</text>
      <button class="date-nav" @click="changeDay(1)">后一天</button>
    </view>

    <!-- 日程列表 -->
    <view class="card" v-for="s in schedules" :key="s.id">
      <view class="schedule-header">
        <text class="schedule-title">{{ s.title }}</text>
        <text class="schedule-priority tag" :class="priorityClass(s.priority)">
          {{ priorityLabel(s.priority) }}
        </text>
      </view>
      <text class="schedule-time" v-if="s.startTime">{{ s.startTime }} - {{ s.endTime }}</text>
      <text class="schedule-content">{{ s.content }}</text>
      <text class="schedule-status tag" :class="scheduleStatusClass(s.status)">
        {{ scheduleStatusLabel(s.status) }}
      </text>
    </view>
    <view v-if="schedules.length === 0" class="empty">当天暂无工作安排</view>
  </view>
</template>

<script>
import { get } from '../../utils/request'

export default {
  data() {
    return {
      currentDate: '',
      schedules: []
    }
  },
  onShow() {
    const now = new Date()
    this.currentDate = this.formatDate(now)
    this.loadSchedules()
  },
  methods: {
    formatDate(d) { return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` },
    changeDay(delta) {
      const d = new Date(this.currentDate)
      d.setDate(d.getDate() + delta)
      this.currentDate = this.formatDate(d)
      this.loadSchedules()
    },
    async loadSchedules() {
      try {
        const res = await get('/butler/schedules', { date: this.currentDate, page: 1, size: 20 })
        this.schedules = res.list || []
      } catch (e) { /* ignore */ }
    },
    priorityClass(p) { return [,'tag-blue','tag-orange','tag-red'][p] || 'tag-gray' },
    priorityLabel(p) { return [,'普通','重要','紧急'][p] || '未知' },
    scheduleStatusClass(s) { return ['tag-orange','tag-blue','tag-green','tag-red'][s] || 'tag-gray' },
    scheduleStatusLabel(s) { return ['待开始','进行中','已完成','已取消'][s] || '未知' }
  }
}
</script>

<style scoped>
.schedule-page { padding-bottom: 40rpx; }
.date-row { display: flex; align-items: center; justify-content: space-between; }
.date-nav { background: #f5f5f5; border: none; border-radius: 30rpx; padding: 10rpx 24rpx; font-size: 24rpx; color: #666; }
.current-date { font-size: 30rpx; font-weight: bold; }
.schedule-header { display: flex; justify-content: space-between; margin-bottom: 8rpx; }
.schedule-title { font-size: 28rpx; font-weight: bold; }
.schedule-time { font-size: 24rpx; color: #2B85E4; display: block; margin-bottom: 8rpx; }
.schedule-content { font-size: 26rpx; color: #666; display: block; margin-bottom: 8rpx; }
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
