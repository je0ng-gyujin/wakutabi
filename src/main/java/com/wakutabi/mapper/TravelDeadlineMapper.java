package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface TravelDeadlineMapper {
    boolean travelDeadlineMaxparticipants(Map<String, Object> params);

    int travelDeadlineHostClick(Long travelArticleId);

    int autoMatchedExpiredArticles();

    void updateTripArticleStatusBasedOnParticipants(@Param("tripId") Long tripId);
}
