package com.wakutabi.service;

import com.wakutabi.mapper.TravelDeadlineMapper;
import com.wakutabi.mapper.TravelEditMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelDeadlineService {
	
	private final TravelDeadlineMapper travelDeadlineMapper;
	private final TravelEditMapper travelEditMapper;

	// 여행마감처리(인원 다 찼을때)
	public boolean travelDeadline(Map params){
		return travelDeadlineMapper.travelDeadlineMaxparticipants(params);
	}
	// 여행id로 호스트id 가져오기
	public Long hostUserIdByTravelArticleId(Long travelArticleId){
		return travelEditMapper.hostIdFindById(travelArticleId);
	}
	// 방장이 마감 눌렀을 때
	public boolean travelDeadlineHostClick(Long travelArticleId){
		return travelDeadlineMapper.travelDeadlineHostClick(travelArticleId) > 0;
	}

}
