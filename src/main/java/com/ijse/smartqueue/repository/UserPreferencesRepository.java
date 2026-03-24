package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    
    @Query("SELECT up FROM UserPreferences up WHERE up.user.userId = :userId")
    Optional<UserPreferences> findByUserId(@Param("userId") Long userId);
}
