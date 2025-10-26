package com.ibs.userservice.service;

import com.ibs.userservice.entity.Role;
import com.ibs.userservice.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_shouldReturnSortedListOfRoles() {
        // Arrange
        Role admin = new Role();
        admin.setRoleId(1);
        admin.setRoleName("ADMIN");

        Role hr = new Role();
        hr.setRoleId(2);
        hr.setRoleName("HR");

        List<Role> mockRoles = Arrays.asList(admin, hr);

        when(roleRepository.findAll(any(Sort.class))).thenReturn(mockRoles);

        // Act
        List<Role> result = roleService.getAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getRoleName());
        assertEquals("HR", result.get(1).getRoleName());
        verify(roleRepository, times(1)).findAll(any(Sort.class));
    }
}
