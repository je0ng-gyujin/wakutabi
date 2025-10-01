package com.wakutabi.domain;

import lombok.Data; // Lombok 사용 가정

@Data // Getter, Setter, toString 등을 자동 생성
public class RequestStatusDto {
    
    // 프론트엔드에서 보낸 JSON의 "status" 키와 매핑됩니다.
    private String status; // 값: "ACCEPT" 또는 "REJECT"
}