package com.example.mqtt.Controller;

import com.example.mqtt.dto.LoginRequest;
import com.example.mqtt.dto.RegisterRequest;
import com.example.mqtt.dto.UserDTO;
import com.example.mqtt.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        UserDTO userDTO = userService.register(request);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
        UserDTO userDTO = userService.login(request);
        return ResponseEntity.ok(userDTO);
    }
} 