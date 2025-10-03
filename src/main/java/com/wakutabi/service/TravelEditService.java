package com.wakutabi.service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TripTagDto;
import com.wakutabi.mapper.TravelEditMapper;
import com.wakutabi.mapper.TravelTagMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelEditService {

	private final TravelEditMapper travelEditmapper;
	private final TravelTagMapper travelTagMapper;
	
	@Transactional
	public void insertTravelEdit(TravelEditDto traveledit) {
		travelEditmapper.insertTravelEdit(traveledit);
	}
	
	 // DB에서 게시글 정보를 가져옵니다.
    public TravelEditDto findTravelById(Long id) {
        // 1️⃣ Mapper에서 여행 게시글 조회
        TravelEditDto travelEditDto = travelEditmapper.findById(id);

        // 2️⃣ 해당 게시글의 태그 조회 후 DTO에 세팅
        if (travelEditDto != null) {
            List<String> tags = travelTagMapper.findTagsByTripArticleId(id);
            travelEditDto.setTags(tags);
        }

        // 3️⃣ 완성된 DTO 반환
        return travelEditDto;
    }
    
 // 복합 검색 및 필터링 기능을 위한 메서드
    public List<TravelEditDto> findFilteredTravels(String query, Integer minPrice, Integer maxPrice,
            String region, LocalDateTime startDate, LocalDateTime endDate,
            List<String> tags, List<String> groupSize,String status) {

	Map<String, Object> params = new HashMap<>();
	params.put("query", query);
	params.put("minPrice", minPrice);
	params.put("maxPrice", maxPrice);
	params.put("region", region);
	params.put("startDate", startDate);
	params.put("endDate", endDate);
	params.put("tagsList", tags);      // Mapper XML에서 <foreach collection="tagsList"> 사용
	params.put("groupSize", groupSize);
	params.put("status", status);
	
	// 1️⃣ Mapper에서 여행 게시글 조회
    List<TravelEditDto> travels = travelEditmapper.selectTravels(params);

    // 2️⃣ 각 여행 게시글에 대한 태그 조회 후 DTO에 세팅
    for (TravelEditDto travel : travels) {
        List<String> tagList = travelTagMapper.findTagsByTripArticleId(travel.getId());
        travel.setTags(tagList);
    }

    return travels;
}
    @Transactional // ⭐트랜잭션 처리를 위해 어노테이션을 붙입니다.
    public void saveTravelWithTags(TravelEditDto travel) {
        // 1. 여행글 등록
        travelEditmapper.insertTravelEdit(travel);

        // 2. 선택한 태그를 중간 테이블에 저장
        if (travel.getTags() != null) {
            for (String tagName : travel.getTags()) {
            	// ⭐⭐⭐ 로직 변경: 태그를 먼저 찾아보고, 없으면 생성합니다 ⭐⭐⭐
                Long tagId = travelTagMapper.findTagIdByName(tagName);
             // ⭐ ⭐ ⭐ 변경: tagId가 null이면 새로운 태그를 생성하고 ID를 가져오는 로직 ⭐ ⭐ ⭐
                if (tagId == null) {
                    TripTagDto newTag = new TripTagDto();
                    newTag.setTagName(tagName);
                    travelTagMapper.insertTag(newTag); // 새로운 태그 저장
                    tagId = newTag.getId(); // ⭐⭐⭐ 생성된 ID를 DTO에서 직접 가져옴 ⭐⭐⭐
                }

                if (tagId != null) {
                    travelTagMapper.insertTripTag(travel.getId(), tagId);
                }
            }
        }
    }
}
