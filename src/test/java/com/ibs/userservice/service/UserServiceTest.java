package com.ibs.userservice.service;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.SlotResponseDto;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.feign.SlotClient;
import com.ibs.userservice.mapper.UserMapper;
import com.ibs.userservice.repository.RoleRepository;
import com.ibs.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SlotClient slotClient;

    @InjectMocks
    private UserService userService;

    private MockedStatic<UserMapper> mockedUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @BeforeEach
    void init() {
        mockedUserMapper = mockStatic(UserMapper.class);
    }
    @AfterEach
    void tearDown() {
        mockedUserMapper.close();
    }

    // Create User
    @Test
    void createUser_shouldSaveUserAndReturnResponse() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUserName("john");
        request.setPassword("pass123");
        request.setRoleId(1);
        request.setFullName("John Doe");

        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("ADMIN");

        User user = new User();
        user.setUserName("john");

        User savedUser = new User();
        savedUser.setUserId(10);
        savedUser.setUserName("john");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(10)
                        .fullName("John")
                        .roleName("ADMIN")
                        .userName("john")
                        .email("john@example.com").build());

        mockedUserMapper.when(() -> UserMapper.toEntity(request, role))
                .thenReturn(user);

        UserResponseDTO response = userService.createUser(request);
        assertNotNull(response);
        assertEquals("john", response.getUserName());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void createUser_shouldThrowRuntimeException_whenRoleRepositoryFails() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO();
        request.setUserName("john");
        request.setPassword("secret");
        request.setRoleId(1);

        // Simulate exception during role fetch
        when(roleRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.createUser(request));

        // Verify exception is rethrown from catch block
        assertTrue(exception.getMessage().contains("Database error"));
        verify(roleRepository, times(1)).findById(1);
    }

    // Get all users (paged)
    @Test
    void getAllUsers_shouldReturnPagedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = new User();
        user1.setUserName("john");
        Page<User> userPage = new PageImpl<>(List.of(user1));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(10)
                        .fullName("John")
                        .roleName("ADMIN")
                        .userName("john")
                        .email("john@example.com").build());

        Page<UserResponseDTO> result = userService.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("john", result.getContent().getFirst().getUserName());
    }
    @Test
    void getUserById_shouldThrowRuntimeException_whenRepositoryFails() {
        // Arrange
        int userId = 1;

        // Simulate repository throwing an unexpected exception
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));

        // Verify
        assertTrue(exception.getMessage().contains("Database error"));
        verify(userRepository, times(1)).findById(userId);
    }

    // Get user by ID
    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setUserId(1);
        user.setUserName("john");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(10)
                        .fullName("John")
                        .roleName("ADMIN")
                        .userName("john")
                        .email("john@example.com").build());

        UserResponseDTO dto = userService.getUserById(1);
        assertEquals("john", dto.getUserName());
        verify(userRepository).findById(1);
    }

    // Update user
    @Test
    void updateUser_shouldUpdateAndReturnResponse() {
        UserRequestDTO request = new UserRequestDTO();
        request.setUserName("updated");
        request.setPassword("newpass");
        request.setRoleId(1);
        request.setFullName("Updated User");

        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("ADMIN");

        User existing = new User();
        existing.setUserId(1);
        existing.setUserName("oldUser");

        User updated = new User();
        updated.setUserId(1);
        updated.setUserName("updated");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(existing)).thenReturn(updated);

        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("updated")
                        .roleName("ADMIN")
                        .userName("updated")
                        .email("updated@example.com").build());

        UserResponseDTO dto = userService.updateUser(1, request);

        assertEquals("updated", dto.getUserName());
        verify(userRepository).save(existing);
    }
    @Test
    void updateUser_shouldThrowRuntimeException_whenRepositoryFails() {
        // Arrange
        int userId = 1;
        UserRequestDTO request = new UserRequestDTO();
        request.setUserName("John");
        request.setRoleId(2);
        request.setPassword("test123");

        // Simulate repository throwing exception during findById
        when(userRepository.findById(userId)).thenThrow(new RuntimeException("Database failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userId, request));

        // Verify catch block was triggered (rethrows RuntimeException)
        assertTrue(exception.getMessage().contains("Database failure"));
        verify(userRepository, times(1)).findById(userId);
    }

    // Delete user
    @Test
    void deleteUser_shouldDeleteWhenExists() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userRepository).deleteById(1);
    }
    @Test
    void deleteUser_shouldThrowRuntimeException_whenUserNotFound() {
        // Arrange
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));

        // Verify
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(anyInt());
    }
    @Test
    void deleteUser_shouldThrowRuntimeException_whenRepositoryThrows() {
        // Arrange
        int userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);
        doThrow(new RuntimeException("DB delete failed")).when(userRepository).deleteById(userId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));

        // Verify
        assertTrue(exception.getMessage().contains("DB delete failed"));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    // Get users by role (paged)
    @Test
    void getUsersByRole_shouldReturnPagedUsers() {
        Role role = new Role();
        role.setRoleName("HR");

        User user = new User();
        user.setUserName("hrUser");

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(roleRepository.findByRoleNameIgnoreCase("HR")).thenReturn(Optional.of(role));
        when(userRepository.findByRole(role, PageRequest.of(0, 10))).thenReturn(userPage);

        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("hrUser")
                        .roleName("HR")
                        .userName("hrUser")
                        .email("hrUser@example.com").build());

        Page<UserResponseDTO> result = userService.getUsersByRole("HR", 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByRole(role, PageRequest.of(0, 10));
    }
    @Test
    void getUsersByRole_shouldThrowRuntimeException_whenRepositoryFails() {
        // Arrange
        String roleName = "ADMIN";
        int page = 0;
        int size = 5;

        // Simulate an exception when trying to fetch the role
        when(roleRepository.findByRoleNameIgnoreCase(roleName))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUsersByRole(roleName, page, size));

        // Verify behavior
        assertTrue(exception.getMessage().contains("Database connection failed"));
        verify(roleRepository, times(1)).findByRoleNameIgnoreCase(roleName);
    }
    @Test
    void getUsersByRole_shouldThrowRuntimeException_whenRoleNotFound() {
        // Arrange
        String roleName = "INVALID_ROLE";
        int page = 0;
        int size = 5;

        when(roleRepository.findByRoleNameIgnoreCase(roleName)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUsersByRole(roleName, page, size));

        assertTrue(exception.getMessage().contains("Invalid rome"));
        verify(roleRepository).findByRoleNameIgnoreCase(roleName);
    }

    // Get all users by role (non-paged)
    @Test
    void getAllUsersByRole_shouldReturnList() {
        User user = new User();
        user.setUserName("panel");

        when(userRepository.findByRole_RoleNameIgnoreCase("PANEL")).thenReturn(List.of(user));
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("panel")
                        .roleName("PANEL")
                        .userName("panel")
                        .email("panel@example.com").build());

        List<UserResponseDTO> result = userService.getAllUsersByRole("PANEL");

        assertEquals(1, result.size());
        assertEquals("panel", result.getFirst().getUserName());
    }

    // Get users as panel with same slot
    @Test
    void getUsersAsPanelWithSameSlot_shouldReturnMatchingUsers() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        SlotResponseDto slot = new SlotResponseDto();
        slot.setPanelistId(5);

        User user = new User();
        user.setUserId(5);
        user.setUserName("panelUser");

        when(slotClient.getAvailableSlots(start, end)).thenReturn(List.of(slot));
        when(userRepository.findAllById(Set.of(5))).thenReturn(List.of(user));
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("panel")
                        .roleName("PANEL")
                        .userName("panelUser")
                        .email("panel@example.com").build());

        List<UserResponseDTO> result = userService.getUsersAsPanelWithSameSlot(start, end);

        assertEquals(1, result.size());
        assertEquals("panelUser", result.getFirst().getUserName());
    }
    @Test
    void getUsersAsPanelWithSameSlot_shouldReturnEmptyList_whenSlotsAreEmpty() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        when(slotClient.getAvailableSlots(start, end)).thenReturn(Collections.emptyList());

        // Act
        List<UserResponseDTO> result = userService.getUsersAsPanelWithSameSlot(start, end);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(slotClient).getAvailableSlots(start, end);
        verify(userRepository, never()).findAllById(anySet());
    }
    @Test
    void getUsersAsPanelWithSameSlot_shouldReturnEmptyList_whenPanelistIdsEmpty() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        SlotResponseDto slotWithoutPanel = new SlotResponseDto();
        slotWithoutPanel.setPanelistId(null);

        when(slotClient.getAvailableSlots(start, end)).thenReturn(List.of(slotWithoutPanel));

        // Act
        List<UserResponseDTO> result = userService.getUsersAsPanelWithSameSlot(start, end);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(slotClient).getAvailableSlots(start, end);
        verify(userRepository, never()).findAllById(anySet());
    }
    @Test
    void getUsersAsPanelWithSameSlot_shouldThrowRuntimeException_whenFeignFails() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        when(slotClient.getAvailableSlots(start, end))
                .thenThrow(new RuntimeException("Feign unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUsersAsPanelWithSameSlot(start, end));

        assertTrue(exception.getMessage().contains("Feign unavailable"));
        verify(slotClient).getAvailableSlots(start, end);
    }

    // Get all users
    @Test
    void getAllUsers_shouldReturnList() {
        User user = new User();
        user.setUserName("john");
        when(userRepository.findAll()).thenReturn(List.of(user));
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("panel")
                        .roleName("PANEL")
                        .userName("panel")
                        .email("panel@example.com").build());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    // Get pending panelists
    @Test
    void getPendingPanelists_shouldReturnFilteredList() {
        User panel = new User();
        panel.setUserId(10);
        panel.setUserName("panelUser");

        SlotResponseDto slot = new SlotResponseDto();
        slot.setPanelistId(20); // different ID

        when(userRepository.findByRole_RoleNameIgnoreCase("PANEL")).thenReturn(List.of(panel));
        when(slotClient.getAllSlots()).thenReturn(List.of(slot));
        mockedUserMapper.when(() -> UserMapper.toResponseDTO(any(User.class)))
                .thenReturn(UserResponseDTO.builder()
                        .userId(1)
                        .fullName("panel")
                        .roleName("PANEL")
                        .userName("panelUser")
                        .email("panel@example.com").build());

        List<UserResponseDTO> result = userService.getPendingPanelists();

        assertEquals(1, result.size());
        assertEquals("panelUser", result.getFirst().getUserName());
    }
    @Test
    void getPendingPanelists_shouldThrowRuntimeException_whenFeignFails() {
        // Arrange
        User panelUser = new User();
        panelUser.setUserId(10);
        panelUser.setUserName("PanelUser");

        // Mock repository to return some panel users
        when(userRepository.findByRole_RoleNameIgnoreCase("PANEL"))
                .thenReturn(List.of(panelUser));

        // Mock Feign client (slotClient) to throw exception
        when(slotClient.getAllSlots())
                .thenThrow(new RuntimeException("Feign service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getPendingPanelists());

        // Verify behavior
        assertTrue(exception.getMessage().contains("Feign service unavailable"));
        verify(userRepository, times(1)).findByRole_RoleNameIgnoreCase("PANEL");
        verify(slotClient, times(1)).getAllSlots();
    }
}
