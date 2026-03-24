package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.PriorityUserDTO;
import java.util.List;

public interface PriorityUserService {
    PriorityUserDTO createPriorityUser(PriorityUserDTO dto);
    PriorityUserDTO updatePriorityUser(Long id, PriorityUserDTO dto);
    PriorityUserDTO getPriorityUserById(Long id);
    PriorityUserDTO getPriorityUserByUserId(Long userId);
    void deletePriorityUser(Long id);
}

