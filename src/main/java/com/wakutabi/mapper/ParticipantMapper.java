package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.ParticipantDto;
@Mapper
public interface ParticipantMapper {
	List<ParticipantDto> findParticipantsWithHost(@Param("id") Long tripArticleId);

}
