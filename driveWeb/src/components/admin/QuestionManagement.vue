<template>
  <div class="question-management">
    <div class="management-header">
      <h3>试题管理</h3>
      <div class="header-controls">
        <div class="filter-controls">
          <select v-model="selectedSubject" class="filter-select">
            <option value="all">全部科目</option>
            <option value="subject1">科目一</option>
            <option value="subject4">科目四</option>
          </select>
          <select v-model="selectedType" class="filter-select">
            <option value="all">全部题型</option>
            <option value="single">单选题</option>
            <option value="multiple">多选题</option>
            <option value="judge">判断题</option>
          </select>
          <select v-model="selectedChapter" class="filter-select">
            <option value="all">全部章节</option>
            <option v-for="chapter in chapters" :key="chapter" :value="chapter">
              {{ chapter }}
            </option>
          </select>
        </div>
        <button class="btn btn-primary" @click="addQuestion">
          <span class="btn-icon">➕</span>
          添加题目
        </button>
      </div>
    </div>

    <div class="question-list">
      <div v-for="question in filteredQuestions" :key="question.id" class="question-card">
        <div class="question-header">
          <div class="question-meta">
            <span class="question-id">ID: {{ question.id }}</span>
            <span :class="['question-type', question.type]">
              {{ getTypeText(question.questionType) }}
            </span>
            <span class="question-chapter">{{ question.chapter }}</span>
          </div>
          <div class="question-actions">
            <button class="btn-icon-small" @click="editQuestion(question)" title="编辑">
              ✏️
            </button>
            <button class="btn-icon-small" @click="toggleFavorite(question)" :title="question.favorite ? '取消收藏' : '收藏'">
              {{ question.favorite ? '★' : '☆' }}
            </button>
            <button class="btn-icon-small btn-danger" @click="deleteQuestion(question)" title="删除">
              🗑️
            </button>
          </div>
        </div>

        <div class="question-content">
          <p class="question-text">{{ question.question }}</p>
          <div v-if="question.image" class="question-image">
            <img :src="question.image" alt="题目图片" />
          </div>
          <div class="question-options">
            <div v-for="(option, index) in question.options" :key="index"
                 :class="['option-item', { correct: isCorrectOption(question, index) }]">
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <span class="option-text">{{ option }}</span>
            </div>
          </div>
        </div>

        <div class="question-footer">
          <div class="answer-info">
            <span class="correct-answer">
              正确答案：{{ formatAnswer(question.correctAnswer) }}
            </span>
            <span class="difficulty" :class="question.difficulty">
              难度：{{ getDifficultyText(question.difficulty) }}
            </span>
          </div>
          <div class="question-explanation" v-if="question.explanation">
            <strong>解析：</strong>{{ question.explanation }}
          </div>
        </div>
      </div>
    </div>

    <div class="pagination">
      <button class="btn btn-secondary" @click="prevPage" :disabled="currentPage === 1">
        上一页
      </button>
      <span class="page-info">
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
      </span>
      <button class="btn btn-secondary" @click="nextPage" :disabled="currentPage === totalPages">
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'

