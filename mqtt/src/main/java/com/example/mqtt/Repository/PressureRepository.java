package com.example.mqtt.Repository;

import com.example.mqtt.Entity.Pressure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface PressureRepository extends JpaRepository<Pressure, Long> {
    Pressure findByDate(String date);
    Pressure findTopByOrderByIdDesc();
}
