package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TravelEditDto;

@Mapper
public interface TravelUpdateDeleteMapper {

	int updateTravelArticle(TravelEditDto dto);
	int deleteTravelArticle(@Param("id") Long id,@Param("hostUserId") Long hostUserId);
}
