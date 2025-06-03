<template>
  <div class="mqtt-container">
    <!-- 背景装饰 -->
    <div class="background-overlay"></div>
    <div class="floating-particles">
      <div class="particle" v-for="n in 15" :key="n" :style="getParticleStyle(n)"></div>
    </div>

    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-gradient">📡 数据发送与接收</span>
      </h1>
      <p class="page-description">
        实时发布与监控MQTT数据，支持温度、湿度、气压等多种数据类型
      </p>
    </div>

    <!-- 主内容区 -->
    <div class="content-container">
      <!-- 发布消息卡片 -->
      <div class="chart-container" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon publish">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12L21 12M21 12L12 3M21 12L12 21" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>发布消息</h3>
            <span class="chart-subtitle">Publish Message</span>
          </div>
        </div>

        <div class="form-container">
          <div class="form-group">
            <label for="topic">Topic</label>
            <input type="text" id="topic" v-model="topic" disabled class="input-field" />
          </div>

          <div class="form-group">
            <label for="date">时间</label>
            <input type="datetime-local" id="date" v-model="date" class="input-field" />
          </div>

          <div class="form-group">
            <label for="dataType">数据类型</label>
            <select id="dataType" v-model="selectedType" class="input-field">
              <option disabled value="">请选择类型</option>
              <option value="humidity">湿度</option>
              <option value="pressure">气压</option>
              <option value="temperature">温度</option>
            </select>
          </div>

          <div class="form-group">
            <label for="value">数据值</label>
            <input type="number" id="value" v-model="value" class="input-field" />
          </div>
        </div>

        <div class="action-buttons">
          <button class="primary-btn" @click="publishMessage">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12L21 12M21 12L12 3M21 12L12 21" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
            发布
          </button>
          <button class="secondary-btn" @click="updateData">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 4V20H20V8M4 4H20M4 4L8 8" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
            更新数据
          </button>
        </div>

        <div v-if="publishError" class="status-display error">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M12 8V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          请填写所有字段再发布！
        </div>

        <div v-if="publishedPayload" class="status-display success">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          <h4>发布成功</h4>
          <pre>{{ publishedPayload }}</pre>
        </div>
      </div>

      <!-- 数据展示卡片 -->
      <div class="chart-container" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon receive">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 12L21 12M3 12L12 21M3 12L12 3" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>数据展示</h3>
            <span class="chart-subtitle">Data Display</span>
          </div>
        </div>

        <div class="action-buttons">
          <button class="secondary-btn" @click="fetchHumidityData">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 2.69L17.66 8.35C20.78 11.47 20.78 16.53 17.66 19.65C14.54 22.77 9.46 22.77 6.34 19.65C3.22 16.53 3.22 11.47 6.34 8.35L12 2.69Z"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            湿度
          </button>
          <button class="secondary-btn" @click="fetchPressureData">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" />
              <path
                d="M12 1V3M12 21V23M4.22 4.22L5.64 5.64M18.36 18.36L19.78 19.78M1 12H3M21 12H23M4.22 19.78L5.64 18.36M18.36 5.64L19.78 4.22"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
            气压
          </button>
          <button class="secondary-btn" @click="fetchTemperatureData">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M14 4V12.5C15.6569 13.2 16.8 14.7 16.8 16.5C16.8 18.9853 14.9853 21 12.5 21C10.0147 21 8.2 18.9853 8.2 16.5C8.2 14.7 9.34315 13.2 11 12.5V4C11 2.89543 11.8954 2 13 2H13C14.1046 2 15 2.89543 15 4Z"
                stroke="currentColor" stroke-width="2" />
              <circle cx="12.5" cy="16.5" r="2" fill="currentColor" />
            </svg>
            温度
          </button>
          <button class="secondary-btn" @click="getLatestMessage">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M13 7H7V13M17 17H7V13M7 13L17 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
            最新数据
          </button>
        </div>

        <div class="data-display">
          <h4>接收数据</h4>
          <div class="scrollable-data" v-if="data.length">
            <ul>
              <li v-for="(item, index) in data" :key="index">{{ item }}</li>
            </ul>
          </div>
          <div v-else class="no-data">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M9.172 16.242L4.929 20.485C4.109 21.305 4.109 22.605 4.929 23.425C5.749 24.245 7.049 24.245 7.869 23.425L12.112 19.182M14.828 16.242L19.071 20.485C19.891 21.305 19.891 22.605 19.071 23.425C18.251 24.245 16.951 24.245 16.131 23.425L11.888 19.182M11.888 19.182L14.828 16.242L11.888 13.303L9.172 16.242L11.888 19.182ZM12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            暂无数据
          </div>
        </div>
      </div>
    </div>
    <!-- 返回按钮 -->
    <div class="back-button">
      <button class="secondary-btn" @click="goBack()">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round" />
        </svg>
        返回首页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref} from 'vue';
