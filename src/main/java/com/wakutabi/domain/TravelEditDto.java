package com.wakutabi.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TravelEditDto {
    private Long id;
    private Long hostUserId;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
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
    private String mainImagePath;
 // TravelEditDto.java 에 List<String> tags 추가
    private List<String> tags;
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
