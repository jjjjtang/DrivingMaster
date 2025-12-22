<template>
  <div class="question-bank-page">

    <!-- 主体内容 -->
    <main class="main-content">
      <h1 class="page-title">题库中心</h1>

      <div class="bank-grid">
        <!-- 科目一 -->
        <div class="bank-card subject1">
          <h2 class="bank-title">科目一题库</h2>
          <p class="bank-desc">道路交通安全法律法规</p>

          <div class="btn-grid">
            <button class="bank-btn" @click="go('subject1', 'sequential')">
              顺序练习
            </button>
            <button class="bank-btn" @click="go('subject1', 'special')">
              题型练习
            </button>
            <button class="bank-btn" @click="go('subject1', 'chapter')">
              章节练习
            </button>
            <button class="bank-btn" @click="go('subject1', 'wrong')">
              错题集
            </button>
            <button class="bank-btn" @click="go('subject1', 'favorite')">
              我的收藏
            </button>
          </div>
        </div>

        <!-- 科目四 -->
        <div class="bank-card subject4">
          <h2 class="bank-title">科目四题库</h2>
          <p class="bank-desc">安全文明驾驶常识</p>

          <div class="btn-grid">
            <button class="bank-btn" @click="go('subject4', 'sequential')">
              顺序练习
            </button>
            <button class="bank-btn" @click="go('subject4', 'special')">
              题型练习
            </button>
            <button class="bank-btn" @click="go('subject4', 'chapter')">
              章节练习
            </button>
            <button class="bank-btn" @click="go('subject4', 'wrong')">
              错题集
            </button>
            <button class="bank-btn" @click="go('subject4', 'favorite')">
              我的收藏
            </button>
          </div>
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

// 模拟登录学员名（来自登录页传递即可）
const username = ref('测试学员')

// 占位跳转（后续你可以细分路由）
const go = (subject, mode) => {
  console.log('选择：', subject, mode)
  // 示例：router.push('/practice')
}
</script>

<style scoped>
.question-bank-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.main-content {
  max-width: 1440px;
  margin: 0 auto;
  padding: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 24px;
}

.bank-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

/* 卡片 */
.bank-card {
  border-radius: 16px;
  padding: 32px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.subject1 {
  background: linear-gradient(135deg, #1890ff, #096dd9);
}

.subject4 {
  background: linear-gradient(135deg, #722ed1, #531dab);
}

.bank-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.bank-desc {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 24px;
}

/* 按钮 */
.btn-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.bank-btn {
  background: #fff;
  color: inherit;
  padding: 12px 0;
  border-radius: 8px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.subject1 .bank-btn {
  color: #1890ff;
}

.subject4 .bank-btn {
  color: #722ed1;
}

.bank-btn:hover {
  transform: translateY(-2px);
  background: #f0f0f0;
}
</style>