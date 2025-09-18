package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelImageDto;

@Mapper
public interface TravelImageMapper {
	
	void insertTravelImage(TravelImageDto travelImageDto);
}
