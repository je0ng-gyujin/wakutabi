package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.wakutabi.domain.TravelEditDto;
@Mapper
public interface TravelEditMapper {

	void insertTravelEdit(TravelEditDto traveledit);
}
