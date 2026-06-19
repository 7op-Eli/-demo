<template>
  <div class="employees-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>员工管理</span>
          <el-button @click="showDialog = true" type="primary" icon="Plus">新增员工</el-button>
        </div>
      </template>
      <el-table :data="employees" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="position" label="职位" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="entryDate" label="入职日期" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="新增员工" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="部门"><el-input v-model="form.department" /></el-form-item>
        <el-form-item label="入职日期"><el-date-picker v-model="form.entryDate" type="date" /></el-form-item>
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
import { getEmployees, createEmployee } from '../api/admin'

const employees = ref([])
const showDialog = ref(false)
const form = ref({ name: '', phone: '', position: '', department: '', entryDate: '' })

const loadData = async () => {
  try { employees.value = await getEmployees() || [] } catch (e) {}
}

const handleSave = async () => {
  try {
    await createEmployee(form.value)
    ElMessage.success('新增成功')
    showDialog.value = false
    form.value = { name: '', phone: '', position: '', department: '', entryDate: '' }
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
