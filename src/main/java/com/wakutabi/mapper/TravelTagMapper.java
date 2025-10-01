package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelTagDto;

@Mapper
public interface TravelTagMapper {
    // 1. 태그 이름으로 ID를 조회
    Long findTagIdByName(String tagName);

    // 2. 새로운 태그를 삽입하고 ID 반환
    void insertNewTag(TravelTagDto travelTagDto); // 이 메서드는 XML에서 useGeneratedKeys를 사용해 ID를 태그 객체에 저장할 수 있습니다.

    // 3. 여행 게시글과 태그를 연결 (중간 테이블에 삽입)
    void linkArticleAndTag(Long tripArticleId, Long tripTagId);
}
