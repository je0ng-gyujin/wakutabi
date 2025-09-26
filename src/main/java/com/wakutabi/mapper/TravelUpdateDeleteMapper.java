package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelEditDto;

@Mapper
public interface TravelUpdateDeleteMapper {

    int updateTravelArticle(TravelEditDto dto);
    int updateTravelImage(TravelEditDto dto);
    
    // 게시글 본문을 삭제하는 메서드 (부모 테이블)
    int deleteTravelArticle(@Param("id") Long id, @Param("hostUserId") Long hostUserId);
    
    // 게시글과 연관된 이미지를 먼저 삭제하는 메서드 (자식 테이블)
    int deleteTravelImages(@Param("id") Long id);
}