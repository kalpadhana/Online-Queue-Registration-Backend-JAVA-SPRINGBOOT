package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.CrowdLevelDTO;
import com.ijse.smartqueue.service.custom.CrowdLevelService;
import com.ijse.smartqueue.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crowd-levels")
@RequiredArgsConstructor
@CrossOrigin
public class CrowdLevelController {

    private final CrowdLevelService crowdLevelService;

    @GetMapping
    public ResponseEntity<APIResponse> getAllCrowdLevels() {
        List<CrowdLevelDTO> levels = crowdLevelService.getAllCrowdLevels();
        return ResponseEntity.ok(new APIResponse("All crowd levels retrieved successfully", levels));
    }

    @GetMapping("/{branchId}")
    public ResponseEntity<APIResponse> getCrowdLevelByBranchId(@PathVariable Long branchId) {
        CrowdLevelDTO level = crowdLevelService.getCrowdLevelByBranchId(branchId);
        return ResponseEntity.ok(new APIResponse("Crowd level retrieved successfully", level));
    }
    
    @PutMapping("/update/{branchId}")
    public ResponseEntity<APIResponse> updateCrowdLevel(@PathVariable Long branchId) {
        CrowdLevelDTO updated = crowdLevelService.updateCrowdLevel(branchId);
        return ResponseEntity.ok(new APIResponse("Crowd level updated successfully", updated));
    }
}

