<template>
  <div class="data-chart">
    <!-- 水平排列图表 -->
    <div class="chart-container-wrapper">
      <!-- 第一个图表及其说明与按钮 -->
      <div class="chart-container">
        <canvas ref="temperatureChart"></canvas>
        <div class="chart-info">
          <h3>温度数据</h3>
          <p>该图展示了 2-6 月期间的平均温度变化。点击按钮查看详细数据。</p>
          <div class="card" @click="goToComponent('/data-chart/temperature')">
            <button>查看温度详细数据</button>
          </div>
        </div>
      </div>

      <!-- 第二个图表及其说明与按钮 -->
      <div class="chart-container">
        <canvas ref="humidityChart"></canvas>
        <div class="chart-info">
          <h3>湿度数据</h3>
          <p>该图展示了 2-6 月期间的平均湿度变化。点击按钮查看详细数据。</p>
          <div class="card" @click="goToComponent('/data-chart/humidity')">
            <button>查看湿度详细数据</button>
          </div>
        </div>
      </div>

      <!-- 第三个图表及其说明与按钮 -->
      <div class="chart-container">
        <canvas ref="pressureChart"></canvas>
        <div class="chart-info">
          <h3>气压数据</h3>
          <p>该图展示了 2-6 月期间的平均气压变化。点击按钮查看详细数据。</p>
          <div class="card" @click="goToComponent('/data-chart/pressure')">
            <button>查看气压详细数据</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import Chart from 'chart.js/auto';

const router = useRouter();  // 获取路由实例

const temperatureChart = ref<HTMLCanvasElement | null>(null);
const humidityChart = ref<HTMLCanvasElement | null>(null);
const pressureChart = ref<HTMLCanvasElement | null>(null);

// 跳转到指定路径
const goToComponent = (path: string) => {
  console.log("Navigating to:", path);  // 打印跳转路径，确认路径是否正确
  router.push(path);
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

  // 将数据按月份分组
  data.forEach((item) => {
    const month = new Date(item.date).getMonth() + 1; // 获取月份 (1-12)
    if (!monthlyData[month]) {
      monthlyData[month] = [];
    }
    monthlyData[month].push(item[type]); // 根据传入的类型推断数据（温度、湿度或压强）
  });

  // 计算每个月的平均值
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
const createChart = (canvasRef: any, data: any, label: string) => {
  if (!canvasRef.value) return;

  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((item: any) => `${item.month}月`), // X轴为2-6月
      datasets: [
        {
          label,
          data: data.map((item: any) => item.average), // Y轴为每月的平均值
          borderColor: 'rgba(75, 192, 192, 1)',
          fill: false,
          tension: 0.1,
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
      },
      scales: {
        y: {
          beginAtZero: false,
        },
      },
    },
  });
};

onMounted(async () => {
  // 获取温度、湿度和压强数据
  const temperatureData = await fetchData('temperature');
  const humidityData = await fetchData('humidity');
  const pressureData = await fetchData('pressure');

  // 计算每月的平均值
  const monthlyTemperatureData = calculateMonthlyAverage(temperatureData, 'temperature');
  const monthlyHumidityData = calculateMonthlyAverage(humidityData, 'humidity');
  const monthlyPressureData = calculateMonthlyAverage(pressureData, 'pressure');

  // 创建温度图表
  createChart(temperatureChart, monthlyTemperatureData, '2-6月平均温度（单位：摄氏度℃）');
  // 创建湿度图表
  createChart(humidityChart, monthlyHumidityData, '2-6月平均湿度（单位：%）');
  // 创建压强图表
  createChart(pressureChart, monthlyPressureData, '2-6月平均气压（单位：帕pa）');
});
</script>

<style scoped>
.data-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  min-height: 100vh;
  background-color: #f4f4f9;
}

.chart-container-wrapper {
  display: flex;
  justify-content: space-around;
  gap: 20px;
  width: 100%;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.chart-container {
  width: 30%; /* 每个图表容器宽度为30% */
  height: 500px;
  background-color: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chart-container canvas {
  width: 100%;
  height: 80%;
}

.chart-info {
  width: 100%;
  text-align: center;
  padding: 10px;
}

.chart-info h3 {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.chart-info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.card {
  margin-top: 15px;
  text-align: center;
}

button {
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s ease-in-out, transform 0.2s;
  min-width: 160px;
}

button:hover {
  background-color: #45a049;
  transform: scale(1.05);
}

button:active {
  transform: scale(0.98);
}

@media (max-width: 1200px) {
  .chart-container-wrapper {
    flex-direction: column;
    align-items: center;
  }

  .chart-container {
    width: 80%;
    margin-bottom: 20px;
  }
}

@media (max-width: 768px) {
  .chart-container {
    width: 100%;
  }

  button {
    min-width: 140px;
  }
}

@media (max-width: 480px) {
  .chart-container {
    width: 100%;
  }

  button {
    min-width: 120px;
  }
}
</style>
