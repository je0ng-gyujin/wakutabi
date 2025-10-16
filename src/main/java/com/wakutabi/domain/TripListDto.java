package com.wakutabi.domain;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Lombok의 Builder 패턴을 사용하면 객체 생성이 간편해집니다.
public class TripListDto {

	private Long id;
    private String title;
    private String location; // 여행지
    private LocalDate startDate;
    private LocalDate endDate;
    private String mainImagePath; // 이미지 경로
    private String status;        // OPEN, CLOSED 등 상태
    private String content; // 여행 상세 내용 필드 추가
    
    // ⭐ 새로 추가된 필수 필드 ⭐
    private int currentParticipants; // 현재 참여 인원 수
    private int maxParticipants;     // 최대 모집 인원 수
    
    //private String applicationStatus;
    
    // (선택) 목록에서 태그를 보여주려면 List<String> tags를 추가할 수 있습니다.
    // private List<String> tags;
    
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
