package com.wakutabi.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class TravelUploadDto {
	
	private String tripArticleId;
	private String location;
    private String content;
    private String title;
    private String startDate;
    private String endDate;
    private Integer maxParticipants;
    private String ageLimit;
    private String genderLimit;
    private Integer estimatedCost;
    private String tag;
    private String orderNumber;
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
}
