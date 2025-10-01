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
    private String status;  // OPEN, MATCHED, CLOSED, CANCELED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    private String tagString; // ✨ 1. 태그 문자열 필드 추가
    private List<TravelImageDto> images; // ✨ 2. 이미지 리스트 필드 추가
    private List<String> tagNames; // ✨ 3. 태그 리스트 필드 추가
    private List<String> displayTags; // ✨ 4. 화면 표시용 태그 리스트 필드 추가
    private String displayGenderLimit; // ✨ 5. 화면 표시용 성별 제한 필드 추가
    private String displayStatus; // ✨ 6. 화면 표시용 상태 필드 추가
    private String displayLocation; // ✨ 7. 화면 표시용 위치 필드 추가
}
