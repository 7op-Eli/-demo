<template>
  <div class="buildings-page">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>楼栋管理</span>
          <el-button @click="showBuildingDialog = true" type="primary" icon="Plus">新建楼栋</el-button>
        </div>
      </template>
      <el-table :data="buildings" stripe border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="楼栋名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="totalFloors" label="总楼层" />
        <el-table-column prop="totalRooms" label="总户数" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showRooms(row)">查看房间</el-button>
            <el-button size="small" type="primary" @click="editBuilding(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="bldPage"
          :page-size="bldSize"
          :total="bldTotal"
          layout="total, prev, pager, next"
          @current-change="loadBuildings"
        />
      </div>
    </el-card>

    <!-- 房间列表 -->
    <el-card style="margin-top: 20px" v-if="selectedBuilding">
      <template #header>
        <div style="display: flex; justify-content: space-between">
          <span>{{ selectedBuilding.name }} - 房间列表</span>
          <el-button @click="showRoomDialog = true" type="primary" size="small" icon="Plus">新增房间</el-button>
        </div>
      </template>
      <el-table :data="rooms" stripe border>
        <el-table-column prop="roomNo" label="房号" />
        <el-table-column prop="unitNo" label="单元" />
        <el-table-column prop="floor" label="楼层" />
        <el-table-column prop="area" label="面积(㎡)" />
        <el-table-column prop="ownerName" label="业主" />
        <el-table-column prop="ownerPhone" label="业主电话" />
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ {1:'已售',2:'未售',3:'出租'}[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editRoom(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 楼栋弹窗 -->
    <el-dialog v-model="showBuildingDialog" :title="editingBuilding ? '编辑楼栋' : '新建楼栋'" width="450px">
      <el-form :model="buildingForm" label-width="100px">
        <el-form-item label="楼栋名称"><el-input v-model="buildingForm.name" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="buildingForm.address" /></el-form-item>
        <el-form-item label="总楼层"><el-input-number v-model="buildingForm.totalFloors" :min="1" /></el-form-item>
        <el-form-item label="总户数"><el-input-number v-model="buildingForm.totalRooms" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBuildingDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBuilding">保存</el-button>
      </template>
    </el-dialog>

    <!-- 房间弹窗 -->
    <el-dialog v-model="showRoomDialog" :title="editingRoom ? '编辑房间' : '新增房间'" width="450px">
      <el-form :model="roomForm" label-width="100px">
        <el-form-item label="房号"><el-input v-model="roomForm.roomNo" /></el-form-item>
        <el-form-item label="单元"><el-input v-model="roomForm.unitNo" /></el-form-item>
        <el-form-item label="楼层"><el-input-number v-model="roomForm.floor" :min="1" /></el-form-item>
        <el-form-item label="面积"><el-input-number v-model="roomForm.area" :precision="2" /></el-form-item>
        <el-form-item label="业主姓名"><el-input v-model="roomForm.ownerName" /></el-form-item>
        <el-form-item label="业主电话"><el-input v-model="roomForm.ownerPhone" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoomDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRoom">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBuildings, createBuilding, updateBuilding, getRooms, createRoom, updateRoom } from '../api/admin'

const buildings = ref([])
const bldPage = ref(1)
const bldSize = ref(20)
const bldTotal = ref(0)
const rooms = ref([])
const selectedBuilding = ref(null)

const showBuildingDialog = ref(false)
const showRoomDialog = ref(false)
const editingBuilding = ref(null)
const editingRoom = ref(null)
const buildingForm = ref({ name: '', address: '', totalFloors: 1, totalRooms: 1 })
const roomForm = ref({ roomNo: '', unitNo: '', floor: 1, area: 0, ownerName: '', ownerPhone: '' })

const loadBuildings = async () => {
  try {
    const res = await getBuildings(bldPage.value, bldSize.value)
    buildings.value = res?.list || []
    bldTotal.value = res?.total || 0
  } catch (e) {}
}

const showRooms = async (building) => {
  selectedBuilding.value = building
  try { rooms.value = await getRooms(building.id) || [] } catch (e) {}
}

const editBuilding = (row) => {
  editingBuilding.value = row.id
  buildingForm.value = { name: row.name, address: row.address, totalFloors: row.totalFloors, totalRooms: row.totalRooms }
  showBuildingDialog.value = true
}

const saveBuilding = async () => {
  try {
    if (editingBuilding.value) {
      await updateBuilding(editingBuilding.value, buildingForm.value)
      ElMessage.success('更新成功')
    } else {
      await createBuilding(buildingForm.value)
      ElMessage.success('新建成功')
    }
    showBuildingDialog.value = false
    editingBuilding.value = null
    buildingForm.value = { name: '', address: '', totalFloors: 1, totalRooms: 1 }
    loadBuildings()
  } catch (e) {}
}

const editRoom = (row) => {
  editingRoom.value = row.id
  roomForm.value = { roomNo: row.roomNo, unitNo: row.unitNo, floor: row.floor, area: row.area, ownerName: row.ownerName, ownerPhone: row.ownerPhone }
  showRoomDialog.value = true
}

const saveRoom = async () => {
  try {
    if (editingRoom.value) {
      await updateRoom(editingRoom.value, { ...roomForm.value, buildingId: selectedBuilding.value.id })
      ElMessage.success('更新成功')
    } else {
      await createRoom({ ...roomForm.value, buildingId: selectedBuilding.value.id })
      ElMessage.success('新增成功')
    }
    showRoomDialog.value = false
    editingRoom.value = null
    roomForm.value = { roomNo: '', unitNo: '', floor: 1, area: 0, ownerName: '', ownerPhone: '' }
    if (selectedBuilding.value) showRooms(selectedBuilding.value)
  } catch (e) {}
}

onMounted(loadBuildings)
</script>
