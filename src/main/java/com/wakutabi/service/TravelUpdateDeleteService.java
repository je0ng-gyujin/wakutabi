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
    public boolean deleteTravelArticel(Long id, Long hostUserId) {
        // 소프트 삭제: 상태를 CANCELED로 변경하고 deleted_at을 기록합니다. (연관 데이터는 유지)
        int affected = travelupdatedeletemapper.deleteTravelArticle(id, hostUserId);
        return affected > 0;
    }
    // 여행 취소
    public boolean canceledTravelArticle(Long id, Long hostUserId){
        int result = travelupdatedeletemapper.canceledTravelArticle(id, hostUserId);
        return result > 0;
    }
}