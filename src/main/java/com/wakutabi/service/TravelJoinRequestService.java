package com.wakutabi.service;
import org.springframework.stereotype.Service;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class TravelJoinRequestService {
	
	private final TravelJoinRequestMapper travelJoinRequestMapper;

	public void insertTravelJoinRequest(TravelJoinRequestDto TravelJoinRequest){
		travelJoinRequestMapper.insertTravelJoinRequest(TravelJoinRequest);
	}
	public void changeStatusToAccepted(Long userId){
		travelJoinRequestMapper.changeStatusToAccepted(userId);
	}
	public void changeStatusToRejected(Long userId){
		travelJoinRequestMapper.changeStatusToRejected(userId);
	}
}