// 临时写死的数据
const questions = ref([
  {
    id: 1,
    subject: 'subject1',
    chapter: '第一章',
    type: 'special',
    specialType: '交通标志识别',
    questionType: 'single',
    question: '这个标志是何含义？',
    image: '/placeholder.svg?height=150&width=150',
    options: ['禁止通行', '限速标志', '停车让行', '减速让行'],
    correctAnswer: 'C',
    explanation: '此标志为停车让行标志，表示车辆必须在停止线前停车瞭望，确认安全后，才准许通行。',
    difficulty: 'medium',
    favorite: false
  },
  {
    id: 2,
    subject: 'subject1',
    chapter: '第二章',
    type: 'special',
    specialType: '交通标志识别',
    questionType: 'single',
    question: '遇到这种情况的路口，以下做法正确的是什么？',
    options: ['沿左侧车道掉头', '该路口不能掉头', '选择中间车道掉头', '在路口内掉头'],
    correctAnswer: 'B',
    explanation: '在有禁止掉头标志的路口，不能掉头。',
    difficulty: 'easy',
    favorite: true
  },
  {
    id: 3,
    subject: 'subject1',
    chapter: '第一章',
    type: 'normal',
    questionType: 'single',
    question: '驾驶机动车在道路上向左变更车道时如何使用灯光？',
    options: ['不用开启转向灯', '提前开启右转向灯', '提前开启左转向灯', '提前开启近光灯'],
    correctAnswer: 'C',
    explanation: '变更车道前，应提前开启转向灯，向左变道开左转向灯。',
    difficulty: 'easy',
    favorite: false
  },
  {
    id: 4,
    subject: 'subject1',
    chapter: '第二章',
    type: 'normal',
    questionType: 'multiple',
    question: '在这种雨天跟车行驶使用灯光，以下做法正确的是？（多选）',
    options: ['使用远光灯', '不能使用近光灯', '不能使用远光灯', '使用雾灯'],
    correctAnswer: 'CD',
    explanation: '雨天跟车行驶应使用近光灯，不能使用远光灯，以免影响前车驾驶员视线。必要时可以使用雾灯。',
    difficulty: 'medium',
    favorite: false
  },
  {
    id: 5,
    subject: 'subject1',
    chapter: '第一章',
    type: 'special',
    specialType: '违章扣分',
    questionType: 'single',
    question: '驾驶机动车违反道路交通信号灯通行的，一次记几分？',
    options: ['2分', '3分', '6分', '12分'],
    correctAnswer: 'C',
    explanation: '根据《道路交通安全违法行为记分管理办法》规定，驾驶机动车违反道路交通信号灯通行的，一次记6分。',
    difficulty: 'hard',
    favorite: true
  },
  {
    id: 101,
    subject: 'subject4',
    chapter: '第一章',
    type: 'special',
    specialType: '安全驾驶',
    questionType: 'single',
    question: '驾驶机动车遇到这种情况怎样做最安全？',
    image: '/placeholder.svg?height=150&width=150',
    options: ['尽快加速超越前车', '主动减速让行', '鸣喇叭催促前车', '保持原速行驶'],
    correctAnswer: 'B',
    explanation: '遇到特殊车辆执行紧急任务时，应当主动减速让行，确保安全。',
    difficulty: 'easy',
    favorite: false
  }
])

// 过滤条件
const selectedSubject = ref('all')
const selectedType = ref('all')
const selectedChapter = ref('all')
const currentPage = ref(1)
const pageSize = 5

// 章节数据
const chapters = ref([
  '第一章', '第二章', '第三章', '第四章', '第五章'
])

// 计算属性
const filteredQuestions = computed(() => {
  let filtered = questions.value

  // 科目过滤
  if (selectedSubject.value !== 'all') {
    filtered = filtered.filter(q => q.subject === selectedSubject.value)
  }

  // 题型过滤
  if (selectedType.value !== 'all') {
    filtered = filtered.filter(q => q.questionType === selectedType.value)
  }

  // 章节过滤
  if (selectedChapter.value !== 'all') {
    filtered = filtered.filter(q => q.chapter === selectedChapter.value)
  }

  // 分页
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filtered.slice(start, end)
})

const totalPages = computed(() => {
  const filtered = questions.value.filter(q => {
    if (selectedSubject.value !== 'all' && q.subject !== selectedSubject.value) return false
    if (selectedType.value !== 'all' && q.questionType !== selectedType.value) return false
    if (selectedChapter.value !== 'all' && q.chapter !== selectedChapter.value) return false
    return true
  })
  return Math.ceil(filtered.length / pageSize)
})

// 方法
const getTypeText = (type) => {
  const typeMap = {
    single: '单选题',
    multiple: '多选题',
    judge: '判断题'
  }
  return typeMap[type] || type
}

const getDifficultyText = (difficulty) => {
  const difficultyMap = {
    easy: '简单',
    medium: '中等',
    hard: '困难'
  }
  return difficultyMap[difficulty] || difficulty
}

