<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>政府人员管理</span>
          <el-button type="primary" @click="showDialog=true" icon="Plus">新增政府人员</el-button>
        </div>
      </template>
      <el-table :data="officials" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="department" label="部门" />
        <el-table-column prop="position" label="职位" />
        <el-table-column label="状态">
          <template #default="{row}">
            <el-tag :type="row.status===1?'success':'info'">{{row.status===1?'在职':'离职'}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button size="small" @click="editRow(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;display:flex;justify-content:flex-end">
        <el-pagination v-model:current-page="pg" :page-size="ps" :total="total"
          layout="total,prev,pager,next" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="editingId?'编辑政府人员':'新增政府人员'" width="450px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="部门"><el-input v-model="form.department" /></el-form-item>
        <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGovernmentOfficials, createGovernmentOfficial, updateGovernmentOfficial, deleteGovernmentOfficial } from '../api/admin'

const officials = ref([])
const pg = ref(1); const ps = ref(20); const total = ref(0)
const showDialog = ref(false); const editingId = ref(null)
const form = ref({ name:'', phone:'', department:'', position:'' })

const loadData = async () => {
  try { const r = await getGovernmentOfficials(pg.value, ps.value); officials.value = r?.list||[]; total.value = r?.total||0 } catch(e){}
}
const editRow = (r) => { editingId.value = r.id; form.value = {...r}; showDialog.value = true }
const handleSave = async () => {
  try {
    if (editingId.value) { await updateGovernmentOfficial(editingId.value, form.value); ElMessage.success('更新成功') }
    else { await createGovernmentOfficial(form.value); ElMessage.success('新增成功') }
    showDialog.value = false; editingId.value = null; form.value = { name:'', phone:'', department:'', position:'' }
    loadData()
  } catch(e){}
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该政府人员吗？').then(async () => {
    await deleteGovernmentOfficial(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}
onMounted(loadData)
</script>
