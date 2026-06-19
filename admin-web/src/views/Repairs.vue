<template>
  <div class="repairs-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>报修管理</span>
          <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 140px" @change="loadData">
            <el-option label="全部" :value="null" />
            <el-option label="待接单" :value="1" />
            <el-option label="已接单" :value="2" />
            <el-option label="已派单" :value="3" />
            <el-option label="维修中" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已结束" :value="7" />
          </el-select>
        </div>
      </template>
      <el-table :data="orders" stripe border @row-click="showDetail">
        <el-table-column prop="orderNo" label="工单编号" width="170" />
        <el-table-column prop="contactName" label="业主" width="100" />
        <el-table-column prop="repairType" label="维修类型" />
        <el-table-column prop="description" label="问题描述" show-overflow-tooltip />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="170" />
      </el-table>
    </el-card>

    <!-- 详情 -->
    <el-dialog v-model="showDetailDialog" title="工单详情" width="600px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="工单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业主">{{ detail.contactName }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="维修类型">{{ detail.repairType }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">{{ ['普通','紧急','非常紧急'][detail.urgencyLevel - 1] }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ detail.description }}</el-descriptions-item>
        </el-descriptions>
        <el-divider />
        <h4 style="margin-bottom: 12px;">操作日志</h4>
        <el-timeline>
          <el-timeline-item v-for="log in detailLogs" :key="log.id" :timestamp="log.createdAt">
            {{ log.action }}: {{ log.content }}
          </el-timeline-item>
        </el-timeline>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '../utils/request'

const orders = ref([])
const detail = ref(null)
const detailLogs = ref([])
const showDetailDialog = ref(false)
const statusFilter = ref(null)

const loadData = async () => {
  try {
    const params = statusFilter.value != null ? { status: statusFilter.value } : {}
    const res = await get('/repair/orders', params)
    orders.value = res?.list || []
  } catch (e) {}
}

const showDetail = async (row) => {
  try {
    detail.value = await get(`/repair/orders/${row.id}`)
    detailLogs.value = await get(`/repair/orders/${row.id}/logs`) || []
    showDetailDialog.value = true
  } catch (e) {}
}

const statusType = (s) => {
  return { 1: 'warning', 2: 'primary', 3: 'primary', 4: 'warning', 5: 'success', 6: 'info', 7: 'info', 8: 'danger' }[s] || 'info'
}
const statusLabel = (s) => {
  return { 1:'待接单',2:'已接单',3:'已派单',4:'维修中',5:'已完成',6:'回访中',7:'已结束',8:'已取消' }[s] || '未知'
}

onMounted(loadData)
</script>
