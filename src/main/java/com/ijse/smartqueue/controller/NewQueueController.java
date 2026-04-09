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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/queues")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"}, 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class NewQueueController {
    private final QueueService queueService;

    @GetMapping
    public ResponseEntity<APIResponse<List<QueueDTO>>> getAllQueues() {
        List<QueueDTO> queues = queueService.getAllQueues();
        return ResponseEntity.ok(new APIResponse<>(200, "All queues retrieved", queues));
    }

    @PostMapping("/join")
    public ResponseEntity<APIResponse<QueueDTO>> joinQueue(@Valid @RequestBody QueueRequestDTO request) {
        System.out.println("DEBUG: Join queue request received with userId=" + request.getUserId() + 
                          ", serviceId=" + request.getServiceId() + ", branchId=" + request.getBranchId());
        QueueDTO response = queueService.joinQueue(request);
        System.out.println("DEBUG: Queue joined successfully! Token=" + response.getToken() + 
                          " at position " + response.getPosition());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Successfully joined queue", response));
    }

    @GetMapping("/track/{token}")
    public ResponseEntity<APIResponse<QueueDTO>> trackQueue(@PathVariable String token) {
        QueueDTO response = queueService.getQueueByToken(token);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue tracked successfully", response));
    }

    @GetMapping("/details/{token}")
    public ResponseEntity<APIResponse<Map<String, Object>>> getQueueDetails(@PathVariable String token) {
        try {
            System.out.println("DEBUG: Fetching queue details for token: " + token);
            Map<String, Object> details = queueService.getQueueDetails(token);
            System.out.println("DEBUG: Successfully retrieved queue details");
            return ResponseEntity.ok(new APIResponse<>(200, "Queue details retrieved successfully", details));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to get queue details for token " + token + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Queue not found with token: " + token, e);
        }
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

    @GetMapping("/upcoming/branch/{branchId}/service/{serviceId}")
    public ResponseEntity<APIResponse<List<String>>> getUpcomingTokens(
            @PathVariable Long branchId,
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "5") int limit) {
        List<String> tokens = queueService.getUpcomingQueueTokens(branchId, serviceId, limit);
        return ResponseEntity.ok(new APIResponse<>(200, "Upcoming queue tokens retrieved", tokens));
    }

    @GetMapping("/active/branch/{branchId}")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> getActiveQueuesByBranch(@PathVariable Long branchId) {
        List<Map<String, Object>> activeQueues = queueService.getActiveQueuesByBranch(branchId);
        return ResponseEntity.ok(new APIResponse<>(200, "Active queues retrieved", activeQueues));
    }

    @PutMapping("/call/{id}")
    public ResponseEntity<APIResponse<Void>> callQueue(@PathVariable Long id) {
        queueService.callQueue(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue called successfully - Email notification sent", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<QueueDTO>> updateQueue(@PathVariable Long id, @RequestBody QueueDTO queueDTO) {
        QueueDTO updatedQueue = queueService.updateQueue(id, queueDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "Queue updated successfully", updatedQueue));
    }
}


