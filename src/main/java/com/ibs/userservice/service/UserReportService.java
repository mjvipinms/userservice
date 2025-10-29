package com.ibs.userservice.service;

import com.ibs.userservice.dtos.responseDtos.ReportResponseDto;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserRepository userRepository;

    public ReportResponseDto getUserReport(
            String role,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortField,
            String sortDir
    ) {
        // Sorting setup
        Sort sort = Sort.unsorted();
        if (sortField != null && !sortField.isBlank()) {
            sort = Sort.by(Sort.Direction.fromString(sortDir), sortField);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // DB call
        Page<User> userPage = userRepository.findByRoleAndDateRange(
                role.toUpperCase(), startDate, endDate, pageable
        );

        // Map to simple DTOs or maps
        List<Map<String, Object>> data = userPage.getContent().stream()
                .map(u -> Map.<String,Object>of(
                        "userId", u.getUserId(),
                        "fullName", u.getFullName(),
                        "email", u.getEmail(),
                        "phone", u.getUserPhone(),
                        "role", u.getRole().getRoleName(),
                        "active", u.isActive(),
                        "createdAt", u.getCreatedAt()
                ))
                .toList();

        // Return response DTO
        return new ReportResponseDto(data, page, size, userPage.getTotalElements());
    }
}
