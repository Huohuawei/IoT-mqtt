<template>
  <div class="fit-curve">
    <h2>拟合曲线与预测点</h2>
    <p>此处绘制拟合曲线及预测点，用于预测未来趋势。</p>
    <div class="button-container">
      <button @click="drawChart">绘制曲线</button>
    </div>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    <canvas id="fitCurveChart"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import Chart from "chart.js/auto";
import regression from "regression";

let chartInstance = null;
const errorMessage = ref("");
const successMessage = ref("");

// 从文件中读取温度数据并转换为数组
async function fetchTemperatureData() {
  try {
    const response = await fetch('/temperature.txt');
    const rawText = await response.text();
    const rawObjects = rawText
      .trim()
      .split('\n')
      .map((line) => JSON.parse(line));

    const mergedData = rawObjects.reduce((acc, obj) => ({ ...acc, ...obj }), {});

    const data = Object.entries(mergedData).map(([time, temp]) => ({
      time: new Date(time).getTime(),
      temp: parseFloat(temp),
    }));

    return data.sort((a, b) => a.time - b.time);
  } catch (error) {
    console.error("Failed to fetch temperature data:", error);
    return [];
  }
}

// 原始的 removeOutliers 函数，保留异常点过滤逻辑
function removeOutliers(data) {
  if (data.length < 2) return data;

  return data.filter((entry, index) => {
    if (index === 0) return true; // 保留第一个点
    const rateOfChange = Math.abs(entry.temp - data[index - 1].temp) / ((entry.time - data[index - 1].time) / 3600000);
    return rateOfChange <= 50; // 使用速率变化阈值
  });
}

async function drawChart() {
  const ctx = document.getElementById("fitCurveChart").getContext("2d");
  const temperatureData = await fetchTemperatureData();

  if (chartInstance) chartInstance.destroy();

  if (temperatureData.length === 0) {
    errorMessage.value = "数据为空，无法绘制图表。";
    successMessage.value = "";
    return;
  }

  const filteredData = removeOutliers(temperatureData);

  if (filteredData.length < 2) {
    errorMessage.value = `有效数据不足，无法进行拟合。\n总数据点数: ${temperatureData.length}，有效数据点数: ${filteredData.length}`;
    successMessage.value = "";
    return;
  }

  errorMessage.value = "";

  // 确定时间和温度的范围
  const startTime = filteredData[0].time;
  const maxTime = Math.max(...filteredData.map((entry) => entry.time));
  const minTemp = Math.min(...filteredData.map((entry) => entry.temp));
  const maxTemp = Math.max(...filteredData.map((entry) => entry.temp));

  // 对时间和温度进行归一化
  const normalizedData = filteredData.map((entry) => [
    (entry.time - startTime) / (maxTime - startTime), // 时间归一化到 [0, 1]
    (entry.temp - minTemp) / (maxTemp - minTemp), // 温度归一化到 [0, 1]
  ]);

  try {
    const resultPolynomial = regression.polynomial(normalizedData, { order: 3 });

    // 将拟合结果反归一化到原始温度范围
    const fittedDataPolynomial = resultPolynomial.points.map(([time, temp]) => ({
      time: new Date(startTime + time * (maxTime - startTime)).toLocaleTimeString("zh-CN", { hour12: false }),
      temp: temp * (maxTemp - minTemp) + minTemp, // 反归一化温度
    }));

    successMessage.value = `拟合成功！有效数据点数: ${filteredData.length}`;

    chartInstance = new Chart(ctx, {
      type: "line",
      data: {
        labels: filteredData.map((entry) => new Date(entry.time).toLocaleTimeString("zh-CN", { hour12: false })),
        datasets: [
          {
            label: "原始数据",
            data: filteredData.map((d) => d.temp),
            borderColor: "rgba(75, 192, 192, 0)", // 删除原始数据的连线
            pointBackgroundColor: "rgba(75, 192, 192, 1)",
            fill: false,
          },
          {
            label: "三次多项式拟合",
            data: fittedDataPolynomial.map((d) => d.temp),
            borderColor: "rgba(255, 0, 0, 1)",
            borderDash: [5, 5],
            borderWidth: 2,
          },
        ],
      },
      options: {
        plugins: {
          legend: { display: true },
        },
        scales: {
          x: { title: { display: true, text: "时间" } },
          y: { title: { display: true, text: "温度 (°C)" } },
        },
      },
    });
  } catch (error) {
    console.error("Error during regression or chart rendering:", error);
    errorMessage.value = "拟合失败，请检查数据质量。";
    successMessage.value = "";
  }
}

onMounted(() => {
  drawChart();
});
</script>

<style scoped>
.fit-curve {
  background: linear-gradient(to bottom, #fdf5e6, #f9e4b7);
  padding: 25px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.button-container {
  margin-bottom: 20px;
}

button {
  background: #4caf50;
  color: white;
  padding: 12px 25px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover {
  background: #45a049;
}

.error-message {
  color: #e53935;
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
}

.success-message {
  color: #43a047;
  font-weight: bold;
  font-size: 16px;
  margin-top: 10px;
}

canvas {
  margin-top: 20px;
  max-width: 100%;
  border: 1px solid #ddd;
  border-radius: 10px;
}
</style>

