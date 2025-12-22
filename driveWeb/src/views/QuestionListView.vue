<template>
  <div class="question-bank-page">
    <!-- 顶部筛选栏 -->
    <div class="filter-section">
      <div class="filter-grid">
        <!-- 科目选择 -->
        <div class="filter-item">
          <div class="filter-label">选择科目</div>
          <el-select
              v-model="filters.subject"
              placeholder="全部科目"
              @change="handleFilterChange"
              clearable
              class="filter-select"
          >
            <el-option label="全部科目" value="" />
            <el-option label="科目一" value="C1C2科目一" />
            <el-option label="科目四" value="C1C2科目四" />
            <el-option label="A1/A3科目一" value="A1A3科目一" />
            <el-option label="B1/B2科目一" value="B1B2科目一" />
          </el-select>
        </div>

        <!-- 章节选择 -->
        <div class="filter-item">
          <div class="filter-label">选择章节</div>
          <el-select
              v-model="filters.chapter"
              placeholder="全部章节"
              @change="handleFilterChange"
              clearable
              class="filter-select"
          >
            <el-option label="全部章节" value="" />
            <el-option
                v-for="chapter in uniqueChapters"
                :key="chapter"
                :label="chapter"
                :value="chapter"
            />
          </el-select>
        </div>

        <!-- 题型选择 -->
        <div class="filter-item">
          <div class="filter-label">选择题型</div>
          <el-select
              v-model="filters.type"
              placeholder="全部题型"
              @change="handleFilterChange"
              clearable
              class="filter-select"
          >
            <el-option label="全部题型" value="" />
            <el-option label="单选题" value="单选题" />
            <el-option label="多选题" value="多选题" />
            <el-option label="判断题" value="判断题" />
          </el-select>
        </div>

        <!-- 难度选择 -->
        <div class="filter-item">
          <div class="filter-label">选择难度</div>
          <el-select
              v-model="filters.difficulty"
              placeholder="全部难度"
              @change="handleFilterChange"
              clearable
              class="filter-select"
          >
            <el-option label="全部难度" value="" />
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </div>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <el-input
            v-model="searchQuery"
            placeholder="搜索题目内容..."
            clearable
            @input="handleSearchInput"
            @clear="handleSearchClear"
            class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 题目统计和操作按钮 -->
    <div class="action-section">
      <div class="action-header">
        <div>
          <h2>题库中心</h2>
          <p class="question-count">
            共 <span class="count-number">{{ filteredQuestions.length }}</span> 道题目
            <span v-if="activeFilters" class="filter-tag">(已筛选)</span>
          </p>
        </div>
        <div class="action-buttons">
          <el-button
              type="primary"
              @click="startSequentialPractice"
              class="action-btn"
          >
            <el-icon><List /></el-icon>
            顺序练习
          </el-button>
          <el-button
              type="danger"
              @click="startWrongQuestions"
              class="action-btn"
          >
            <el-icon><Warning /></el-icon>
            错题练习
          </el-button>
          <el-button
              type="warning"
              @click="startFavoritePractice"
              class="action-btn"
          >
            <el-icon><Star /></el-icon>
            收藏练习
          </el-button>
          <el-button
              type="success"
              @click="startMockExam"
              class="action-btn"
          >
            <el-icon><Checked /></el-icon>
            模拟考试
          </el-button>
        </div>
      </div>
    </div>

    <!-- 题目列表 -->
    <div class="questions-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>加载题目中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredQuestions.length === 0" class="empty-state">
        <el-icon class="empty-icon"><Document /></el-icon>
        <p class="empty-text">未找到相关题目</p>
        <p class="empty-hint">尝试调整筛选条件或搜索关键词</p>
        <el-button @click="resetFilters" plain>
          重置筛选条件
        </el-button>
      </div>

      <!-- 题目卡片 -->
      <div v-else class="questions-list">
        <div
            v-for="question in paginatedQuestions"
            :key="question.question_id"
            class="question-card"
        >
          <div class="question-header">
            <div class="question-info">
              <div class="question-number">
                <span>{{ question.question_no }}</span>
              </div>

              <!-- 题型标签 -->
              <el-tag
                  :type="getQuestionTypeTag(question.type)"
                  class="type-tag"
              >
                {{ question.type }}
              </el-tag>

              <!-- 难度标签 -->
              <el-tag
                  :type="getDifficultyTag(question.difficulty)"
                  class="difficulty-tag"
              >
                {{ question.difficulty || '未知' }}
              </el-tag>

              <!-- 科目标签 -->
              <el-tag class="subject-tag">
                {{ formatSubject(question.subject) }}
              </el-tag>
            </div>

            <div class="question-actions">
              <!-- 收藏按钮 -->
              <el-button
                  @click="toggleFavorite(question.question_id)"
                  :type="isFavorite(question.question_id) ? 'warning' : 'info'"
                  :icon="isFavorite(question.question_id) ? StarFilled : Star"
                  circle
                  size="small"
                  :title="isFavorite(question.question_id) ? '取消收藏' : '收藏题目'"
              />

              <!-- 错题标记 -->
              <el-button
                  @click="toggleWrongMark(question.question_id)"
                  :type="isWrongQuestion(question.question_id) ? 'danger' : 'info'"
                  :icon="isWrongQuestion(question.question_id) ? WarningFilled : Warning"
                  circle
                  size="small"
                  :title="isWrongQuestion(question.question_id) ? '标记为已掌握' : '标记为错题'"
              />

              <!-- 更多操作 -->
              <el-dropdown @command="handleMoreAction($event, question)">
                <el-button circle size="small">
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="practice">从此题开始练习</el-dropdown-item>
                    <el-dropdown-item command="explanation">查看解析</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <!-- 题目内容 -->
          <div class="question-content">
            <h3 class="question-title">{{ question.content }}</h3>

            <!-- 图片展示 -->
            <div v-if="question.img" class="question-image">
              <el-image
                  :src="question.img"
                  :preview-src-list="[question.img]"
                  fit="contain"
                  class="image-preview"
              />
            </div>

            <!-- 选项 -->
            <div class="question-options">
              <div
                  v-for="(optionValue, optionKey) in question.option"
                  :key="optionKey"
                  class="option-item"
                  :class="{ 'correct-option': optionKey === question.option.answer }"
              >
                <span class="option-label" :class="{ 'correct-label': optionKey === question.option.answer }">
                  {{ optionKey }}.
                </span>
                <span class="option-text">{{ optionValue }}</span>
                <span v-if="optionKey === question.option.answer" class="correct-mark">
                  <el-icon><Check /></el-icon>
                </span>
              </div>
            </div>
          </div>

          <!-- 题目底部信息 -->
          <div class="question-footer">
            <div class="question-meta">
              <span>{{ question.chapter }}</span>
              <span class="separator">•</span>
              <span>{{ formatSubject(question.subject) }}</span>
            </div>
            <div class="question-id">
              ID: {{ question.question_id }}
            </div>
          </div>

          <!-- 解析区域 -->
          <el-collapse-transition>
            <div v-show="showExplanationId === question.question_id" class="explanation-section">
              <div class="explanation-content">
                <h4 class="explanation-title">
                  <el-icon><InfoFilled /></el-icon>
                  题目解析
                </h4>
                <p class="explanation-text" v-html="formatExplanation(question.explain)"></p>

                <!-- 相关知识点 -->
                <div v-if="question.relatedTopics" class="related-topics">
                  <h5 class="topics-title">相关知识点：</h5>
                  <div class="topics-list">
                    <el-tag
                        v-for="topic in question.relatedTopics"
                        :key="topic"
                        type="info"
                        size="small"
                        class="topic-tag"
                    >
                      {{ topic }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </el-collapse-transition>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredQuestions.length > 0 && !loading" class="pagination-section">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="filteredQuestions.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search,
  List,
  Warning,
  Star,
  Checked,
  Loading,
  Document,
  More,
  Check,
  InfoFilled,
  StarFilled,
  WarningFilled
} from '@element-plus/icons-vue'

