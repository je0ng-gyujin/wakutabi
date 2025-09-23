package com.wakutabi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.mapper.TravelUpdateDeleteMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelUpdateDeleteService {

 	private final TravelUpdateDeleteMapper travelupdatedeletemapper;
 	
 	
 	public boolean updateTravelArticle(TravelEditDto dto) {
 		int updateRows = travelupdatedeletemapper.updateTravelArticle(dto);
 		return updateRows > 0;
 	}
 	
 	
 	@Transactional //이미지 + 글 두 테이블동시에 삭제
 	public boolean deleteTravelArticle(Long id,Long hostUserId) {
 		int deleteRows = travelupdatedeletemapper.deleteTravelArticle(id, hostUserId);
 		return deleteRows > 0 ;
 	}
}
