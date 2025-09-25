package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatMsgDto {
	private Long id;
	private String type;
	private Long roomId;
	private Long userId;
	private String userImagePath; // users.image_path (발신자 프로필 이미지)
	private String nickname;
	private String message;
	private String messageImagePath; // chat_message.image_path (채팅 메시지 이미지)
	private LocalDateTime createdAt;
}
