<template>
  <div class="home-container">
    <!-- 页面标题 -->
    <h1 class="page-title">Home Page</h1>
    <p class="page-description">Explore the features below:</p>

    <!-- 卡片容器 -->
    <div class="cards-container">
      <!-- 数据图表组件卡片 -->
      <div class="card" @click="goToComponent('/data-chart')">
        <img src="@/assets/1.jpg" alt="Data Chart" class="card-img" />
        <h3 class="card-title">Data Visualization</h3>
        <p class="card-description">
          Visualize data graphically for clear analysis and insights.
        </p>
      </div>

      <!-- MQTT 信息展示组件卡片 -->
      <div class="card" @click="goToComponent('/descriptive-info')">
        <img src="@/assets/1.jpg" alt="Descriptive Info" class="card-img" />
        <h3 class="card-title">MQTT Information</h3>
        <p class="card-description">
          Send and receive MQTT messages with live updates.
        </p>
      </div>

      <!-- 数据拟合组件卡片 -->
      <div class="card" @click="goToComponent('/fit-curve')">
        <img src="@/assets/1.jpg" alt="Fit Curve" class="card-img" />
        <h3 class="card-title">Data Fitting</h3>
        <p class="card-description">
          Perform data fitting with temperature data to predict future weather.
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
  background-image: url("@/assets/background.jpg"); /* 设置背景图片 */
  background-size: cover; /* 确保图片完全覆盖整个页面 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat; /* 防止图片重复 */
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  overflow: hidden;
}

/* 半透明遮罩层 (可选，用于提升可读性) */
.home-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6); /* 白色半透明 */
  z-index: 0;
}

.page-title,
.page-description,
.cards-container {
  position: relative; /* 提升内容层级，确保在遮罩层上显示 */
  z-index: 1;
}

/* 页面标题样式 */
.page-title {
  font-size: 2.5rem;
  color: #333;
  margin-top: 20px;
}

.page-description {
  font-size: 1.2rem;
  color: #555;
  margin-bottom: 30px;
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