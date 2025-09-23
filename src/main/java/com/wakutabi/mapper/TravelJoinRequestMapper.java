package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelJoinRequestDto;
@Mapper
public interface TravelJoinRequestMapper{

    void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest);
    void changeStatusToAccepted(Long id);
    void changeStatusToRejected(Long id);

}
