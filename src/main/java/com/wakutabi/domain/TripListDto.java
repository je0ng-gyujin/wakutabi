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
    
    // ⭐ 새로 추가된 필수 필드 ⭐
    private int currentParticipants; // 현재 참여 인원 수
    private int maxParticipants;     // 최대 모집 인원 수
    
    // (선택) 목록에서 태그를 보여주려면 List<String> tags를 추가할 수 있습니다.
    // private List<String> tags;
}
