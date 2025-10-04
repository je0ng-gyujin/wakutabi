package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Mapper
public interface TravelDeadlineMapper {

    // 여행마감처리(최대인원일 때)
    boolean travelDeadlineMaxparticipants(Map params);
    // 방장이 모집마감 눌렀을 때
    int travelDeadlineHostClick(@RequestParam("travelArticleId")Long travelArticleId);
    // 기간만료 자동마감처리
    int autoMatchedExpiredArticles();

}
