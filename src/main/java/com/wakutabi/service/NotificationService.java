package com.wakutabi.service;

import java.time.LocalDateTime;
import java.util.List;

import com.wakutabi.controller.NotificationController;
import com.wakutabi.mapper.UserMapper;
import org.springframework.stereotype.Service;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationMapper notificationMapper;
	private final UserMapper userMapper;

	public void insertNotification(NotificationDto noticeDto) {
		notificationMapper.insertNotification(noticeDto);
	}
	
	public List<NotificationDto> findNotificationsById (Long userId) {
		return notificationMapper.findNotificationsById(userId);
	}
	
	public NotificationDto findNotificationById(Long notificationId) {
		return notificationMapper.findNotificationById(notificationId);
	}
	
	public void markAsRead(Long notificationId) {
		notificationMapper.updateReadStatus(notificationId);
	}
	
	public Integer countNotificationsByUserId(Long userId) {
		return notificationMapper.countNotificationsByUserId(userId);
	}

	// 여행 참가신청 호스트에게 시스템알림db로 보내기
	public void sendJoinRequest(Long travelArticleId, Long hostUserId, Long applicantUserId){
		NotificationDto noticeDto = new NotificationDto();
		noticeDto.setUserId(hostUserId);
		noticeDto.setTravelArticleId(travelArticleId);
		String sendJoinRequestUrl = "/schedule/detail?id=" + travelArticleId;
		noticeDto.setLink(sendJoinRequestUrl);
		String applicantUserName = userMapper.getUsernameById(applicantUserId);
		noticeDto.setTitle(applicantUserName);
		noticeDto.setType("TRAVEL_REQUEST");
		notificationMapper.insertNotification(noticeDto);
	}
	// 여행 참가신청 수락 거절 답변 참가자에게 시스템알림db로 보내기
	public void sendRequestAnswer(Long applicantUserId, Long travelArticleId, String title,String type){
		NotificationDto notificationDto = new NotificationDto();

		notificationDto.setUserId(applicantUserId);
		String url = "/schedule/detail?id="+travelArticleId;
		notificationDto.setLink(url);
		notificationDto.setType(type);
		notificationDto.setTitle(title);
		notificationMapper.insertNotification(notificationDto);
	}

}
