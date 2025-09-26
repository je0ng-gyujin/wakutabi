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
	public boolean changeStatusToAccepted(Long id, Long hostUserId){
		int updated = travelJoinRequestMapper.changeStatusToAccepted(id, hostUserId);
		return updated > 0;
	}
	// 참가거절
	public boolean changeStatusToRejected(Long id, Long hostUserId){
		int updated = travelJoinRequestMapper.changeStatusToRejected(id, hostUserId);
		return updated > 0;
	}
}
