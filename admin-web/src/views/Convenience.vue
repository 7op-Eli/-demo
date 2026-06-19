<template>
  <div class="convenience-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>便民服务管理</span>
          <el-button @click="showServiceDialog = true" type="primary" icon="Plus">新增服务</el-button>
        </div>
      </template>
      <el-table :data="services" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="服务名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="类型">
          <template #default="{ row }">
            <el-tag>{{ { 'info':'信息告知', 'risk':'风险告知', 'tool':'工具借用', 'other':'其他' }[row.serviceType] || row.serviceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="toggleService(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="editService(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showServiceDialog" :title="editingService ? '编辑服务' : '新增服务'" width="500px">
      <el-form :model="serviceForm" label-width="100px">
        <el-form-item label="服务名称"><el-input v-model="serviceForm.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="serviceForm.description" type="textarea" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="serviceForm.serviceType">
            <el-option label="信息告知" value="info" />
            <el-option label="风险告知" value="risk" />
            <el-option label="工具借用" value="tool" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话"><el-input v-model="serviceForm.contactPhone" /></el-form-item>
        <el-form-item label="服务时间"><el-input v-model="serviceForm.serviceTime" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showServiceDialog = false">取消</el-button>
        <el-button type="primary" @click="saveService">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post, put } from '../utils/request'

const services = ref([])
const showServiceDialog = ref(false)
const editingService = ref(null)
const serviceForm = ref({ name: '', description: '', serviceType: 'info', contactPhone: '', serviceTime: '' })

const loadData = async () => {
  try { services.value = await get('/convenience/services') || [] } catch (e) {}
}

const editService = (row) => {
  editingService.value = row.id
  serviceForm.value = { name: row.name, description: row.description, serviceType: row.serviceType, contactPhone: row.contactPhone, serviceTime: row.serviceTime }
  showServiceDialog.value = true
}

const saveService = async () => {
  try {
    if (editingService.value) {
      await put(`/convenience/services/${editingService.value}`, serviceForm.value)
      ElMessage.success('更新成功')
    } else {
      await post('/convenience/services', serviceForm.value)
      ElMessage.success('新增成功')
    }
    showServiceDialog.value = false
    editingService.value = null
    serviceForm.value = { name: '', description: '', serviceType: 'info', contactPhone: '', serviceTime: '' }
    loadData()
  } catch (e) {}
}

const toggleService = async (row) => {
  try {
    await put(`/convenience/services/${row.id}`, row)
  } catch (e) {}
}

onMounted(loadData)
</script>
