package com.wakutabi.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRoomDto {
	private Long id; // chat_room.id
	private Long tripArticleId; // r.trip_article_id
	private String role; // chat_participants.role
	private String title; // trip_article.title
	private String latestNickname;
	private String latestMessage; // chat_message.message
	private LocalDateTime latestMessageTime; // chat_message.created_at
}
