package com.wakutabi.service;
import org.springframework.stereotype.Service;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelJoinRequestService {
	
	private final TravelJoinRequestMapper travelJoinRequestMapper;
	private final ChatService chatService; // Inject ChatService
	private final TravelDeadlineService travelDeadlineService;
	private final ChatParticipantsService chatParticipantsService;

	// 여행참가신청
	public void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest){
		travelJoinRequestMapper.insertTravelJoinRequest(TravelJoinRequest);
	}
	// 참가수락
	@Transactional
	public void changeStatusToAccepted(TravelJoinRequestDto statusToAccepted){
		statusToAccepted.setStatus(TravelJoinRequestDto.Status.ACCEPTED);
		travelJoinRequestMapper.changeStatusToAccepted(statusToAccepted);
	}
	// 참가거절
	public void changeStatusToRejected(TravelJoinRequestDto statusToRejected){
		travelJoinRequestMapper.changeStatusToRejected(statusToRejected);
	}

	@Transactional
	public void cancelJoinRequest(Long tripId, Long userId) {
	    // 1. 기존 참가 신청 정보 조회
	    TravelJoinRequestDto existingRequest = travelJoinRequestMapper.findJoinRequestByTripAndApplicant(tripId, userId);

	    if (existingRequest == null) {
	        throw new IllegalStateException("취소할 참가 신청을 찾을 수 없습니다.");
	    }
	    
	    boolean wasAccepted = existingRequest.getStatus() == TravelJoinRequestDto.Status.ACCEPTED;

	    // 2. 참가 신청 상태 CANCELED로 변경
	    travelJoinRequestMapper.cancelJoinRequest(existingRequest);

	    // 3. 만약 ACCEPTED 상태였다면, chat_participants에서도 상태 변경
	    if (wasAccepted) {
	        Long chatRoomId = chatService.chatRoomFindByTripArticleId(tripId);
	        if (chatRoomId != null) {
	            log.info("Cancelling join request: chatRoomId={}, userId={}", chatRoomId, userId);
	            chatParticipantsService.updateParticipantStatus(chatRoomId, userId, "LEFT"); // Or "CANCELED"
	        }
	    }
	    
	    // 4. 참가자 수 변동에 따라 여행 상태 업데이트 (OPEN 또는 MATCHED)
	    if(wasAccepted){
			travelDeadlineService.updateTravelStatus(tripId);
		}
	}

}
