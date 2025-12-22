<template>
  <div class="user-management">
    <div class="table-header">
      <div class="header-left">
        <h3>用户列表</h3>
        <div class="search-box">
          <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索用户名或姓名..."
              @input="searchUsers"
          />
          <span class="search-icon">🔍</span>
        </div>
      </div>
      <div class="header-right">
        <button class="btn btn-primary" @click="addUser">
          <span class="btn-icon">➕</span>
          添加用户
        </button>
        <button class="btn btn-secondary" @click="exportData">
          <span class="btn-icon">📥</span>
          导出数据
        </button>
      </div>
    </div>

    <div class="table-container">
      <table class="user-table">
        <thead>
        <tr>
          <th>
            <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" />
          </th>
          <th @click="sortBy('username')">
            用户名
            <span class="sort-icon">{{ getSortIcon('username') }}</span>
          </th>
          <th @click="sortBy('realname')">
            真实姓名
            <span class="sort-icon">{{ getSortIcon('realname') }}</span>
          </th>
          <th @click="sortBy('userType')">
            用户类型
            <span class="sort-icon">{{ getSortIcon('userType') }}</span>
          </th>
          <th @click="sortBy('registerDate')">
            注册时间
            <span class="sort-icon">{{ getSortIcon('registerDate') }}</span>
          </th>
          <th>学习进度</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="user in paginatedUsers" :key="user.id">
          <td>
            <input type="checkbox" v-model="selectedUsers" :value="user.id" />
          </td>
          <td>{{ user.username }}</td>
          <td>{{ user.realname }}</td>
          <td>
              <span :class="['user-type', user.userType]">
                {{ user.userType === 'student' ? '学员' : '管理员' }}
              </span>
          </td>
          <td>{{ formatDate(user.registerDate) }}</td>
          <td>
            <div class="progress-container">
              <div class="progress-bar">
                <div
                    class="progress-fill"
                    :style="{ width: user.progress + '%' }"
                ></div>
              </div>
              <span class="progress-text">{{ user.progress }}%</span>
            </div>
          </td>
          <td>
              <span :class="['status', user.status]">
                {{ user.status === 'active' ? '活跃' : '禁用' }}
              </span>
          </td>
          <td>
            <div class="action-buttons">
              <button class="btn-icon-small" @click="editUser(user)" title="编辑">
                ✏️
              </button>
              <button
                  class="btn-icon-small"
                  @click="toggleUserStatus(user)"
                  :title="user.status === 'active' ? '禁用' : '启用'"
              >
                {{ user.status === 'active' ? '⛔' : '✅' }}
              </button>
              <button class="btn-icon-small btn-danger" @click="deleteUser(user)" title="删除">
                🗑️
              </button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页控件 -->
    <div class="pagination">
      <div class="pagination-info">
        显示 {{ startItem }}-{{ endItem }} 条，共 {{ filteredUsers.length }} 条
      </div>
      <div class="pagination-controls">
        <button
            class="pagination-btn"
            @click="prevPage"
            :disabled="currentPage === 1"
        >
          上一页
        </button>
        <div class="page-numbers">
          <button
              v-for="page in visiblePages"
              :key="page"
              class="page-number"
              :class="{ active: page === currentPage }"
              @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </div>
        <button
            class="pagination-btn"
            @click="nextPage"
            :disabled="currentPage === totalPages"
        >
          下一页
        </button>
      </div>
      <div class="page-size">
        <select v-model="pageSize" @change="resetPagination">
          <option value="10">10 条/页</option>
          <option value="20">20 条/页</option>
          <option value="50">50 条/页</option>
        </select>
      </div>
    </div>

    <!-- 批量操作 -->
    <div v-if="selectedUsers.length > 0" class="batch-actions">
      <span>已选择 {{ selectedUsers.length }} 个用户</span>
      <div class="batch-buttons">
        <button class="btn btn-secondary" @click="batchEnable">
          <span class="btn-icon">✅</span>
          批量启用
        </button>
        <button class="btn btn-secondary" @click="batchDisable">
          <span class="btn-icon">⛔</span>
          批量禁用
        </button>
        <button class="btn btn-danger" @click="batchDelete">
          <span class="btn-icon">🗑️</span>
          批量删除
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// 临时写死的数据
const users = ref([
  { id: 1, username: 'admin', realname: '管理员', userType: 'admin', registerDate: '2024-01-01', progress: 100, status: 'active' },
  { id: 2, username: 'student1', realname: '张三', userType: 'student', registerDate: '2024-02-15', progress: 85, status: 'active' },
  { id: 3, username: 'student2', realname: '李四', userType: 'student', registerDate: '2024-03-10', progress: 92, status: 'active' },
  { id: 4, username: 'student3', realname: '王五', userType: 'student', registerDate: '2024-03-12', progress: 76, status: 'inactive' },
  { id: 5, username: 'student4', realname: '赵六', userType: 'student', registerDate: '2024-03-18', progress: 68, status: 'active' },
  { id: 6, username: 'student5', realname: '孙七', userType: 'student', registerDate: '2024-03-20', progress: 95, status: 'active' },
  { id: 7, username: 'teacher1', realname: '周老师', userType: 'teacher', registerDate: '2024-02-01', progress: 100, status: 'active' },
  { id: 8, username: 'student6', realname: '吴八', userType: 'student', registerDate: '2024-03-22', progress: 88, status: 'inactive' },
  { id: 9, username: 'student7', realname: '郑九', userType: 'student', registerDate: '2024-03-25', progress: 72, status: 'active' },
  { id: 10, username: 'student8', realname: '王十', userType: 'student', registerDate: '2024-03-28', progress: 81, status: 'active' }
])

