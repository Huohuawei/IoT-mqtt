package com.example.mqtt.Entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class Humidity {

    @Id  // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 如果使用自增长主键的话，使用这个注解（如果你使用的是数据库自动生成的 ID）
    private Long id;  // 主键字段，可以是长整型，确保唯一

    private String date;  // 存储日期时间
    private Integer humidity;  // 存储湿度数据

    // 构造函数、getter、setter等


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }
}
