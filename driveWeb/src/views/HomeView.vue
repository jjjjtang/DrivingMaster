<template>
  <div class="home-page">

    <main class="main-content">
      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-content">
          <h1>欢迎来到驾驶大师学习平台</h1>
          <p>科学备考，轻松过关</p>
          <div class="hero-buttons">
            <button class="btn btn-primary" @click="startLearning">
              开始学习
            </button>
            <button class="btn btn-outline-white" @click="startExam">
              模拟考试
            </button>
          </div>
        </div>
      </section>

      <!-- Subject Sections -->
      <div class="container">
        <!-- 科目一 -->
        <section class="subject-section">
          <h2 class="section-title">
            <i class="section-icon">📄</i>
            科目一
          </h2>

          <div class="practice-grid">
            <div
                v-for="card in subject1Cards"
                :key="card.id"
                class="practice-card"
                @click="handleCardClick(card)"
            >
              <div class="card-header">
                <div class="card-icon" :style="{ color: card.color }">
                  <i>{{ card.icon }}</i>
                </div>
                <span v-if="card.recommend" class="recommend-tag">推荐</span>
              </div>
              <h3>{{ card.title }}</h3>
              <p>{{ card.description }}</p>
            </div>
          </div>
        </section>

        <!-- 科目四 -->
        <section class="subject-section">
          <h2 class="section-title">
            <i class="section-icon">📄</i>
            科目四
          </h2>

          <div class="practice-grid">
            <div
                v-for="card in subject4Cards"
                :key="card.id"
                class="practice-card"
                @click="handleCardClick(card)"
            >
              <div class="card-header">
                <div class="card-icon" :style="{ color: card.color }">
                  <i>{{ card.icon }}</i>
                </div>
              </div>
              <h3>{{ card.title }}</h3>
              <p>{{ card.description }}</p>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import {clearAuth} from "@/api/index.js";

/* router */
const router = useRouter()

/* 基本状态 */
const activeView = ref('home')
const userType = ref('student')
const username = ref('学员')

/* 科目一卡片 */
const subject1Cards = ref([
  {
    id: 1,
    title: '顺序练习',
    description: '按题库顺序逐题练习',
    icon: '📋',
    color: '#1890FF',
    mode: 'sequential',
    subject: 'subject1',
    recommend: true
  },
  {
    id: 2,
    title: '题型练习',
    description: '按题型分类练习',
    icon: '🗂️',
    color: '#52c41a',
    mode: 'special',
    subject: 'subject1'
  },
  {
    id: 3,
    title: '章节练习',
    description: '按教材章节练习',
    icon: '📖',
    color: '#673ab7',
    mode: 'chapter',
    subject: 'subject1'
  },
  {
    id: 4,
    title: '错题集',
    description: '重点复习错题',
    icon: '⚠️',
    color: '#f5222d',
    mode: 'wrong',
    subject: 'subject1'
  }
])

/* 科目四卡片 */
const subject4Cards = ref([
  {
    id: 5,
    title: '顺序练习',
    description: '按题库顺序逐题练习',
    icon: '📋',
    color: '#673ab7',
    mode: 'sequential',
    subject: 'subject4'
  },
  {
    id: 6,
    title: '题型练习',
    description: '按题型分类练习',
    icon: '🗂️',
    color: '#52c41a',
    mode: 'special',
    subject: 'subject4'
  },
  {
    id: 7,
    title: '章节练习',
    description: '按教材章节练习',
    icon: '📖',
    color: '#673ab7',
    mode: 'chapter',
    subject: 'subject4'
  },
  {
    id: 8,
    title: '错题集',
    description: '重点复习错题',
    icon: '⚠️',
    color: '#f5222d',
    mode: 'wrong',
    subject: 'subject4'
  }
])

/* 方法 */
const startLearning = () => {
  router.push('/question-bank')
}

const startExam = () => {
  router.push('/exam-intro')
}

const handleCardClick = (card) => {
  if (card.mode === 'special') {
    router.push('/question-type-select')
  } else if (card.mode === 'chapter') {
    router.push('/chapter-select')
  } else {
    router.push('/practice')
  }
}

const handleLogout = () => {
  clearAuth()
  router.push('/login')
}
</script>

<style scoped>
/* ✅ 样式与你原来完全一致，未做任何删减 */
.home-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  padding: 24px;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
}

.hero-section {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 32px;
  color: white;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

.hero-content h1 {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 16px;
}

.hero-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn {
  padding: 12px 32px;
  border-radius: 8px;
  border: none;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-primary {
  background: white;
  color: #1890ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.btn-outline-white {
  background: transparent;
  border: 2px solid white;
  color: white;
}

.btn-outline-white:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.subject-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 24px;
}

.practice-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.practice-card {
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
}

.practice-card:hover {
  border-color: #1890ff;
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.15);
}
</style>