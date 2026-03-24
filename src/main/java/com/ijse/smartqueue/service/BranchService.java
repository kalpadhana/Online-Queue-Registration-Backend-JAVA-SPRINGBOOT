package com.ijse.smartqueue.service;

import com.ijse.smartqueue.dto.BranchDTO;
import com.ijse.smartqueue.entity.Branch;
import com.ijse.smartqueue.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchService {
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    public BranchDTO createBranch(BranchDTO dto) {
        Branch branch = modelMapper.map(dto, Branch.class);
        branch.setIsActive(true);
        Branch saved = branchRepository.save(branch);
        return modelMapper.map(saved, BranchDTO.class);
    }

    public List<BranchDTO> getAllActiveBranches() {
        return branchRepository.findByIsActiveTrue()
                .stream()
                .map(b -> modelMapper.map(b, BranchDTO.class))
                .collect(Collectors.toList());
    }

    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(b -> modelMapper.map(b, BranchDTO.class))
                .collect(Collectors.toList());
    }

    public BranchDTO getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        return modelMapper.map(branch, BranchDTO.class);
    }

    public BranchDTO updateBranch(Long id, BranchDTO dto) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        modelMapper.map(dto, branch);
        Branch updated = branchRepository.save(branch);
        return modelMapper.map(updated, BranchDTO.class);
    }

    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }
}

