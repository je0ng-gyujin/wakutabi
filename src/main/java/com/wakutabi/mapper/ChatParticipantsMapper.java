package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatParticipantsMapper {

	// 여행 등록시 채팅방에 호스트 INSERT
	void addUserToChatHost(@Param("chatRoomId")Long chatRoomId, @Param("hostId")Long hostId);
	// 참가신청 수락된 사람 채팅참가자로 insert
	void addUserToChatParticipants(@Param("chatRoomId")Long chatRoomId, @Param("applicantUserId") Long applicantUserId);
	// 여행만료때까지 여행참가하고 있는 참가자ID리스트
	List<Long> findParticipantsStayedUntilEnd(@Param("travelArticleId")Long travelArticleId);
	// 여행 취소되면참가자 상태 '취소'로 변경
	int updateStatusToCanceled(@Param("list") List<Long> canceledTripIds);
	// 여행 취소, 종료되면 참가자 상태 '완료'로 변경
	int updateStatusToCompleted(@Param("list") List<Long> endedTripIds);
}
