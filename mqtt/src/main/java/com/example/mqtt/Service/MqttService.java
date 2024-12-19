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

    public void connectAndSubscribe() {
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setSocketFactory(getSSLSocketFactory());

            mqttClient.connect(options);
            mqttClient.subscribe(defaultTopic, this::handleMessage);
            System.out.println("Connected to broker and subscribed to topic: " + defaultTopic);
        } catch (Exception e) {
            System.err.println("Error connecting to MQTT broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private SSLSocketFactory getSSLSocketFactory() throws Exception {
        // Configure SSL using truststore.jks
        System.setProperty("javax.net.ssl.trustStore", trustStorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        return (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public void publishMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topic, mqttMessage);
            System.out.println("(publish) Published to topic: " + topic + " payload: " + mqttMessage);
        } catch (MqttException e) {
            System.err.println("Error publishing message: " + e.getMessage());
            e.printStackTrace();
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
