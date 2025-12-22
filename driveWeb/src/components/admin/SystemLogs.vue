<template>
  <div class="system-log-container">
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
      <h3 class="text-xl font-bold text-gray-800 mb-6">系统日志</h3>

      <!-- 搜索和筛选区域 -->
      <div class="flex flex-col md:flex-row gap-4 mb-6">
        <div class="relative flex-1">
          <span class="iconify absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" data-icon="carbon:search"></span>
          <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索日志内容或用户名..."
              class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
          />
        </div>

        <div class="flex gap-2">
          <button
              @click="loadLogs"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center gap-2"
          >
            <span class="iconify" data-icon="carbon:refresh"></span>
            刷新
          </button>
        </div>
      </div>

      <!-- 日志表格 -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
          <tr class="border-b border-gray-200">
            <th class="text-left py-3 px-4 text-gray-600 font-medium">时间</th>
            <th class="text-left py-3 px-4 text-gray-600 font-medium">用户</th>
            <th class="text-left py-3 px-4 text-gray-600 font-medium">日志内容</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="loading">
            <td colspan="3" class="py-8 text-center text-gray-500">
              <span class="iconify inline-block text-3xl text-gray-300 mb-2" data-icon="carbon:progress-bar-round"></span>
              <p>加载中...</p>
            </td>
          </tr>

          <tr v-else-if="filteredLogs.length === 0">
            <td colspan="3" class="py-8 text-center text-gray-500">
              <span class="iconify inline-block text-3xl text-gray-300 mb-2" data-icon="carbon:document-blank"></span>
              <p>暂无日志数据</p>
            </td>
          </tr>

          <tr
              v-else
              v-for="log in filteredLogs"
              :key="log.logId"
              class="border-b border-gray-100 hover:bg-gray-50 transition-colors"
          >
            <td class="py-3 px-4">
              <div class="text-gray-600">{{ formatDateTime(log.createTime) }}</div>
            </td>
            <td class="py-3 px-4">
              <div class="font-medium text-gray-800">{{ log.username || `用户${log.userId}` }}</div>
            </td>
            <td class="py-3 px-4">
              <div class="text-gray-700">{{ log.content }}</div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div v-if="filteredLogs.length > 0" class="flex items-center justify-between mt-6 pt-6 border-t border-gray-200">
        <div class="text-sm text-gray-600">
          显示 {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, total) }} 条，共 {{ total }} 条
        </div>
        <div class="flex items-center gap-2">
          <button
              @click="prevPage"
              :disabled="currentPage === 1"
              class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            上一页
          </button>
          <span class="px-3 py-1 text-gray-700">{{ currentPage }} / {{ totalPages }}</span>
          <button
              @click="nextPage"
              :disabled="currentPage === totalPages"
              class="px-3 py-1 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { formatDateTime } from '@/utils/dateUtils' // 假设有这个工具函数

// 日志数据模型
const logData = ref([])
const loading = ref(true)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 计算属性
const filteredLogs = computed(() => {
  if (!searchQuery.value) {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return logData.value.slice(start, end)
  }

  const search = searchQuery.value.toLowerCase()
  return logData.value.filter(log =>
      (log.content && log.content.toLowerCase().includes(search)) ||
      (log.username && log.username.toLowerCase().includes(search))
  ).slice(0, pageSize.value)
})

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

// 方法
const loadLogs = async () => {
  loading.value = true
  try {
    // 模拟API调用
    const response = await fetch('/api/logs')
    const data = await response.json()
    logData.value = data
    total.value = data.length
  } catch (error) {
    console.error('加载日志失败:', error)
    // 使用模拟数据作为备选
    logData.value = getMockLogs()
    total.value = logData.value.length
  } finally {
    loading.value = false
  }
}

const getMockLogs = () => {
  return [
    {
      logId: 1,
      content: '管理员登录系统',
      createTime: '2024-03-20T14:30:25',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 2,
      content: '修改考试配置',
      createTime: '2024-03-20T13:45:33',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 3,
      content: '添加新用户：张三',
      createTime: '2024-03-20T13:20:15',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 4,
      content: '完成科目一模拟考试，得分：95分',
      createTime: '2024-03-20T14:30:25',
      userId: 2,
      username: '张三'
    },
    {
      logId: 5,
      content: '开始章节练习：交通信号',
      createTime: '2024-03-20T14:15:10',
      userId: 3,
      username: '李四'
    },
    {
      logId: 6,
      content: '收藏题目：第25题',
      createTime: '2024-03-20T12:05:45',
      userId: 4,
      username: '王五'
    },
    {
      logId: 7,
      content: '用户注册',
      createTime: '2024-03-19T09:30:22',
      userId: 5,
      username: '赵六'
    },
    {
      logId: 8,
      content: '修改个人信息',
      createTime: '2024-03-19T16:45:18',
      userId: 2,
      username: '张三'
    },
    {
      logId: 9,
      content: '删除试题：第102题',
      createTime: '2024-03-19T11:20:33',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 10,
      content: '完成科目四模拟考试，得分：88分',
      createTime: '2024-03-18T15:10:47',
      userId: 3,
      username: '李四'
    },
    {
      logId: 11,
      content: '查看错题集',
      createTime: '2024-03-18T10:25:55',
      userId: 4,
      username: '王五'
    },
    {
      logId: 12,
      content: '导出学习报告',
      createTime: '2024-03-17T17:30:12',
      userId: 2,
      username: '张三'
    },
    {
      logId: 13,
      content: '重置用户密码',
      createTime: '2024-03-17T14:15:08',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 14,
      content: '上传试题图片',
      createTime: '2024-03-16T11:40:29',
      userId: 1,
      username: 'Admin'
    },
    {
      logId: 15,
      content: '模拟考试中断',
      createTime: '2024-03-16T09:55:16',
      userId: 5,
      username: '赵六'
    }
  ]
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

// 生命周期
onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.system-log-container {
  min-height: calc(100vh - 8rem);
}

/* 表格样式优化 */
table {
  border-collapse: separate;
  border-spacing: 0;
}

thead th {
  background-color: #f9fafb;
  font-weight: 600;
}

tbody tr:last-child {
  border-bottom: none;
}

/* 搜索框图标对齐 */
.iconify {
  vertical-align: middle;
}

/* 分页按钮样式 */
.pagination-btn {
  min-width: 2.5rem;
}

/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.iconify[data-icon="carbon:progress-bar-round"] {
  animation: spin 1s linear infinite;
}
</style>