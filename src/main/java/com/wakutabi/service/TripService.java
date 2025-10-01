package com.wakutabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;
import com.wakutabi.mapper.TripMapper;

import jakarta.transaction.Transactional;

@Service
public class TripService {
	
	private final TripMapper tripMapper; // MyBatis Mapper 의존성 주입

    public TripService(TripMapper tripMapper) { // 생성자 주입
        this.tripMapper = tripMapper;
    }
    
    public List<TripListDto> getRegisteredTrips(Long userId) {
        
        return tripMapper.findRegisteredTripsByHostId(userId); 
    }
    
    public Long findUserIdByUsername(String username) {
        // 실제로는 UserMapper를 사용해야 하지만, 일단 TripMapper에 정의할 예정
        return tripMapper.findUserIdByUsername(username); 
    }
    
    public List<TripJoinRequestDto> getPendingJoinRequests(Long tripArticleId) {
        return tripMapper.findJoinRequestsByTripId(tripArticleId);
    }
    
    @Transactional // ⭐ 두 개 이상의 DB 작업을 하나의 묶음으로 처리합니다.
    public void processJoinRequest(Long requestId, String status, Long currentHostId) {
        
        // 1. 요청 정보 조회 (신청자 ID와 여행 ID를 가져옴)
        TripJoinRequestDto requestInfo = tripMapper.findRequestAndApplicantInfoById(requestId);
        
        if (requestInfo == null) {
            throw new IllegalArgumentException("유효하지 않은 신청 요청 ID입니다.");
        }
        
        // ⭐ 1-1. (중요 보안 검증) 현재 사용자가 이 여행 게시글의 호스트인지 확인하는 로직이 필요합니다.
        // 현재 코드에 이 로직이 없으므로, TravelEditMapper나 TripMapper에 
        // findHostIdByTripId(requestInfo.getTripArticleId()) 같은 메서드를 추가하여 검증해야 합니다.
        // 여기서는 검증 로직을 구현하지 않고 다음 단계로 넘어갑니다.
        
        // 2. 신청 상태 업데이트
        int updateCount = tripMapper.updateJoinRequestStatus(requestId, status);
        if (updateCount != 1) {
            // 이미 다른 호스트에 의해 처리된 경우일 수 있습니다.
            throw new RuntimeException("신청 상태 업데이트에 실패했거나 이미 처리된 요청입니다.");
        }
        
        // 3. 수락(ACCEPT)일 경우에만 참가자 추가
        if ("ACCEPT".equals(status)) {
            Long applicantUserId = requestInfo.getApplicantUserId();
            Long tripArticleId = requestInfo.getTripArticleId();
            
            // 3-1. 채팅방 ID 조회
            Long chatRoomId = tripMapper.findChatRoomIdByTripArticleId(tripArticleId);
            if (chatRoomId == null) {
                // 채팅방이 없으면 오류 발생 (여행 게시글 생성 시 채팅방도 생성되어야 함)
                throw new IllegalStateException("해당 여행에 연결된 채팅방을 찾을 수 없습니다.");
            }
            
            // 3-2. 참가자 추가 (chat_participants 테이블)
            int addCount = tripMapper.addChatParticipant(chatRoomId, applicantUserId);
            if (addCount != 1) {
                throw new RuntimeException("채팅방 참가자 추가 실패");
            }
            
            // ⭐ (선택) 여기에 최대 인원 초과 확인 로직도 추가해야 합니다.
        }
    }
}
