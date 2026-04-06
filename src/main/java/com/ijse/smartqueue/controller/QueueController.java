/*
package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.QueueDTO;
import com.ijse.smartqueue.service.custom.QueueService;
import com.ijse.smartqueue.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queue")
@CrossOrigin
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/join")
    public ResponseEntity<APIResponse<QueueDTO>> joinQueue(
            @RequestParam Integer userId,
            @RequestParam Integer serviceId) {

        QueueDTO queueDTO = queueService.joinQueue(userId, serviceId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Token Issued Successfully", queueDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<APIResponse<Void>> updateQueueStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        queueService.updateQueueStatus(id, status);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue status updated to " + status, null));
    }

    @GetMapping("/active/{serviceId}")
    public ResponseEntity<List<QueueDTO>> getActiveQueues(@PathVariable Integer serviceId) {
        return ResponseEntity.ok(queueService.getActiveQueuesByService(serviceId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QueueDTO>> getUserHistory(@PathVariable Integer userId) {
        return ResponseEntity.ok(queueService.getUserQueueHistory(userId));
    }
}
*/
