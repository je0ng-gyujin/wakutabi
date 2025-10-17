package com.wakutabi.mapper;

import com.wakutabi.domain.ReviewUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.ReviewTravelDto;

@Mapper
public interface ReviewMapper {
    // 리뷰창에 띄울 여행, 참가자 정보 DTO객체에 반환
    ReviewTravelDto getTripAndParticipantsForReview(@Param("tripId")Long tripId);
    // 리뷰 확인을 위한 리뷰 갯수 검색
    int countExistingReview(@Param("userId")Long userId, @Param("tripId")Long tripId);
    // 여행리뷰내용 DB에 저장
    void insertTravleReview(ReviewTravelDto reviewTravleDto);
    // 여행리뷰사진 DB에 저장
    void insertTravleReviewImage(@Param("reviewId") Long reviewId, @Param("fileName") String fileName);
    // 참가자리뷰내용 DB에 저장
    void insertUserReview(ReviewUserDto user);
}
