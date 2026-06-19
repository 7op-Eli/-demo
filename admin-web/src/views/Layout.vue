<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #304156">
      <div class="logo">
        <span class="logo-text">智慧物业</span>
      </div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-sub-menu index="1">
          <template #title>
            <el-icon><User /></el-icon>
            <span>人员管理</span>
          </template>
          <el-menu-item index="/owners">业主管理</el-menu-item>
          <el-menu-item index="/employees">员工管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon><HomeFilled /></el-icon>
            <span>楼栋房间</span>
          </template>
          <el-menu-item index="/buildings">楼栋管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>物业缴费</span>
          </template>
          <el-menu-item index="/fee-items">费用项目</el-menu-item>
          <el-menu-item index="/fee-bills">缴费账单</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/repairs">
          <el-icon><Tools /></el-icon>
          <span>报修管理</span>
        </el-menu-item>
        <el-menu-item index="/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/convenience">
          <el-icon><Service /></el-icon>
          <span>便民服务</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between; padding: 0 20px;">
        <h3 style="font-size: 16px; color: #333;">{{ route.meta.title || '控制台' }}</h3>
        <div>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              {{ user?.realName || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main style="background: #f0f2f5; padding: 20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()
const router = useRouter()

const user = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('admin-user') || '{}')
  } catch { return {} }
})

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    localStorage.removeItem('admin-token')
    localStorage.removeItem('admin-user')
    router.push('/login')
  }
}
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-text {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 2px;
}
.user-dropdown {
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
</style>
