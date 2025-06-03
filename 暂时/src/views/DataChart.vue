<template>
  <div class="data-chart">
    <!-- 背景装饰 -->
    <div class="background-overlay"></div>
    <div class="floating-particles">
      <div class="particle" v-for="n in 15" :key="n" :style="getParticleStyle(n)"></div>
    </div>

    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-gradient">数据分析处理展示</span>
      </h1>
      <p class="page-description">
        实时监控温度、湿度、气压数据变化趋势，提供智能化数据分析服务
      </p>
    </div>

    <!-- 图表容器 -->
    <div class="chart-container-wrapper">
      <!-- 温度图表 -->
      <div class="chart-container" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon temperature">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M14 4V12.5C15.6569 13.2 16.8 14.7 16.8 16.5C16.8 18.9853 14.9853 21 12.5 21C10.0147 21 8.2 18.9853 8.2 16.5C8.2 14.7 9.34315 13.2 11 12.5V4C11 2.89543 11.8954 2 13 2H13C14.1046 2 15 2.89543 15 4Z"
                stroke="currentColor" stroke-width="2" />
              <circle cx="12.5" cy="16.5" r="2" fill="currentColor" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>温度监控</h3>
            <span class="chart-subtitle">Temperature Monitor</span>
          </div>
        </div>

        <div class="chart-canvas-wrapper">
          <canvas ref="temperatureChart"></canvas>
          <div v-if="isLoading" class="loading-spinner">
            <div class="spinner"></div>
          </div>
        </div>

        <div class="chart-info">
          <p class="chart-description">
            展示2-6月期间的平均温度变化趋势，通过数据分析帮助您了解温度规律
          </p>
          <div class="action-buttons">
            <button class="primary-btn" @click="goToComponent('/data-chart/temperature')">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              查看详细数据
            </button>
          </div>
        </div>
      </div>

      <!-- 湿度图表 -->
      <div class="chart-container" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon humidity">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 2.69L17.66 8.35C20.78 11.47 20.78 16.53 17.66 19.65C14.54 22.77 9.46 22.77 6.34 19.65C3.22 16.53 3.22 11.47 6.34 8.35L12 2.69Z"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>湿度监控</h3>
            <span class="chart-subtitle">Humidity Monitor</span>
          </div>
        </div>

        <div class="chart-canvas-wrapper">
          <canvas ref="humidityChart"></canvas>
          <div v-if="isLoading" class="loading-spinner">
            <div class="spinner"></div>
          </div>
        </div>

        <div class="chart-info">
          <p class="chart-description">
            展示2-6月期间的平均湿度变化趋势，助您掌握环境湿度变化规律
          </p>
          <div class="action-buttons">
            <button class="primary-btn" @click="goToComponent('/data-chart/humidity')">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              查看详细数据
            </button>
          </div>
        </div>
      </div>

      <!-- 气压图表 -->
      <div class="chart-container" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon pressure">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2" />
              <path
                d="M12 1V3M12 21V23M4.22 4.22L5.64 5.64M18.36 18.36L19.78 19.78M1 12H3M21 12H23M4.22 19.78L5.64 18.36M18.36 5.64L19.78 4.22"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>气压监控</h3>
            <span class="chart-subtitle">Pressure Monitor</span>
          </div>
        </div>

        <div class="chart-canvas-wrapper">
          <canvas ref="pressureChart"></canvas>
          <div v-if="isLoading" class="loading-spinner">
            <div class="spinner"></div>
          </div>
        </div>

        <div class="chart-info">
          <p class="chart-description">
            展示2-6月期间的平均气压变化趋势，为气象分析提供重要数据支撑
          </p>
          <div class="action-buttons">
            <button class="primary-btn" @click="goToComponent('/data-chart/pressure')">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              查看详细数据
            </button>
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

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import Chart from 'chart.js/auto';

const router = useRouter();

const temperatureChart = ref<HTMLCanvasElement | null>(null);
const humidityChart = ref<HTMLCanvasElement | null>(null);
const pressureChart = ref<HTMLCanvasElement | null>(null);
const isLoading = ref(true);

// 跳转到指定路径
const goToComponent = (path: string) => {
  console.log("Navigating to:", path);
  router.push(path);
};

// 返回首页
const goBack = () => {
  router.push('/');
};

// 生成粒子样式
const getParticleStyle = (index: number) => {
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

// 通用的数据获取方法
const fetchData = async (type: string) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/data/${type}`);
    console.log(`get ${type} data successfully`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching ${type} data:`, error);
    return [];
  }
};

