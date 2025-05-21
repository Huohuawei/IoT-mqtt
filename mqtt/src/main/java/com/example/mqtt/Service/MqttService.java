package com.example.mqtt.Service;

import com.example.mqtt.Entity.Humidity;
import com.example.mqtt.Entity.Pressure;
import com.example.mqtt.Entity.Temperature;
import com.example.mqtt.Repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqttService {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.default-topic}")
    private String defaultTopic;

    @Value("${mqtt.trust.store.path}")
    private String trustStorePath;

    @Value("${mqtt.trust.store.password}")
    private String trustStorePassword;

    @Autowired
    private HumidityRepository humidityRepository;

    @Autowired
    private PressureRepository pressureRepository;

    @Autowired
    private TemperatureRepository temperatureRepository;

    private MqttClient mqttClient;

    private final Map<String, Object> latestMessage = new HashMap<>();

    private volatile boolean isConnected = false;

    public void init() {
        connectAndSubscribe();
    }

    public synchronized void connectAndSubscribe() {
        if (isConnected) return;

        try {
            System.out.println("Attempting to connect to MQTT broker...");
            mqttClient = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true); // 启用自动重连

            // 设置连接超时
            options.setConnectionTimeout(10);

            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    isConnected = false;
                    System.err.println("Connection lost: " + cause.getMessage());
                    // 尝试重新连接
                    reconnect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    handleMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // 消息发布完成
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe(defaultTopic, 1); // QoS 1
            isConnected = true;
            System.out.println("Successfully connected to broker and subscribed to topic: " + defaultTopic);
        } catch (Exception e) {
            isConnected = false;
            System.err.println("Error connecting to MQTT broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized void reconnect() {
        if (isConnected) return;

        System.out.println("Attempting to reconnect...");
        try {
            if (mqttClient != null) {
                mqttClient.disconnectForcibly();
                mqttClient.close();
            }
            connectAndSubscribe();
        } catch (Exception e) {
            System.err.println("Reconnect failed: " + e.getMessage());
            // 可以添加定时重试逻辑
        }
    }

    public void publishMessage(String topic, String message) {
        if (!isConnected || mqttClient == null) {
            System.err.println("MQTT client not connected, attempting to reconnect...");
            reconnect();
            if (!isConnected) {
                throw new IllegalStateException("Cannot publish message - MQTT client is not connected");
            }
        }

        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
            System.out.println("Published to topic: " + topic + " payload: " + message);
        } catch (MqttException e) {
            isConnected = false;
            System.err.println("Error publishing message: " + e.getMessage());
            e.printStackTrace();
            reconnect(); // 发布失败时尝试重连
        }
    }

    private void handleMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            System.out.println("Message received: " + payload);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            synchronized (latestMessage) {
                latestMessage.put("payload", jsonNode);
            }
            if (jsonNode.has("humidity")) {
                handleHumidity(jsonNode);
            } else if (jsonNode.has("pressure")) {
                handlePressure(jsonNode);
            } else if (jsonNode.has("temperature")) {
                handleTemperature(jsonNode);
            } else {
                System.err.println("Unknown data type in payload: " + payload);
            }
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 提供一个方法获取最新消息
    public Map<String, Object> getLatest() {
        synchronized (latestMessage) {
            return new HashMap<>(latestMessage);
        }
    }

    private void handleHumidity(JsonNode jsonNode) {
        try {
            // Extract date as String
            String date = jsonNode.get("date").asText();
            int humidityValue = jsonNode.get("humidity").asInt();

            Humidity existing = humidityRepository.findByDate(date);
            if (existing != null) {
                existing.setHumidity(humidityValue);
                humidityRepository.save(existing);
            } else {
                Humidity newRecord = new Humidity();
                newRecord.setDate(date);
                newRecord.setHumidity(humidityValue);
                humidityRepository.save(newRecord);
            }
        } catch (Exception e) {
            System.err.println("Error handling humidity: " + e.getMessage());
        }
    }

    private void handlePressure(JsonNode jsonNode) {
        try {
            // Extract date as String
            String date = jsonNode.get("date").asText();
            int pressureValue = jsonNode.get("pressure").asInt();

            Pressure existing = pressureRepository.findByDate(date);
            if (existing != null) {
                existing.setPressure(pressureValue);
                pressureRepository.save(existing);
            } else {
                Pressure newRecord = new Pressure();
                newRecord.setDate(date);
                newRecord.setPressure(pressureValue);
                pressureRepository.save(newRecord);
            }
        } catch (Exception e) {
            System.err.println("Error handling pressure: " + e.getMessage());
        }
    }

    private void handleTemperature(JsonNode jsonNode) {
        try {
            // Extract date as String
            String date = jsonNode.get("date").asText();
            int temperatureValue = jsonNode.get("temperature").asInt();

            Temperature existing = temperatureRepository.findByDate(date);
            if (existing != null) {
                existing.setTemperature(temperatureValue);
                temperatureRepository.save(existing);
            } else {
                Temperature newRecord = new Temperature();
                newRecord.setDate(date);
                newRecord.setTemperature(temperatureValue);
                temperatureRepository.save(newRecord);
            }
        } catch (Exception e) {
            System.err.println("Error handling temperature: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
