package com.ibs.userservice.repository;

import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Page<User> findByRole(Role role, Pageable pageable);
    List<User> findByRole_RoleNameIgnoreCase(String roleName);

    @Query("""
        SELECT u FROM User u
        WHERE UPPER(u.role.roleName) = :role
        AND (:startDate IS NULL OR u.createdAt >= :startDate)
        AND (:endDate IS NULL OR u.createdAt <= :endDate)
    """)
    Page<User> findByRoleAndDateRange(String role, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
