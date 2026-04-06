package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.BranchDTO;
import com.ijse.smartqueue.service.BranchService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"}, 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<APIResponse<List<BranchDTO>>> getAllBranches() {
        List<BranchDTO> branches = branchService.getAllActiveBranches();
        return ResponseEntity.ok(new APIResponse<>(200, "Branches retrieved successfully", branches));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<BranchDTO>> getBranchById(@PathVariable Long id) {
        BranchDTO branch = branchService.getBranchById(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Branch retrieved successfully", branch));
    }

    @PostMapping
    public ResponseEntity<APIResponse<BranchDTO>> createBranch(@Valid @RequestBody BranchDTO dto) {
        BranchDTO created = branchService.createBranch(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(201, "Branch created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<BranchDTO>> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchDTO dto) {
        BranchDTO updated = branchService.updateBranch(id, dto);
        return ResponseEntity.ok(new APIResponse<>(200, "Branch updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Branch deleted successfully", null));
    }
}

