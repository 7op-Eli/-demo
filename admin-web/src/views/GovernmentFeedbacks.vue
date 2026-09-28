<template>
  <div>
    <el-card>
      <template #header><span>政府反馈管理</span></template>
      <el-table :data="feedbacks" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="statusType(row.status)">{{statusLabel(row.status)}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-button size="small" type="primary" @click="openReply(row)"
                       v-if="row.status !== 2">回复</el-button>
            <el-button size="small" @click="openReply(row)" v-else>查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination v-model:current-page="pg" :page-size="ps" :total="total"
          layout="total,prev,pager,next" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="replyVisible" title="回复反馈" width="500px">
      <el-form label-width="100px">
        <el-form-item label="标题"><span>{{ currentFeedback?.title }}</span></el-form-item>
        <el-form-item label="内容"><span>{{ currentFeedback?.content }}</span></el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="replyText" type="textarea" :rows="4" placeholder="输入回复内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible=false">取消</el-button>
        <el-button type="primary" @click="submitReply" :disabled="!replyText">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getGovernmentFeedbacks, replyGovernmentFeedback } from '../api/admin'

const feedbacks = ref([])
const pg = ref(1); const ps = ref(20); const total = ref(0)
const replyVisible = ref(false); const currentFeedback = ref(null); const replyText = ref('')

const loadData = async () => {
  try { const r = await getGovernmentFeedbacks(pg.value, ps.value); feedbacks.value = r?.list||[]; total.value = r?.total||0 } catch(e){}
}
const openReply = (row) => { currentFeedback.value = row; replyText.value = row.reply || ''; replyVisible.value = true }
const submitReply = async () => {
  try {
    await replyGovernmentFeedback(currentFeedback.value.id, { reply: replyText.value })
    ElMessage.success('回复成功'); replyVisible.value = false; loadData()
  } catch(e){}
}
const statusType = (s) => ({0:'warning',1:'',2:'success',3:'info'})[s]||'info'
const statusLabel = (s) => ({0:'待处理',1:'处理中',2:'已回复',3:'已关闭'})[s]||'未知'
onMounted(loadData)
</script>
