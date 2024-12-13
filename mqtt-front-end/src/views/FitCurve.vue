<template>
  <div class="fit-curve">
    <h2>拟合曲线与预测点</h2>
    <p>此处绘制拟合曲线及预测点，用于预测未来趋势。</p>
    <div class="button-container">
      <button @click="drawChart">绘制曲线</button>
    </div>
    <canvas id="fitCurveChart"></canvas>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import Chart from "chart.js/auto";
import * as math from "math.js"; // 引入 math.js 库

// 从文件中读取温度数据并转换为数组
async function fetchTemperatureData() {
  try {
    const response = await fetch('/temperature.txt');
    const rawText = await response.text(); // 读取文件内容为文本
    const rawObjects = rawText
      .trim() // 去除多余的空白字符
      .split('\n') // 按行分割
      .map((line) => JSON.parse(line)); // 将每行解析为 JSON 对象

    // 合并所有对象为一个时间-温度的键值对数组
    const mergedData = rawObjects.reduce((acc, obj) => ({ ...acc, ...obj }), {});

    // 转换为数组格式并去重
    const data = Object.entries(mergedData).map(([time, temp]) => ({
      time: new Date(time),
      temp: parseFloat(temp),
    }));
    return data.sort((a, b) => a.time - b.time); // 按时间排序
  } catch (error) {
    console.error("Failed to fetch temperature data:", error);
    return [];
  }
}

// 计算多项式拟合
function polynomialFit(x, y, degree = 3) {
  const A = [];
  const b = [];

  for (let i = 0; i < x.length; i++) {
    const row = [];
    for (let j = 0; j <= degree; j++) {
      row.push(Math.pow(x[i], j));
    }
    A.push(row);
    b.push(y[i]);
  }

  try {
    const pseudoInverse = math.pinv(A); // 使用伪逆矩阵
    const coeffs = math.multiply(pseudoInverse, b); // 求解最小二乘解
    return coeffs.reverse(); // 返回从最高次到最低次的系数
  } catch (error) {
    console.error("Error solving polynomial coefficients:", error);
    return [];
  }
}

// 根据拟合系数生成曲线数据
function generateFittedData(coeffs, xRange) {
  return xRange.map((x) => {
    return coeffs.reduce((sum, coeff, index) => sum + coeff * Math.pow(x, coeffs.length - 1 - index), 0);
  });
}

// 根据拟合模型生成未来预测点
function generatePredictionPoints(coeffs, start, count, step) {
  const predictions = [];
  for (let i = 1; i <= count; i++) {
    const x = start + i * step;
    const y = coeffs.reduce((sum, coeff, index) => sum + coeff * Math.pow(x, coeffs.length - 1 - index), 0);
    predictions.push({ x, y });
  }
  return predictions;
}

// 绘制曲线图
let chartInstance = null;

async function drawChart() {
  const ctx = document.getElementById("fitCurveChart").getContext("2d");

  // 如果有已有图表实例，先销毁
  if (chartInstance) {
    chartInstance.destroy();
  }

  const temperatureData = await fetchTemperatureData();

  if (temperatureData.length === 0) {
    console.error("No data to draw the chart.");
    return;
  }

  const x = temperatureData.map((entry) => entry.time.getTime());
  const y = temperatureData.map((entry) => entry.temp);
  const degree = 3;
  // const coeffs = polynomialFit(x, y, degree);

  // const xRange = Array.from(
  //   { length: 100 },
  //   (_, i) => Math.min(...x) + (i * (Math.max(...x) - Math.min(...x))) / 99
  // );
  // const fittedY = generateFittedData(coeffs, xRange);

  // const predictionStep = (Math.max(...x) - Math.min(...x)) / x.length;
  // const predictionPoints = generatePredictionPoints(
  //   coeffs,
  //   Math.max(...x),
  //   10,
  //   predictionStep
  // );

  chartInstance = new Chart(ctx, {
    type: "line",
    data: {
      labels: [
        ...temperatureData.map((entry) =>
          entry.time.toLocaleTimeString("en-US", { hour12: false })
        ),
        // ...predictionPoints.map((point) =>
        //   new Date(point.x).toLocaleTimeString("en-US", { hour12: false })
        // ),
      ],
      datasets: [
        {
          label: "原始数据",
          data: y,
          borderColor: "rgba(75, 192, 192, 1)",
          fill: false,
          tension: 0.4,
        },
        // {
        //   label: "拟合曲线",
        //   data: fittedY,
        //   borderColor: "rgba(255, 99, 132, 1)",
        //   fill: false,
        //   borderDash: [5, 5],
        // },
        // {
        //   label: "预测点",
        //   data: predictionPoints.map((point) => point.y),
        //   borderColor: "rgba(255, 165, 0, 1)",
        //   fill: false,
        //   pointStyle: "circle",
        //   pointRadius: 5,
        //   showLine: false,
        // },
      ],
    },
    options: {
      plugins: {
        legend: {
          display: true,
        },
      },
      scales: {
        x: {
          ticks: {
            maxTicksLimit: 10,
          },
          title: {
            display: true,
            text: "时间",
          },
        },
        y: {
          title: {
            display: true,
            text: "温度 (°C)",
          },
        },
      },
    },
  });
}

// 初始化图表
onMounted(() => {
  drawChart();
});
</script>

<style scoped>
.fit-curve {
  background: #fef7e0;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

button {
  background: #007bff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background: #0056b3;
}
</style>

