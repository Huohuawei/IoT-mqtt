<template>
  <div class="temperature-container">
    <!-- 背景装饰 -->
    <div class="background-overlay"></div>
    <div class="floating-particles">
      <div class="particle" v-for="n in 15" :key="n" :style="getParticleStyle(n)"></div>
    </div>

    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-gradient">🌡️ 温度趋势预测</span>
      </h1>
      <p class="page-description">
        基于正弦波模型的历史数据分析与未来温度预测
      </p>
    </div>

    <!-- 主内容区 -->
    <div class="content-wrapper">
      <!-- 图表卡片 -->
      <div class="chart-card" :class="{ 'loading': isLoading }">
        <div class="chart-header">
          <div class="chart-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M14 4V12.5C15.6569 13.2 16.8 14.7 16.8 16.5C16.8 18.9853 14.9853 21 12.5 21C10.0147 21 8.2 18.9853 8.2 16.5C8.2 14.7 9.34315 13.2 11 12.5V4C11 2.89543 11.8954 2 13 2H13C14.1046 2 15 2.89543 15 4Z"
                stroke="currentColor" stroke-width="2" />
              <circle cx="12.5" cy="16.5" r="2" fill="currentColor" />
            </svg>
          </div>
          <div class="chart-title-info">
            <h3>温度趋势分析</h3>
            <span class="chart-subtitle">Temperature Trend Analysis</span>
            <div class="time-range">
              <button v-for="range in timeRanges" :key="range.value" :class="{ 'active': activeRange === range.value }"
                @click="changeTimeRange(range.value)">
                {{ range.label }}
              </button>
            </div>
          </div>
        </div>

        <div class="chart-canvas-wrapper">
          <canvas id="fitCurveChart" ref="chartCanvas"></canvas>
          <div v-if="isLoading" class="loading-spinner">
            <div class="spinner"></div>
          </div>
          <div v-if="errorMessage" class="error-message">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 8V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            {{ errorMessage }}
          </div>
        </div>
      </div>

      <!-- 数据统计卡片 -->
      <div class="stats-card">
        <div class="card-header">
          <div class="card-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M9 19v-6M15 19V5M12 19V9M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </div>
          <h3>数据统计</h3>
        </div>

        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ stats.currentTemp }}°C</div>
            <div class="stat-label">当前温度</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgTemp }}°C</div>
            <div class="stat-label">平均温度</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.maxTemp }}°C</div>
            <div class="stat-label">最高温度</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.minTemp }}°C</div>
            <div class="stat-label">最低温度</div>
          </div>
        </div>

        <div class="prediction-info">
          <div class="prediction-item">
            <div class="prediction-label">未来3小时预测</div>
            <div class="prediction-value">{{ stats.future3hTemp }}°C</div>
          </div>
          <div class="prediction-item">
            <div class="prediction-label">趋势变化</div>
            <div class="prediction-trend">
              <span :class="stats.trendClass">{{ stats.trendText }}</span>
              <svg v-if="stats.trend === 'up'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 15L12 9L6 15" stroke="#ef4444" stroke-width="2" stroke-linecap="round"
                  stroke-linejoin="round" />
              </svg>
              <svg v-if="stats.trend === 'down'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M6 9L12 15L18 9" stroke="#10b981" stroke-width="2" stroke-linecap="round"
                  stroke-linejoin="round" />
              </svg>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 返回按钮（优化后） -->
    <div class="back-button-container">
      <button class="back-button" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round" />
        </svg>
        <span>返回首页</span>
      </button>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import Chart from "chart.js/auto";
import axios from "axios";
import { useRouter } from 'vue-router';

const router = useRouter();
// 返回首页
const goBack = () => {
  router.push('/');
};

// 响应式数据
const chartInstance = ref(null);
const errorMessage = ref("");
const isLoading = ref(true);
const activeRange = ref("24h");
const temperatureData = ref([]);
const predictions = ref([]);

// 时间范围选项
const timeRanges = ref([
  { label: "24小时", value: "24h" },
  { label: "7天", value: "7d" },
  { label: "30天", value: "30d" }
]);

