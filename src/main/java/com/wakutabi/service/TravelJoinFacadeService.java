package com.wakutabi.service;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.mapper.TravelJoinRequestMapper;
import com.wakutabi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
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
	private final TravelEditService travelEditService;
	private final UserMapper userMapper;

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
		Map<String,Object> check = travelJoinRequestMapper.existJoinRequest(travelJoinRequest.getTripArticleId(), userId);
		int hasPending =((Number) check.get("has_pending")).intValue();
		int hasActive = ((Number) check.get("has_active")).intValue();
		if(hasPending > 0){
			throw new IllegalStateException("이미 참가 신청 대기 중입니다.");
		}
		if(hasActive > 0){
			throw new IllegalStateException("이미 참가중인 여행입니다.");
		}

		// 제한 사항 확인
		TravelEditDto travel = travelEditService.findTravelById(travelJoinRequest.getTripArticleId());
		String username = userMapper.getUsernameById(userId);
		UserUpdateDto user = userMapper.getUserInfo(username);

		// 성별 제한 확인
		String genderLimit = travel.getGenderLimit();
		if (!"N".equals(genderLimit)) {
			String fullGenderLimit = getFullGenderName(genderLimit);
			if (!fullGenderLimit.equalsIgnoreCase(user.getGender().name())) {
				throw new IllegalStateException("성별 제한에 맞지 않습니다.");
			}
		}

		// 나이 제한 확인
		String ageLimit = travel.getAgeLimit();
		if (!"NO".equals(ageLimit)) {
			String[] ageRanges = ageLimit.split(",");
			LocalDate birthDate = user.getBirth();
			int age = Period.between(birthDate, LocalDate.now()).getYears();
			boolean ageMatch = Arrays.stream(ageRanges).anyMatch(range -> {
				int startAge = Integer.parseInt(range);
				int endAge = startAge + 9;
				return age >= startAge && age <= endAge;
			});
			if (!ageMatch) {
				throw new IllegalStateException("나이 제한에 맞지 않습니다.");
			}
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

	private String getFullGenderName(String shortGender) {
		switch (shortGender.toUpperCase()) {
			case "M": return "MALE";
			case "F": return "FEMALE";
			case "O": return "OTHER";
			case "N": return "NONE";
			default: return shortGender; // 알 수 없는 약어인 경우 원래 값을 반환
		}
	}
}
