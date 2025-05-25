<template>
  <div class="home-container">
    <!-- 背景装饰 -->
    <div class="background-overlay"></div>
    <div class="floating-particles">
      <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle(n)"></div>
    </div>

    <!-- 页面内容 -->
    <div class="content-wrapper">
      <!-- 页面头部 -->
      <header class="page-header">
        <h1 class="page-title">
          <span class="title-gradient">温度/湿度/气压数据</span>
          <span class="title-accent">发布订阅与分析处理系统</span>
        </h1>
        <p class="page-description">
          欢迎来到智能数据分析与处理系统，体验实时数据监控、智能分析和精准预测
        </p>
      </header>

      <!-- 卡片容器 -->
      <div class="cards-container">
        <!-- 数据图表组件卡片 -->
        <div class="card" @click="goToComponent('/data-chart')">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M3 3V21H21" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
              <path d="M9 9L12 6L16 10L20 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
          <div class="card-content">
            <h3 class="card-title">数据分析处理展示</h3>
            <p class="card-description">
              智能数据可视化平台，通过多维图表展示历史数据趋势，支持折线图、直方图、面积图等多种展示方式，让数据洞察一目了然。
            </p>
            <div class="card-tags">
              <span class="tag">数据可视化</span>
              <span class="tag">趋势分析</span>
            </div>
          </div>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
        </div>

        <!-- MQTT 信息展示组件卡片 -->
        <div class="card" @click="goToComponent('/descriptive-info')">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
          <div class="card-content">
            <h3 class="card-title">消息发布与订阅</h3>
            <p class="card-description">
              基于MQTT协议的实时消息传输系统，实现高效的发布订阅模式，确保数据传输的可靠性和实时性，支持多设备连接。
            </p>
            <div class="card-tags">
              <span class="tag">实时通信</span>
              <span class="tag">MQTT协议</span>
            </div>
          </div>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
        </div>

        <!-- 数据拟合组件卡片 -->
        <div class="card" @click="goToComponent('/fit-curve')">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21.21 15.89A10 10 0 1 1 8 2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
              <path d="M22 12A10 10 0 0 0 12 2V12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
          <div class="card-content">
            <h3 class="card-title">数据拟合与预测</h3>
            <p class="card-description">
              智能预测分析引擎，基于机器学习算法对历史温度数据进行深度分析，构建预测模型，提供准确的未来趋势预测。
            </p>
            <div class="card-tags">
              <span class="tag">数据拟合</span>
              <span class="tag">趋势预测</span>
            </div>
          </div>
          <div class="card-arrow">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ref, onMounted } from "vue";

const router = useRouter();

// 页面跳转函数
const goToComponent = (path) => {
  router.push(path);
};

// 生成粒子样式
const getParticleStyle = (index) => {
  const size = Math.random() * 4 + 2;
  const left = Math.random() * 100;
  const animationDelay = Math.random() * 20;
  const animationDuration = Math.random() * 10 + 15;

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${animationDelay}s`,
    animationDuration: `${animationDuration}s`
  };
};
</script>

<style scoped>
/* 全局重置和基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 主容器样式 */
.home-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 背景遮罩层 */
.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(1px);
}

/* 浮动粒子效果 */
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
  background: rgba(255, 255, 255, 0.3);
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

/* 内容包装器 */
.content-wrapper {
  width: 100%;
  max-width: 1400px;
  padding: 2rem;
  z-index: 2;
  position: relative;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
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

/* 页面标题 */
.page-title {
  font-size: clamp(2rem, 4vw, 3.5rem);
  font-weight: 700;
  margin-bottom: 1rem;
  line-height: 1.2;
}

.title-gradient {
  background: linear-gradient(45deg, #fff, #e0e7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: block;
}

.title-accent {
  color: #f1f5f9;
  font-weight: 600;
  display: block;
  margin-top: 0.5rem;
}

/* 页面描述 */
.page-description {
  font-size: clamp(1rem, 2vw, 1.3rem);
  color: #e2e8f0;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
  opacity: 0.9;
}

/* 卡片容器 */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  padding: 1rem 0;
}

/* 卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.8s ease-out;
  animation-fill-mode: both;
}

.card:nth-child(1) {
  animation-delay: 0.1s;
}

.card:nth-child(2) {
  animation-delay: 0.2s;
}

.card:nth-child(3) {
  animation-delay: 0.3s;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s;
}

.card:hover::before {
  left: 100%;
}

.card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 卡片图标 */
.card-icon {
  width: 60px;
  height: 60px;
  margin-bottom: 1.5rem;
  padding: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s ease;
}

.card:hover .card-icon {
  transform: rotate(360deg) scale(1.1);
}

.card-icon svg {
  width: 100%;
  height: 100%;
}

/* 卡片内容 */
.card-content {
  flex: 1;
  margin-bottom: 1.5rem;
}

/* 卡片标题 */
.card-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1rem;
  line-height: 1.3;
}

/* 卡片描述 */
.card-description {
  font-size: 1rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

/* 标签容器 */
.card-tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.tag {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

/* 卡片箭头 */
.card-arrow {
  width: 24px;
  height: 24px;
  color: #94a3b8;
  transition: all 0.3s ease;
  align-self: flex-end;
}

.card:hover .card-arrow {
  color: #667eea;
  transform: translateX(5px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-wrapper {
    padding: 1rem;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .cards-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .card {
    padding: 1.5rem;
  }

  .card-icon {
    width: 50px;
    height: 50px;
  }
}

@media (max-width: 480px) {
  .home-container {
    height: auto;
    min-height: 100vh;
    padding: 1rem 0;
  }

  .content-wrapper {
    padding: 0.5rem;
  }

  .card {
    padding: 1.2rem;
  }
}

/* 滚动条样式 */
.home-container::-webkit-scrollbar {
  width: 8px;
}

.home-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.home-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

.home-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}
</style>