import axios from "axios";
import { useRouter } from 'vue-router';


const router = useRouter();

const topic = ref("test");
const date = ref("");
const selectedType = ref("");
const value = ref(null);
const data = ref([]);
const publishedPayload = ref("");
const publishError = ref(false);
const isLoading = ref(false);

// 生成粒子样式
const getParticleStyle = (index) => {
  const size = Math.random() * 3 + 1;
  const left = Math.random() * 100;
  const animationDelay = Math.random() * 20;
  const animationDuration = Math.random() * 15 + 20;

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${animationDelay}s`,
    animationDuration: `${animationDuration}s`
  };
};

// 返回首页
const goBack = () => {
  router.push('/');
};


const publishMessage = async () => {
  if (!date.value || !selectedType.value || value.value === null) {
    publishError.value = true;
    return;
  }

  publishError.value = false;
  isLoading.value = true;

  try {
    const payload = {
      date: `${date.value}:00`,
      [selectedType.value]: value.value.toString(),
    };

    await axios.post("http://localhost:8080/api/data/publish", null, {
      params: {
        topic: topic.value,
        message: JSON.stringify(payload),
      },
    });

    publishedPayload.value = JSON.stringify(payload, null, 2);
    setTimeout(() => isLoading.value = false, 800);
  } catch (error) {
    console.error("Error publishing:", error);
    isLoading.value = false;
  }
};

const fetchData = async (type) => {
  isLoading.value = true;
  try {
    const response = await axios.get(`http://localhost:8080/api/data/${type}`);
    data.value = response.data;
    setTimeout(() => isLoading.value = false, 800);
  } catch (error) {
    console.error(`Error fetching ${type}:`, error);
    isLoading.value = false;
  }
};

const fetchHumidityData = () => fetchData("humidity");
const fetchPressureData = () => fetchData("pressure");
const fetchTemperatureData = () => fetchData("temperature");

const updateData = async () => {
  isLoading.value = true;
  try {
    await axios.post("http://localhost:8080/api/data/update");
    setTimeout(() => isLoading.value = false, 800);
  } catch (error) {
    console.error("Update error:", error);
    isLoading.value = false;
  }
};

const getLatestMessage = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get("http://localhost:8080/api/data/getLatest");
    data.value = [JSON.stringify(response.data.payload)];
    setTimeout(() => isLoading.value = false, 800);
  } catch (error) {
    console.error("Latest fetch error:", error);
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 返回按钮 */
.back-button {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
  padding: 0 2rem;
  z-index: 2;
  position: relative;
}

/* 主容器 */
.mqtt-container {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
  padding: 2rem 0;
}

/* 背景装饰 */
.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.05);
}

/* 浮动粒子 */
.floating-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  animation: float infinite linear;
}

@keyframes float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }

  10% {
    opacity: 1;
  }

  90% {
    opacity: 1;
  }

  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
  padding: 0 2rem;
  z-index: 2;
  position: relative;
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 700;
  margin-bottom: 1rem;
}

