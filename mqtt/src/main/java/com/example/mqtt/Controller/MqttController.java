package com.example.mqtt.Controller;

import com.example.mqtt.Entity.*;
import com.example.mqtt.Repository.*;
import com.example.mqtt.Service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;

// 允许来自所有源的跨域请求
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/data")
public class MqttController {

    @Autowired
    private HumidityRepository humidityRepository;

    @Autowired
    private PressureRepository pressureRepository;

    @Autowired
    private TemperatureRepository temperatureRepository;

    @Autowired
    private MqttService mqttService;

    @GetMapping("/humidity")
    public List<Humidity> getHumidityData() {
        return humidityRepository.findAll();
    }

    @GetMapping("/pressure")
    public List<Pressure> getPressureData() {
        return pressureRepository.findAll();
    }

    @GetMapping("/temperature")
    public List<Temperature> getTemperatureData() {
        return temperatureRepository.findAll();
    }

    @GetMapping("/humidity/latest")
    public Humidity getLatestHumidity() {
        return humidityRepository.findTopByOrderByIdDesc();
    }

    @GetMapping("/pressure/latest")
    public Pressure getLatestPressure() {
        return pressureRepository.findTopByOrderByIdDesc();
    }

    @GetMapping("/temperature/latest")
    public Temperature getLatestTemperature() {
        return temperatureRepository.findTopByOrderByIdDesc();
    }
    @PostMapping("/publish")
    public String publishMessage(@RequestParam String topic, @RequestParam String message) {
        mqttService.publishMessage(topic, message);
        return "Message published to topic: " + topic;
    }

    @PostMapping("/update")
    public String update() {
        mqttService.connectAndSubscribe();
        return "Subscribed to topic and processing messages.";
    }
    @GetMapping("/getLatest")
    public Map<String, Object> getLatest() {
        return mqttService.getLatest();
    }

}