const searchQuery = ref('')
const selectedUsers = ref([])
const selectAll = ref(false)
const sortField = ref('registerDate')
const sortDirection = ref('desc')
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const filteredUsers = computed(() => {
  let filtered = users.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(user =>
        user.username.toLowerCase().includes(query) ||
        user.realname.toLowerCase().includes(query)
    )
  }

  // 排序
  filtered = [...filtered].sort((a, b) => {
    let aVal = a[sortField.value]
    let bVal = b[sortField.value]

    if (sortField.value === 'registerDate') {
      aVal = new Date(aVal)
      bVal = new Date(bVal)
    }

    if (aVal < bVal) return sortDirection.value === 'asc' ? -1 : 1
    if (aVal > bVal) return sortDirection.value === 'asc' ? 1 : -1
    return 0
  })

  return filtered
})

const totalPages = computed(() => Math.ceil(filteredUsers.value.length / pageSize.value))
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
})
const startItem = computed(() => (currentPage.value - 1) * pageSize.value + 1)
const endItem = computed(() => Math.min(currentPage.value * pageSize.value, filteredUsers.value.length))
const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2))
  let end = Math.min(totalPages.value, start + maxVisible - 1)

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// 方法
const searchUsers = () => {
  currentPage.value = 1
}

const sortBy = (field) => {
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortField.value = field
    sortDirection.value = 'asc'
  }
}

const getSortIcon = (field) => {
  if (sortField.value !== field) return '↕️'
  return sortDirection.value === 'asc' ? '↑' : '↓'
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedUsers.value = paginatedUsers.value.map(user => user.id)
  } else {
    selectedUsers.value = []
  }
}

const editUser = (user) => {
  alert(`编辑用户: ${user.realname}`)
  // TODO: 实现编辑功能
}

const toggleUserStatus = (user) => {
  user.status = user.status === 'active' ? 'inactive' : 'active'
  // TODO: 调用API更新状态
}

