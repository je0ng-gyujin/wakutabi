package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import com.wakutabi.domain.TravelImageDto;

@Mapper
public interface TravelImageMapper {
	
	void insertTravelImage(TravelImageDto travelImageDto);
	
	// --- 이 메소드를 추가하여 특정 게시글의 이미지 목록을 찾습니다. ---
    List<TravelImageDto> findByTripArticleId(@Param("tripArticleId") Long tripArticleId);
}
