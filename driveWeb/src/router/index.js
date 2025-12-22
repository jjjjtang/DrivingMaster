// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import HomeView from "@/views/HomeView.vue";
import QuestionBankView from "@/views/QuestionBankView.vue";
import QuestionListView from "@/views/QuestionListView.vue";
import ExamIntroView from "@/views/ExamIntroView.vue";
import ExamView from "@/views/ExamView.vue";
import CommunityView from "@/views/CommunityView.vue";
import QuestionTypeSelectView from "@/views/QuestionTypeSelectView.vue";
import AdminHomeView from "@/views/AdminHomeView.vue";


const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminHomeView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: MainLayout, // 使用主布局
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'home',
        component: HomeView,
        meta: { title: '首页' }
      },
      {
        path: 'question-bank',
        name: 'question-bank',
        component: QuestionBankView,
        meta: { title: '题库中心' }
      },
      {
        path: 'question-list',
        name: 'question-list',
        component: QuestionListView,
        meta: { title: '题目列表' }
      },
      {
        path: 'exam-intro',
        name: 'exam-intro',
        component: ExamIntroView,
        meta: { title: '模拟考试' }
      },
      {
        path: 'exam',
        name: 'exam',
        component: ExamView,
        meta: { title: '模拟考试页面' }
      },
      {
        path: 'community',
        name: 'community',
        component: CommunityView,
        meta: { title: '模拟考试页面' }
      },
      {
        path: 'question-type-select',
        name: 'question-type-select',
        component: QuestionTypeSelectView,
        meta: { title: '题型选择练习' }
      }
    ],
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


export default router