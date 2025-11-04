package com.ibs.userservice.service;

import com.ibs.userservice.dtos.responseDtos.ReportResponseDto;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserReportServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserReportService userReportService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Role role = new Role();
        role.setRoleName("ADMIN");

        sampleUser = new User();
        sampleUser.setUserId(1);
        sampleUser.setFullName("John Doe");
        sampleUser.setEmail("john@example.com");
        sampleUser.setUserPhone("9876543210");
        sampleUser.setRole(role);
        sampleUser.setActive(true);
        sampleUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetUserReport_Success() {
        // Given
        List<User> users = List.of(sampleUser);
        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findByRoleAndDateRange(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        // When
        ReportResponseDto response = userReportService.getUserReport(
                "ADMIN",
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                1,
                10,
                "fullName",
                "ASC"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(1);

        Map<String, Object> userMap = response.getData().get(0);
        assertThat(userMap)
                .containsEntry("fullName", "John Doe")
                .containsEntry("email", "john@example.com")
                .containsEntry("role", "ADMIN")
                .containsEntry("active", true);

        assertThat(response.getTotal()).isEqualTo(1);
    }

    @Test
    void testGetUserReport_WithException() {
        when(userRepository.findByRoleAndDateRange(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () ->
                userReportService.getUserReport(
                        "ADMIN",
                        LocalDateTime.now().minusDays(7),
                        LocalDateTime.now(),
                        1, 10, "email", "DESC")
        );
    }

    @Test
    void testGetUserReport_WithoutSortField() {
        List<User> users = List.of(sampleUser);
        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findByRoleAndDateRange(
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(userPage);

        ReportResponseDto response = userReportService.getUserReport(
                "ADMIN",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                1, 5, "", ""
        );

        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().getFirst())
                .containsEntry("fullName", "John Doe")
                .containsEntry("email", "john@example.com");
    }
}
