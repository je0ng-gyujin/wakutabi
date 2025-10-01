package com.wakutabi.mapper;

<<<<<<< HEAD
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.wakutabi.domain.TripTagDto;

@Mapper
public interface TravelTagMapper {

	 // 특정 여행 게시글에 연결된 태그 이름 리스트 조회
    List<String> findTagsByTripArticleId(@Param("tripArticleId") Long tripArticleId);
    
    void insertTripTag(@Param("tripArticleId") Long tripArticleId,
            @Param("tagId") Long tagId);
    
    @Select("SELECT id FROM trip_tag WHERE tag_name = #{tagName}")
    Long findTagIdByName(@Param("tagName") String tagName);
    
 // ⭐ ⭐ ⭐ 변경: 파라미터를 TripTagDto로 변경 ⭐ ⭐ ⭐
    void insertTag(TripTagDto tagDto);
=======
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
>>>>>>> spring/travel_article_crud_mgk
}
