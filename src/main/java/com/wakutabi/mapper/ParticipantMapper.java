// src/main/java/com/wakutabi/mapper/ParticipantMapper.java
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
     * @return 호스트와 참여자 목록 (호스트가 항상 첫 번째)
     */
    List<ParticipantDto> findParticipantsByTripId(@Param("tripArticleId") Long tripArticleId);

    // 5. 여행 참여 여부
    int isParticipants(@Param("userId")Long userId, @Param("tripArticleId")Long tripArticleId);
}