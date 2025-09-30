package com.wakutabi.service;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TravelJoinFacadeService {

	private final TravelDeadlineService travelDeadlineService;
	private final TravelJoinRequestService travelJoinRequestService;
	private final NotificationService notificationService;
	private final TravelJoinRequestMapper travelJoinRequestMapper;
	@Transactional
	public String joinTravel(TravelJoinRequestDto travelJoinRequest,
							  Long chatRoomId,
							  Long userId){
		if(userId == null){
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		if(Objects.equals(userId, travelJoinRequest.getHostUserId())){
			throw new IllegalStateException("호스트는 참가신청 할 수 없습니다.");
		}
		// 중복신청 체크(PENDING/ACTIVE)
		Map<String,Object> check = travelJoinRequestMapper.existJoinRequest(
				Map.of("travelArticleId", travelJoinRequest.getTripArticleId(),
						"applicantUserId", userId));
		int hasPending =((Number) check.get("has_pending")).intValue();
		int hasActive = ((Number) check.get("has_active")).intValue();
		if(hasPending > 0){
			throw new IllegalStateException("이미 참가 신청 대기 중입니다.");
		}
		if(hasActive > 0){
			throw new IllegalStateException("이미 참가중인 여행입니다.");
		}
		// 참가자 insert
		travelJoinRequest.setApplicantUserId(userId);
		travelJoinRequestService.insertTravelJoinRequest(travelJoinRequest);

		// 알림 발송
		notificationService.sendJoinRequest(
				travelJoinRequest.getTripArticleId(),
				travelJoinRequest.getHostUserId(),
				userId
		);
		//마감 체크
		Map<String,Object> params = new HashMap<>();
		params.put("travelArticleId", travelJoinRequest.getTripArticleId());
		params.put("chatRoomId", chatRoomId);

		boolean closed = travelDeadlineService.travelDeadline(params);
		return closed ? "인원이 다 찼습니다." : "참가 신청이 완료되었습니다.";
	}
}
