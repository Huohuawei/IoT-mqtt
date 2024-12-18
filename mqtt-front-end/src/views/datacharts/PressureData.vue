<template>
    <div class="data-chart-container">
        <!-- 主图区域 -->
        <div class="charts">
            <div class="chart-container-wrapper">
                <!-- 压强折线图 -->
                <div class="chart-container">
                    <canvas ref="pressureLineChart"></canvas>
                </div>
                <!-- 最高最低气压图 -->
                <div class="chart-container">
                    <canvas ref="pressureMaxMinChart"></canvas>
                </div>
            </div>
            <div class="chart-container-wrapper">
                <!-- 压强直方图 -->
                <div class="chart-container">
                    <canvas ref="pressureHistogram"></canvas>
                </div>
                <!-- 压强面积图 -->
                <div class="chart-container">
                    <canvas ref="pressureAreaChart"></canvas>
                </div>
            </div>
        </div>

        <!-- 右侧压强数据说明 -->
        <div class="data-description">
            <h3>气压数据说明</h3>
            <p>以下是基于过去几个月的每日平均气压数据，展示了气压变化趋势、分布情况以及波动范围。气压的变化通常与天气变化、季节交替等因素密切相关，可以帮助我们预测天气模式和气候变化。</p>
            <ul>
              <li><strong>折线图</strong>: 显示每日气压的变化趋势。通过折线图，您可以观察到气压随时间的波动和变化，并分析是否有季节性变化或异常天气引起的气压波动。</li>
              <li><strong>直方图</strong>: 展示气压的分布情况，按气压区间进行统计。直方图有助于我们了解气压的常见范围，判断气压变化的频率，以及气压是否存在某种规律。</li>
              <li><strong>面积图</strong>: 强调气压波动的变化趋势，填充了气压曲线下方的区域。面积图能够突出气压的波动范围，帮助分析气压变化是否剧烈，以及其可能对天气产生的影响。</li>
              <li><strong>气压最大值最小值图</strong>: 显示每日的最高气压和最低气压，填充它们之间的区域。此图表有助于观察气压的波动范围，了解高气压和低气压的分布情况，预测可能的天气变化。</li>
            </ul>
            <p><strong>注意</strong>: 气压数据是根据每天的实际测量结果计算得出的平均值。气压的变化与天气系统的形成和移动密切相关，因此需要结合其他气象因素（如温度、湿度等）共同分析。</p>
          </div>
    </div>
</template>


<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';

const pressureLineChart = ref<HTMLCanvasElement | null>(null);
const pressureHistogram = ref<HTMLCanvasElement | null>(null);
const pressureAreaChart = ref<HTMLCanvasElement | null>(null);
const pressureMaxMinChart = ref<HTMLCanvasElement | null>(null);

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

// 按日期分组并计算每一天的平均、最高、最低压强
const calculateDailyAverage = (data: any[]) => {
    const dailyData: { [key: string]: number[] } = {};

    // 将数据按日期分组
    data.forEach((item) => {
        const date = new Date(item.date);
        const formattedDate = `${date.getMonth() + 1}月${date.getDate()}日`; // 格式化为：2月1日

        if (!dailyData[formattedDate]) {
            dailyData[formattedDate] = [];
        }
        dailyData[formattedDate].push(item.pressure); // 按压强分组
    });

    // 计算每一天的平均压强
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

// 创建压强折线图
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
                    data: data.map((item: any) => item.average), // Y轴为每一天的平均压强
                    borderColor: 'rgba(255, 99, 132, 1)',
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

// 创建压强直方图
const createHistogram = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    const pressureValues = data.map((item: any) => item.average);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['950-960 hPa', '960-970 hPa', '970-980 hPa', '980-990 hPa', '990-1000 hPa', '1000-1010 hPa', '1010 hPa+'],
            datasets: [{
                label: '压强分布',
                data: [
                    pressureValues.filter((val: number) => val >= 950 && val < 960).length,
                    pressureValues.filter((val: number) => val >= 960 && val < 970).length,
                    pressureValues.filter((val: number) => val >= 970 && val < 980).length,
                    pressureValues.filter((val: number) => val >= 980 && val < 990).length,
                    pressureValues.filter((val: number) => val >= 990 && val < 1000).length,
                    pressureValues.filter((val: number) => val >= 1000 && val < 1010).length,
                    pressureValues.filter((val: number) => val >= 1010).length
                ],
                backgroundColor: 'rgba(255, 99, 132, 0.5)',
                borderColor: 'rgba(255, 99, 132, 1)',
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

// 创建压强面积图
const createAreaChart = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((item: any) => item.date),
            datasets: [{
                label: '压强变化趋势',
                data: data.map((item: any) => item.average),
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
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

// 创建最高最低气压图
const createMaxMinPressureChart = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;
    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((item: any) => item.date),
            datasets: [
                {
                    label: '每日最高气压',
                    data: data.map((item: any) => item.max),
                    borderColor: 'rgba(75, 192, 192, 1)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    fill: true,
                    tension: 0.4
                },
                {
                    label: '每日最低气压',
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
    // 获取压强数据
    const pressureData = await fetchData('pressure');

    // 计算每日的平均压强
    const dailyPressureData = calculateDailyAverage(pressureData);

    // 创建不同类型的压强图表
    createLineChart(pressureLineChart, dailyPressureData, '2月--6月，每日平均压强（单位hPa）');
    createHistogram(pressureHistogram, dailyPressureData);
    createAreaChart(pressureAreaChart, dailyPressureData);
    createMaxMinPressureChart(pressureMaxMinChart, dailyPressureData);
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