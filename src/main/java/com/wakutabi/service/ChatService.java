package com.wakutabi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wakutabi.domain.ChatMsgDto;
import com.wakutabi.domain.ChatRoomDto;
import com.wakutabi.mapper.ChatMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	
	private final ChatMapper chatMapper;
	
	public List<ChatRoomDto> findChatRoomsByUserId(Long userId){
		return chatMapper.findChatRoomsByUserId(userId);
	}
	
	public List<ChatMsgDto> findChatMsgByRoomId(Long roomId) {
		return chatMapper.findChatMsgByRoomId(roomId);
	}
	
	public ChatMsgDto saveChatMsg(ChatMsgDto chatMsgDto) {
		if (chatMsgDto.getType().equals("TEXT")) {
			chatMapper.insertChatMsgWhenText(chatMsgDto);
			Long msgId = chatMsgDto.getId(); 
			return chatMapper.findChatMsgByMsgId(msgId);
		}
		return null;
	}
	
}
