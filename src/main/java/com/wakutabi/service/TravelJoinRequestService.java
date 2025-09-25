package com.wakutabi.service;
import org.springframework.stereotype.Service;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TravelJoinRequestService {
	
	private final TravelJoinRequestMapper travelJoinRequestMapper;

	// 여행참가신청
	public void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest){
		travelJoinRequestMapper.insertTravelJoinRequest(TravelJoinRequest);
	}
	// 참가수락
	public void changeStatusToAccepted(Long travelArticleId, Long hostUserId, Long applicantUserId){
		travelJoinRequestMapper.changeStatusToAccepted(travelArticleId, hostUserId, applicantUserId);
	}
	// 참가거절
	public void changeStatusToRejected(Long travelArticleId, Long hostUserId, Long applicantUserId){
		travelJoinRequestMapper.changeStatusToRejected(travelArticleId, hostUserId, applicantUserId);
	}
}
