package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.AdminStaffDTO;
import com.ijse.smartqueue.entity.AdminStaff;
import com.ijse.smartqueue.entity.Branch;
import com.ijse.smartqueue.repository.AdminStaffRepository;
import com.ijse.smartqueue.repository.BranchRepository;
import com.ijse.smartqueue.service.custom.AdminStaffService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class AdminStaffServiceImpl implements AdminStaffService {

    private final AdminStaffRepository adminStaffRepository;
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    @Override
    public AdminStaffDTO createAdminStaff(AdminStaffDTO adminStaffDTO) {
        Branch branch = branchRepository.findById(adminStaffDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + adminStaffDTO.getBranchId()));

        AdminStaff adminStaff = modelMapper.map(adminStaffDTO, AdminStaff.class);
        adminStaff.setBranch(branch);
        // Password encoding should happen here if security is enabled
        
        AdminStaff savedStaff = adminStaffRepository.save(adminStaff);
        return convertToDTO(savedStaff);
    }

    @Override
    public AdminStaffDTO updateAdminStaff(Long id, AdminStaffDTO adminStaffDTO) {
        AdminStaff existingStaff = adminStaffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin Staff not found with ID: " + id));

        // Update fields
        existingStaff.setName(adminStaffDTO.getName());
        existingStaff.setEmail(adminStaffDTO.getEmail());
        existingStaff.setPhone(adminStaffDTO.getPhone());
        existingStaff.setRole(adminStaffDTO.getRole());
        existingStaff.setStatus(adminStaffDTO.getStatus());
        existingStaff.setIsActive(adminStaffDTO.getIsActive());

        if (adminStaffDTO.getBranchId() != null) {
            Branch branch = branchRepository.findById(adminStaffDTO.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            existingStaff.setBranch(branch);
        }
        
        if (adminStaffDTO.getPassword() != null && !adminStaffDTO.getPassword().isEmpty()) {
            existingStaff.setPassword(adminStaffDTO.getPassword());
        }

        AdminStaff updatedStaff = adminStaffRepository.save(existingStaff);
        return convertToDTO(updatedStaff);
    }

    @Override
    public AdminStaffDTO getAdminStaffById(Long id) {
        AdminStaff adminStaff = adminStaffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin Staff not found with ID: " + id));
        return convertToDTO(adminStaff);
    }

    @Override
    public List<AdminStaffDTO> getAllAdminStaff() {
        return adminStaffRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAdminStaff(Long id) {
        if (!adminStaffRepository.existsById(id)) {
            throw new RuntimeException("Admin Staff not found with ID: " + id);
        }
        adminStaffRepository.deleteById(id);
    }

    private AdminStaffDTO convertToDTO(AdminStaff adminStaff) {
        AdminStaffDTO dto = modelMapper.map(adminStaff, AdminStaffDTO.class);
        dto.setBranchId(adminStaff.getBranch() != null ? adminStaff.getBranch().getBranchId() : null);
        return dto;
    }
}

