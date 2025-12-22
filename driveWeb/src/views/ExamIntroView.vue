<template>
  <div class="exam-intro-page">

    <main class="exam-container">
      <!-- 标题 -->
      <div class="exam-header">
        <h1>模拟考试</h1>
        <p>真实模拟考试环境，检验您的学习成果</p>
      </div>

      <div class="exam-card-grid">
        <!-- 科目一 -->
        <div class="exam-card exam-card-blue">
          <div class="card-header">
            <span class="icon">📄</span>
            <h2>科目一模拟考试</h2>
            <p>道路交通安全法律法规</p>
          </div>

          <div class="card-info">
            <div class="info-item">
              <span>题目数量</span>
              <span class="value">100 题</span>
            </div>

            <div class="info-item column">
              <span class="label">考试时间</span>
              <div class="time-selector blue">
                <div
                    v-for="t in subject1Times"
                    :key="t"
                    :class="['time-option', { active: subject1Time === t }]"
                    @click="subject1Time = t"
                >
                  {{ t }} 分钟
                </div>
              </div>
            </div>

            <div class="info-item">
              <span>及格分数</span>
              <span class="value danger">90 分</span>
            </div>
          </div>

          <button class="start-btn blue" @click="startExam('subject1')">
            开始考试
          </button>
        </div>

        <!-- 科目四 -->
        <div class="exam-card exam-card-purple">
          <div class="card-header">
            <span class="icon">📄</span>
            <h2>科目四模拟考试</h2>
            <p>安全文明驾驶常识</p>
          </div>

          <div class="card-info">
            <div class="info-item">
              <span>题目数量</span>
              <span class="value">100 题</span>
            </div>

            <div class="info-item column">
              <span class="label">考试时间</span>
              <div class="time-selector purple">
                <div
                    v-for="t in subject4Times"
                    :key="t"
                    :class="['time-option', { active: subject4Time === t }]"
                    @click="subject4Time = t"
                >
                  {{ t }} 分钟
                </div>
              </div>
            </div>

            <div class="info-item">
              <span>及格分数</span>
              <span class="value danger">90 分</span>
            </div>
          </div>

          <button class="start-btn purple" @click="startExam('subject4')">
            开始考试
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()
const username = '测试学员'

// 可选考试时间
const subject1Times = [30, 45, 60]
const subject4Times = [20, 30, 45]

// 默认选中
const subject1Time = ref(45)
const subject4Time = ref(30)

const startExam = (subject) => {
  router.push({
    path: '/exam',
    query: {
      subject,
      count: 100,
      time: subject === 'subject1' ? subject1Time.value : subject4Time.value
    }
  })
}
</script>

<style scoped>
.exam-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 32px;
}

.exam-header {
  text-align: center;
  margin-bottom: 48px;
}

.exam-header h1 {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
}

.exam-header p {
  color: #666;
}

.exam-card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  max-width: 900px;
  margin: 0 auto;
}

.exam-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  border: 2px solid #e5e7eb;
  transition: all 0.3s;
}

.exam-card:hover {
  transform: translateY(-4px);
}

.card-header {
  text-align: center;
  margin-bottom: 24px;
}

.card-header .icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
}

.card-header h2 {
  font-size: 22px;
  font-weight: bold;
}

.card-header p {
  color: #666;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9fafb;
  padding: 12px 16px;
  border-radius: 10px;
}

.info-item.column {
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.value {
  font-weight: 600;
}

.value.danger {
  color: #ef4444;
}

/* 🎨 艺术化时间选择器 */
.time-selector {
  display: flex;
  gap: 10px;
}

.time-option {
  padding: 8px 14px;
  border-radius: 999px;
  font-size: 14px;
  cursor: pointer;
  background: #e5e7eb;
  transition: all 0.25s ease;
  font-weight: 500;
}

.time-option:hover {
  transform: translateY(-1px);
}

.time-selector.blue .time-option.active {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
}

.time-selector.purple .time-option.active {
  background: linear-gradient(to right, #8b5cf6, #7c3aed);
  color: white;
}

.start-btn {
  width: 100%;
  padding: 14px;
  border-radius: 10px;
  color: white;
  font-weight: bold;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.start-btn.blue {
  background: linear-gradient(to right, #3b82f6, #2563eb);
}

.start-btn.purple {
  background: linear-gradient(to right, #8b5cf6, #7c3aed);
}

.start-btn:hover {
  transform: scale(1.05);
}
</style>