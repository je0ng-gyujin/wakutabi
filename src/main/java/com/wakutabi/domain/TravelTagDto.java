package com.wakutabi.domain;

import lombok.Data;

@Data
public class TravelTagDto { // 용도: 태그 이름과 ID를 함께 관리
    private Long id; // MyBatis가 생성된 ID를 여기에 설정합니다.
    private String tagName;
}
