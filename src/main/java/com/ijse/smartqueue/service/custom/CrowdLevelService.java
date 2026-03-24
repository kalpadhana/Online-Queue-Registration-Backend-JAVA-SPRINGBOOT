package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.CrowdLevelDTO;
import java.util.List;

public interface CrowdLevelService {
    CrowdLevelDTO getCrowdLevelByBranchId(Long branchId);
    List<CrowdLevelDTO> getAllCrowdLevels();
    CrowdLevelDTO updateCrowdLevel(Long branchId); // Recalculate based on current queues
}

