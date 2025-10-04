package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Mapper
public interface TravelEndMapper {

    // 현재 OPEN,MATCHED 여행ID 찾기
    List<Long> findOpenAndMatchedArticleIds();
    int updateEndTravels(List<Long> articles);

}
