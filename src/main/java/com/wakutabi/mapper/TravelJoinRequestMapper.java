package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelJoinRequestDto;
@Mapper
public interface TravelJoinRequestMapper{

    // 여행참가신청
    void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest);
    // 참가수락 
    void changeStatusToAccepted(@Param("travelArticleId") Long TravelArticleId, @Param("hostUserId") Long hostUserId, @Param("applicantUserId") Long applicantUserId);
    // 참가거절
    void changeStatusToRejected(@Param("travelArticleId") Long TravelArticleId, @Param("hostUserId") Long hostUserId, @Param("applicantUserId") Long applicantUserId);

}
