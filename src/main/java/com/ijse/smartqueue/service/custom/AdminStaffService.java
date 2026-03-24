package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.AdminStaffDTO;
import java.util.List;

public interface AdminStaffService {
    AdminStaffDTO createAdminStaff(AdminStaffDTO adminStaffDTO);
    AdminStaffDTO updateAdminStaff(Long id, AdminStaffDTO adminStaffDTO);
    AdminStaffDTO getAdminStaffById(Long id);
    List<AdminStaffDTO> getAllAdminStaff();
    void deleteAdminStaff(Long id);
}

