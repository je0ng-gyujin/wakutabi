package com.wakutabi.mapper;

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
}
