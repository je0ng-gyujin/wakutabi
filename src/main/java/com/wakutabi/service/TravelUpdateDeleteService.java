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
    public boolean updateTravelImage(TravelEditDto dto) {
    	int updateImg = travelupdatedeletemapper.updateTravelImage(dto);
    	return updateImg > 0;
    }
    
    @Transactional
    public boolean deleteTravelArticle(Long id, Long hostUserId) {
        
        // 1. 게시글에 연결된 이미지들을 먼저 삭제합니다. (자식 테이블)
        travelupdatedeletemapper.deleteTravelImages(id);
        
        // 2. 그 다음, 게시글 본문을 삭제합니다. (부모 테이블)
        int deleteRows = travelupdatedeletemapper.deleteTravelArticle(id, hostUserId);
        
        return deleteRows > 0;
    }
}