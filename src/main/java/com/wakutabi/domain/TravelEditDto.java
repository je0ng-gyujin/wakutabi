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
    
    // 지역명을 한글로 변환하는 메서드
    public String getLocationKorean() {
        if (location == null) return "지역 미정";
        
        switch (location.toLowerCase()) {
            case "tokyo": return "도쿄";
            case "osaka": return "오사카";
            case "kyoto": return "교토";
            case "hokkaido": return "홋카이도";
            case "okinawa": return "오키나와";
            case "shikoku": return "시코쿠";
            case "kyushu": return "규슈";
            case "chubu": return "주부";
            case "tohoku": return "도호쿠";
            case "chugoku": return "주고쿠";
            case "kansai": return "간사이";
            case "kanto": return "간토";
            default: return location;
        }
    }
    
    // 연령 제한을 한글로 변환하는 메서드
    public String getAgeLimitKorean() {
        if (ageLimit.equalsIgnoreCase("NO")) {
            return "제한 없음";
        }
        
        // "20,30,40" 형태의 문자열을 "20대,30대,40대"로 변환
        return ageLimit.replace("20", "20대")
                    .replace("30", "30대")
                    .replace("40", "40대")
                    .replace("MX", "혼합");
    }
}
