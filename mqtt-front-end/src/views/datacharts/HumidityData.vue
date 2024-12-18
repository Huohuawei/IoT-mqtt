<template>
  <div class="data-chart-container">
    <!-- 主图区域 -->
    <div class="charts">
      <div class="chart-container-wrapper">
        <!-- 湿度折线图 -->
        <div class="chart-container">
          <canvas ref="humidityLineChart"></canvas>
        </div>
        <!-- 湿度最大值最小值图表 -->
        <div class="chart-container">
          <canvas ref="humidityMaxMinChart"></canvas>
        </div>
      </div>
      <div class="chart-container-wrapper">
        <!-- 湿度直方图 -->
        <div class="chart-container">
          <canvas ref="humidityHistogram"></canvas>
        </div>
        <!-- 湿度面积图 -->
        <div class="chart-container">
          <canvas ref="humidityAreaChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 右侧湿度数据说明 -->
    <div class="data-description">
      <h3>湿度数据说明</h3>
      <p>以下是基于过去几个月的每日平均湿度数据，展示了湿度变化趋势、分布情况以及波动范围。这些图表可以帮助我们更好地理解环境湿度的变化规律，识别湿度异常波动以及趋势变化。</p>
      <ul>
        <li><strong>折线图</strong>: 显示每日湿度的变化趋势，帮助观察湿度随时间的变化情况。通过折线图可以直观地看到湿度是否存在周期性波动，以及是否存在持续的增减趋势。</li>
        <li><strong>直方图</strong>: 展示湿度的分布情况，按湿度区间进行统计。直方图有助于识别湿度分布的集中程度，查看湿度是否大多数时间处于某个特定范围，或者湿度分布是否较为均匀。</li>
        <li><strong>面积图</strong>: 强调湿度波动的变化趋势，填充了湿度曲线下方的区域。面积图可以更好地突出湿度的上下波动，特别是湿度在某些特定时间段内的剧烈变化，帮助分析湿度异常的可能原因。</li>
        <li><strong>湿度最大值最小值图</strong>: 显示每日的最高湿度和最低湿度，填充它们之间的区域。这张图表有助于观察湿度的波动范围，了解环境的湿度极值，特别是在极端天气条件下的湿度变化。</li>
      </ul>
      <p><strong>注意</strong>: 湿度数据是根据每天的实际测量结果计算得出的平均值。由于湿度受多种因素（如温度、气压、天气变化等）的影响，因此在分析湿度变化时需要综合考虑这些外部因素的作用。</p> 
    </div>
    
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';

const humidityLineChart = ref<HTMLCanvasElement | null>(null);
const humidityHistogram = ref<HTMLCanvasElement | null>(null);
const humidityAreaChart = ref<HTMLCanvasElement | null>(null);
const humidityMaxMinChart = ref<HTMLCanvasElement | null>(null);

// 跳转到指定路径
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

// 按日期分组并计算每一天的平均、最高、最低湿度
const calculateDailyAverage = (data: any[]) => {
  const dailyData: { [key: string]: number[] } = {};

  // 将数据按日期分组
  data.forEach((item) => {
    const date = new Date(item.date);
    const formattedDate = `${date.getMonth() + 1}月${date.getDate()}日`; // 格式化为：2月1日

    if (!dailyData[formattedDate]) {
      dailyData[formattedDate] = [];
    }
    dailyData[formattedDate].push(item.humidity); // 按湿度分组
  });

  // 计算每一天的平均湿度
  const dailyAverages: { date: string, max: number, min: number, average: number }[] = [];
  Object.keys(dailyData).forEach((date) => {
    const temps = dailyData[date];
    const max = Math.max(...temps);
    const min = Math.min(...temps);
    const sum = dailyData[date].reduce((acc, val) => acc + val, 0);
    const average = sum / dailyData[date].length;
    dailyAverages.push({ date, max, min, average });
  });

  // 按日期排序
  dailyAverages.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

  return dailyAverages;
};

