package com.wakutabi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.mapper.TravelUpdateDeleteMapper;
import com.wakutabi.mapper.TravelTagMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelUpdateDeleteService {

    private final TravelUpdateDeleteMapper travelupdatedeletemapper;
    private final TravelTagMapper travelTagMapper;

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
        // 0. 태그 중간 테이블 삭제 (FK 제약으로 부모 삭제 전에 필요)
        travelTagMapper.deleteTripTagsByTravelId(id);

        // 1. 게시글에 연결된 이미지들을 먼저 삭제합니다. (자식 테이블)
        travelupdatedeletemapper.deleteTravelImages(id);

        // TODO: 필요 시 아래 자식 테이블들도 정리 (FK 에러 발생 시 순차 삭제)
        // - trip_join_request (trip_article_id)
        // - chat_room -> chat_participants, chat_message (trip_article_id)
        // - system_notification (trip_article_id)
        // - qna_questions (trip_article_id)
        
        // 2. 그 다음, 게시글 본문을 삭제합니다. (부모 테이블)
        int deleteRows = travelupdatedeletemapper.deleteTravelArticle(id, hostUserId);
        
        return deleteRows > 0;
    }
    // 여행 취소
    public boolean canceledTravelArticle(Long id){
        int result = travelupdatedeletemapper.canceledTravelArticle(id);
        return result > 0;
    }
}