package com.wakutabi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wakutabi.domain.ChatMsgDto;
import com.wakutabi.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatService chatService;
	
	@GetMapping("/messages/{roomId}")
	private ResponseEntity<List<ChatMsgDto>> getChatMessages(@PathVariable("roomId") Long roomId) {
		try {
			List<ChatMsgDto> messages = chatService.findChatMsgByRoomId(roomId);
			return ResponseEntity.ok(messages);
		} catch (Exception e) {
			// 오류 발생 시 500 Internal Server Error 반환
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