const isCorrectOption = (question, index) => {
  const letter = String.fromCharCode(65 + index)
  if (question.questionType === 'multiple') {
    return question.correctAnswer.includes(letter)
  }
  return question.correctAnswer === letter
}

const formatAnswer = (answer) => {
  if (Array.isArray(answer)) {
    return answer.join('')
  }
  return answer
}

const addQuestion = () => {
  alert('添加题目功能')
  // TODO: 实现添加题目功能
}

const editQuestion = (question) => {
  alert(`编辑题目: ${question.question}`)
  // TODO: 实现编辑功能
}

const deleteQuestion = (question) => {
  if (confirm(`确定要删除题目 "${question.question.substring(0, 50)}..." 吗？`)) {
    const index = questions.value.findIndex(q => q.id === question.id)
    if (index !== -1) {
      questions.value.splice(index, 1)
    }
  }
}

const toggleFavorite = (question) => {
  question.favorite = !question.favorite
  // TODO: 调用API更新收藏状态
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 当过滤条件变化时重置页码
const resetPage = () => {
  currentPage.value = 1
}

// 监听过滤条件变化
watch([selectedSubject, selectedType, selectedChapter], resetPage)

onMounted(() => {
  // TODO: 从API加载数据
})
</script>

<style scoped>
.question-management {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.management-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-controls {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  background: white;
  color: #262626;
  font-size: 14px;
  cursor: pointer;
  min-width: 120px;
}

.filter-select:focus {
  outline: none;
  border-color: #1890ff;
}

.btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-primary {
  background: #1890ff;
  color: white;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-icon {
  font-size: 16px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-card {
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 24px;
  transition: all 0.3s;
}

.question-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.question-id {
  font-size: 12px;
  color: #8c8c8c;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.question-type {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.question-type.single {
  background: #e6f7ff;
  color: #1890ff;
}

.question-type.multiple {
  background: #f6ffed;
  color: #52c41a;
}

.question-type.judge {
  background: #fff7e6;
  color: #fa8c16;
}

.question-chapter {
  font-size: 12px;
  color: #595959;
  background: #fafafa;
  padding: 4px 8px;
  border-radius: 4px;
}

.question-actions {
  display: flex;
  gap: 8px;
}

.btn-icon-small {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background: #f5f5f5;
  color: #595959;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  font-size: 14px;
}

.btn-icon-small:hover {
  background: #e8e8e8;
  color: #262626;
}

.btn-icon-small.btn-danger {
  color: #ff4d4f;
}

.btn-icon-small.btn-danger:hover {
  background: #fff2f0;
  color: #ff7875;
}

.question-content {
  margin-bottom: 16px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  color: #262626;
  margin-bottom: 16px;
}

.question-image {
  margin-bottom: 16px;
}

.question-image img {
  max-width: 200px;
  height: auto;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  background: #fafafa;
  transition: all 0.3s;
}

.option-item.correct {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.option-label {
  font-weight: 600;
  color: #262626;
  min-width: 20px;
}

.option-text {
  flex: 1;
  color: #595959;
}

.question-footer {
  padding-top: 16px;
  border-top: 1px dashed #f0f0f0;
}

.answer-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.correct-answer {
  font-size: 14px;
  color: #52c41a;
  font-weight: 500;
}

.difficulty {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.difficulty.easy {
  background: #f6ffed;
  color: #52c41a;
}

.difficulty.medium {
  background: #fff7e6;
  color: #fa8c16;
}

.difficulty.hard {
  background: #fff2f0;
  color: #ff4d4f;
}

.question-explanation {
  font-size: 14px;
  line-height: 1.6;
  color: #595959;
  background: #fafafa;
  padding: 12px;
  border-radius: 6px;
  border-left: 4px solid #1890ff;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.page-info {
  font-size: 14px;
  color: #595959;
}

.btn-secondary {
  background: #f5f5f5;
  color: #262626;
  border: 1px solid #d9d9d9;
  padding: 8px 16px;
}

.btn-secondary:hover {
  background: #e8e8e8;
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .management-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-controls {
    flex-direction: column;
  }

  .question-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .question-actions {
    align-self: flex-end;
  }
}
</style>