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
	private final ChatParticipantsService chatParticipantsService;
	// 여행 등록시 채팅방 만들기, 채팅방에 호스트 넣기
	public void setChatRoom(Long tripArticleId, Long hostId){
		// 여행 등록시 채팅방 만들기
		ChatRoomDto chatRoom = new ChatRoomDto();
		chatRoom.setTripArticleId(tripArticleId);
		chatMapper.setChatRoom(chatRoom);
		// 채팅방에 호스트 넣기
		Long chatRoomId = chatRoom.getId();
		chatParticipantsService.addUserToChatHost(chatRoomId, hostId);
	};
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
	// tripArticleId로 chatParticipantsId구하기
	public Long chatRoomFindByTripArticleId(Long tripArticleId) {
		return chatMapper.chatRoomFindByTripArticleId(tripArticleId);
	}
}
