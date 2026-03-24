package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.UserDTO;
import com.ijse.smartqueue.entity.Branch;
import com.ijse.smartqueue.entity.User;
import com.ijse.smartqueue.repository.BranchRepository;
import com.ijse.smartqueue.repository.UserRepository;
import com.ijse.smartqueue.service.custom.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = modelMapper.map(userDTO, User.class);
        if (userDTO.getPreferredBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getPreferredBranchId())
                    .orElse(null);
            user.setPreferredBranch(branch); // This will update the user entity
        }
        
        user.setMemberSince(LocalDateTime.now());
        user.setIsActive(true);
        
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        
        if (!user.getEmail().equals(userDTO.getEmail())) {
             if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
             }
             user.setEmail(userDTO.getEmail());
        }
        
        if (userDTO.getPreferredBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getPreferredBranchId())
                    .orElse(null);
            user.setPreferredBranch(branch);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO getUserDetails(Long userId) {
         User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
         return convertToDTO(user);
    }
    
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        if (user.getPreferredBranch() != null) {
            dto.setPreferredBranchId(user.getPreferredBranch().getBranchId());
        }
        return dto;
    }
}

