<template>
  <div class="owners-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>业主管理</span>
          <el-button type="primary" @click="showDialog = true" icon="Plus">新增业主</el-button>
        </div>
      </template>
      <el-table :data="owners" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="roomId" label="房间ID" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '注销' }}
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
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="showDialog" :title="editingId ? '编辑业主' : '新增业主'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="form.idCard" /></el-form-item>
        <el-form-item label="房间ID"><el-input-number v-model="form.roomId" :min="1" /></el-form-item>
        <el-form-item label="业主类型">
          <el-select v-model="form.ownerType">
            <el-option :value="1" label="自住" />
            <el-option :value="2" label="出租" />
            <el-option :value="3" label="空置" />
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwners, createOwner, updateOwner, deleteOwner } from '../api/admin'

const owners = ref([])
const showDialog = ref(false)
const editingId = ref(null)
const form = ref({ name: '', phone: '', idCard: '', roomId: 1, ownerType: 1 })

const loadData = async () => {
  try { owners.value = await getOwners() || [] } catch (e) {}
}

const handleEdit = (row) => {
  editingId.value = row.id
  form.value = { ...row }
  showDialog.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await updateOwner(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createOwner(form.value)
      ElMessage.success('新增成功')
    }
    showDialog.value = false
    editingId.value = null
    form.value = { name: '', phone: '', idCard: '', roomId: 1, ownerType: 1 }
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该业主吗？').then(async () => {
    await deleteOwner(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(loadData)
</script>
