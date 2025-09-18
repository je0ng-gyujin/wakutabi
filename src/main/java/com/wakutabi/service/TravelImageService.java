package com.wakutabi.service;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.mapper.TravelImageMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TravelImageService {
	private final TravelImageMapper travelImageMapper;
	
	public void insertTravelImage(TravelImageDto dto) {
		travelImageMapper.insertTravelImage(dto);
	}
}
