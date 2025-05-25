<template>
    <div class="data-chart-container">
        <div class="charts">
            <div class="chart-container-wrapper">
                <div class="chart-container">
                    <canvas ref="temperatureLineChart"></canvas>
                </div>
                <div class="chart-container">
                    <canvas ref="temperatureAreaChart"></canvas>
                </div>
            </div>
            <div class="chart-container-wrapper">
                <div class="chart-container">
                    <canvas ref="temperatureHistogram"></canvas>
                </div>
                <div class="chart-container">
                    <canvas ref="temperatureAreaChart1"></canvas>
                </div>
            </div>
        </div>

        <div class="data-description">
            <div class="description-card">
                <h3 class="description-title">气温数据分析</h3>
                <div class="description-content">
                    <p>基于过去几个月的每日气温数据，可视化展示了气温变化趋势、分布情况及波动范围。</p>

                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M3 17L9 11L13 15L21 7" stroke="currentColor" stroke-width="2"
                                    stroke-linecap="round" />
                            </svg>
                        </div>
                        <div class="feature-text">
                            <strong>折线图</strong>: 直观展示每日气温变化趋势，识别季节性变化和异常波动。
                        </div>
                    </div>

                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <rect x="3" y="12" width="4" height="8" rx="1" fill="currentColor" />
                                <rect x="9" y="8" width="4" height="12" rx="1" fill="currentColor" />
                                <rect x="15" y="4" width="4" height="16" rx="1" fill="currentColor" />
                            </svg>
                        </div>
                        <div class="feature-text">
                            <strong>直方图</strong>: 分析气温分布情况，了解常见温度范围和气候特点。
                        </div>
                    </div>

                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M3 17L9 11L13 15L21 7" stroke="currentColor" stroke-width="2"
                                    stroke-linecap="round" />
                                <path d="M3 17L9 11L13 15L21 7V17H3Z" fill="currentColor" fill-opacity="0.2" />
                            </svg>
                        </div>
                        <div class="feature-text">
                            <strong>面积图</strong>: 突出气温波动变化，识别极端天气时段。
                        </div>
                    </div>

                    <div class="feature-item">
                        <div class="feature-icon">
                            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M3 17L9 11L13 15L21 7" stroke="currentColor" stroke-width="2"
                                    stroke-linecap="round" />
                                <path d="M3 12L9 6L13 10L21 2" stroke="currentColor" stroke-width="2"
                                    stroke-linecap="round" />
                                <path d="M3 17L9 11L13 15L21 7V17H3Z" fill="currentColor" fill-opacity="0.2" />
                            </svg>
                        </div>
                        <div class="feature-text">
                            <strong>极值图</strong>: 显示每日最高/最低气温，分析昼夜温差变化。
                        </div>
                    </div>

                    <div class="note-box">
                        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path
                                d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z"
                                stroke="currentColor" stroke-width="2" />
                            <path d="M12 8V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                            <path d="M12 16H12.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                        </svg>
                        <p>气温数据受季节、天气和气候模式影响，分析时需综合考虑。</p>
                    </div>
                </div>
            </div>
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

const fetchData = async (type: string) => {
    try {
        const response = await axios.get(`http://localhost:8080/api/data/${type}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching ${type} data:`, error);
        return [];
    }
};

