package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.UserDTO;
import com.ijse.smartqueue.service.custom.UserService;
import com.ijse.smartqueue.util.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"}, 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<APIResponse<Void>> saveUser(@RequestBody @Valid UserDTO userDTO) {
        try {
            userService.saveUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new APIResponse<>(201, "User Registered Successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping
    public ResponseEntity<APIResponse<Void>> updateUser(@RequestBody @Valid UserDTO userDTO) {
        userService.updateUser(userDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "User Updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new APIResponse<>(200, "User Deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDetails(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<UserDTO>> login(@RequestBody UserDTO loginRequest) {
        try {
            UserDTO user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new APIResponse<>(200, "Login successful", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(401, e.getMessage(), null));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserDTO>> getUserProfile(@RequestParam String name) {
        try {
            UserDTO user = userService.getUserByName(name);
            return ResponseEntity.ok(new APIResponse<>(200, "Profile retrieved successfully", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>(404, e.getMessage(), null));
        }
    }
}