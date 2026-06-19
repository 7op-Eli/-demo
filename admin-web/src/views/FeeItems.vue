<template>
  <div class="fee-items-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>费用项目管理</span>
          <el-button @click="showDialog = true" type="primary" icon="Plus">新增项目</el-button>
        </div>
      </template>
      <el-table :data="items" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="费用项目" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑费用项目' : '新增费用项目'" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.unitPrice" :precision="2" :step="0.1" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" placeholder="如: 元/㎡·月" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { get, post, put } from '../utils/request'

const items = ref([])
const showDialog = ref(false)
const editingId = ref(null)
const form = ref({ name: '', unitPrice: 0, unit: '', description: '', status: 1 })

const loadData = async () => {
  try { items.value = await get('/fee/items') || [] } catch (e) {}
}

const handleEdit = (row) => {
  editingId.value = row.id
  form.value = { ...row }
  showDialog.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await put(`/fee/items/${editingId.value}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await post('/fee/items', form.value)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    editingId.value = null
    form.value = { name: '', unitPrice: 0, unit: '', description: '', status: 1 }
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
