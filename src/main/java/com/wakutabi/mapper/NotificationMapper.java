package com.wakutabi.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.NotificationDto;

@Mapper
public interface NotificationMapper {
	NotificationDto findNotificationsById(@Param("userId") Long userId);
	
	void insertNotification(NotificationDto noticeDto);
}
