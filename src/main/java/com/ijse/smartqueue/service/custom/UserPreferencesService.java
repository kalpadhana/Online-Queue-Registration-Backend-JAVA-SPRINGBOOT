package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.UserPreferencesDTO;

public interface UserPreferencesService {
    UserPreferencesDTO saveOrUpdatePreferences(UserPreferencesDTO dto);
    UserPreferencesDTO getPreferencesByUserId(Long userId);
    void deletePreferences(Long preferencesId);
}