// 统计数据
const stats = computed(() => {
  if (temperatureData.value.length === 0 || predictions.value.length === 0) {
    return {
      currentTemp: "--",
      avgTemp: "--",
      maxTemp: "--",
      minTemp: "--",
      future3hTemp: "--",
      trend: "stable",
      trendText: "稳定",
      trendClass: "text-gray-500"
    };
  }

  const currentTemp = temperatureData.value[temperatureData.value.length - 1].temp;
  const avgTemp = (temperatureData.value.reduce((sum, item) => sum + item.temp, 0) / temperatureData.value.length).toFixed(1);
  const maxTemp = Math.max(...temperatureData.value.map(item => item.temp));
  const minTemp = Math.min(...temperatureData.value.map(item => item.temp));

  // 未来3小时预测（每10分钟一个点，共18个点）
  const future3hTemp = predictions.value.length > 18
    ? predictions.value[18].temp.toFixed(1)
    : predictions.value[predictions.value.length - 1].temp.toFixed(1);

  // 趋势判断
  const lastIndex = temperatureData.value.length - 1;
  const trendValue = predictions.value[0].temp - currentTemp;
  let trend, trendText, trendClass;

  if (Math.abs(trendValue) < 0.5) {
    trend = "stable";
    trendText = "稳定";
    trendClass = "text-gray-500";
  } else if (trendValue > 0) {
    trend = "up";
    trendText = "上升";
    trendClass = "text-red-500";
  } else {
    trend = "down";
    trendText = "下降";
    trendClass = "text-green-500";
  }

  return {
    currentTemp: currentTemp.toFixed(1),
    avgTemp,
    maxTemp: maxTemp.toFixed(1),
    minTemp: minTemp.toFixed(1),
    future3hTemp,
    trend,
    trendText,
    trendClass
  };
});

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

// 获取预测数据
const fetchPredictionData = async () => {
  try {
    isLoading.value = true;
    errorMessage.value = "";

    const response = await axios.get("http://localhost:5000/predict", {
      params: { range: activeRange.value }
    });

    if (!response.data.cleaned_data || !response.data.predictions) {
      throw new Error("后端返回数据格式不正确");
    }

    temperatureData.value = response.data.cleaned_data;
    predictions.value = response.data.predictions;

    drawChart();
  } catch (error) {
    console.error("获取数据失败:", error);
    errorMessage.value = error.response?.data?.error || error.message || "获取数据失败，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};

// 切换时间范围
const changeTimeRange = (range) => {
  activeRange.value = range;
  fetchPredictionData();
};

// 绘制图表
const drawChart = () => {
  const ctx = document.getElementById("fitCurveChart")?.getContext("2d");
  if (!ctx) return;

  if (chartInstance.value) {
    chartInstance.value.destroy();
  }

  // 创建渐变色
  const gradient1 = ctx.createLinearGradient(0, 0, 0, 400);
  gradient1.addColorStop(0, 'rgba(0, 123, 255, 0.4)');
  gradient1.addColorStop(1, 'rgba(0, 123, 255, 0.1)');

  const gradient2 = ctx.createLinearGradient(0, 0, 0, 400);
  gradient2.addColorStop(0, 'rgba(255, 99, 132, 0.4)');
  gradient2.addColorStop(1, 'rgba(255, 99, 132, 0.1)');

  // 根据时间范围调整显示密度
  const displayDensity = activeRange.value === "24h" ? 6 :
    activeRange.value === "7d" ? 24 : 72;

  const filteredLabels = temperatureData.value
    .filter((_, index) => index % displayDensity === 0)
    .map(item => item.time);

  chartInstance.value = new Chart(ctx, {
    type: "line",
    data: {
      labels: filteredLabels,
      datasets: [
        {
          label: "实际温度数据",
          data: temperatureData.value.map(d => d.temp),
          borderColor: "rgba(0, 123, 255, 1)",
          backgroundColor: gradient1,
          pointBackgroundColor: "#ffffff",
          pointBorderColor: "rgba(0, 123, 255, 1)",
          pointBorderWidth: 2,
          borderWidth: 3,
          tension: 0.3,
          fill: true,
        },
        {
          label: "预测温度曲线",
          data: predictions.value.map(d => d.temp),
          borderColor: "rgba(255, 99, 132, 1)",
          backgroundColor: gradient2,
          pointBackgroundColor: "#ffffff",
          pointBorderColor: "rgba(255, 99, 132, 1)",
          pointBorderWidth: 2,
          borderWidth: 3,
          tension: 0.3,
          fill: true,
          borderDash: [5, 5],
        }
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
          labels: {
            color: '#1e293b',
            font: {
              size: 12,
              weight: 500
            },
            padding: 20,
            usePointStyle: true,
            pointStyle: 'circle'
          },
          onClick: (e) => e.stopPropagation()
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: '#ffffff',
          bodyColor: '#ffffff',
          cornerRadius: 8,
          displayColors: true,
          callbacks: {
            label: (context) => {
              const label = context.dataset.label || '';
              const value = context.raw;
              return `${label}: ${value}°C`;
            }
          }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: '时间',
            color: '#94a3b8'
          },
          grid: {
            display: false,
          },
          ticks: {
            color: '#64748b',
            maxRotation: 45,
            minRotation: 45,
            callback: (value, index) => {
              // 根据时间范围显示不同的时间格式
              if (activeRange.value === "24h") {
                return filteredLabels[index];
              } else {
                return filteredLabels[index].split(' ')[0]; // 只显示日期部分
              }
            }
          }
        },
        y: {
          title: {
            display: true,
            text: '温度 (°C)',
            color: '#94a3b8'
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.05)',
          },
          ticks: {
            color: '#64748b',
            callback: (value) => `${value}°C`
          },
          min: Math.min(...temperatureData.value.map(d => d.temp)) - 2,
          max: Math.max(...temperatureData.value.map(d => d.temp)) + 2
        }
      },
      interaction: {
        intersect: false,
        mode: 'index'
      }
    }
  });
};

