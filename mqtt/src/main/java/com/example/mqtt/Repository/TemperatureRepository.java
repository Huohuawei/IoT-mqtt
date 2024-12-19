package com.example.mqtt.Repository;

import com.example.mqtt.Entity.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
    Temperature findByDate(String date);
    Temperature findTopByOrderByIdDesc();
}
