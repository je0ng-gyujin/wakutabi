package com.wakutabi.service;

import com.wakutabi.mapper.ChatParticipantsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatParticipantsService {

	private final ChatParticipantsMapper chatParticipantsMapper;
	// 참가신청 수락된 사람 채팅참가자로 등록
	public void addUserToChatParticipants(Long chatRoomId, Long applicantUserId){
		chatParticipantsMapper.addUserToChatParticipants(chatRoomId, applicantUserId);
	}
}
