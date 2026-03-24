package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.CounterDTO;
import com.ijse.smartqueue.service.custom.CounterService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/counter")
@CrossOrigin
public class CounterController {

    private final CounterService counterService;

    @PostMapping
    public ResponseEntity<APIResponse<Void>> saveCounter(@RequestBody @Valid CounterDTO counterDTO) {
        counterService.saveCounter(counterDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Counter Added Successfully", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<APIResponse<Void>> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        counterService.updateCounterStatus(id, status);
        return ResponseEntity.ok(new APIResponse<>(200, "Counter Status Updated", null));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<CounterDTO>> getCountersByService(@PathVariable Integer serviceId) {
        return ResponseEntity.ok(counterService.getCountersByService(serviceId));
    }

    @GetMapping
    public ResponseEntity<List<CounterDTO>> getAllCounters() {
        return ResponseEntity.ok(counterService.getAllCounters());
    }
}