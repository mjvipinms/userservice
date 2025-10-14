package com.ibs.userservice.controller;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    /**
     *
     * @param dto contains the data to be saved
     * @return UserResponseDTO
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'PANEL')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }
    /**
     *
     * @return List<UserResponseDTO>
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'PANEL')")
    @GetMapping
    public Page<UserResponseDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        return userService.getAllUsers(pageable);
    }

    /**
     *
     * @param id userid
     * @return UserResponseDTO
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'PANEL')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    /**
     *
     * @param id userid
     * @param dto contains the data to be updated
     * @return UserResponseDTO
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'PANEL')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
    /**
     *
     * @param id userid
     * @return String
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR', 'PANEL')")
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserResponseDTO>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponseDTO> response = userService.getUsersByRole(role, page, size);
        return ResponseEntity.ok(response);
    }
}