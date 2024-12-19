package com.example.mqtt.Repository;

import com.example.mqtt.Entity.Humidity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface HumidityRepository extends JpaRepository<Humidity, Long> {
    Humidity findByDate(String date); // 根据日期查找 Humidity 数据
    // 自动按 date 排序（升序）
    List<Humidity> findAllByOrderByDateAsc();

    Humidity findTopByOrderByIdDesc();
}
