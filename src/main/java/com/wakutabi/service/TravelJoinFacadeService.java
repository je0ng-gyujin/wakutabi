package com.wakutabi.service;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelJoinFacadeService {

	private final TravelDeadlineService travelDeadlineService;
	private final TravelJoinRequestService travelJoinRequestService;
	private final NotificationService notificationService;

	@Transactional
	public boolean joinTravel(TravelJoinRequestDto travelJoinRequest,
							  Long chatRoomId,
							  Long userId){
		travelJoinRequest.setApplicantUserId(userId);
		travelJoinRequestService.insertTravelJoinRequest(travelJoinRequest);

		notificationService.sendJoinRequest(
				travelJoinRequest.getTripArticleId(),
				travelJoinRequest.getHostUserId(),
				userId
		);
		Map<String,Object> params = new HashMap<>();
		params.put("travelArticleId", travelJoinRequest.getTripArticleId());
		params.put("chatRoomId", chatRoomId);
		travelDeadlineService.travelDeadline(params);
		return travelDeadlineService.travelDeadline(params);
	}
}
