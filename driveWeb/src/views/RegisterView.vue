<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <!-- Logo和标题 -->
        <div class="register-header">
          <div class="logo">
            <i>🚗</i>
          </div>
          <h1>驾驶大师</h1>
          <p>注册新账号，开始学习之旅</p>
        </div>

        <!-- 注册表单 -->
        <form class="register-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <label>用户名</label>
            <input
                type="text"
                v-model="formData.username"
                placeholder="请输入用户名"
                required
                :disabled="loading"
            >
            <div v-if="errors.username" class="error-text">{{ errors.username }}</div>
          </div>

          <div class="form-group">
            <label>密码</label>
            <input
                type="password"
                v-model="formData.password"
                placeholder="请输入密码（至少6位）"
                required
                :disabled="loading"
            >
            <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <input
                type="password"
                v-model="formData.confirmPassword"
                placeholder="请再次输入密码"
                required
                :disabled="loading"
            >
            <div v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</div>
          </div>

          <div class="form-group">
            <label>手机号</label>
            <input
                type="tel"
                v-model="formData.phone"
                placeholder="请输入手机号"
                :disabled="loading"
            >
            <div v-if="errors.phone" class="error-text">{{ errors.phone }}</div>
          </div>

          <div class="form-group">
            <label>邮箱</label>
            <input
                type="email"
                v-model="formData.email"
                placeholder="请输入邮箱（选填）"
                :disabled="loading"
            >
            <div v-if="errors.email" class="error-text">{{ errors.email }}</div>
          </div>

          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <button
              type="submit"
              class="btn-register"
              :disabled="loading"
          >
            <span v-if="!loading">注册</span>
            <span v-else>注册中...</span>
          </button>

          <div class="login-link">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const errorMessage = ref('')
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: ''
})

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: ''
})

// 表单验证
const validateForm = () => {
  let isValid = true

  // 清除之前的错误信息
  Object.keys(errors).forEach(key => errors[key] = '')
  errorMessage.value = ''

  // 用户名验证
  if (!formData.username.trim()) {
    errors.username = '用户名不能为空'
    isValid = false
  } else if (formData.username.length < 3 || formData.username.length > 20) {
    errors.username = '用户名长度应在3-20个字符之间'
    isValid = false
  }

  // 密码验证
  if (!formData.password.trim()) {
    errors.password = '密码不能为空'
    isValid = false
  } else if (formData.password.length < 6) {
    errors.password = '密码长度不能少于6位'
    isValid = false
  }

  // 确认密码验证
  if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }

  // 手机号验证（可选）
  if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
    errors.phone = '请输入有效的手机号'
    isValid = false
  }

  // 邮箱验证（可选）
  if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }

  return isValid
}

// 处理注册
const handleRegister = async () => {
  // 验证表单
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    // 准备注册数据
    const registerData = {
      username: formData.username.trim(),
      password: formData.password.trim()
    }

    // 添加可选字段
    if (formData.phone.trim()) {
      registerData.phone = formData.phone.trim()
    }

    if (formData.email.trim()) {
      registerData.email = formData.email.trim()
    }

    // 调用注册API
    const response = await userAPI.register(registerData)

    // 显示成功消息
    ElMessage.success(response.msg || '注册成功')

    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push('/login')
    }, 1500)

  } catch (error) {
    // 处理错误
    if (error.response) {
      const { status, data } = error.response
      if (status === 409) {
        errorMessage.value = '用户名已存在'
      } else if (status === 400) {
        errorMessage.value = data.msg || '注册失败，请检查输入'
      } else {
        errorMessage.value = '注册失败，请稍后重试'
      }
    } else if (error.request) {
      errorMessage.value = '网络错误，请检查网络连接'
    } else {
      errorMessage.value = '注册失败，请稍后重试'
    }
    console.error('注册错误:', error)
  } finally {
    loading.value = false
  }
}

// 实时验证输入
const validateInput = (field) => {
  switch (field) {
    case 'username':
      if (!formData.username.trim()) {
        errors.username = '用户名不能为空'
      } else if (formData.username.length < 3 || formData.username.length > 20) {
        errors.username = '用户名长度应在3-20个字符之间'
      } else {
        errors.username = ''
      }
      break

    case 'password':
      if (!formData.password.trim()) {
        errors.password = '密码不能为空'
      } else if (formData.password.length < 6) {
        errors.password = '密码长度不能少于6位'
      } else {
        errors.password = ''
      }
      break

    case 'confirmPassword':
      if (formData.password !== formData.confirmPassword) {
        errors.confirmPassword = '两次输入的密码不一致'
      } else {
        errors.confirmPassword = ''
      }
      break

    case 'phone':
      if (formData.phone && !/^1[3-9]\d{9}$/.test(formData.phone)) {
        errors.phone = '请输入有效的手机号'
      } else {
        errors.phone = ''
      }
      break

    case 'email':
      if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
        errors.email = '请输入有效的邮箱地址'
      } else {
        errors.email = ''
      }
      break
  }

  // 清除全局错误信息
  errorMessage.value = ''
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 480px;
}

.register-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.5s ease-out;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header .logo {
  font-size: 48px;
  margin-bottom: 16px;
}

.register-header h1 {
  font-size: 32px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.register-header p {
  color: #8c8c8c;
  font-size: 14px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
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
  border-color: #52c41a;
  box-shadow: 0 0 0 2px rgba(82, 196, 26, 0.2);
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.error-text {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
}

.error-message {
  color: #ff4d4f;
  font-size: 14px;
  padding: 8px 12px;
  background-color: #fff2f0;
  border-radius: 6px;
  border: 1px solid #ffccc7;
  text-align: center;
}

.btn-register {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 14px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 12px;
}

.btn-register:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(82, 196, 26, 0.3);
}

.btn-register:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #8c8c8c;
  margin-top: 16px;
}

.login-link a {
  color: #52c41a;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
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