.title-gradient {
  background: linear-gradient(45deg, #fff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-description {
  font-size: clamp(1rem, 2vw, 1.2rem);
  color: #e2e8f0;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
  opacity: 0.9;
}

/* 内容容器 */
.content-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: 2rem;
  padding: 0 2rem;
  max-width: 1400px;
  margin: 0 auto;
  z-index: 2;
  position: relative;
}

/* 图表容器 */
.chart-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.8s ease-out;
  animation-fill-mode: both;
}

.chart-container:nth-child(1) {
  animation-delay: 0.1s;
}

.chart-container:nth-child(2) {
  animation-delay: 0.2s;
}

.chart-container:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

.chart-container.loading {
  opacity: 0.7;
}

/* 图表头部 */
.chart-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.chart-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: transform 0.3s ease;
}

.chart-icon.publish {
  background: linear-gradient(135deg, #ec4899, #8b5cf6);
}

.chart-icon.receive {
  background: linear-gradient(135deg, #10b981, #3b82f6);
}

.chart-container:hover .chart-icon {
  transform: scale(1.1) rotate(5deg);
}

.chart-icon svg {
  width: 24px;
  height: 24px;
}

.chart-title-info h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.25rem;
}

.chart-subtitle {
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 500;
}

/* 表单容器 */
.form-container {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #475569;
}

.input-field {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  background: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.input-field:focus {
  border-color: #818cf8;
  outline: none;
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.2);
}

.input-field[disabled] {
  background: #f1f5f9;
  color: #64748b;
}

/* 按钮样式 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.primary-btn,
.secondary-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.secondary-btn {
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  border: 2px solid rgba(102, 126, 234, 0.3);
}

.secondary-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
  transform: translateY(-2px);
}

.primary-btn svg,
.secondary-btn svg {
  width: 16px;
  height: 16px;
}

/* 状态显示 */
.status-display {
  margin-top: 1.5rem;
  padding: 1rem;
  border-radius: 12px;
  font-size: 0.95rem;
  line-height: 1.6;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 0.5rem;
}

.status-display svg {
  width: 24px;
  height: 24px;
}

.status-display.success {
  background: rgba(110, 231, 183, 0.15);
  border: 1px solid rgba(110, 231, 183, 0.3);
  color: #065f46;
}

.status-display.error {
  background: rgba(252, 165, 165, 0.15);
  border: 1px solid rgba(252, 165, 165, 0.3);
  color: #991b1b;
}

.status-display h4 {
  font-weight: 600;
  margin: 0;
}

.status-display pre {
  background: rgba(255, 255, 255, 0.7);
  padding: 0.75rem;
  border-radius: 8px;
  width: 100%;
  margin-top: 0.5rem;
  overflow-x: auto;
  text-align: left;
}

/* 数据展示 */
.data-display {
  margin-top: 1.5rem;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.data-display h4 {
  font-size: 1.1rem;
  color: #1e293b;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.scrollable-data {
  max-height: 220px;
  overflow-y: auto;
  padding: 0.5rem;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.5);
}

.scrollable-data ul {
  padding-left: 1rem;
  margin: 0;
  list-style-type: none;
}

.scrollable-data li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f5f9;
  font-family: monospace;
  font-size: 0.9rem;
  color: #334155;
}

.scrollable-data li:last-child {
  border-bottom: none;
}

.no-data {
  padding: 1.5rem;
  font-style: italic;
  color: #64748b;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.no-data svg {
  width: 32px;
  height: 32px;
  opacity: 0.5;
}

/* 加载动画 */
.loading-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-container {
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  }
}

@media (max-width: 768px) {
  .mqtt-container {
    padding: 1rem 0;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .content-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 0 1rem;
  }

  .chart-container {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .chart-container {
    padding: 1.2rem;
  }

  .chart-header {
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }

  .chart-icon {
    width: 40px;
    height: 40px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>