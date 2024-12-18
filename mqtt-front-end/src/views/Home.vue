<template>
  <div class="home-container">
    <!-- 页面标题 -->
    <h1 class="page-title">温度/湿度/气压数据发布订阅与分析处理系统</h1>
    <p class="page-description">欢迎来到数据分析与处理系统，查看不同环境数据并进行订阅与预测分析。</p>

    <!-- 卡片容器 -->
    <div class="cards-container">
      <!-- 数据图表组件卡片 -->
      <div class="card" @click="goToComponent('/data-chart')">
        <img src="@/assets/2.jpg" alt="Data Chart" class="card-img" />
        <h3 class="card-title">数据分析处理展示</h3>
        <p class="card-description">
          该模块提供了数据图表的展示功能。通过将历史数据按日期分组，生成各类图表（如折线图、直方图、面积图）展示数据的总体趋势。
        </p>
      </div>

      <!-- MQTT 信息展示组件卡片 -->
      <div class="card" @click="goToComponent('/descriptive-info')">
        <img src="@/assets/2.jpg" alt="Descriptive Info" class="card-img" />
        <h3 class="card-title">消息发布与订阅</h3>
        <p class="card-description">
          本模块实现了基于 MQTT 协议的消息发布与订阅功能。您可以通过 MQTT 协议与服务器进行实时数据交换。
        </p>
      </div>

      <!-- 数据拟合组件卡片 -->
      <div class="card" @click="goToComponent('/fit-curve')">
        <img src="@/assets/2.jpg" alt="Fit Curve" class="card-img" />
        <h3 class="card-title">数据拟合与预测</h3>
        <p class="card-description">
          数据拟合与预测模块基于历史温度数据进行分析，通过拟合模型预测未来的天气变化趋势。
        </p>
      </div>
    </div>

  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

// 使用 Vue Router 进行页面跳转
const router = useRouter();

const goToComponent = (path) => {
  router.push(path); // 跳转到指定路由路径
};
</script>

<style scoped>
/* 页面容器样式，确保全屏，并设置背景图片 */
.home-container {
  width: 100vw; /* 视口宽度 */
  height: 100vh; /* 视口高度 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-image: url("@/assets/3.jpg"); /* 设置背景图片 */
  background-size: cover; /* 确保图片完全覆盖整个页面 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat; /* 防止图片重复 */
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
  position: relative; /* 允许使用绝对定位遮罩层 */
}



/* 页面标题样式 */
.page-title {
  font-size: 2.5rem;
  color: rgb(248, 248, 248);
  margin-top: 20px;
  z-index: 1; /* 确保标题层级高于遮罩层 */
}

/* 页面描述 */
.page-description {
  font-size: 1.2rem;
  color: rgb(249, 248, 248);
  margin-bottom: 30px;
  z-index: 1;
}

/* 卡片容器 */
.cards-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
  flex-wrap: wrap;
  padding: 10px;
  box-sizing: border-box;
  z-index: 1; /* 确保卡片容器位于遮罩层上方 */
}

/* 卡片样式 */
.card {
  width: 400px;
  height: 450px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.card:hover {
  transform: translateY(-8px);
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.2);
}

/* 卡片内图片 */
.card-img {
  width: 100%;
  height: 60%;
  object-fit: cover;
  border-radius: 8px;
}

/* 卡片标题 */
.card-title {
  font-size: 1.5rem;
  color: #333;
  margin: 10px 0;
}

/* 卡片描述 */
.card-description {
  font-size: 1rem;
  color: #555;
}
</style>
