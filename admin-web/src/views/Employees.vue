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
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="empPage"
          :page-size="empSize"
          :total="empTotal"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑员工' : '新增员工'" width="450px">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEmployees, createEmployee, updateEmployee, deleteEmployee } from '../api/admin'

const employees = ref([])
const empPage = ref(1)
const empSize = ref(20)
const empTotal = ref(0)
const showDialog = ref(false)
const editingId = ref(null)
const form = ref({ name: '', phone: '', position: '', department: '', entryDate: '' })

const loadData = async () => {
  try {
    const res = await getEmployees(empPage.value, empSize.value)
    employees.value = res?.list || []
    empTotal.value = res?.total || 0
  } catch (e) {}
}

const handleEdit = (row) => {
  editingId.value = row.id
  form.value = { ...row }
  showDialog.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await updateEmployee(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createEmployee(form.value)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    editingId.value = null
    form.value = { name: '', phone: '', position: '', department: '', entryDate: '' }
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该员工吗？').then(async () => {
    await deleteEmployee(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>