const router = useRouter()

/* ========================
   基础状态
======================== */
const questions = ref([])
const loading = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const showExplanationId = ref(null)

/* ========================
   筛选条件
======================== */
const filters = ref({
  subject: '',
  chapter: '',
  type: '',
  difficulty: ''
})

/* ========================
   本地用户行为
======================== */
const favoriteQuestions = ref(
    JSON.parse(localStorage.getItem('favoriteQuestions')) || []
)
const wrongQuestions = ref(
    JSON.parse(localStorage.getItem('wrongQuestions')) || []
)

/* ========================
   从后端加载题目
======================== */
const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await fetch('http://124.220.99.159:8081/api/question/list')
    const data = await res.json()

    // 🔥 数据标准化（关键）
    questions.value = data.map(q => ({
      question_id: q.questionId,
      question_no: q.questionNo,
      type: q.type,
      content: q.content,
      chapter: q.chapter,
      subject: q.subject,
      img: q.img,
      explain: q.explain,
      difficulty: q.difficuity, // ⚠️ 后端拼写
      option: JSON.parse(q.option) // ⚠️ option 是 JSON 字符串
    }))
  } catch (err) {
    ElMessage.error('题目加载失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

/* ========================
   计算属性
======================== */
const uniqueChapters = computed(() => {
  return [...new Set(questions.value.map(q => q.chapter))].sort()
})

const filteredQuestions = computed(() => {
  let result = questions.value

  if (filters.value.subject)
    result = result.filter(q => q.subject === filters.value.subject)

  if (filters.value.chapter)
    result = result.filter(q => q.chapter === filters.value.chapter)

  if (filters.value.type)
    result = result.filter(q => q.type === filters.value.type)

  if (filters.value.difficulty)
    result = result.filter(q => q.difficulty === filters.value.difficulty)

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    result = result.filter(item =>
        item.content.toLowerCase().includes(q) ||
        item.chapter.toLowerCase().includes(q)
    )
  }

  return result
})

const paginatedQuestions = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredQuestions.value.slice(start, start + pageSize.value)
})

