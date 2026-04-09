package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.PriorityUserDTO;
import com.ijse.smartqueue.entity.PriorityUser;
import com.ijse.smartqueue.entity.User;
import com.ijse.smartqueue.repository.PriorityUserRepository;
import com.ijse.smartqueue.repository.UserRepository;
import com.ijse.smartqueue.service.custom.PriorityUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class PriorityUserServiceImpl implements PriorityUserService {

    private final PriorityUserRepository priorityUserRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public PriorityUserDTO createPriorityUser(PriorityUserDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        PriorityUser priorityUser = modelMapper.map(dto, PriorityUser.class);
        priorityUser.setUser(user);
        
        PriorityUser savedUser = priorityUserRepository.save(priorityUser);
        return convertToDTO(savedUser);
    }

    @Override
    public PriorityUserDTO updatePriorityUser(Long id, PriorityUserDTO dto) {
        PriorityUser existing = priorityUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Priority User not found with ID: " + id));

        existing.setTier(dto.getTier());
        existing.setSkipPositions(dto.getSkipPositions());
        existing.setBenefits(dto.getBenefits());
        existing.setIsActive(dto.getIsActive());
        
        if (dto.getExpiresAt() != null) {
            existing.setExpiresAt(dto.getExpiresAt());
        }

        PriorityUser updated = priorityUserRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    public PriorityUserDTO getPriorityUserById(Long id) {
        PriorityUser priorityUser = priorityUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Priority User not found with ID: " + id));
        return convertToDTO(priorityUser);
    }

    @Override
    public PriorityUserDTO getPriorityUserByUserId(Long userId) {
        PriorityUser priorityUser = priorityUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Priority User not found for user ID: " + userId));
        return convertToDTO(priorityUser);
    }

    @Override
    public void deletePriorityUser(Long id) {
        if (!priorityUserRepository.existsById(id)) {
            throw new RuntimeException("Priority User not found with ID: " + id);
        }
        priorityUserRepository.deleteById(id);
    }
    
    private PriorityUserDTO convertToDTO(PriorityUser entity) {
        PriorityUserDTO dto = modelMapper.map(entity, PriorityUserDTO.class);
        dto.setUserId(entity.getUser().getUserId());
        return dto;
    }
}


