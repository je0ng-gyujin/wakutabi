package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelJoinRequestDto;
@Mapper
public interface TravelJoinRequestMapper{
    
    // 여행참가신청
    void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest);
    // 참가수락 
    void changeStatusToAccepted(Long id);
    // 참가거절
    void changeStatusToRejected(Long id);

}
