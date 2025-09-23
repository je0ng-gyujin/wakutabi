package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMsgDto {
	private Long id;
	private Long userId;
	private Long chatRoomId;
	private String message;
	private String image_path;
	private LocalDateTime created_at;
}
