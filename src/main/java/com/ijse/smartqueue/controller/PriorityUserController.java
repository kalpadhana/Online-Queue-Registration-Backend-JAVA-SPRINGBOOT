package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.PriorityUserDTO;
import com.ijse.smartqueue.service.custom.PriorityUserService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/priority-users")
@RequiredArgsConstructor
@CrossOrigin
public class PriorityUserController {

    private final PriorityUserService priorityUserService;

    @PostMapping
    public ResponseEntity<APIResponse> createPriorityUser(@Valid @RequestBody PriorityUserDTO dto) {
        PriorityUserDTO created = priorityUserService.createPriorityUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse("Priority User created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updatePriorityUser(@PathVariable Long id, @Valid @RequestBody PriorityUserDTO dto) {
        PriorityUserDTO updated = priorityUserService.updatePriorityUser(id, dto);
        return ResponseEntity.ok(new APIResponse("Priority User updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getPriorityUserById(@PathVariable Long id) {
        PriorityUserDTO dto = priorityUserService.getPriorityUserById(id);
        return ResponseEntity.ok(new APIResponse("Priority User retrieved successfully", dto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<APIResponse> getPriorityUserByUserId(@PathVariable Long userId) {
        PriorityUserDTO dto = priorityUserService.getPriorityUserByUserId(userId);
        return ResponseEntity.ok(new APIResponse("Priority User retrieved successfully", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deletePriorityUser(@PathVariable Long id) {
        priorityUserService.deletePriorityUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new APIResponse("Priority User deleted successfully", null));
    }
}

