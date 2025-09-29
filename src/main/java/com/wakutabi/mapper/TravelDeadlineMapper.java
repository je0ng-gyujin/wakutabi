package com.wakutabi.mapper;

import com.wakutabi.domain.TravelJoinRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Mapper
public interface TravelDeadlineMapper {

    // 여행마감처리(최대인원일 때)
    boolean travelDealine(Map params);
}
