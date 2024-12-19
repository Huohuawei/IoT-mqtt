<template>
  <div class="mqtt-container">
    <h2 class="mqtt-title">数据发送与接收</h2>
    <!-- 整体左右布局容器 -->
    <div class="content-container">
      <!-- 左侧：发布消息面板 -->
      <div class="panel publish-panel">
        <h3>Publish Message</h3>

        <!-- 固定 Topic -->
        <div class="form-group">
          <label for="topic">Topic:</label>
          <input type="text" id="topic" v-model="topic" disabled />
        </div>

        <!-- 时间选择器 -->
        <div class="form-group">
          <label for="date">Date:</label>
          <input type="datetime-local" id="date" v-model="date" />
        </div>

        <!-- 数据类型选择 -->
        <div class="form-group">
          <label for="dataType">Data Type:</label>
          <select id="dataType" v-model="selectedType">
            <option value="">Select Type</option>
            <option value="humidity">humidity</option>
            <option value="pressure">pressure</option>
            <option value="temperature">temperature</option>
          </select>
        </div>

        <!-- 数据值输入 -->
        <div class="form-group">
          <label for="value">Value:</label>
          <input type="number" id="value" v-model="value" />
        </div>

        <!-- 按钮 -->
        <div class="btn-group">
          <button class="btn btn-primary" @click="publishMessage">Publish</button>
          <button class="btn btn-secondary" @click="updateData">Update Data</button>
        </div>

        <!-- 发布状态显示 -->
        <div v-if="publishedPayload" class="status-display">
          <h4>Publish Status:</h4>
          <p><strong>Topic:</strong> {{ topic }}</p>
          <p><strong>Payload:</strong> {{ publishedPayload }}</p>
          <p style="color: green;">Message published successfully!</p>
        </div>
      </div>

      <!-- 右侧：数据获取和展示面板 -->
      <div class="panel data-panel">
        <h3>Retrieve Data</h3>
        <div class="btn-group">
          <button class="btn" @click="fetchHumidityData">Get Humidity</button>
          <button class="btn" @click="fetchPressureData">Get Pressure</button>
          <button class="btn" @click="fetchTemperatureData">Get Temperature</button>
          <button class="btn" @click="getLatestMessage">Get Latest</button>
        </div>

        <!-- 数据展示区域 -->
        <div v-if="data.length" class="data-display">
          <h4>Received Data:</h4>
          <div class="scrollable-data">
            <ul>
              <li v-for="(item, index) in data" :key="index">{{ item }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

// 定义变量
const topic = ref("test");
const date = ref("");
const selectedType = ref("");
const value = ref(null);
const data = ref([]);
const publishedPayload = ref(""); // 发布的 Payload 状态

// 发布消息
const publishMessage = async () => {
  if (!date.value || !selectedType.value || value.value === null) {
    // alert("Please fill in all fields!");
    return;
  }

  try {
    const payload = {
      date: `${date.value}:00`,
      [selectedType.value]: value.value.toString(),
    };
    await axios.post("http://localhost:8080/api/data/publish", null, {
      params: {
        topic: topic.value,
        message: JSON.stringify(payload),
      },
    });

    // 成功状态反馈
    publishedPayload.value = JSON.stringify(payload, null, 2);
  } catch (error) {
    console.error("Error publishing message:", error);
  }
};

// 数据获取函数
const fetchHumidityData = async () => fetchData("humidity");
const fetchPressureData = async () => fetchData("pressure");
const fetchTemperatureData = async () => fetchData("temperature");
const updateData = async () => {
  try {
    await axios.post("http://localhost:8080/api/data/update");
  } catch (error) {
    console.error("Error updating data:", error);
  }
};

const getLatestMessage = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/data/getLatest");
    // const response = await axios.get("http://localhost:8080/api/data/pressure/latest");
    // data.value = response.data;
    console.log("get latest message successfully")
    data.value = [JSON.stringify(response.data.payload)];

  } catch (error) {
    console.error("Error fetching latest message:", error);
  }
};

const fetchData = async (type) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/data/${type}`);

    console.log("get relevant data successfully\n")
    data.value = response.data;
  } catch (error) {
    console.error(`Error fetching ${type} data:`, error);
  }
};
</script>

<style scoped>
/* 容器样式 */
.mqtt-container {
  width: 80%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f9fafc;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.mqtt-title {
  text-align: center;
  font-size: 1.8em;
  color: #333;
  margin-bottom: 20px;
}

.content-container {
  display: flex;
  gap: 20px;
}

.panel {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
  flex: 1;
}

.form-group {
  margin-bottom: 15px;
}

label {
  font-weight: 600;
  color: #555;
}

input,
select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

button.btn {
  padding: 10px;
  margin-right: 10px;
  background-color: #4a90e2;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button.btn:hover {
  background-color: #357abd;
}

.btn-group {
  display: flex;
  gap: 10px;
}

.data-display {
  margin-top: 15px;
}

.scrollable-data {
  max-height: 250px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 10px;
}

.status-display {
  margin-top: 20px;
  background-color: #e6f7ff;
  border-left: 5px solid #4a90e2;
  padding: 10px;
  border-radius: 5px;
}
</style>