// 计算每月的平均值
const calculateMonthlyAverage = (data: any[], type: string) => {
  const monthlyData: { [key: number]: number[] } = {};

  data.forEach((item) => {
    const month = new Date(item.date).getMonth() + 1;
    if (!monthlyData[month]) {
      monthlyData[month] = [];
    }
    monthlyData[month].push(item[type]);
  });

  const monthlyAverages: { month: number, average: number }[] = [];
  for (let month = 2; month <= 6; month++) {
    if (monthlyData[month]) {
      const sum = monthlyData[month].reduce((acc, val) => acc + val, 0);
      const average = sum / monthlyData[month].length;
      monthlyAverages.push({ month, average });
    }
  }

  return monthlyAverages;
};

// 创建图表的方法
const createChart = (canvasRef: any, data: any, label: string, color: string) => {
  if (!canvasRef.value) return;

  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  // 创建渐变色
  const gradient = ctx.createLinearGradient(0, 0, 0, 400);
  gradient.addColorStop(0, color + '40');
  gradient.addColorStop(1, color + '10');

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((item: any) => `${item.month}月`),
      datasets: [
        {
          label,
          data: data.map((item: any) => item.average),
          borderColor: color,
          backgroundColor: gradient,
          fill: true,
          tension: 0.4,
          pointRadius: 6,
          pointHoverRadius: 8,
          pointBackgroundColor: '#ffffff',
          pointBorderColor: color,
          pointBorderWidth: 3,
          borderWidth: 3,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        intersect: false,
        mode: 'index',
      },
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: '#ffffff',
          bodyColor: '#ffffff',
          cornerRadius: 8,
          displayColors: false,
        },
      },
      scales: {
        x: {
          grid: {
            display: false,
          },
          ticks: {
            color: '#64748b',
            font: {
              size: 12,
              weight: 500,
            },
          },
        },
        y: {
          beginAtZero: false,
          grid: {
            color: 'rgba(0, 0, 0, 0.1)',
          },
          ticks: {
            color: '#64748b',
            font: {
              size: 12,
            },
          },
        },
      },
    },
  });
};

onMounted(async () => {
  try {
    // 获取数据
    const [temperatureData, humidityData, pressureData] = await Promise.all([
      fetchData('temperature'),
      fetchData('humidity'),
      fetchData('pressure')
    ]);

    // 计算每月的平均值
    const monthlyTemperatureData = calculateMonthlyAverage(temperatureData, 'temperature');
    const monthlyHumidityData = calculateMonthlyAverage(humidityData, 'humidity');
    const monthlyPressureData = calculateMonthlyAverage(pressureData, 'pressure');

    // 创建图表
    createChart(temperatureChart, monthlyTemperatureData, '2-6月平均温度（单位：摄氏度℃）', '#ef4444');
    createChart(humidityChart, monthlyHumidityData, '2-6月平均湿度（单位：%）', '#3b82f6');
    createChart(pressureChart, monthlyPressureData, '2-6月平均气压（单位：帕pa）', '#10b981');

    isLoading.value = false;
  } catch (error) {
    console.error('Error loading charts:', error);
    isLoading.value = false;
  }
});
</script>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 主容器 */
.data-chart {
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

/* 图表容器包装器 */
.chart-container-wrapper {
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

.chart-container:nth-child(3) {
  animation-delay: 0.3s;
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

.chart-icon.temperature {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.chart-icon.humidity {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.chart-icon.pressure {
  background: linear-gradient(135deg, #10b981, #059669);
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

/* 图表画布包装器 */
.chart-canvas-wrapper {
  position: relative;
  height: 300px;
  margin-bottom: 1.5rem;
  border-radius: 12px;
  overflow: hidden;
}

.chart-canvas-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
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

/* 图表信息 */
.chart-info {
  text-align: center;
}

.chart-description {
  font-size: 0.95rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

/* 按钮样式 */
.action-buttons {
  display: flex;
  justify-content: center;
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

/* 返回按钮 */
.back-button {
  display: flex;
  justify-content: center;
  margin-top: 3rem;
  padding: 0 2rem;
  z-index: 2;
  position: relative;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .chart-container-wrapper {
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  }
}

@media (max-width: 768px) {
  .data-chart {
    padding: 1rem 0;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .chart-container-wrapper {
    grid-template-columns: 1fr;
    gap: 1.5rem;
    padding: 0 1rem;
  }

  .chart-container {
    padding: 1.5rem;
  }

  .chart-canvas-wrapper {
    height: 250px;
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

  .chart-canvas-wrapper {
    height: 200px;
  }
}
</style>