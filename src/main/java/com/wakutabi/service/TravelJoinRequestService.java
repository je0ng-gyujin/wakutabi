package com.wakutabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.mapper.TravelImageMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TravelJoinRequestService {
	private final TravelImageMapper travelImageMapper;
	
	public void insertTravelImage(TravelImageDto dto) {
		travelImageMapper.insertTravelImage(dto);
	}
	
	// 이 메서드를 추가하여 이미지 목록을 조회합니다.
    public List<TravelImageDto> findImagesByTripArticleId(Long tripArticleId) {
        return travelImageMapper.findByTripArticleId(tripArticleId);
    }
}
