package com.ibs.userservice.controller;

import com.ibs.userservice.entity.Role;
import com.ibs.userservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_shouldReturnListOfRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setRoleName("ADMIN");

        Role role2 = new Role();
        role2.setRoleId(2);
        role2.setRoleName("HR");

        List<Role> mockRoles = Arrays.asList(role1, role2);
        when(roleService.getAllRoles()).thenReturn(mockRoles);

        // Act
        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockRoles, response.getBody());
        verify(roleService, times(1)).getAllRoles();
    }
}
