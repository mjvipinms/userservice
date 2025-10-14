package com.ibs.userservice.service;

import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.mapper.UserMapper;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.RoleRepository;
import com.ibs.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

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
            Role roleData = roleRepository.findByRoleNameIgnoreCase(role).orElseThrow(() -> new RuntimeException("Invalid rome ,"+role));
            Page<User> userPage = userRepository.findByRole(roleData, pageable);
            return userPage.map(UserMapper::toResponseDTO);
        } catch (Exception e) {
            log.error("Exception occurred in getUsersByRole, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}