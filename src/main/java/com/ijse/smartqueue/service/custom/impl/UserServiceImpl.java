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
        System.out.println("\n\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║  [UserService.saveUser] REGULAR SIGNUP USER CREATED      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("📌 Received DTO Data:");
        System.out.println("   Full Name: " + userDTO.getFullName());
        System.out.println("   Email: " + userDTO.getEmail());
        System.out.println("   Phone: " + userDTO.getPhone());
        System.out.println("   Email is NULL: " + (userDTO.getEmail() == null));
        System.out.println("   Email is EMPTY: " + (userDTO.getEmail() != null && userDTO.getEmail().isEmpty()));
        
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            System.out.println("❌ Email already exists in database");
            throw new RuntimeException("Email already exists");
        }

        // MANUAL MAPPING - Ensure all fields are explicitly set
        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword());
        user.setMemberSince(LocalDateTime.now());
        user.setIsActive(true);
        
        System.out.println("\n📝 User Object Created (Before Save):");
        System.out.println("   Email: " + (user.getEmail() != null ? user.getEmail() : "[NULL]"));
        System.out.println("   Full Name: " + user.getFullName());
        System.out.println("   Phone: " + user.getPhone());
        
        // Handle preferred branch if provided
        if (userDTO.getPreferredBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getPreferredBranchId())
                    .orElse(null);
            user.setPreferredBranch(branch);
        }
        
        // SAVE TO DATABASE
        User savedUser = userRepository.save(user);
        System.out.println("\n✅ User Saved to Database:");
        System.out.println("   User ID: " + savedUser.getUserId());
        System.out.println("   Email: " + (savedUser.getEmail() != null ? savedUser.getEmail() : "[NULL] ❌ EMAIL NOT SAVED!"));
        System.out.println("   Full Name: " + savedUser.getFullName());
        System.out.println("   Phone: " + savedUser.getPhone());
        
        // VERIFY BY RE-FETCHING FROM DATABASE
        System.out.println("\n🔄 RE-FETCHING from Database (Verification):");
        User verifyUser = userRepository.findById(savedUser.getUserId()).orElse(null);
        if (verifyUser != null) {
            System.out.println("   ✅ User found in database");
            System.out.println("   Email from DB: " + (verifyUser.getEmail() != null ? verifyUser.getEmail() : "[NULL] ❌"));
            System.out.println("   Full Name from DB: " + verifyUser.getFullName());
        } else {
            System.out.println("   ❌ User NOT found in database!");
        }
        
        System.out.println("╔════════════════════════════════════════════════════════╗\n");
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

    @Override
    public UserDTO authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Simple password check without encryption for now
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        
        return convertToDTO(user);
    }

    @Override
    public UserDTO getUserByName(String name) {
        User user = userRepository.findByFullName(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        if (user.getPreferredBranch() != null) {
            dto.setPreferredBranchId(user.getPreferredBranch().getBranchId());
            dto.setPreferredBranchName(user.getPreferredBranch().getName());
        }
        dto.setMemberSince(user.getMemberSince());
        return dto;
    }
}

