<template>
  <div class="type-select-page">
    <!-- 顶部说明 -->
    <div class="header">
      <h1>题型练习</h1>
      <p>请选择你要练习的题型</p>
    </div>

    <!-- 题型卡片 -->
    <div class="type-grid">
      <div
          v-for="item in typeList"
          :key="item.type"
          class="type-card"
          @click="goPractice(item.type)"
      >
        <div class="icon" :style="{ color: item.color }">
          {{ item.icon }}
        </div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.desc }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

/* 题型配置 */
const typeList = [
  {
    type: 'choice',
    title: '选择题',
    desc: '单选、多选题专项训练',
    icon: '📝',
    color: '#1890ff'
  },
  {
    type: 'blank',
    title: '填空题',
    desc: '关键知识点填空练习',
    icon: '✍️',
    color: '#52c41a'
  },
  {
    type: 'judge',
    title: '判断题',
    desc: '快速判断正误',
    icon: '✅',
    color: '#faad14'
  }
]

/* 跳转到练习页（这里只做入口） */
const goPractice = (type) => {
  router.push({
    path: '/practice',
    query: {
      mode: 'special',
      type
    }
  })
}
</script>

<style scoped>
.type-select-page {
  min-height: calc(100vh - 64px);
  background: #f5f5f5;
  padding: 40px 24px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.header p {
  color: #8c8c8c;
  font-size: 16px;
}

/* 卡片布局 */
.type-grid {
  max-width: 1000px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

/* 卡片样式 */
.type-card {
  background: #fff;
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  cursor: pointer;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.type-card:hover {
  border-color: #1890ff;
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(24, 144, 255, 0.15);
}

.icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.type-card h3 {
  font-size: 20px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.type-card p {
  font-size: 14px;
  color: #8c8c8c;
}
</style>