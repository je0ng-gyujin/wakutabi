package com.wakutabi.service;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationMapper notificationMapper;
	
	public void insertNotification(NotificationDto noticeDto) {
		notificationMapper.insertNotification(noticeDto);
	}
	
	public NotificationDto findNotificationsById (Long userId) {
		return notificationMapper.findNotificationsById(userId);
	}
}
