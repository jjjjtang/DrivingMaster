<template>
  <div class="community-page">


    <main class="community-container">
      <!-- 页面标题 -->
      <header class="community-header">
        <h1>学习社区</h1>
        <p>交流学习经验 · 分享考试心得</p>
      </header>

      <!-- 发帖区 -->
      <section class="post-editor">
        <input
            v-model="newPost.title"
            placeholder="帖子标题"
            class="post-title-input"
        />
        <textarea
            v-model="newPost.content"
            placeholder="分享你的学习经验或问题…"
            class="post-content-input"
        ></textarea>

        <div class="editor-actions">
          <button @click="publishPost">发布帖子</button>
        </div>
      </section>

      <!-- 帖子列表 -->
      <section class="post-list">
        <div
            v-for="post in posts"
            :key="post.id"
            class="post-card"
        >
          <div class="post-header">
            <div class="avatar">{{ post.author.charAt(0) }}</div>
            <div>
              <div class="author">{{ post.author }}</div>
              <div class="time">{{ post.time }}</div>
            </div>
          </div>

          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-content">{{ post.content }}</p>

          <!-- 操作区 -->
          <div class="post-actions">
            <span @click="likePost(post)">👍 {{ post.likes }}</span>
            <span @click="post.showComment = !post.showComment">
              💬 {{ post.comments.length }}
            </span>
          </div>

          <!-- 评论区 -->
          <div v-if="post.showComment" class="comment-section">
            <div
                v-for="(c, index) in post.comments"
                :key="index"
                class="comment"
            >
              <strong>{{ c.author }}：</strong>{{ c.content }}
            </div>

            <div class="comment-input">
              <input
                  v-model="post.newComment"
                  placeholder="写下你的评论..."
              />
              <button @click="addComment(post)">发送</button>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Navbar from '@/components/Navbar.vue'

const username = '测试学员'

const newPost = ref({
  title: '',
  content: ''
})

const posts = ref([
  {
    id: 1,
    author: '小李',
    time: '刚刚',
    title: '科目一刷题心得',
    content: '多刷错题真的很有用，推荐大家重点看易错点。',
    likes: 3,
    comments: [],
    newComment: '',
    showComment: false
  }
])

const publishPost = () => {
  if (!newPost.value.title || !newPost.value.content) return

  posts.value.unshift({
    id: Date.now(),
    author: username,
    time: '刚刚',
    title: newPost.value.title,
    content: newPost.value.content,
    likes: 0,
    comments: [],
    newComment: '',
    showComment: false
  })

  newPost.value.title = ''
  newPost.value.content = ''
}

const likePost = (post) => {
  post.likes++
}

const addComment = (post) => {
  if (!post.newComment) return
  post.comments.push({
    author: username,
    content: post.newComment
  })
  post.newComment = ''
}
</script>

<style scoped>
.community-page {
  background: #f5f7fb;
  min-height: 100vh;
}

.community-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px;
}

.community-header {
  text-align: center;
  margin-bottom: 32px;
}

.community-header h1 {
  font-size: 34px;
  font-weight: bold;
}

.community-header p {
  color: #666;
}

.post-editor {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  margin-bottom: 32px;
}

.post-title-input,
.post-content-input {
  width: 100%;
  border: none;
  background: #f3f4f6;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 12px;
  font-size: 14px;
}

.post-content-input {
  min-height: 100px;
  resize: none;
}

.editor-actions {
  text-align: right;
}

.editor-actions button {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 999px;
  cursor: pointer;
  font-weight: bold;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar {
  width: 42px;
  height: 42px;
  background: #3b82f6;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.author {
  font-weight: 600;
}

.time {
  font-size: 12px;
  color: #888;
}

.post-title {
  font-size: 18px;
  font-weight: bold;
  margin: 8px 0;
}

.post-content {
  color: #444;
  line-height: 1.6;
}

.post-actions {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  cursor: pointer;
  color: #555;
}

.comment-section {
  margin-top: 16px;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.comment {
  font-size: 14px;
  margin-bottom: 8px;
}

.comment-input {
  display: flex;
  gap: 8px;
}

.comment-input input {
  flex: 1;
  padding: 8px;
  border-radius: 999px;
  border: 1px solid #ddd;
}

.comment-input button {
  border: none;
  background: #3b82f6;
  color: white;
  border-radius: 999px;
  padding: 8px 16px;
}
</style>