package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.UserPreferencesDTO;
import com.ijse.smartqueue.entity.User;
import com.ijse.smartqueue.entity.UserPreferences;
import com.ijse.smartqueue.repository.UserPreferencesRepository;
import com.ijse.smartqueue.repository.UserRepository;
import com.ijse.smartqueue.service.custom.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserPreferencesDTO saveOrUpdatePreferences(UserPreferencesDTO dto) {
        UserPreferences preferences = userPreferencesRepository.findByUserId(dto.getUserId())
                .orElse(new UserPreferences());

        // Preserve ID if it's an update to existing record fetched by user ID
        // Or if new record, ID is null
        
        if (preferences.getPreferenceId() == null) {
            // New record
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserId()));
            preferences.setUser(user);
        }
        
        // Map basic fields
        if(dto.getTheme() != null) preferences.setTheme(dto.getTheme());
        if(dto.getLanguage() != null) preferences.setLanguage(dto.getLanguage());
        if(dto.getTimeFormat() != null) preferences.setTimeFormat(dto.getTimeFormat());
        if(dto.getAutoRefresh() != null) preferences.setAutoRefresh(dto.getAutoRefresh());
        
        // Notifications
        if(dto.getQueueUpdates() != null) preferences.setQueueUpdates(dto.getQueueUpdates());
        if(dto.getWaitTimeAlerts() != null) preferences.setWaitTimeAlerts(dto.getWaitTimeAlerts());
        if(dto.getPromotions() != null) preferences.setPromotions(dto.getPromotions());
        if(dto.getSystemNotifications() != null) preferences.setSystemNotifications(dto.getSystemNotifications());
        if(dto.getEmailNotifications() != null) preferences.setEmailNotifications(dto.getEmailNotifications());
        if(dto.getSmsNotifications() != null) preferences.setSmsNotifications(dto.getSmsNotifications());
        
        // Privacy
        if(dto.getProfilePublic() != null) preferences.setProfilePublic(dto.getProfilePublic());
        if(dto.getAllowDataCollection() != null) preferences.setAllowDataCollection(dto.getAllowDataCollection());
        if(dto.getShowOnlineStatus() != null) preferences.setShowOnlineStatus(dto.getShowOnlineStatus());
        if(dto.getShareAnalytics() != null) preferences.setShareAnalytics(dto.getShareAnalytics());
        
        UserPreferences saved = userPreferencesRepository.save(preferences);
        return convertToDTO(saved);
    }

    @Override
    public UserPreferencesDTO getPreferencesByUserId(Long userId) {
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + userId));
        return convertToDTO(preferences);
    }

    @Override
    public void deletePreferences(Long preferencesId) {
        userPreferencesRepository.deleteById(preferencesId);
    }
    
    private UserPreferencesDTO convertToDTO(UserPreferences entity) {
        UserPreferencesDTO dto = modelMapper.map(entity, UserPreferencesDTO.class);
        dto.setUserId(entity.getUser().getUserId());
        return dto;
    }
}

