<template>
  <div class="fee-bills-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>缴费账单</span>
          <div>
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 12px" @change="loadData">
              <el-option label="全部" :value="null" />
              <el-option label="未缴" :value="0" />
              <el-option label="部分缴纳" :value="1" />
              <el-option label="已缴清" :value="2" />
              <el-option label="已逾期" :value="3" />
            </el-select>
            <el-button type="primary" icon="Plus" @click="showDialog = true">生成账单</el-button>
          </div>
        </div>
      </template>
      <el-table :data="bills" stripe border>
        <el-table-column prop="billNo" label="账单编号" width="180" />
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column label="金额">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="已付">
          <template #default="{ row }">¥{{ row.paidAmount }}</template>
        </el-table-column>
        <el-table-column prop="billPeriodStart" label="周期开始" />
        <el-table-column prop="dueDate" label="截止日" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="['info', 'warning', 'success', 'danger'][row.status]">
              {{ ['未缴费','部分缴纳','已缴清','已逾期'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="生成账单" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="业主ID"><el-input-number v-model="form.ownerId" :min="1" /></el-form-item>
        <el-form-item label="房间ID"><el-input-number v-model="form.roomId" :min="1" /></el-form-item>
        <el-form-item label="费用项目">
          <el-select v-model="form.feeItemId">
            <el-option v-for="item in items" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额"><el-input-number v-model="form.amount" :precision="2" :min="0" /></el-form-item>
        <el-form-item label="周期开始"><el-date-picker v-model="form.billPeriodStart" type="date" /></el-form-item>
        <el-form-item label="截止日"><el-date-picker v-model="form.dueDate" type="date" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post } from '../utils/request'

const bills = ref([])
const items = ref([])
const showDialog = ref(false)
const statusFilter = ref(null)
const form = ref({ ownerId: 1, roomId: 1, feeItemId: 1, amount: 0, billPeriodStart: '', billPeriodEnd: '', dueDate: '' })

const loadData = async () => {
  try {
    const params = statusFilter.value != null ? { status: statusFilter.value } : {}
    const res = await get('/fee/bills', params)
    bills.value = res?.list || []
  } catch (e) {}
}

const handleSave = async () => {
  try {
    await post('/fee/bills', {
      ...form.value,
      billPeriodStart: form.value.billPeriodStart?.toISOString?.()?.substring(0,10),
      billPeriodEnd: form.value.billPeriodEnd?.toISOString?.()?.substring(0,10),
      dueDate: form.value.dueDate?.toISOString?.()?.substring(0,10)
    })
    ElMessage.success('账单生成成功')
    showDialog.value = false
    loadData()
  } catch (e) {}
}

onMounted(async () => {
  loadData()
  try { items.value = await get('/fee/items') || [] } catch (e) {}
})
</script>
