package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.ServiceDTO;
import com.ijse.smartqueue.service.custom.ServiceManagementService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/service")
@CrossOrigin
public class ServiceManagementController {

    private final ServiceManagementService serviceManagementService;

    @PostMapping
    public ResponseEntity<APIResponse<Void>> saveService(@RequestBody @Valid ServiceDTO serviceDTO) {
        serviceManagementService.saveService(serviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Service Created Successfully", null));
    }

    @PutMapping
    public ResponseEntity<APIResponse<Void>> updateService(@RequestBody @Valid ServiceDTO serviceDTO) {
        serviceManagementService.updateService(serviceDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "Service Updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteService(@PathVariable Integer id) {
        serviceManagementService.deleteService(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Service Deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceManagementService.getServiceDetails(id));
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(serviceManagementService.getAllServices());
    }
}