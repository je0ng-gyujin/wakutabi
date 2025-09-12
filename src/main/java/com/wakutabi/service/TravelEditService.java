package com.wakutabi.service;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.mapper.TravelEditMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelEditService {

	private final TravelEditMapper travelEditmapper;
	
	
	public void insertTravelEdit(TravelEditDto traveledit) {
		travelEditmapper.insertTravelEdit(traveledit);
	}
}
