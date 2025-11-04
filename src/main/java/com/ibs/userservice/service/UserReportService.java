package com.ibs.userservice.service;

import com.ibs.userservice.dtos.responseDtos.ReportResponseDto;
import com.ibs.userservice.entity.User;
import com.ibs.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReportService {

    private final UserRepository userRepository;

    /**
     *
     * @param role user role
     * @param startDate start date
     * @param endDate end date
     * @param page page
     * @param size size
     * @param sortField sort field
     * @param sortDir sort direction
     * @return ReportResponseDto
     */
    public ReportResponseDto getUserReport(
            String role,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortField,
            String sortDir
    ) {
        try {
            log.info("Fetching user report");
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
        } catch (Exception e) {
            log.error("Exception occurred while fetching user report");
            throw new RuntimeException(e);
        }
    }
}