// 创建湿度折线图
const createLineChart = (canvasRef: any, data: any, label: string) => {
  if (!canvasRef.value) return;

  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((item: any) => item.date), // X轴为每一天的日期
      datasets: [
        {
          label,
          data: data.map((item: any) => item.average), // Y轴为每一天的平均湿度
          borderColor: 'rgba(153, 102, 255, 1)',
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

// 创建湿度直方图
const createHistogram = (canvasRef: any, data: any) => {
  if (!canvasRef.value) return;

  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  const humidityValues = data.map((item: any) => item.average);

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['0-20%', '20-40%', '40-60%', '60-80%', '80-100%'],
      datasets: [{
        label: '日平均湿度分布',
        data: [
          humidityValues.filter((val: number) => val >= 0 && val < 20).length,
          humidityValues.filter((val: number) => val >= 20 && val < 40).length,
          humidityValues.filter((val: number) => val >= 40 && val < 60).length,
          humidityValues.filter((val: number) => val >= 60 && val < 80).length,
          humidityValues.filter((val: number) => val >= 80).length
        ],
        backgroundColor: 'rgba(153, 102, 255, 0.5)',
        borderColor: 'rgba(153, 102, 255, 1)',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// 创建湿度面积图
const createAreaChart = (canvasRef: any, data: any) => {
  if (!canvasRef.value) return;

  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((item: any) => item.date),
      datasets: [{
        label: '湿度变化趋势',
        data: data.map((item: any) => item.average),
        borderColor: 'rgba(153, 102, 255, 1)',
        backgroundColor: 'rgba(153, 102, 255, 0.2)',
        fill: true,
        tension: 0.4
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top'
        }
      },
      scales: {
        y: {
          beginAtZero: false
        }
      }
    }
  });
};
// 创建湿度最高最低折线图
const createMaxMinHumidityChart = (canvasRef: any, data: any) => {
  if (!canvasRef.value) return;
  const ctx = canvasRef.value.getContext('2d');
  if (!ctx) return;

  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map((item: any) => item.date),
      datasets: [
        {
          label: '每日最高湿度',
          data: data.map((item: any) => item.max),
          borderColor: 'rgba(75, 192, 192, 1)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          fill: true,
          tension: 0.4
        },
        {
          label: '每日最低湿度',
          data: data.map((item: any) => item.min),
          borderColor: 'rgba(153, 102, 255, 1)',
          backgroundColor: 'rgba(153, 102, 255, 0.2)',
          fill: true,
          tension: 0.4
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top'
        }
      },
      scales: {
        y: {
          beginAtZero: false
        }
      }
    }
  });
};

onMounted(async () => {
  // 获取湿度数据
  const humidityData = await fetchData('humidity');

  // 计算每日的平均湿度
  const dailyHumidityData = calculateDailyAverage(humidityData);

  // 创建不同类型的湿度图表
  createLineChart(humidityLineChart, dailyHumidityData, '2月--6月，每日平均湿度（单位%）');
  createHistogram(humidityHistogram, dailyHumidityData);
  createAreaChart(humidityAreaChart, dailyHumidityData);
  createMaxMinHumidityChart(humidityMaxMinChart, dailyHumidityData);
});
</script>

<style scoped>
.data-chart-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 20px;
}

.charts {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 30px;
  width: 70%;
}

.chart-container-wrapper {
  display: flex;
  justify-content: space-between;
  gap: 15px;
}

.chart-container {
  width: 48%;
  height: 400px;
}

.data-description {
  flex: 1;
  width: 28%;
  background-color: #f4f4f4;
  padding: 15px;
  border-radius: 8px;
}

.data-description h3 {
  margin-bottom: 20px;
  font-size: 1.25em;
  font-weight: bold;
}

.data-description ul {
  list-style-type: disc;
  padding-left: 20px;
}

.data-description ul li {
  margin: 10px 0;
}
</style>
