package com.wakutabi.mapper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelEditDto;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface TravelEditMapper {

	// 여행 일정 등록
	void insertTravelEdit(TravelEditDto traveledit);
<<<<<<< HEAD

    // 여행id로 호스트id 찾기
    Long hostIdFindById(@RequestParam("travelArticleId")Long travelArticleId);

	// --- 이 메소드를 추가하여 ID로 게시글을 찾습니다. ---
    TravelEditDto findById(@Param("id") Long id);
    
 // Map으로 필터 전달
    List<TravelEditDto> selectTravels(Map<String, Object> params);
=======
>>>>>>> spring/travel_article_crud_mgk

	TravelEditDto selectTravelDetail(Long id);
}
