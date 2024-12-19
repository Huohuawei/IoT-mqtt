<template>
  <div class="temperature-data-container">
    <!-- 主图区域 -->
    <div class="temperature-charts">
      <!-- 温度预测曲线 -->
      <div class="chart-container">
        <canvas id="fitCurveChart"></canvas>
      </div>
    </div>

    <!-- 右侧温度数据说明 -->
    <div class="data-description">
      <h3>温度数据说明</h3>
      <p>以下是基于温度数据的处理和预测过程，旨在为后续分析和应用提供准确的温度趋势预测。</p>
      <ul>
        <li><strong>数据清洗</strong>: 检测并去除异常值，剔除异常波动较大的数据，以确保数据的有效性。</li>
        <li><strong>时间聚合</strong>: 对处理后的数据进行时间聚合，计算每个时间段的平均温度，以便更加清晰地展示温度变化趋势。</li>
        <li><strong>正弦波拟合模型</strong>: 基于正弦波模型对温度数据进行预测，生成未来时间点的温度趋势数据，为进一步分析提供可靠输入。</li>
        <li><strong>预测结果</strong>: 展示处理后的文档数据和预测的温度趋势曲线，帮助直观理解未来温度变化趋势。</li>
      </ul>
      <p><strong>注意</strong>: 温度数据预测是基于历史温度数据及拟合模型生成的未来趋势，预测结果可能会受到外部因素的影响。</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import Chart from "chart.js/auto";

// Chart.js实例
let chartInstance = null;
const errorMessage = ref("");

// 从后端获取预测数据
async function fetchPredictionData() {
  try {
    const response = await fetch("http://127.0.0.1:5000/predict", { method: "GET" });

    if (!response.ok) {
      const errorDetail = await response.json();
      throw new Error(`后端错误: ${errorDetail.error || response.statusText}`);
    }

    const result = await response.json();
    const cleanedData = result.cleaned_data || [];
    const predictions = result.predictions || [];

    if (cleanedData.length === 0 || predictions.length === 0) {
      throw new Error("后端返回数据为空或不完整");
    }

    return { cleaned_data: cleanedData, predictions: predictions };
  } catch (error) {
    console.error("Error fetching prediction data:", error);
    throw new Error(error.message || "无法获取数据，请检查后端服务或网络连接。");
  }
}

// 过滤无效数据
function filterValidData(data) {
  return data.filter((item) => item.time && !isNaN(item.temp));
}

// 绘制图表
async function drawChart() {
  const ctx = document.getElementById("fitCurveChart").getContext("2d");

  if (chartInstance) chartInstance.destroy();

  try {
    const result = await fetchPredictionData();
    const cleanedData = filterValidData(result.cleaned_data);
    const predictions = filterValidData(result.predictions);

    if (cleanedData.length === 0 || predictions.length === 0) {
      throw new Error("数据过滤后为空，无法绘制图表。");
    }

    // 创建新的图表实例
    chartInstance = new Chart(ctx, {
      type: "line",
      data: {
        labels: cleanedData.map((d) => d.time), // X轴为时间（每10分钟）
        datasets: [
          {
            label: "处理后的温度数据",
            data: cleanedData.map((d) => d.temp),
            borderColor: "rgba(0, 123, 255, 1)",
            pointBackgroundColor: "rgba(0, 123, 255, 1)",
            borderDash: [5, 5],
            borderWidth: 2,
            fill: false,
          },
          {
            label: "预测温度曲线",
            data: predictions.map((d) => d.temp),
            borderColor: "rgba(255, 99, 132, 1)",
            pointBackgroundColor: "rgba(255, 99, 132, 1)",
            borderWidth: 2,
            fill: false,
          },
        ],
      },
      options: {
        plugins: {
          legend: { display: true },
        },
        scales: {
          x: { title: { display: true, text: "时间 (每10分钟)" } },
          y: { title: { display: true, text: "温度 (°C)" } },
        },
        responsive: true,
        maintainAspectRatio: false,
      },
    });
  } catch (error) {
    console.error("Chart Drawing Error:", error);
    errorMessage.value = error.message || "绘制图表失败，请重试。";
  }
}

// 页面加载时绘制图表
onMounted(() => {
  drawChart();
});
</script>

<style scoped>
.temperature-data-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding: 20px;
}

.temperature-charts {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 30px;
  width: 70%;
}

.chart-container {
  width: 100%;
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
