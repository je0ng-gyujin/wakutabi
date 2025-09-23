package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelJoinRequestDto;
@Mapper
public interface TravelJoinRequestMapper{

    // 여행참가신청
    void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest);
    // 참가수락 
    int changeStatusToAccepted(@Param("id") Long id, @Param("hostUserId") Long hostUserId);
    // 참가거절
    int changeStatusToRejected(@Param("id") Long id, @Param("hostUserId") Long hostUserId);

}
