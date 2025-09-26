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
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String mainImagePath;
 // TravelEditDto.java 에 List<String> tags 추가
    private List<String> tags;
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    private String tag; 
    private List<MultipartFile> images;
}
