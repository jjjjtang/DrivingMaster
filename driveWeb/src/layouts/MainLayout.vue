<!-- src/layouts/MainLayout.vue -->
<template>
  <div class="main-layout">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="container">
        <div class="navbar-left">
          <div class="logo" @click="goHome">
            <span class="iconify" data-icon="mdi:steering"></span>
            <span>驾驶大师</span>
          </div>

          <!-- 学员导航链接 -->
          <div v-if="userType === 'student'" class="nav-links">
            <router-link
                to="/"
                class="nav-link"
                :class="{ active: activeView === 'home' }"
            >
              <span class="iconify" data-icon="carbon:home"></span>
              <span>首页</span>
            </router-link>

            <router-link
                to="/question-bank"
                class="nav-link"
                :class="{ active: activeView === 'question-bank' }"
            >
              <span class="iconify" data-icon="carbon:document"></span>
              <span>题库中心</span>
            </router-link>

            <router-link
                to="/question-list"
                class="nav-link"
                :class="{ active: activeView === 'question-list' }"
            >
              <span class="iconify" data-icon="carbon:table-of-contents"></span>
              <span>题目列表</span>
            </router-link>

            <router-link
                to="/exam-intro"
                class="nav-link"
                :class="{ active: activeView === 'exam-intro' }"
            >
              <span class="iconify" data-icon="carbon:checkbox-checked"></span>
              <span>模拟考试</span>
            </router-link>

            <router-link
                to="/community"
                class="nav-link"
                :class="{ active: activeView === 'community' }"
            >
              <span class="iconify" data-icon="carbon:user-multiple"></span>
              <span>学习社区</span>
            </router-link>
          </div>
        </div>

        <div class="navbar-right">
          <!-- 通知图标（仅学员） -->
          <button v-if="userType === 'student'" class="notification-btn">
            <span class="iconify" data-icon="carbon:notification"></span>
            <span class="notification-badge"></span>
          </button>

          <!-- 用户信息 -->
          <div class="user-info">
            <span class="iconify" :data-icon="userIcon"></span>
            <span>{{ username }}</span>
          </div>

          <!-- 个人中心按钮（仅学员） -->
          <button
              v-if="userType === 'student'"
              class="btn-profile"
              @click="goProfile"
          >
            个人中心
          </button>

          <!-- 退出登录按钮 -->
          <button class="btn-logout" @click="handleLogout">
            退出登录
          </button>
        </div>
      </div>
    </nav>

    <!-- 页面内容 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth } from '@/api'

const route = useRoute()
const router = useRouter()

// 用户状态 - 可以从 localStorage 获取
const userType = ref(localStorage.getItem('userType') || 'student')
const username = ref(localStorage.getItem('username') || '学员')

// 监听路由变化，更新活动视图
const activeView = computed(() => {
  const routeName = route.name
  const viewMap = {
    'home': 'home',
    'question-bank': 'question-bank',
    'question-list': 'question-list',
    'exam-intro': 'exam-intro',
    'profile': 'profile',
    'community': 'community',
    'practice': 'question-bank',
    'chapter-select': 'question-bank',
    'question-type-select': 'question-bank'
  }
  return viewMap[routeName] || 'home'
})

// 用户图标
const userIcon = computed(() => {
  switch (userType.value) {
    case 'admin': return 'carbon:user-admin'
    case 'student': return 'carbon:user-avatar-filled'
    default: return 'carbon:user-avatar'
  }
})

// 方法
const goHome = () => {
  router.push('/')
}

const goProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  clearAuth()
  // 清除用户信息
  localStorage.removeItem('userType')
  localStorage.removeItem('username')
  localStorage.removeItem('token')
  // 跳转到登录页
  router.push('/login')
}

// 初始化时检查用户状态
const initUserState = () => {
  // 从 localStorage 恢复用户状态
  const savedUserType = localStorage.getItem('userType')
  const savedUsername = localStorage.getItem('username')

  if (savedUserType) {
    userType.value = savedUserType
  }
  if (savedUsername) {
    username.value = savedUsername
  }
}

// 组件挂载时初始化
initUserState()
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.navbar {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e8e8e8;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
  cursor: pointer;
}

.logo .iconify {
  font-size: 24px;
  color: #1890ff;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: #666;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-link:hover {
  color: #1890ff;
  background: #e6f7ff;
}

.nav-link.active {
  color: #1890ff;
  background: #e6f7ff;
  font-weight: 500;
}

.nav-link .iconify {
  font-size: 16px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-btn {
  position: relative;
  padding: 8px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
}

.notification-btn:hover {
  background: #f5f5f5;
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 6px;
  height: 6px;
  background: #ff4d4f;
  border-radius: 50%;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-weight: 500;
}

.user-info .iconify {
  font-size: 20px;
}

.btn-profile {
  padding: 6px 16px;
  border: 1px solid #1890ff;
  border-radius: 6px;
  color: #1890ff;
  background: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-profile:hover {
  background: #e6f7ff;
}

.btn-logout {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  color: white;
  background: #ff4d4f;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: #ff7875;
}

.main-content {
  padding-top: 0;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>