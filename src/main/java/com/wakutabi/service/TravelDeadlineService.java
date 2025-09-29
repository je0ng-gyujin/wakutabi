package com.wakutabi.service;

import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelDeadlineMapper;
import com.wakutabi.mapper.TravelJoinRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelDeadlineService {
	
	private final TravelDeadlineMapper travelDeadlineMapper;

	// 여행마감처리(인원 다 찼을때)
	public boolean travelDeadline(Map params){
		return travelDeadlineMapper.travelDealine(params);

	}
}
