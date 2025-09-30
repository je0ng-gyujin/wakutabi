package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelEditDto;
@Mapper
public interface TravelEditMapper {

	// 여행 일정 등록
	void insertTravelEdit(TravelEditDto traveledit);

	TravelEditDto selectTravelDetail(Long id);
}
