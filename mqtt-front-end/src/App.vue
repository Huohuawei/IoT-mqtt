<template>
  <div id="app">
    <router-view /> <!-- 显示当前路由的内容 -->
  </div>
</template>

<script setup lang="ts">
// 无需特别引入 Home，Vue Router 会自动加载
</script>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}

#app {
  display: flex; /* 使用 flex 布局 */
  flex-direction: column;
}
</style>
