package com.wakutabi.mapper;

import com.wakutabi.domain.ParticipantDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParticipantMapper {
    /**
     * 특정 여행 게시글의 호스트와 참여자 목록을 조회합니다.
     * @param tripArticleId 여행 게시글 ID
     * @return 호스트와 참여자 DTO 리스트
     */
    List<ParticipantDto> findParticipantsByTripId(@Param("tripArticleId") Long tripArticleId);
}