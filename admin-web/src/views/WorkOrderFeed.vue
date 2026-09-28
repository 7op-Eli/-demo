<template>
  <div>
    <el-card>
      <template #header><span>工单展示（已完成且有评价的工单）</span></template>
      <el-table :data="orders" stripe border>
        <el-table-column prop="orderNo" label="工单编号" width="160" />
        <el-table-column prop="repairType" label="维修类型" width="100" />
        <el-table-column prop="description" label="问题描述" show-overflow-tooltip />
        <el-table-column prop="completeTime" label="完成时间" width="160" />
        <el-table-column label="评价" width="200">
          <template #default="{row}">
            <span v-if="row._evaluation">
              <span v-for="i in 5" :key="i" :style="{color:i<=row._evaluation.rating?'#F56C6C':'#ccc'}">★</span>
              <span style="margin-left:8px;font-size:12px;color:#999">{{row._evaluation.comment||''}}</span>
            </span>
            <span v-else style="color:#999">暂无评价</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination v-model:current-page="pg" :page-size="ps" :total="total"
          layout="total,prev,pager,next" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPublicFeed } from '../api/admin'
import { get } from '../utils/request'

const orders = ref([])
const pg = ref(1); const ps = ref(20); const total = ref(0)

const loadData = async () => {
  try {
    const r = await getPublicFeed(pg.value, ps.value)
    const list = r?.list || []
    // 获取每个工单的评价
    for (const o of list) {
      try {
        const ev = await get(`/repair/orders/${o.id}/evaluation`)
        o._evaluation = ev
      } catch(e) { o._evaluation = null }
    }
    orders.value = list
    total.value = r?.total || 0
  } catch(e){}
}
onMounted(loadData)
</script>
