<template>
  <div class="login-container">
    <div class="login-box">
      <h2>{{ isLogin ? '登录' : '注册' }}</h2>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <input
            v-model="formData.username"
            type="text"
            placeholder="用户名"
            required
          />
        </div>
        <div class="form-group">
          <input
            v-model="formData.password"
            type="password"
            placeholder="密码"
            required
          />
        </div>
        <div class="form-group" v-if="!isLogin">
          <input
            v-model="formData.email"
            type="email"
            placeholder="邮箱"
            required
          />
        </div>
        <button type="submit">{{ isLogin ? '登录' : '注册' }}</button>
      </form>
      <p class="switch-mode" @click="isLogin = !isLogin">
        {{ isLogin ? '没有账号？点击注册' : '已有账号？点击登录' }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { userApi, type LoginData, type RegisterData } from '../api/user';

const router = useRouter();
const isLogin = ref(true);
const formData = reactive({
  username: '',
  password: '',
  email: ''
});

const handleSubmit = async () => {
  try {
    if (isLogin.value) {
      const { username, password } = formData;
      const response = await userApi.login({ username, password });
      // 存储token
      localStorage.setItem('token', response.token);
      router.push('/');
    } else {
      const response = await userApi.register(formData as RegisterData);
      alert('注册成功，请登录');
      isLogin.value = true;
    }
  } catch (error) {
    alert('操作失败，请重试');
    console.error(error);
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

button {
  width: 100%;
  padding: 0.8rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
}

button:hover {
  background-color: #45a049;
}

.switch-mode {
  text-align: center;
  margin-top: 1rem;
  color: #666;
  cursor: pointer;
}

.switch-mode:hover {
  color: #4CAF50;
}
</style> 