const deleteUser = (user) => {
  if (confirm(`确定要删除用户 ${user.realname} 吗？`)) {
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value.splice(index, 1)
      // 从选中列表中移除
      const selectedIndex = selectedUsers.value.indexOf(user.id)
      if (selectedIndex !== -1) {
        selectedUsers.value.splice(selectedIndex, 1)
      }
    }
  }
}

const addUser = () => {
  alert('添加用户功能')
  // TODO: 实现添加用户功能
}

const exportData = () => {
  alert('导出数据功能')
  // TODO: 实现导出功能
}

// 分页方法
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

const goToPage = (page) => {
  currentPage.value = page
}

const resetPagination = () => {
  currentPage.value = 1
}

// 批量操作方法
const batchEnable = () => {
  users.value.forEach(user => {
    if (selectedUsers.value.includes(user.id)) {
      user.status = 'active'
    }
  })
  selectedUsers.value = []
  selectAll.value = false
}

const batchDisable = () => {
  users.value.forEach(user => {
    if (selectedUsers.value.includes(user.id)) {
      user.status = 'inactive'
    }
  })
  selectedUsers.value = []
  selectAll.value = false
}

const batchDelete = () => {
  if (confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`)) {
    users.value = users.value.filter(user => !selectedUsers.value.includes(user.id))
    selectedUsers.value = []
    selectAll.value = false
  }
}

onMounted(() => {
  // TODO: 从API加载数据
})
</script>

<style scoped>
.user-management {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.header-left h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #262626;
}

.search-box {
  position: relative;
  width: 300px;
}

.search-box input {
  width: 100%;
  padding: 8px 36px 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #8c8c8c;
}

.header-right {
  display: flex;
  gap: 12px;
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
}

.btn-primary {
  background: #1890ff;
  color: white;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-secondary {
  background: #f5f5f5;
  color: #262626;
  border: 1px solid #d9d9d9;
}

.btn-secondary:hover {
  background: #e8e8e8;
}

.btn-icon {
  font-size: 16px;
}

.table-container {
  overflow-x: auto;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background: white;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1000px;
}

.user-table thead {
  background: #fafafa;
}

.user-table th {
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #262626;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  user-select: none;
  position: relative;
}

.user-table th:hover {
  background: #f5f5f5;
}

.sort-icon {
  margin-left: 4px;
  font-size: 12px;
}

.user-table tbody tr {
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}

.user-table tbody tr:hover {
  background: #fafafa;
}

.user-table td {
  padding: 16px;
  color: #595959;
}

.user-type {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.user-type.student {
  background: #e6f7ff;
  color: #1890ff;
}

.user-type.admin {
  background: #f6ffed;
  color: #52c41a;
}

.user-type.teacher {
  background: #fff7e6;
  color: #fa8c16;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #52c41a;
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 12px;
  color: #8c8c8c;
  min-width: 40px;
}

.status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status.active {
  background: #f6ffed;
  color: #52c41a;
}

.status.inactive {
  background: #fff2f0;
  color: #ff4d4f;
}

.action-buttons {
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

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 16px;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  color: #8c8c8c;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-btn {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  color: #262626;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 6px;
  color: #262626;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.page-number:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.page-number.active {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
}

.page-size select {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  background: white;
  color: #262626;
  font-size: 14px;
  cursor: pointer;
}

.page-size select:focus {
  outline: none;
  border-color: #1890ff;
}

.batch-actions {
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px 24px;
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.batch-actions span {
  font-weight: 500;
  color: #262626;
}

.batch-buttons {
  display: flex;
  gap: 12px;
}

.btn-danger {
  background: #ff4d4f;
  color: white;
}

.btn-danger:hover {
  background: #ff7875;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .table-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-left {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }

  .header-right {
    justify-content: flex-end;
  }

  .pagination {
    flex-direction: column;
    align-items: stretch;
  }

  .batch-actions {
    flex-direction: column;
    gap: 16px;
  }

  .batch-buttons {
    flex-wrap: wrap;
  }
}
</style>