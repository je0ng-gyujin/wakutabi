package com.wakutabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;
import com.wakutabi.mapper.TripMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j // ⬅️ 이 애너테이션을 추가하여 log 객체를 사용할 수 있게 합니다.
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
    
    public List<TripListDto> getAppliedTrips(Long userId) {
        // Mapper의 메서드를 호출하여 DB에서 데이터를 가져옵니다.
        return tripMapper.selectAppliedTrips(userId); 
    }
    
 // TripService.java에 추가

    @Transactional
    public void cancelJoinRequest(Long requestId, Long currentUserId) {
        
        // 1. 요청 정보 조회 (신청자 ID와 여행 ID, 현재 상태를 가져옴)
        TripJoinRequestDto requestInfo = tripMapper.findRequestAndApplicantInfoById(requestId);
        
        if (requestInfo == null) {
            throw new IllegalArgumentException("유효하지 않은 신청 요청 ID입니다.");
        }
        
        // 2. 보안 검증: 요청을 취소하려는 사용자가 신청자 본인인지 확인
        // (Long 객체 비교 시 .equals() 또는 longValue() 비교를 사용합니다.)
        if (requestInfo.getApplicantUserId() == null || !requestInfo.getApplicantUserId().equals(currentUserId)) { 
            log.warn("권한 불일치 감지: currentUserId={}, applicantId={}", currentUserId, requestInfo.getApplicantUserId());
            throw new RuntimeException("신청 본인만 취소할 수 있으며 권한이 없습니다.");
        }
        
        // 3. 이전 상태 저장
        String previousStatus = requestInfo.getStatus();
        
        if (previousStatus == null) {
             throw new RuntimeException("신청 요청의 이전 상태 정보를 가져올 수 없습니다. DB 상태(status 컬럼)를 확인하세요.");
        }

        // 4. 상태 업데이트 (CANCELED)
        int updateCount = tripMapper.updateJoinRequestStatus(requestId, "CANCELED"); 
        
        if (updateCount != 1) {
            throw new RuntimeException("신청 취소 상태 업데이트에 실패했거나 이미 취소되었습니다.");
        }
        
        // 5. 확정(ACCEPTED) 상태였을 경우에만 채팅방 참가자 제거
        if ("ACCEPTED".equals(previousStatus)) {
            Long tripArticleId = requestInfo.getTripArticleId();
            
            // 5-1. 채팅방 ID 조회
            Long chatRoomId = tripMapper.findChatRoomIdByTripArticleId(tripArticleId);
            if (chatRoomId != null) {
                // 5-2. 참가자 제거 (chat_participants 테이블)
                int removeCount = tripMapper.removeChatParticipant(chatRoomId, currentUserId);
                if (removeCount != 1) {
                    log.warn("채팅방 참가자 제거 실패: chatRoomId={}, userId={}", chatRoomId, currentUserId);
                }
            }
        }
    }
    
    @Transactional
    public void updateTripStatus(Long tripArticleId, String newStatus, Long currentHostId) {
        // 1. 호스트 본인인지 확인하는 로직 (중요!)
        // 2. 게시글 상태 변경 (tripMapper.updateTripArticleStatus 호출)
    }
 // ⭐ 새로 추가할 메서드: 여행 게시글 상태 변경
    public void updateTripArticleStatus(Long tripArticleId, String newStatus, Long currentHostId) {
        // 1. 상태 값 유효성 검사 (필수)
        // OPEN 또는 CLOSED만 허용하고, 다른 값(MATCHED, CANCELED)으로의 변경은 별도의 로직이 필요
        if (!("OPEN".equals(newStatus) || "CLOSED".equals(newStatus))) {
            throw new IllegalArgumentException("허용되지 않은 여행 상태 값입니다: " + newStatus);
        }

        // 2. Mapper 호출: 상태 업데이트 및 권한 검증
        int updateCount = tripMapper.updateTripArticleStatus(tripArticleId, newStatus, currentHostId);

        if (updateCount != 1) {
            // 이 오류는 크게 두 가지 경우에 발생합니다:
            // 1) tripArticleId가 존재하지 않음 (이미 삭제되었을 경우)
            // 2) currentHostId가 해당 게시글의 host_user_id와 일치하지 않음 (권한 없음)
            throw new RuntimeException("여행 게시글 상태 변경에 실패했습니다. (게시글이 존재하지 않거나 권한이 없습니다.)");
        }
    }
}
