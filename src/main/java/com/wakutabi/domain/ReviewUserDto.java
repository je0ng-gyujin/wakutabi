package com.wakutabi.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewUserDto {

    /** 
     * @NotNull: null이 아닌지 검사
     * reviewedUserId: 리뷰를 받는 사용자의 ID
     * userRating: 사용자가 준 별점
     */
    
    @NotNull
    private Long reviewedUserId;

    @NotNull
    private int userRating;

}
