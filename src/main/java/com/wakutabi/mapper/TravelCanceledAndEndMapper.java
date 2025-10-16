package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TravelCanceledAndEndMapper {

    // 현재 OPEN,MATCHED 여행ID 찾기
    List<Long> findOpenAndMatchedArticleIds();
    // 현재 CANCELED 여행ID 찾기
    List<Long> findCanceledArticleIds();
    // 현재 CLOSE 여행ID 찾기
    List<Long> findEndArticleIds();
    // 여행 종료
    int updateEndTravels(List<Long> articles);

}
