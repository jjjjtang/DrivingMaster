import axios from 'axios'
import Cookies from 'js-cookie'

// 创建axios实例
const api = axios.create({
    baseURL: 'http://124.220.99.159:8081/api', // Spring Boot默认端口
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
    config => {
        // 从Cookie获取token
        const token = Cookies.get('token')
        if (token) {
            config.headers.Authorization = `${token}`
        }
        // 添加跨域相关头部
        config.headers['Access-Control-Allow-Origin'] = '*'
        config.headers['Access-Control-Allow-Methods'] = 'GET,PUT,POST,DELETE,PATCH,OPTIONS'
        config.headers['Access-Control-Allow-Headers'] = 'Content-Type, Authorization'
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器 - 统一处理错误
api.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response) {
            const { status, data } = error.response
            switch (status) {
                case 401:
                    console.error('未授权，请重新登录')
                    Cookies.remove('token')
                    Cookies.remove('userInfo')
                    window.location.href = '/login'
                    break
                case 403:
                    console.error('权限不足')
                    break
                case 404:
                    console.error('请求的资源不存在')
                    break
                case 422:
                    console.error('参数验证失败', data.errors)
                    break
                case 500:
                    console.error('服务器错误')
                    break
            }
        } else if (error.request) {
            console.error('网络错误，请检查网络连接')
        } else {
            console.error('请求配置错误', error.message)
        }
        return Promise.reject(error)
    }
)

// 用户相关API - 根据您的后端接口
export const userAPI = {
    // 用户注册
    register: (data) => {
        return api.post('/user/register', data)
    },

    // 用户登录
    login: (data) => {
        return api.post('/user/login', data)
    },

    // 获取用户信息
    getUserInfo: (token) => {
        return api.get('/user/info', {
            headers: {
                Authorization: token
            }
        })
    }
}

// 工具函数：检查登录状态
export const checkAuth = () => {
    const token = Cookies.get('token')
    const userInfo = Cookies.get('userInfo')

    if (token && userInfo) {
        try {
            const user = JSON.parse(userInfo)
            return {
                isAuthenticated: true,
                user,
                token
            }
        } catch (e) {
            // Cookie解析失败，清除无效数据
            Cookies.remove('token')
            Cookies.remove('userInfo')
            return {
                isAuthenticated: false,
                user: null,
                token: null
            }
        }
    }

    return {
        isAuthenticated: false,
        user: null,
        token: null
    }
}

// 工具函数：设置认证信息
export const setAuth = (token, user) => {
    // 将token存储到Cookie，设置7天过期
    Cookies.set('token', token, { expires: 7 })

    // 将用户信息存储到Cookie
    Cookies.set('userInfo', JSON.stringify(user), { expires: 7 })
}

// 工具函数：清除认证信息
export const clearAuth = () => {
    Cookies.remove('token')
    Cookies.remove('userInfo')
}

export default api