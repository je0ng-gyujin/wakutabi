package com.wakutabi.service;

import java.util.List;

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
}
