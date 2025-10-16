package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;

@Mapper
public interface TripMapper {

	List<TripListDto> findRegisteredTripsByHostId(@Param("hostId") Long userId);
	
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
    
    // 5. 채팅방 참가자 제거
    int removeChatParticipant(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
    
    // 사용자의 신청 상태 및 여행 상세 정보를 포함하는 리스트를 조회합니다.
    List<TripListDto> selectAppliedTrips(@Param("userId") Long userId); 
    
    // 호스트 ID로 여행 게시글 작성자 ID를 조회 (권한 검증용)
    Long findHostIdByTripArticleId(@Param("tripArticleId") Long tripArticleId); // <-- 추가
    
    //여행 게시글 상태 업데이트 (OPEN <-> CLOSED)
    int updateTripArticleStatus(@Param("tripArticleId") Long tripArticleId, @Param("status") String status,@Param("hostUserId") Long hostUserId	);
}

