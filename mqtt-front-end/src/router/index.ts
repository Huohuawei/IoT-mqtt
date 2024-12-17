import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/Home.vue'
import DataChart from "../views/DataChart.vue";
import DescriptiveInfo from "../views/DescriptiveInfo.vue";
import FitCurve from "../views/FitCurve.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: "/data-chart",
      name: "DataChart",
      component: DataChart
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

export default router
