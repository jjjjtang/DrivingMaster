<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- 左侧 -->
      <div class="navbar-left">
        <div class="logo" @click="goHome">
          <div class="logo-icon">🚗</div>
          <span class="logo-text">驾驶大师</span>
        </div>

        <div v-if="isStudent" class="nav-links">
          <a
              v-for="item in navList"
              :key="item.key"
              href="#"
              :class="['nav-link', { active: activeView === item.key }]"
              @click.prevent="goTo(item.key)"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </a>
        </div>
      </div>

      <!-- 右侧 -->
      <div class="navbar-right">
        <div v-if="isStudent" class="notification">
          🔔
          <span class="notification-dot"></span>
        </div>

        <div class="user-info">
          <div class="user-avatar">{{ userIcon }}</div>
          <span class="username">{{ username }}</span>
        </div>

        <button
            v-if="isStudent"
            class="btn btn-outline-primary"
            @click="goTo('profile')"
        >
          个人中心
        </button>

        <button class="btn btn-danger" @click="logout">
          退出登录
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { defineEmits } from 'vue'

/* props */
const props = defineProps({
  activeView: {
    type: String,
    default: 'home'
  },
  userType: {
    type: String,
    default: 'student'
  },
  username: {
    type: String,
    default: '学员'
  }
})

const router = useRouter()
const emit = defineEmits(['logout'])
/* 菜单配置 */
const navList = [
  { key: 'home', label: '首页', icon: '🏠' },
  { key: 'question-bank', label: '题库中心', icon: '📚' },
  { key: 'question-list', label: '题目列表', icon: '📋' },
  { key: 'exam-intro', label: '模拟考试', icon: '📝' }
]

/* computed */
const isStudent = computed(() => props.userType === 'student')
const userIcon = computed(() =>
    props.userType === 'admin' ? '👨‍💼' : '👤'
)

/* methods */
const goHome = () => {
  router.push(props.userType === 'admin' ? '/admin' : '/home')
}

const goTo = (view) => {
  const map = {
    home: '/home',
    'question-bank': '/question-bank',
    'question-list': '/question-list',
    'exam-intro': '/exam-intro',
    profile: '/profile'
  }
  router.push(map[view])
}

const logout = () => {
  emit('logout')   // 🔥 关键：通知父组件
}
</script>

<style scoped>
.navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1440px;
  margin: auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  gap: 10px;
  cursor: pointer;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #1890ff;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  padding: 8px 16px;
  border-radius: 6px;
  color: #666;
  text-decoration: none;
}

.nav-link.active {
  background: #e6f7ff;
  color: #1890ff;
}

.navbar-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.notification {
  position: relative;
  cursor: pointer;
}

.notification-dot {
  position: absolute;
  top: 2px;
  right: 0;
  width: 6px;
  height: 6px;
  background: red;
  border-radius: 50%;
}

.user-info {
  display: flex;
  gap: 8px;
  background: #f5f5f5;
  padding: 6px 12px;
  border-radius: 20px;
}

/* 按钮样式修改 - 主要改动在这里 */
.btn {
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  border: none; /* 移除默认边框 */
  outline: none; /* 移除轮廓线 */
  transition: all 0.3s ease;
}

/* 移除所有焦点状态的不必要边框 */
.btn:focus,
.btn:active {
  border: none;
  outline: none;
  box-shadow: none;
}

/* 为了更好的可访问性，保留键盘导航的焦点指示 */
.btn:focus-visible {
  outline: 2px solid #1890ff;
  outline-offset: 2px;
}

.btn-outline-primary {
  background: transparent;
  color: #1890ff;
  border: 1px solid #1890ff; /* 明确设置边框颜色 */
}

.btn-outline-primary:hover {
  background: #1890ff;
  color: white;
}

.btn-danger {
  background: #ff4d4f;
  color: #fff;
  border: none; /* 确保没有边框 */
}

.btn-danger:hover {
  background: #ff7875;
}
</style>