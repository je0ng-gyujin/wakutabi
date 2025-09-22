package com.wakutabi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.mapper.TravelEditMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelEditService {

	private final TravelEditMapper travelEditmapper;
	
	@Transactional
	public void insertTravelEdit(TravelEditDto traveledit) {
		travelEditmapper.insertTravelEdit(traveledit);
	}
	
	 // --- 이 메소드를 추가하여 DB에서 게시글 정보를 가져옵니다. ---
    public TravelEditDto findTravelById(Long id) {
        return travelEditmapper.findById(id);
    }
}