const activeFilters = computed(() => {
  return Object.values(filters.value).some(v => v) || searchQuery.value
})

/* ========================
   UI 辅助方法
======================== */
const getQuestionTypeTag = (type) => {
  if (type === '单选题') return 'primary'
  if (type === '多选题') return 'warning'
  if (type === '判断题') return 'success'
  return 'info'
}

const getDifficultyTag = (difficulty) => {
  if (difficulty === '简单') return 'success'
  if (difficulty === '中等') return 'warning'
  if (difficulty === '困难') return 'danger'
  return 'info'
}

const formatSubject = (subject) => {
  return subject.replace('C1C2', '')
}

const formatExplanation = (explain) => {
  return explain ? explain.replace(/\n/g, '<br>') : '暂无解析'
}

/* ========================
   收藏 / 错题
======================== */
const isFavorite = id => favoriteQuestions.value.includes(id)
const isWrongQuestion = id => wrongQuestions.value.includes(id)

const toggleFavorite = id => {
  const idx = favoriteQuestions.value.indexOf(id)
  idx > -1 ? favoriteQuestions.value.splice(idx, 1) : favoriteQuestions.value.push(id)
  localStorage.setItem('favoriteQuestions', JSON.stringify(favoriteQuestions.value))
}

const toggleWrongMark = id => {
  const idx = wrongQuestions.value.indexOf(id)
  idx > -1 ? wrongQuestions.value.splice(idx, 1) : wrongQuestions.value.push(id)
  localStorage.setItem('wrongQuestions', JSON.stringify(wrongQuestions.value))
}