const calculateDailyStats = (data: any[]) => {
    const dailyData: { [key: string]: number[] } = {};

    data.forEach((item) => {
        const date = new Date(item.date);
        const formattedDate = `${date.getMonth() + 1}月${date.getDate()}日`;

        if (!dailyData[formattedDate]) {
            dailyData[formattedDate] = [];
        }
        dailyData[formattedDate].push(item.temperature);
    });

    const dailyStats: { date: string, max: number, min: number, average: number }[] = [];
    Object.keys(dailyData).forEach((date) => {
        const temps = dailyData[date];
        const max = Math.max(...temps);
        const min = Math.min(...temps);
        const average = temps.reduce((acc, val) => acc + val, 0) / temps.length;
        dailyStats.push({ date, max, min, average });
    });

    dailyStats.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    return dailyStats;
};

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
                    borderColor: 'rgba(239, 68, 68, 1)',
                    backgroundColor: 'rgba(239, 68, 68, 0.1)',
                    fill: true,
                    tension: 0.3,
                    borderWidth: 2,
                    pointBackgroundColor: 'rgba(255, 255, 255, 1)',
                    pointBorderColor: 'rgba(239, 68, 68, 1)',
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
            ],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#64748b',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(30, 41, 59, 0.9)',
                    titleColor: '#f8fafc',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(239, 68, 68, 0.5)',
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                },
                y: {
                    beginAtZero: false,
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                }
            }
        },
    });
};

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
                backgroundColor: [
                    'rgba(239, 68, 68, 0.7)',
                    'rgba(220, 38, 38, 0.7)',
                    'rgba(239, 68, 68, 0.7)',
                    'rgba(220, 38, 38, 0.7)',
                    'rgba(239, 68, 68, 0.7)',
                    'rgba(220, 38, 38, 0.7)',
                    'rgba(239, 68, 68, 0.7)'
                ],
                borderColor: [
                    'rgba(239, 68, 68, 1)',
                    'rgba(220, 38, 38, 1)',
                    'rgba(239, 68, 68, 1)',
                    'rgba(220, 38, 38, 1)',
                    'rgaku(239, 68, 68, 1)',
                    'rgba(220, 38, 38, 1)',
                    'rgba(239, 68, 68, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(30, 41, 59, 0.9)',
                    titleColor: '#f8fafc',
                    bodyColor: '#e2e8f0'
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: '#64748b'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                }
            }
        }
    });
};

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
                    borderColor: 'rgba(239, 68, 68, 1)',
                    backgroundColor: 'rgba(239, 68, 68, 0.1)',
                    fill: true,
                    tension: 0.4,
                    borderWidth: 2,
                    pointBackgroundColor: 'rgba(255, 255, 255, 1)',
                    pointBorderColor: 'rgba(239, 68, 68, 1)'
                },
                {
                    label: '每日最低气温',
                    data: data.map((item: any) => item.min),
                    borderColor: 'rgba(220, 38, 38, 1)',
                    backgroundColor: 'rgba(220, 38, 38, 0.1)',
                    fill: true,
                    tension: 0.4,
                    borderWidth: 2,
                    pointBackgroundColor: 'rgba(255, 255, 255, 1)',
                    pointBorderColor: 'rgba(220, 38, 38, 1)'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#64748b',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(30, 41, 59, 0.9)',
                    titleColor: '#f8fafc',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(239, 68, 68, 0.5)',
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                },
                y: {
                    beginAtZero: false,
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                }
            }
        }
    });
};

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
                borderColor: 'rgba(239, 68, 68, 1)',
                backgroundColor: 'rgba(239, 68, 68, 0.3)',
                fill: true,
                tension: 0.4,
                borderWidth: 2,
                pointBackgroundColor: 'rgba(255, 255, 255, 1)',
                pointBorderColor: 'rgba(239, 68, 68, 1)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        color: '#64748b',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(30, 41, 59, 0.9)',
                    titleColor: '#f8fafc',
                    bodyColor: '#e2e8f0',
                    borderColor: 'rgba(239, 68, 68, 0.5)',
                    borderWidth: 1
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgaku(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                },
                y: {
                    beginAtZero: false,
                    grid: {
                        color: 'rgba(226, 232, 240, 0.2)'
                    },
                    ticks: {
                        color: '#64748b'
                    }
                }
            }
        }
    });
};
onMounted(async () => {
    const temperatureData = await fetchData('temperature');
    const dailyTemperatureData = calculateDailyStats(temperatureData);

    createLineChart(temperatureLineChart, dailyTemperatureData, '每日温度变化趋势');
    createHistogram(temperatureHistogram, dailyTemperatureData);
    createAreaChart(temperatureAreaChart, dailyTemperatureData);
    createAreaChart1(temperatureAreaChart1, dailyTemperatureData);
});
</script>

<style scoped>
.data-chart-container {
    display: flex;
    min-height: 100vh;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    padding: 2rem;
    gap: 2rem;
}

.charts {
    flex: 3;
    display: flex;
    flex-direction: column;
    gap: 2rem;
}

.chart-container-wrapper {
    display: flex;
    gap: 2rem;
    height: 50%;
}

.chart-container {
    flex: 1;
    background: white;
    border-radius: 1rem;
    padding: 1.5rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.chart-container:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
}

.data-description {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.description-card {
    background: white;
    border-radius: 1rem;
    padding: 2rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    height: 100%;
    display: flex;
    flex-direction: column;
}

.description-title {
    color: #1e293b;
    font-size: 1.5rem;
    font-weight: 600;
    margin-bottom: 1.5rem;
    position: relative;
    padding-bottom: 0.75rem;
}

.description-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 3rem;
    height: 3px;
    background: linear-gradient(90deg, #f00c17, #f00c17);
    border-radius: 3px;
}

.description-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    color: #475569;
    line-height: 1.6;
}

.feature-item {
    display: flex;
    gap: 1rem;
    align-items: flex-start;
}

.feature-icon {
    flex-shrink: 0;
    width: 2.5rem;
    height: 2.5rem;
    background: linear-gradient(135deg, #f00c17, #f00c17);
    border-radius: 0.75rem;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    padding: 0.5rem;
}

.feature-text {
    flex: 1;
    font-size: 0.95rem;
}

.note-box {
    margin-top: auto;
    background: #f8fafc;
    border-radius: 0.75rem;
    padding: 1rem;
    display: flex;
    gap: 0.75rem;
    align-items: flex-start;
    border-left: 4px solid #f00c17;
}

.note-box svg {
    flex-shrink: 0;
    width: 1.25rem;
    height: 1.25rem;
    color: #f00c17;
    margin-top: 0.125rem;
}

.note-box p {
    font-size: 0.9rem;
    color: #64748b;
}

@media (max-width: 1024px) {
    .data-chart-container {
        flex-direction: column;
    }

    .chart-container-wrapper {
        flex-direction: column;
        height: auto;
    }
}

@media (max-width: 768px) {
    .data-chart-container {
        padding: 1rem;
    }

    .chart-container {
        padding: 1rem;
    }

    .description-card {
        padding: 1.5rem;
    }
}
</style>