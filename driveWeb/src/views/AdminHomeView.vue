<template>
  <div class="admin-home-view">
    <Navbar :activeView="'admin'"
            :userType="'admin'"
            :username="username"
            @logout="handleLogout"/>

    <div class="admin-container">
      <!-- 左侧菜单 -->
      <div class="admin-sidebar">
        <div class="sidebar-header">
          <h3>管理员面板</h3>
        </div>
        <div class="sidebar-menu">
          <button
              v-for="tab in tabs"
              :key="tab.key"
              :class="['menu-item', { active: currentTab === tab.key }]"
              @click="changeTab(tab.key)"
          >
            <span class="menu-icon">{{ tab.icon }}</span>
            <span class="menu-text">{{ tab.label }}</span>
          </button>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="admin-content">
        <div class="content-header">
          <h2>{{ getCurrentTabLabel() }}</h2>
          <div class="header-stats">
            <div class="stat-card">
              <div class="stat-icon">👥</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-label">总用户数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">📚</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalQuestions }}</div>
                <div class="stat-label">总题目数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">📊</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.passRate }}%</div>
                <div class="stat-label">平均通过率</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🕒</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.activeUsers }}</div>
                <div class="stat-label">活跃用户</div>
              </div>
            </div>
          </div>
        </div>

        <div class="content-body">
          <!-- 动态加载对应的组件 -->
          <component :is="currentComponent" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import UserManagement from '@/components/admin/UserManagement.vue'
import QuestionManagement from '@/components/admin/QuestionManagement.vue'
import SystemLogs from '@/components/admin/SystemLogs.vue'
import {clearAuth} from "@/api/index.js";

const router = useRouter()

// 当前用户信息（临时写死）
const username = '管理员'

// 标签页配置
const tabs = [
  { key: 'user-management', label: '用户管理', icon: '👥' },
  { key: 'question-management', label: '试题管理', icon: '📝' },
  { key: 'system-logs', label: '系统日志', icon: '📊' }
]

// 当前选中的标签页
const currentTab = ref('user-management')

// 统计数据（临时写死）
const stats = ref({
  totalUsers: 1254,
  totalQuestions: 1560,
  passRate: 94.5,
  activeUsers: 342
})

// 计算当前应该显示的组件
const currentComponent = computed(() => {
  const components = {
    'user-management': UserManagement,
    'question-management': QuestionManagement,
    'system-logs': SystemLogs
  }
  return components[currentTab.value] || UserManagement
})

// 切换标签页
const changeTab = (tabKey) => {
  currentTab.value = tabKey
}

// 获取当前标签页的标签
const getCurrentTabLabel = () => {
  const tab = tabs.find(t => t.key === currentTab.value)
  return tab ? tab.label : '用户管理'
}

const handleLogout = () => {
  clearAuth()
  router.push('/login')
}
</script>

<style scoped>
.admin-home-view {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-container {
  display: flex;
  min-height: calc(100vh - 64px);
}

/* 左侧菜单样式 */
.admin-sidebar {
  width: 240px;
  background: white;
  border-right: 1px solid #e8e8e8;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.06);
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.sidebar-menu {
  padding: 16px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 14px 20px;
  border: none;
  background: transparent;
  color: #595959;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.menu-item:hover {
  background-color: #f5f5f5;
  color: #1890ff;
}

.menu-item.active {
  background-color: #e6f7ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}

.menu-icon {
  margin-right: 12px;
  font-size: 16px;
}

.menu-text {
  font-weight: 500;
}

/* 右侧内容样式 */
.admin-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.content-header {
  margin-bottom: 24px;
}

.content-header h2 {
  margin: 0 0 24px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.header-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 32px;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.content-body {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-height: 500px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }

  .sidebar-menu {
    display: flex;
    overflow-x: auto;
    padding: 0;
  }

  .menu-item {
    flex-shrink: 0;
    padding: 12px 16px;
    border-right: 1px solid #f0f0f0;
  }

  .menu-item.active {
    border-right: 3px solid #1890ff;
    border-bottom: none;
  }

  .header-stats {
    grid-template-columns: 1fr;
  }
}
</style>