/* ========================
   操作事件
======================== */
const handleFilterChange = () => currentPage.value = 1
const handleSearchInput = () => currentPage.value = 1
const handleSearchClear = () => currentPage.value = 1

const resetFilters = () => {
  filters.value = { subject: '', chapter: '', type: '', difficulty: '' }
  searchQuery.value = ''
  currentPage.value = 1
}

const showExplanation = q => {
  showExplanationId.value =
      showExplanationId.value === q.question_id ? null : q.question_id
}

const handleMoreAction = (cmd, q) => {
  if (cmd === 'explanation') showExplanation(q)
  if (cmd === 'practice')
    router.push({ name: 'Practice', query: { startFrom: q.question_id } })
}

/* ========================
   导航
======================== */
const startSequentialPractice = () =>
    router.push({ name: 'Practice', query: { mode: 'sequential' } })

const startWrongQuestions = () => {
  if (!wrongQuestions.value.length)
    return ElMessage.warning('暂无错题')
  router.push({ name: 'Practice', query: { mode: 'wrong' } })
}

const startFavoritePractice = () => {
  if (!favoriteQuestions.value.length)
    return ElMessage.warning('暂无收藏')
  router.push({ name: 'Practice', query: { mode: 'favorite' } })
}

const startMockExam = () => router.push({ name: 'ExamIntro' })

/* ========================
   分页
======================== */
const handleSizeChange = size => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = page => (currentPage.value = page)

/* ========================
   生命周期
======================== */
onMounted(loadQuestions)
</script>

<style scoped>
.question-bank-page {
  padding: 24px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 64px);
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

@media (max-width: 1200px) {
  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
}

.filter-item {
  display: flex;
  flex-direction: column;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.filter-select {
  flex: 1;
}

.search-section {
  margin-top: 16px;
}

.search-input {
  width: 100%;
}

/* 操作区域 */
.action-section {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.action-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.question-count {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.count-number {
  font-weight: 600;
  color: #409eff;
}

.filter-tag {
  color: #f56c6c;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 题目列表区域 */
.questions-section {
  margin-bottom: 24px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  background: white;
  border-radius: 12px;
}

.loading-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  background: white;
  border-radius: 12px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #dcdfe6;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 14px;
  color: #909399;
  margin-bottom: 16px;
}

/* 题目卡片 */
.questions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.question-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.question-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.question-number {
  width: 40px;
  height: 40px;
  background: #ecf5ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #409eff;
}

.type-tag, .difficulty-tag, .subject-tag {
  font-size: 12px;
}

.question-actions {
  display: flex;
  gap: 8px;
}

/* 题目内容 */
.question-content {
  margin-bottom: 20px;
}

.question-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
  line-height: 1.6;
}

.question-image {
  margin: 16px 0;
}

.image-preview {
  max-width: 400px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  cursor: pointer;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
  background: #f6f6f6;
  transition: all 0.3s;
}

.option-item:hover {
  background: #f0f9ff;
}

.option-item.correct-option {
  background: #f0f9eb;
  border-left: 4px solid #67c23a;
}

.option-label {
  font-weight: 600;
  margin-right: 12px;
  min-width: 24px;
  color: #606266;
}

.option-item.correct-option .option-label {
  color: #67c23a;
}

.option-text {
  flex: 1;
  color: #303133;
  line-height: 1.5;
}

.correct-mark {
  margin-left: 8px;
  color: #67c23a;
}

/* 题目底部 */
.question-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 14px;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.separator {
  color: #dcdfe6;
}

/* 解析区域 */
.explanation-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.explanation-content {
  background: #ecf5ff;
  border-radius: 8px;
  padding: 16px;
}

.explanation-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 12px;
}

.explanation-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
}

.related-topics {
  margin-top: 12px;
}

.topics-title {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.topics-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.topic-tag {
  margin-right: 4px;
}

/* 分页 */
.pagination-section {
  background: white;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  justify-content: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pagination {
  width: 100%;
  display: flex;
  justify-content: center;
}
</style>