package com.wakutabi.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelEditDto;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface TravelEditMapper {

	void insertTravelEdit(TravelEditDto traveledit);

    // 여행id로 호스트id 찾기
    Long hostIdFindById(@RequestParam("travelArticleId")Long travelArticleId);

	// --- 이 메소드를 추가하여 ID로 게시글을 찾습니다. ---
    TravelEditDto findById(@Param("id") Long id);
    
 // Map으로 필터 전달
    List<TravelEditDto> selectTravels(Map<String, Object> params);
 // ⭐️ 총 개수를 카운트하는 메서드 추가
    Long countTravels(Map<String, Object> params);
    // 페이지
    int countFilteredTravels(Map<String, Object> params);
}
