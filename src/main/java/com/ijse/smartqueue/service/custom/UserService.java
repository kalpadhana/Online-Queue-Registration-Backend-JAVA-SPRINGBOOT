package com.ijse.smartqueue.service.custom;


import com.ijse.smartqueue.dto.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void deleteUser(Long userId);

    UserDTO getUserDetails(Long userId);

    List<UserDTO> getAllUsers();

    UserDTO authenticateUser(String email, String password);
}


