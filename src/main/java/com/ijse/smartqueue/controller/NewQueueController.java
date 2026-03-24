package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.QueueRequestDTO;
import com.ijse.smartqueue.dto.QueueDTO;
import com.ijse.smartqueue.entity.QueueStatus;
import com.ijse.smartqueue.service.QueueService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/queues")
@RequiredArgsConstructor
@CrossOrigin
public class NewQueueController {
    private final QueueService queueService;

    @PostMapping("/join")
    public ResponseEntity<APIResponse<QueueDTO>> joinQueue(@Valid @RequestBody QueueRequestDTO request) {
        QueueDTO response = queueService.joinQueue(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Successfully joined queue", response));
    }

    @GetMapping("/track/{token}")
    public ResponseEntity<APIResponse<QueueDTO>> trackQueue(@PathVariable String token) {
        QueueDTO response = queueService.getQueueByToken(token);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue tracked successfully", response));
    }

    @GetMapping("/active/branch/{branchId}")
    public ResponseEntity<APIResponse<List<QueueDTO>>> getActiveQueues(@PathVariable Long branchId) {
        List<QueueDTO> queues = queueService.getActiveQueues(branchId);
        return ResponseEntity.ok(new APIResponse<>(200, "Active queues retrieved", queues));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<APIResponse<List<QueueDTO>>> getUserQueues(@PathVariable Long userId) {
        List<QueueDTO> queues = queueService.getUserQueues(userId);
        return ResponseEntity.ok(new APIResponse<>(200, "User queues retrieved", queues));
    }

    @PatchMapping("/{queueId}/status")
    public ResponseEntity<APIResponse<Void>> updateQueueStatus(
            @PathVariable Long queueId,
            @RequestParam QueueStatus status) {
        queueService.updateQueueStatus(queueId, status);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue status updated", null));
    }

    @DeleteMapping("/{queueId}/cancel")
    public ResponseEntity<APIResponse<Void>> cancelQueue(@PathVariable Long queueId) {
        queueService.cancelQueue(queueId);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue cancelled successfully", null));
    }
}


