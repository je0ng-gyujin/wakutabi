package com.wakutabi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wakutabi.domain.ChatMsgDto;
import com.wakutabi.domain.ChatRoomDto;

@Mapper
public interface ChatMapper {
	
	List<ChatRoomDto> findChatRoomsByUserId(@Param("userId") Long userId);
	List<ChatMsgDto> findChatMsgByRoomId(@Param("roomId") Long roomId);
	
	void insertChatMsg(ChatMsgDto chatMsgDto);
}
