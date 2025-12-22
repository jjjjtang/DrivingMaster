<template>
  <div class="exam-page">
    <main class="exam-content">
      <h1>模拟考试中</h1>

      <div class="exam-info">
        <p>考试科目：{{ subjectText }}</p>
        <p>题目数量：{{ count }} 题</p>
      </div>

      <button class="exit-btn" @click="exitExam">
        返回模拟考试入口
      </button>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const router = useRouter()
const username = '测试学员'

const subject = route.query.subject
const count = route.query.count

const subjectText = computed(() =>
    subject === 'subject1' ? '科目一' : '科目四'
)

const exitExam = () => {
  router.push('/exam-intro')
}
</script>

<style scoped>
.exam-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.exam-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 64px 32px;
  text-align: center;
}

.exam-info {
  margin: 32px 0;
  font-size: 18px;
}

.exit-btn {
  padding: 12px 32px;
  border-radius: 8px;
  border: none;
  background: #1890ff;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}
</style>