package com.placementsystem.repository;

import com.placementsystem.entity.User;
import com.placementsystem.util.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;  // ← ADD THIS IMPORT
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (for login)
    Optional<User> findByEmail(String email);

    // Check if email exists (for registration validation)
    boolean existsByEmail(String email);

    // Find users by role
    List<User> findByRole(Role role);
}