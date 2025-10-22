package com.ibs.userservice.service;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.SlotResponseDto;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.feign.SlotClient;
import com.ibs.userservice.mapper.UserMapper;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.RoleRepository;
import com.ibs.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SlotClient slotClient;

    /**
     *
     * @param dto contains user data
     * @return UserResponseDTO
     */
    public UserResponseDTO createUser(UserRequestDTO dto) {
        try {
            log.info("Entering into createUser with data, {}", dto);
            Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

            User user = UserMapper.toEntity(dto, role);
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

            User savedUser = userRepository.save(user);
            log.info("Exit from createUser with data, {}", dto);
            return UserMapper.toResponseDTO(savedUser);
        } catch (RuntimeException e) {
            log.error("Exception occurred in createUser, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return List<UserResponseDTO>
     */
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(UserMapper::toResponseDTO);
    }

    /**
     *
     * @param id userid
     * @return UserResponseDTO
     */
    public UserResponseDTO getUserById(Integer id) {
        try {
            log.info("Entering into getUserById, {}", id);
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            return UserMapper.toResponseDTO(user);
        } catch (RuntimeException e) {
            log.error("Exception occurred getUserById ,{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param id  userid
     * @param dto contains data to be updated.
     * @return UserResponseDTO
     */
    public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {
        try {
            log.info("Entering into updateUser, {}", dto);
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            Role role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

            user.setUserName((dto.getUserName()));
            user.setEmail(dto.getEmail());
            user.setFullName(dto.getFullName());
            user.setRole(role);
            user.setUserPhone(dto.getUserPhone());
            user.setActive(dto.isActive());
            user.setPasswordHash(dto.getPassword());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            }

            User updatedUser = userRepository.save(user);
            log.info("Exit from updateUser");
            return UserMapper.toResponseDTO(updatedUser);
        } catch (RuntimeException e) {
            log.error("Exception occurred in updateUser, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param id userid
     */
    public void deleteUser(Integer id) {
        log.info("Entering into deleteUser, {}", id);
        try {
            if (!userRepository.existsById(id)) {
                throw new RuntimeException("User not found");
            }
            userRepository.deleteById(id);
        } catch (RuntimeException e) {
            log.error("Exception occurred in deleteUser, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param role role of the user
     * @param page page value from UI
     * @param size size value from UI
     * @return returns user details based on role
     */
    public Page<UserResponseDTO> getUsersByRole(String role, int page, int size) {
        try {
            log.info("Entering into getUsersByRole: {} page: {} size: {}", role, page, size);
            PageRequest pageable = PageRequest.of(page, size);
            Role roleData = roleRepository.findByRoleNameIgnoreCase(role).orElseThrow(() -> new RuntimeException("Invalid rome ," + role));
            Page<User> userPage = userRepository.findByRole(roleData, pageable);
            return userPage.map(UserMapper::toResponseDTO);
        } catch (Exception e) {
            log.error("Exception occurred in getUsersByRole, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param role user role
     * @return list users by role
     */
    public List<UserResponseDTO> getAllUsersByRole(String role) {
        return userRepository.findByRole_RoleNameIgnoreCase(role).stream().map(UserMapper::toResponseDTO).toList();
    }

    /**
     * connect to interview-scheduler service via feign client and slot details
     *
     * @param startTime key field in slot table and get from UI
     * @param endTime   key field in slot table and get from UI
     * @return available panelist at a specific time.
     */
    public List<UserResponseDTO> getUsersAsPanelWithSameSlot(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Fetching available panelists");
        try {
            List<SlotResponseDto> slots = slotClient.getAvailableSlots(startTime, endTime);
            if (slots == null || slots.isEmpty()) {
                return Collections.emptyList();
            }
            Set<Integer> userAsPanelistIds = slots.stream().map(SlotResponseDto::getPanelistId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (userAsPanelistIds.isEmpty()) {
                return Collections.emptyList();
            }
            List<User> users = userRepository.findAllById(userAsPanelistIds);
            return users.stream().map(UserMapper::toResponseDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred at feign " + e.getMessage());
        }
    }

    /**
     *
     * @return List<UserResponseDTO>
     */
    public List<UserResponseDTO> getAllUsers() {
        List<User> userPage = userRepository.findAll();
        return userPage.stream().map(UserMapper::toResponseDTO).toList();
    }

    /**
     *
     * @return List<UserResponseDTO>
     */
    public List<UserResponseDTO> getPendingPanelists() {
        log.info("Fetching all pending panellist");
        List<User> panelists = userRepository.findByRole_RoleNameIgnoreCase("PANEL");
        try {
            List<SlotResponseDto> slots = slotClient.getAllSlots();
            return panelists.stream().filter(p -> slots.stream().noneMatch(s -> Objects.equals(s.getPanelistId(), p.getUserId()))).map(UserMapper::toResponseDTO).toList();
        } catch (Exception e) {
            log.error("Exception occurred at getPendingPanelists");
            throw new RuntimeException(e);
        }

    }
}