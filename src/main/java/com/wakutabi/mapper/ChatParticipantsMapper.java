package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatParticipantsMapper {
	
	// 참가신청 수락된 사람 채팅참가자로 insert
	void addUserToChatParticipants(@Param("chatRoomId")Long chatRoomId, @Param("applicantUserId") Long applicantUserId);
	// 여행만료때까지 여행참가하고 있는 참가자ID리스트
	List<Long> findParticipantsStayedUntilEnd(@Param("travelArticleId")Long travelArticleId);
}
