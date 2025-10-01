package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;

@Mapper
public interface TripMapper {

	List<TripListDto> findRegisteredTripsByHostId(@Param("hostId") Long hostId);
	
	List<TripJoinRequestDto> findJoinRequestsByTripId(@Param("tripArticleId") Long tripArticleId);
	
	Long findUserIdByUsername(@Param("username") String username);
	
	// 1. 요청 ID로 신청 정보 조회 (신청자 ID와 여행 ID를 가져옴)
    TripJoinRequestDto findRequestAndApplicantInfoById(@Param("requestId") Long requestId);
    
    // 2. 신청 상태 업데이트 (ACCEPT 또는 REJECT)
    int updateJoinRequestStatus(@Param("requestId") Long requestId, @Param("status") String status);
    
    // 3. 채팅방 ID 조회 (참가자 추가를 위해 필요)
    Long findChatRoomIdByTripArticleId(@Param("tripArticleId") Long tripArticleId);
    
    // 4. 채팅방 참가자 추가
    int addChatParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}

