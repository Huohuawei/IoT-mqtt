import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import HomeView from '../views/Home.vue'
import DataChart from "../views/DataChart.vue";
import DescriptiveInfo from "../views/DescriptiveInfo.vue";
import FitCurve from "../views/FitCurve.vue";
import HumidityData from '@/views/datacharts/HumidityData.vue';
import TemperatureData from '@/views/datacharts/TemperatureData.vue';
import PressureData from '@/views/datacharts/PressureData.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: "/data-chart",
      name: "DataChart",
      component: DataChart,
    },
    {
      path: '/data-chart/temperature',
      name: 'TemperatureDetails',
      component: TemperatureData,
    },
    {
      path: '/data-chart/humidity',
      name: 'HumidityDetails',
      component: HumidityData,
    },
    {
      path: '/data-chart/pressure',
      name: 'PressureDetails',
      component: PressureData,
    },
    {
      path: "/descriptive-info",
      name: "DescriptiveInfo",
      component: DescriptiveInfo
    },
    {
      path: "/fit-curve",
      name: "FitCurve",
      component: FitCurve
    },
  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router;
