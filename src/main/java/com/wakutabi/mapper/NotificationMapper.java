package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.NotificationDto;

@Mapper
public interface NotificationMapper {
	List<NotificationDto> findNotificationsById(@Param("userId") Long userId);
	
	void insertNotification(NotificationDto noticeDto);
	
	NotificationDto findNotificationById(Long notificationId);
	
	void updateReadStatus(Long notificationId);
	
	Integer countNotificationsByUserId(@Param("userId") Long userId);
}
