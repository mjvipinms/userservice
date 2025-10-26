package com.ibs.userservice.controller;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldReturnUserResponse() {
        UserRequestDTO request = new UserRequestDTO();
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setUserId(1);
        responseDTO.setFullName("John Doe");

        when(userService.createUser(request)).thenReturn(responseDTO);

        ResponseEntity<UserResponseDTO> response = userController.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDTO, response.getBody());
        verify(userService, times(1)).createUser(request);
    }

    @Test
    void getAllUsers_shouldReturnPageOfUsers() {
        UserResponseDTO dto1 = UserResponseDTO.builder()
                .userId(1)
                .fullName("John")
                .roleName("ADMIN")
                .email("john@example.com").build();
        UserResponseDTO dto2 = UserResponseDTO.builder()
                .userId(2)
                .fullName("Jane")
                .roleName("HR")
                .email("jane@example.com").build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("userId").ascending());
        Page<UserResponseDTO> page = new PageImpl<>(Arrays.asList(dto1, dto2));

        when(userService.getAllUsers(pageable)).thenReturn(page);

        Page<UserResponseDTO> result = userController.getAllUsers(0, 10);

        assertEquals(2, result.getTotalElements());
        verify(userService, times(1)).getAllUsers(pageable);
    }

    @Test
    void getUserById_shouldReturnUserResponse() {
        UserResponseDTO dto = UserResponseDTO.builder()
                .userId(1)
                .fullName("John")
                .roleName("ADMIN")
                .email("john@example.com").build();
        when(userService.getUserById(1)).thenReturn(dto);

        ResponseEntity<UserResponseDTO> response = userController.getUserById(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
        verify(userService).getUserById(1);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        UserRequestDTO request = new UserRequestDTO();
        UserResponseDTO updated = UserResponseDTO.builder()
                .userId(1)
                .fullName("Updated")
                .roleName("ADMIN")
                .email("Updated@example.com").build();
        when(userService.updateUser(1, request)).thenReturn(updated);

        ResponseEntity<UserResponseDTO> response = userController.updateUser(1, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        verify(userService).updateUser(1, request);
    }

    @Test
    void deleteUser_shouldReturnSuccessMessage() {
        doNothing().when(userService).deleteUser(1);

        ResponseEntity<String> response = userController.deleteUser(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService).deleteUser(1);
    }

    @Test
    void getUsersByRole_shouldReturnPagedResponse() {
        Page<UserResponseDTO> mockPage = new PageImpl<>(List.of(
                UserResponseDTO.builder()
                        .userId(1)
                        .fullName("Updated")
                        .roleName("ADMIN")
                        .email("Updated@example.com").build()
        ));
        when(userService.getUsersByRole("HR", 0, 10)).thenReturn(mockPage);

        ResponseEntity<Page<UserResponseDTO>> response = userController.getUsersByRole("HR", 0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPage, response.getBody());
        verify(userService).getUsersByRole("HR", 0, 10);
    }

    @Test
    void getUsersByRoleWithoutPagination_shouldReturnList() {
        List<UserResponseDTO> list = List.of(
                UserResponseDTO.builder()
                        .userId(1)
                        .fullName("Alice")
                        .roleName("ADMIN")
                        .email("alice@example.com").build()
        );
        when(userService.getAllUsersByRole("ADMIN")).thenReturn(list);

        ResponseEntity<List<UserResponseDTO>> response = userController.getUsersByRoleWithoutPagination("ADMIN");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        verify(userService).getAllUsersByRole("ADMIN");
    }

    @Test
    void getUsersAsPanelWithSameSlot_shouldReturnList() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        List<UserResponseDTO> list = List.of(
                UserResponseDTO.builder()
                        .userId(1)
                        .fullName("Panel User")
                        .roleName("PANEL")
                        .email("panel@example.com").build()
        );

        when(userService.getUsersAsPanelWithSameSlot(start, end)).thenReturn(list);

        ResponseEntity<List<UserResponseDTO>> response = userController.getUsersAsPanelWithSameSlot(start, end);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        verify(userService).getUsersAsPanelWithSameSlot(start, end);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        List<UserResponseDTO> list = List.of(
                UserResponseDTO.builder()
                        .userId(1)
                        .fullName("John")
                        .roleName("HR")
                        .email("john@example.com").build()
        );
        when(userService.getAllUsers()).thenReturn(list);

        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    void getPendingPanelists_shouldReturnList() {
        List<UserResponseDTO> list = List.of(
                UserResponseDTO.builder()
                        .userId(1)
                        .fullName("Pending Panel")
                        .roleName("PANEL")
                        .email("pending@example.com").build()
        );
        when(userService.getPendingPanelists()).thenReturn(list);

        ResponseEntity<List<UserResponseDTO>> response = userController.getPendingPanelists();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        verify(userService).getPendingPanelists();
    }
}
