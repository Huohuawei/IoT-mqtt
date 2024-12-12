<template>
    <div class="fit-curve">
      <h2>拟合曲线</h2>
      <p>此处绘制拟合曲线，用于预测未来趋势。</p>
      <canvas id="fitCurveChart"></canvas>
    </div>
  </template>
  
  <script setup>
  console.log('FitCurve component mounted.');
  </script>
  
  <style>
  .fit-curve {
    background: #fef7e0;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    text-align: center;
  }
  </style>
  