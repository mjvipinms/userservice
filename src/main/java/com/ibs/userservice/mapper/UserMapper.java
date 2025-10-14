package com.ibs.userservice.mapper;


import com.ibs.userservice.dtos.requestDtos.UserRequestDTO;
import com.ibs.userservice.dtos.responseDtos.UserResponseDTO;
import com.ibs.userservice.entity.Role;
import com.ibs.userservice.entity.User;

public class UserMapper {

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getUserName(),
                user.getUserPhone(),
                user.getPasswordHash(),
                user.getEmail(),
                user.getFullName(),
                user.isActive(),
                user.getRole().getRoleId(),
                user.getRole().getRoleName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static User toEntity(UserRequestDTO dto, Role role) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPasswordHash(dto.getPassword());
        user.setUserPhone(dto.getUserPhone());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setActive(dto.isActive());
        user.setRole(role);
        return user;
    }
}
