package com.ibs.userservice.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponseDto {
    private Integer id;
    private Integer panelistId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
