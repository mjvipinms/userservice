package com.ibs.userservice.mapper;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void testToResponseDTO() {
        // Given
        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("ADMIN");

        User user = new User();
        user.setUserId(100);
        user.setUserName("vipin");
        user.setUserPhone("9876543210");
        user.setPasswordHash("secret123");
        user.setEmail("vipin@example.com");
        user.setFullName("Vipin MS");
        user.setActive(true);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.of(2024, 10, 10, 12, 0));
        user.setUpdatedAt(LocalDateTime.of(2024, 10, 12, 12, 0));

        // When
        UserResponseDTO dto = UserMapper.toResponseDTO(user);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(100);
        assertThat(dto.getUserName()).isEqualTo("vipin");
        assertThat(dto.getEmail()).isEqualTo("vipin@example.com");
        assertThat(dto.getFullName()).isEqualTo("Vipin MS");
        assertThat(dto.getRoleId()).isEqualTo(1);
        assertThat(dto.getRoleName()).isEqualTo("ADMIN");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    void testToEntity() {
        // Given
        UserRequestDTO request = new UserRequestDTO();
        request.setUserName("john");
        request.setEmail("john@example.com");
        request.setPassword("pwd123");
        request.setUserPhone("9998887777");
        request.setFullName("John Doe");
        request.setActive(false);

        Role role = new Role();
        role.setRoleId(2);
        role.setRoleName("ADMIN");

        // When
        User user = UserMapper.toEntity(request, role);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUserName()).isEqualTo("john");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("pwd123");
        assertThat(user.getUserPhone()).isEqualTo("9998887777");
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.isActive()).isFalse();
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getRole().getRoleName()).isEqualTo("ADMIN");
    }
}
