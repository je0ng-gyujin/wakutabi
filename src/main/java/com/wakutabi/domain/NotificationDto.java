package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
	private Long id;
	private Long userId;
	private Long tripArticleId;
	private String type;
	private String title;
	private String link;
	private boolean isRead;
	private LocalDateTime createdAt;
	private LocalDateTime expiredAt;
}
