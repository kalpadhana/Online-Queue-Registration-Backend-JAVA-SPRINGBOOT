package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their unique Email
    Optional<User> findByEmail(String email);

    // Get the most recently registered user
    User findTopByOrderByUserIdDesc();
}