package com.ibs.userservice.controller;

import com.ibs.userservice.dtos.responseDtos.ReportResponseDto;
import com.ibs.userservice.service.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserReportController {

    private final UserReportService userReportService;

    @GetMapping("/report")
    public ResponseEntity<ReportResponseDto> getUserReport(
            @RequestParam String role,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        ReportResponseDto reportResponse = userReportService.getUserReport(
                role, startDate, endDate, page, size, sortField, sortDir
        );
        return ResponseEntity.ok(reportResponse);
    }
}
