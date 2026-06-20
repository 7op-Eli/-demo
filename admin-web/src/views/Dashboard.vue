<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in stats" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-num">{{ dashboardData[card.key] }}</span>
            <span class="stat-label">{{ card.label }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>快捷操作</span></template>
          <div class="quick-grid">
            <el-button v-for="act in quickActions" :key="act.label"
              @click="$router.push(act.path)"
              :icon="act.icon" type="primary" plain size="large">
              {{ act.label }}
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>系统信息</span></template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="系统名称">智慧物业管理系统</el-descriptions-item>
            <el-descriptions-item label="版本号">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.7</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, HomeFilled, WarningFilled, SuccessFilled } from '@element-plus/icons-vue'
import { getDashboard } from '../api/admin'

const dashboardData = ref({ totalOwners: 0, totalEmployees: 0, pendingRepairs: 0, pendingVisitors: 0 })

onMounted(async () => {
  try {
    dashboardData.value = await getDashboard()
  } catch (e) { /* keep defaults on error */ }
})

const stats = [
  { label: '业主总数', key: 'totalOwners', icon: 'User', color: '#409EFF' },
  { label: '员工总数', key: 'totalEmployees', icon: 'User', color: '#67C23A' },
  { label: '待处理报修', key: 'pendingRepairs', icon: 'WarningFilled', color: '#E6A23C' },
  { label: '待审核访客', key: 'pendingVisitors', icon: 'User', color: '#F56C6C' }
]

const quickActions = [
  { label: '业主管理', path: '/owners', icon: 'User' },
  { label: '楼栋管理', path: '/buildings', icon: 'HomeFilled' },
  { label: '报修管理', path: '/repairs', icon: 'Tools' },
  { label: '公告发布', path: '/notices', icon: 'Bell' }
]
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
}
.stat-info {
  display: flex;
  flex-direction: column;
}
.stat-num {
  font-size: 28px;
  font-weight: bold;
}
.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
</style>
