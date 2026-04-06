package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.util.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class TestController {

    @GetMapping
    public ResponseEntity<APIResponse<String>> testConnection() {
        return ResponseEntity.ok(new APIResponse<>(200, "Backend connection successful", "OK"));
    }
}