// 初始化加载数据
onMounted(() => {
  fetchPredictionData();

  // 每5分钟自动刷新数据
  setInterval(() => {
    if (!isLoading.value) {
      fetchPredictionData();
    }
  }, 5 * 60 * 1000);
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
.temperature-container {
  width: 100vw;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow-x: hidden;
  padding: 2rem 0;
}

/* 优化后的返回按钮样式 */
.back-button-container {
  display: flex;
  justify-content: center;
  margin: 3rem auto 2rem;
  padding: 0 2rem;
  max-width: 1400px;
  z-index: 10;
  position: relative;
}

.back-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: rgba(255, 255, 255, 0.9);
  color: #6366f1;
  border: 2px solid rgba(99, 102, 241, 0.3);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.back-button:hover {
  background: rgba(99, 102, 241, 0.1);
  border-color: #6366f1;
  transform: translateY(-2px);
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15);
}

.back-button svg {
  width: 18px;
  height: 18px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .back-button-container {
    margin: 2rem auto 1.5rem;
    padding: 0 1rem;
  }

  .back-button {
    padding: 0.6rem 1.2rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .back-button {
    width: 100%;
  }
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

/* 内容包装器 */
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 0.4fr;
  gap: 2rem;
  padding: 0 2rem;
  max-width: 1400px;
  margin: 0 auto;
  z-index: 2;
  position: relative;
}

/* 图表卡片 */
.chart-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.8s ease-out;
  animation-delay: 0.1s;
  animation-fill-mode: both;
}

.chart-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

.chart-card.loading {
  opacity: 0.7;
}

/* 图表头部 */
.chart-header {
  display: flex;
  align-items: flex-start;
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
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.chart-card:hover .chart-icon {
  transform: scale(1.1) rotate(5deg);
}

.chart-icon svg {
  width: 24px;
  height: 24px;
}

.chart-title-info {
  flex: 1;
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
  display: block;
  margin-bottom: 0.75rem;
}

.time-range {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.time-range button {
  padding: 0.25rem 0.75rem;
  font-size: 0.8rem;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.05);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #64748b;
}

.time-range button:hover {
  background: rgba(0, 0, 0, 0.1);
}

.time-range button.active {
  background: #6366f1;
  color: white;
}

/* 图表画布包装器 */
.chart-canvas-wrapper {
  position: relative;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
}

.chart-canvas-wrapper canvas {
  width: 100% !important;
  height: 100% !important;
}

.error-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(252, 165, 165, 0.15);
  border: 1px solid rgba(252, 165, 165, 0.3);
  color: #991b1b;
  padding: 1rem;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  text-align: center;
  max-width: 80%;
}

.error-message svg {
  width: 32px;
  height: 32px;
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

/* 统计卡片 */
.stats-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.8s ease-out;
  animation-delay: 0.2s;
  animation-fill-mode: both;
}

.stats-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: transform 0.3s ease;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.stats-card:hover .card-icon {
  transform: scale(1.1) rotate(5deg);
}

.card-icon svg {
  width: 24px;
  height: 24px;
}

.card-header h3 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  background: rgba(241, 245, 249, 0.7);
  border-radius: 12px;
  padding: 1rem;
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 0.25rem;
}

.stat-label {
  font-size: 0.8rem;
  color: #64748b;
}

.prediction-info {
  background: rgba(241, 245, 249, 0.7);
  border-radius: 12px;
  padding: 1rem;
}

.prediction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
}

.prediction-item:not(:last-child) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.prediction-label {
  font-size: 0.9rem;
  color: #475569;
}

.prediction-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
}

.prediction-trend {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.text-red-500 {
  color: #ef4444;
}

.text-green-500 {
  color: #10b981;
}

.text-gray-500 {
  color: #64748b;
}

.prediction-trend svg {
  width: 16px;
  height: 16px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .temperature-container {
    padding: 1rem 0;
  }

  .page-header {
    margin-bottom: 2rem;
  }

  .content-wrapper {
    padding: 0 1rem;
    gap: 1.5rem;
  }

  .chart-card,
  .stats-card {
    padding: 1.5rem;
  }

  .chart-canvas-wrapper {
    height: 350px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .chart-icon,
  .card-icon {
    width: 40px;
    height: 40px;
  }

  .chart-canvas-wrapper {
    height: 300px;
  }

  .time-range {
    width: 100%;
    justify-content: space-between;
  }

  .time-range button {
    flex: 1;
    text-align: center;
  }
}
</style>