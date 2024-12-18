<template>
    <div class="data-chart-container">
        <!-- 主图区域 -->
        <div class="charts">
            <div class="chart-container-wrapper">
                <!-- 折线图 -->
                <div class="chart-container">
                    <canvas ref="temperatureLineChart"></canvas>
                </div>
                <!-- 每日最高最低气温图 -->
                <div class="chart-container">
                    <canvas ref="temperatureAreaChart"></canvas>
                </div>
            </div>
            <div class="chart-container-wrapper">
                <!-- 直方图 -->
                <div class="chart-container">
                    <canvas ref="temperatureHistogram"></canvas>
                </div>

                <!-- 面积图 -->
                <div class="chart-container1">
                    <canvas ref="temperatureAreaChart1"></canvas>
                </div>

            </div>
        </div>

        <!-- 右侧温度数据说明 -->
        <div class="data-description">
            <h3>气温数据说明</h3>
            <p>以下是基于过去几个月的每日平均气温数据，展示了气温变化趋势、分布情况以及波动范围。这些图表帮助我们分析气温随时间的变化，了解季节性变化和气候模式。</p>
            <ul>
              <li><strong>折线图</strong>: 显示每日气温的变化趋势，可以帮助我们观察气温随时间的波动情况，分析是否存在周期性变化或突发的温度变化。</li>
              <li><strong>直方图</strong>: 展示气温的分布情况，按气温区间进行统计。直方图可以帮助我们了解气温的集中分布范围，分析温度的常见范围，以及温差的分布特点。</li>
              <li><strong>面积图</strong>: 强调气温波动的变化趋势，填充了气温曲线下方的区域。这张图表特别有助于观察气温的极值变化，并且清晰地突出气温在特定时间段内的剧烈变化。</li>
              <li><strong>气温最大值最小值图</strong>: 显示每日的最高气温和最低气温，填充它们之间的区域。该图表有助于我们观察气温波动范围，特别是在极端天气条件下，气温的变化幅度。</li>
            </ul>
            <p><strong>注意</strong>: 气温数据是根据每天的实际测量结果计算得出的平均值。气温的变化受到季节、天气和气候模式等多种因素的影响，因此在分析气温趋势时应考虑这些外部因素。</p>
          </div>
          
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';


const temperatureLineChart = ref<HTMLCanvasElement | null>(null);
const temperatureHistogram = ref<HTMLCanvasElement | null>(null);
const temperatureAreaChart = ref<HTMLCanvasElement | null>(null);
const temperatureAreaChart1 = ref<HTMLCanvasElement | null>(null);

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

// 按日期分组并计算每日的最高、最低和平均温度
const calculateDailyStats = (data: any[]) => {
    const dailyData: { [key: string]: number[] } = {};

    // 将数据按日期分组
    data.forEach((item) => {
        const date = new Date(item.date);
        const formattedDate = `${date.getMonth() + 1}月${date.getDate()}日`; // 格式化为：2月1日

        if (!dailyData[formattedDate]) {
            dailyData[formattedDate] = [];
        }
        dailyData[formattedDate].push(item.temperature);
    });

    // 计算每日的最高、最低和平均温度
    const dailyStats: { date: string, max: number, min: number, average: number }[] = [];
    Object.keys(dailyData).forEach((date) => {
        const temps = dailyData[date];
        const max = Math.max(...temps);
        const min = Math.min(...temps);
        const average = temps.reduce((acc, val) => acc + val, 0) / temps.length;
        dailyStats.push({ date, max, min, average });
    });

    // 按日期排序
    dailyStats.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

    return dailyStats;
};

// 创建折线图
const createLineChart = (canvasRef: any, data: any, label: string) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((item: any) => item.date),
            datasets: [
                {
                    label,
                    data: data.map((item: any) => item.average),
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

// 创建直方图
const createHistogram = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    const temperatureValues = data.map((item: any) => item.average);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['0-5°C', '5-10°C', '10-15°C', '15-20°C', '20-25°C', '25-30°C', '30°C+'],
            datasets: [{
                label: '日平均温度分布',
                data: [
                    temperatureValues.filter((val: number) => val >= 0 && val < 5).length,
                    temperatureValues.filter((val: number) => val >= 5 && val < 10).length,
                    temperatureValues.filter((val: number) => val >= 10 && val < 15).length,
                    temperatureValues.filter((val: number) => val >= 15 && val < 20).length,
                    temperatureValues.filter((val: number) => val >= 20 && val < 25).length,
                    temperatureValues.filter((val: number) => val >= 25 && val < 30).length,
                    temperatureValues.filter((val: number) => val >= 30).length
                ],
                backgroundColor: 'rgba(75, 192, 192, 0.5)',
                borderColor: 'rgba(75, 192, 192, 1)',
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

// 创建每日最高最低气温图
const createAreaChart = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((item: any) => item.date),
            datasets: [
                {
                    label: '每日最高气温',
                    data: data.map((item: any) => item.max),
                    borderColor: 'rgba(255, 99, 132, 1)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    fill: true,
                    tension: 0.4
                },
                {
                    label: '每日最低气温',
                    data: data.map((item: any) => item.min),
                    borderColor: 'rgba(54, 162, 235, 1)',
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
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
// 创建每日最高、最低气温
const createAreaChart1 = (canvasRef: any, data: any) => {
    if (!canvasRef.value) return;

    const ctx = canvasRef.value.getContext('2d');
    if (!ctx) return;

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((item: any) => item.date),
            datasets: [{
                label: '气温变化趋势',
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

onMounted(async () => {
    // 获取温度数据
    const temperatureData = await fetchData('temperature');

    // 计算每日的最高、最低和平均温度
    const dailyTemperatureData = calculateDailyStats(temperatureData);

    // 创建不同类型的图表
    createLineChart(temperatureLineChart, dailyTemperatureData, '每日温度变化趋势');
    createHistogram(temperatureHistogram, dailyTemperatureData);
    createAreaChart(temperatureAreaChart, dailyTemperatureData);
    createAreaChart1(temperatureAreaChart1, dailyTemperatureData);


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
.chart-container1 {
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
