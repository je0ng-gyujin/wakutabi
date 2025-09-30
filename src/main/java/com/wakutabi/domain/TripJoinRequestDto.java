package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TripJoinRequestDto {
    
    // trip_join_request 테이블 정보
    private Long requestId;           // 참가 신청 고유 ID
    private Long tripArticleId;       // 여행 게시글 ID
    private Long applicantUserId;     // 신청자 유저 ID
    private String status;            // 신청 상태 (PENDING, ACCEPTED, REJECTED)
    private LocalDateTime createdAt;  // 신청일자
    
    // users 테이블에서 가져올 신청자 정보 (화면에 표시)
    private String nickname;          // 신청자 닉네임
    private String gender;            // 신청자 성별
    private int age;                  // 신청자 나이 (서비스에서 계산 필요)
    private String introduce;         // 신청자 자기소개
    
    // 기타 필요한 정보 (선택 사항)
    // private List<String> userTags; // 신청자의 관심 태그
}