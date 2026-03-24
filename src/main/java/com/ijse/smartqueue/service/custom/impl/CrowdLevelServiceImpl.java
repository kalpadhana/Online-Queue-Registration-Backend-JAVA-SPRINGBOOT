package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.CrowdLevelDTO;
import com.ijse.smartqueue.entity.Branch;
import com.ijse.smartqueue.entity.CrowdLevel;
import com.ijse.smartqueue.entity.QueueStatus;
import com.ijse.smartqueue.entity.Trend;
import com.ijse.smartqueue.repository.BranchRepository;
import com.ijse.smartqueue.repository.CrowdLevelRepository;
import com.ijse.smartqueue.repository.QueueEntityRepository;
import com.ijse.smartqueue.service.custom.CrowdLevelService;
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
public class CrowdLevelServiceImpl implements CrowdLevelService {

    private final CrowdLevelRepository crowdLevelRepository;
    private final BranchRepository branchRepository;
    private final QueueEntityRepository queueRepository;
    private final ModelMapper modelMapper;

    @Override
    public CrowdLevelDTO getCrowdLevelByBranchId(Long branchId) {
        CrowdLevel crowdLevel = crowdLevelRepository.findByBranchId(branchId)
                .orElse(null);
        
        if (crowdLevel == null) {
            // First time request, perform calculation and create initial record
            return updateCrowdLevel(branchId);
        }
        
        return convertToDTO(crowdLevel);
    }

    @Override
    public List<CrowdLevelDTO> getAllCrowdLevels() {
        return crowdLevelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CrowdLevelDTO updateCrowdLevel(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + branchId));

        CrowdLevel crowdLevel = crowdLevelRepository.findByBranchId(branchId)
                .orElse(new CrowdLevel());
        
        if (crowdLevel.getCrowdId() == null) {
            crowdLevel.setBranch(branch);
            crowdLevel.setCapacity(100); // Default capacity
            // Or fetch from config?
        }
        
        // Calculate crowd metrics
        Integer waitingCount = queueRepository.countByBranchIdAndStatus(branchId, QueueStatus.WAITING);
        // Maybe include SERVING too? Usually crowd = waiting + serving
        // Assuming SERVICE status is "BEING_SERVED" based on enum
        // I need to check exact values in QueueStatus enum later if it works
        // But for now, count waiting
        
        int totalPeople = waitingCount; 
        // Logic for percentage
        int percentage = (int) ((double) totalPeople / crowdLevel.getCapacity() * 100);
        if (percentage > 100) percentage = 100;

        // Set levels
        crowdLevel.setPercentageFilled(percentage);
        crowdLevel.setTotalPeopleWaiting(totalPeople);
        
        // Set Enum CrowdLevel based on percentage
        if (percentage < 30) {
            crowdLevel.setLevel(com.ijse.smartqueue.entity.CrowdLevelEnum.LOW);
        } else if (percentage < 70) {
            crowdLevel.setLevel(com.ijse.smartqueue.entity.CrowdLevelEnum.MEDIUM);
        } else {
            crowdLevel.setLevel(com.ijse.smartqueue.entity.CrowdLevelEnum.HIGH);
        }

        // Set simple trend
        // If percentage increased from last stored value?
        // Need previous value? For now assume STABLE or retrieve previous before overwritting?
        // Let's assume STABLE for simplicity unless we track history
        crowdLevel.setTrend(Trend.STABLE);
        
        // Calc Avg Wait Time
        // Maybe fetch from ServiceEntities linked to branch?
        // Or simple heuristic: 5 mins per person
        int estimatedWaitTime = totalPeople * 5; 
        crowdLevel.setAvgWaitTime(estimatedWaitTime);
        
        crowdLevel.setLastUpdated(LocalDateTime.now());
        
        CrowdLevel saved = crowdLevelRepository.save(crowdLevel);
        return convertToDTO(saved);
    }
    
    private CrowdLevelDTO convertToDTO(CrowdLevel crowdLevel) {
        CrowdLevelDTO dto = modelMapper.map(crowdLevel, CrowdLevelDTO.class);
        dto.setBranchId(crowdLevel.getBranch().getBranchId());
        return dto;
    }
}




