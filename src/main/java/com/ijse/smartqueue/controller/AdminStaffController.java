package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.AdminStaffDTO;
import com.ijse.smartqueue.service.custom.AdminStaffService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin-staff")
@RequiredArgsConstructor
@CrossOrigin
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse> registerAdminStaff(@Valid @RequestBody AdminStaffDTO adminStaffDTO) {
        AdminStaffDTO createdStaff = adminStaffService.createAdminStaff(adminStaffDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse("Admin Staff registered successfully", createdStaff));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateAdminStaff(@PathVariable Long id, @Valid @RequestBody AdminStaffDTO adminStaffDTO) {
        AdminStaffDTO updatedStaff = adminStaffService.updateAdminStaff(id, adminStaffDTO);
        return ResponseEntity.ok(new APIResponse("Admin Staff updated successfully", updatedStaff));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getAdminStaffById(@PathVariable Long id) {
        AdminStaffDTO existingStaff = adminStaffService.getAdminStaffById(id);
        return ResponseEntity.ok(new APIResponse("Admin Staff retrieved successfully", existingStaff));
    }

    @GetMapping
    public ResponseEntity<APIResponse> getAllAdminStaff() {
        List<AdminStaffDTO> allStaff = adminStaffService.getAllAdminStaff();
        return ResponseEntity.ok(new APIResponse("All Admin Staff retrieved successfully", allStaff));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteAdminStaff(@PathVariable Long id) {
        adminStaffService.deleteAdminStaff(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new APIResponse("Admin Staff deleted successfully", null));
    }
}

