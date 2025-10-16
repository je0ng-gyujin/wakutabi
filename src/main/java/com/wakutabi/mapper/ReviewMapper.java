package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.ReviewTravelDto;

@Mapper
public interface ReviewMapper {

    void insertTravleReview(ReviewTravelDto reviewTravleDto);

    void insertTravleReviewImage(@Param("reviewId") Long reviewId, @Param("fileName") String fileName);

    void insertUserReview(@Param("reviewId") Long reviewId, @Param("reviewedUserId") Long reviewedUserId, @Param("userRating") int userRating);
}
