package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelJoinRequestDto;

import java.util.Map;
import java.util.Objects;

@Mapper
public interface TravelJoinRequestMapper{

    // 여행참가신청
    void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest);
    // 참가수락
    void changeStatusToAccepted(TravelJoinRequestDto statusToAccepted);
    // 참가거절
    void changeStatusToRejected(TravelJoinRequestDto statusToRejected);
    // 참가 대기중이거나 참가중일 때
    Map<String,Object> existJoinRequest(Map<String, Object> check);
}
