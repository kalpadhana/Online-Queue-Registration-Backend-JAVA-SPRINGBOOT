package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.UserPreferencesDTO;
import com.ijse.smartqueue.service.custom.UserPreferencesService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-preferences")
@RequiredArgsConstructor
@CrossOrigin
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @PutMapping
    public ResponseEntity<APIResponse<UserPreferencesDTO>> saveOrUpdatePreferences(@Valid @RequestBody UserPreferencesDTO dto) {
        UserPreferencesDTO updated = userPreferencesService.saveOrUpdatePreferences(dto);
        return ResponseEntity.ok(new APIResponse<>("User Preferences updated successfully", updated));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<APIResponse<UserPreferencesDTO>> getPreferencesByUserId(@PathVariable Long userId) {
        UserPreferencesDTO preferences = userPreferencesService.getPreferencesByUserId(userId);
        return ResponseEntity.ok(new APIResponse<>("User Preferences retrieved successfully", preferences));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Object>> deletePreferences(@PathVariable Long id) {
        userPreferencesService.deletePreferences(id);
        return ResponseEntity.ok(new APIResponse<>("User Preferences deleted successfully", null));
    }
}

