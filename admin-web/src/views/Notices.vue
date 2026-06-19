<template>
  <div class="notices-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>公告管理</span>
          <el-button @click="showDialog = true" type="primary" icon="Plus">发布公告</el-button>
        </div>
      </template>
      <el-table :data="notices" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="catType(row.category)">{{ catLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.topStatus === 1" type="danger">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读量" width="80" />
        <el-table-column prop="publishTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category">
            <el-option label="物业通知" value="community" />
            <el-option label="政府公告" value="government" />
            <el-option label="法律法规" value="law" />
            <el-option label="紧急通知" value="emergency" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.topStatus" :active-value="1" :inactive-value="0" />
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
import { getNotices, createNotice, updateNotice, deleteNotice } from '../api/admin'

const notices = ref([])
const showDialog = ref(false)
const editingId = ref(null)
const form = ref({ title: '', content: '', category: 'community', topStatus: 0 })

const loadData = async () => {
  try {
    const res = await getNotices({ page: 1, size: 50 })
    notices.value = res?.list || []
  } catch (e) {}
}

const handleEdit = (row) => {
  editingId.value = row.id
  form.value = { title: row.title, content: row.content, category: row.category, topStatus: row.topStatus }
  showDialog.value = true
}

const handleSave = async () => {
  try {
    if (editingId.value) {
      await updateNotice(editingId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createNotice(form.value)
      ElMessage.success('发布成功')
    }
    showDialog.value = false
    editingId.value = null
    form.value = { title: '', content: '', category: 'community', topStatus: 0 }
    loadData()
  } catch (e) {}
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除吗？').then(async () => {
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const catType = (c) => ({ 'government': 'danger', 'law': 'warning', 'community': 'primary', 'emergency': 'danger' }[c] || 'info')
const catLabel = (c) => ({ 'government': '政府', 'law': '法规', 'community': '物业', 'emergency': '紧急', 'other': '其他' }[c] || c)

onMounted(loadData)
</script>
