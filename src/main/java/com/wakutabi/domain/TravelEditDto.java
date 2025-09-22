package com.wakutabi.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TravelEditDto {
    private Long id;
    private Long hostUserId;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private String ageLimit;
    private String genderLimit;
    private String title;
    private String content;
    private Integer estimatedCost;
    private String status;        // OPEN, MATCHED, CLOSED, CANCELED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String tag; 
    private List<MultipartFile> images;
}
