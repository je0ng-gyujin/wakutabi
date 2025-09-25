package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDto {
	private Long id;
	private Long userId;
	private String type;
	private String title;
	private String link;
	private boolean isRead;
	private LocalDateTime createdAt;
	private LocalDateTime expiredAt;
}
