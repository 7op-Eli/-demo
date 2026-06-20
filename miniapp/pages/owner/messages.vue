<template>
  <view class="chat-page">
    <view class="chat-header card">
      <text class="title">管家服务</text>
      <text class="subtitle">与您的专属管家在线沟通</text>
    </view>

    <view class="message-list">
      <view class="message-item" v-for="msg in messages" :key="msg.id"
            :class="{ 'my-msg': msg.senderRole === 1 }">
        <view class="msg-bubble">
          <text class="msg-content">{{ msg.content }}</text>
          <text class="msg-time">{{ formatTime(msg.createdAt) }}</text>
        </view>
      </view>
      <view v-if="messages.length === 0" class="empty">暂无消息，发送第一条消息吧</view>
    </view>

    <view class="input-bar">
      <input class="chat-input" v-model="inputText" placeholder="输入消息..." />
      <button class="send-btn" @click="sendMsg">发送</button>
    </view>
  </view>
</template>

<script>
import { getMessages, sendMessage } from '../../api/butler'
import { getUserInfo } from '../../utils/auth'

export default {
  data() {
    return {
      userInfo: {},
      messages: [],
      inputText: ''
    }
  },
  onShow() {
    this.userInfo = getUserInfo() || {}
    this.loadMessages()
  },
  methods: {
    async loadMessages() {
      try {
        const res = await getMessages(1)
        this.messages = res.list || []
      } catch (e) { /* ignore */ }
    },
    async sendMsg() {
      if (!this.inputText.trim()) return
      try {
        const msg = {
          content: this.inputText,
          senderId: this.userInfo.userId,
          senderRole: 1,
          roomId: this.userInfo.ownerId
        }
        await sendMessage(msg)
        this.inputText = ''
        this.loadMessages()
      } catch (e) { /* ignore */ }
    },
    formatTime(t) { return t ? t.substring(11, 16) : '' }
  }
}
</script>

<style scoped>
.chat-page { padding-bottom: 120rpx; }
.chat-header { text-align: center; padding: 30rpx; }
.chat-header .title { font-size: 32rpx; font-weight: bold; display: block; }
.chat-header .subtitle { font-size: 24rpx; color: #999; margin-top: 8rpx; }
.message-list { padding: 20rpx; }
.message-item { margin-bottom: 20rpx; display: flex; }
.my-msg { justify-content: flex-end; }
.msg-bubble {
  max-width: 70%; padding: 16rpx 24rpx;
  border-radius: 16rpx; background: #E8F4FD;
}
.my-msg .msg-bubble { background: #2B85E4; }
.my-msg .msg-content { color: #fff; }
.msg-content { font-size: 28rpx; color: #333; display: block; }
.msg-time { font-size: 20rpx; color: #999; margin-top: 8rpx; display: block; }
.my-msg .msg-time { color: rgba(255,255,255,0.7); }
.input-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  padding: 16rpx 30rpx; background: #fff;
  display: flex; align-items: center; border-top: 1rpx solid #eee;
}
.chat-input {
  flex: 1; border: 2rpx solid #eee; border-radius: 40rpx;
  padding: 16rpx 24rpx; font-size: 28rpx;
}
.send-btn {
  margin-left: 16rpx; background: #2B85E4; color: #fff;
  border: none; border-radius: 40rpx; padding: 16rpx 36rpx; font-size: 28rpx;
}
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
