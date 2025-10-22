// src/main/java/com/ibs/userservice/feign/SlotClient.java
package com.ibs.userservice.feign;

import com.ibs.userservice.dtos.responseDtos.SlotResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "interview-scheduler", configuration = FeignClientsConfiguration.class)
public interface SlotClient {

    @GetMapping("/api/v1/slots/overlapping/slots")
    List<SlotResponseDto> getAvailableSlots(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    );

    @GetMapping("/api/v1/slots")
    List<SlotResponseDto> getAllSlots();
}
