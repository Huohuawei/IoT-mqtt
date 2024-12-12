<template>
  <div class="home">
    <div class="home-content">
      <!-- 左侧内容 -->
      <div class="left-panel">
        <DescriptiveInfo />
      </div>

      <!-- 右侧内容 -->
      <div class="right-panel">
        <DataChart />
        <FitCurve />
      </div>
    </div>
  </div>
</template>

<script setup>
import DataChart from './DataChart.vue';
import DescriptiveInfo from './DescriptiveInfo.vue';
import FitCurve from './FitCurve.vue';
</script>

<style>
/* 让页面内容填满整个屏幕 */
body, html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.home {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background-color: #f8f9fa;
}

.home-content {
  display: flex;
  height: 100%;
}

/* 左侧面板，占30%宽度 */
.left-panel {
  width: 30%;
  background-color: #e7f0fd;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 右侧面板，占70%宽度，包含两个组件 */
.right-panel {
  width: 70%;
  background-color: #f3f4f6;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
