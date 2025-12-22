<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- Logo和标题 -->
        <div class="login-header">
          <div class="logo">
            <i>🚗</i>
          </div>
          <h1>驾驶大师</h1>
          <p>专业的驾照考试学习平台</p>
        </div>

        <!-- 登录表单 -->
        <form class="login-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label>用户名</label>
            <input
                type="text"
                v-model="formData.username"
                placeholder="请输入用户名"
                required
                :disabled="loading"
            >
          </div>

          <div class="form-group">
            <label>密码</label>
            <input
                type="password"
                v-model="formData.password"
                placeholder="请输入密码"
                required
                :disabled="loading"
            >
          </div>

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button
              type="submit"
              class="btn-login"
              :disabled="loading"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </button>

          <div class="register-link">
            <span>还没有账号？</span>
            <router-link to="/register">立刻注册</router-link>
          </div>

          <div class="test-accounts">
            <p>测试账号：admin / student 密码：123456</p>
          </div>

          <!-- 统计数据 -->
          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">题库数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">95%</div>
              <div class="stat-label">通过率</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">10万+</div>
              <div class="stat-label">用户数</div>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI, setAuth } from '@/api'
import { ElMessage } from 'element-plus'
import Cookies from "js-cookie";

const router = useRouter()

// 响应式数据
const loading = ref(false)
const errorMessage = ref('')
const formData = reactive({
  username: '',
  password: ''
})

// 页面加载时检查是否已登录
onMounted(() => {
  const token = Cookies.get('token')
  if (token) {
    // 如果已有token，直接跳转到相应页面
    checkUserRoleAndRedirect(token)
  }
})

// 检查用户角色并跳转
const checkUserRoleAndRedirect = async (token) => {
  try {
    const response = await userAPI.getUserInfo(token)
    if (response.role === true) {
      router.push('/admin')
    } else {
      router.push('/home')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 处理登录
const handleLogin = async () => {
  // 验证表单
  if (!formData.username.trim()) {
    errorMessage.value = '请输入用户名'
    return
  }

  if (!formData.password.trim()) {
    errorMessage.value = '请输入密码'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    // 调用登录API
    const response = await userAPI.login({
      username: formData.username.trim(),
      password: formData.password.trim()
    })

    // 设置认证信息到Cookie
    setAuth(response.token, {
      username: formData.username,
      role: response.role // true为管理员，false为普通用户
    })

    // 显示成功消息
    ElMessage.success(response.msg || '登录成功')

    // 根据角色跳转到不同页面
    if (response.role === true) {
      router.push('/admin')
    } else {
      router.push('/')
    }

  } catch (error) {
    // 处理错误
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        errorMessage.value = '用户名或密码错误'
      } else if (status === 400) {
        errorMessage.value = data.msg || '登录失败，请检查输入'
      } else {
        errorMessage.value = '登录失败，请稍后重试'
      }
    } else if (error.request) {
      errorMessage.value = '网络错误，请检查网络连接'
    } else {
      errorMessage.value = '登录失败，请稍后重试'
    }
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}

// 监听输入变化时清除错误信息
const clearErrorMessage = () => {
  errorMessage.value = ''
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 420px;
}

.login-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.5s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header .logo {
  font-size: 48px;
  margin-bottom: 16px;
}

.login-header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.login-header p {
  color: #8c8c8c;
  font-size: 14px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.error-message {
  color: #ff4d4f;
  font-size: 14px;
  padding: 8px 12px;
  background-color: #fff2f0;
  border-radius: 6px;
  border: 1px solid #ffccc7;
}

.btn-login {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 8px;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(24, 144, 255, 0.3);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #8c8c8c;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

.test-accounts {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
  font-size: 12px;
  color: #8c8c8c;
}

.stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>