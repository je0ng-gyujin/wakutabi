package com.wakutabi.mapper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelEditDto;
@Mapper
public interface TravelEditMapper {

	void insertTravelEdit(TravelEditDto traveledit);
	
	// --- 이 메소드를 추가하여 ID로 게시글을 찾습니다. ---
    TravelEditDto findById(@Param("id") Long id);
    
 // Map으로 필터 전달
    List<TravelEditDto> selectTravels(Map<String, Object> params);

}
