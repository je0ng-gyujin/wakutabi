package com.wakutabi.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ChatMsgDto;
import com.wakutabi.service.ChatService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler{
	
	private static final Map<Long, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
	private static final Map<String, Long> sessionToRoomId = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;
	private final ChatService chatService;
	
	// 클라이언트가 서버에 접속을 성공했을 때 호출
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		Long roomId = getRoomIdFromSession(session);
		
		if (roomId != null) {
			// chatRooms 맵에 해당 roomId가 없으면 새로운 Set을 생성
			Set<WebSocketSession> sessionsToRoom = chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet());
			sessionsToRoom.add(session);
			
			// 세션 ID와 roomId를 매핑하여 저장
			sessionToRoomId.put(session.getId(), roomId);
			
			System.out.println("(웹소켓) 새로운 클라이언트 접속: " + session.getId() + " (Room ID: " + roomId + ")");
		} else {
			System.err.println("(웹소켓) Room ID가 없어 연결을 종료합니다.");
			try {
				session.close();
			} catch (Exception e) {
				
			}
		}
		
	}
	
	// 클라이언트가 메시지를 보낼 때마다 호출
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		String payload = message.getPayload();
		
		Long roomId = sessionToRoomId.get(session.getId());
		System.out.println("(웹소켓) 수신 JSON 메시지: " + payload + " // 방 번호 : " + roomId);
		
		try {
			if (roomId == null) {
				System.err.println("(웹소켓) 유효하지 않은 세션입니다. 메시지를 무시합니다.");
				return;
			}
			
			ChatMsgDto chatMsgDtoReceived = objectMapper.readValue(payload, ChatMsgDto.class);
			chatMsgDtoReceived.setRoomId(roomId);
			ChatMsgDto chatMsgDtoSending = chatService.saveChatMsg(chatMsgDtoReceived);
			
			// 서비스에서 반환된 DTO에 type 정보가 없으므로, 원본 DTO에서 다시 설정
			if (chatMsgDtoSending != null) {
				chatMsgDtoSending.setType(chatMsgDtoReceived.getType());
			} else {
				// 서비스에서 null이 반환된 경우 (예: TEXT 타입이 아닐 때)
				System.err.println("(웹소켓) 서비스가 DTO를 반환하지 않았습니다. 메시지 전송을 중단합니다.");
				return;
			}
			
			String jsonMessage = objectMapper.writeValueAsString(chatMsgDtoSending);
			
			Set<WebSocketSession> sessionsInRoom = chatRooms.get(roomId);
			if (sessionsInRoom != null) {
				for (WebSocketSession s : sessionsInRoom) {
					try {
						s.sendMessage(new TextMessage(jsonMessage));
					} catch (Exception e) {
						System.err.println("(웹소켓) 메시지 전송 중 오류 발생: " + e.getMessage());
						// 전송 실패 세션은 제거
						sessionsInRoom.remove(s);
					}
				}
			}
			
		} catch (Exception e) {
			System.err.println("(웹소켓) 메시지 처리 중 오류 발생: " + e.getMessage());
			// 예외 발생 시 연결 종료
		}
		
	}
	
	// 클라이언트 연결이 종료되었을 때 호출
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Long roomId = sessionToRoomId.remove(session.getId());
		
		if (roomId != null) {
			Set<WebSocketSession> sessionsInRoom = chatRooms.get(roomId);
			if (sessionsInRoom != null) {
				sessionsInRoom.remove(session);
				if (sessionsInRoom.isEmpty()) {
					chatRooms.remove(roomId);
				}
			}
		}
		System.out.println("(웹소켓) 클라이언트 연결 종료: " + session.getId());
	}
	
	// URL에서 roomId를 추출하는 헬퍼 메서드
	private Long getRoomIdFromSession(WebSocketSession session) {
		try {
			String query = session.getUri().getQuery();
			if (query != null && query.startsWith("roomId=")) {
				String roomIdStr = query.substring("roomId=".length());
				return Long.parseLong(roomIdStr);
			}
		} catch (Exception e) {
			System.err.println("(웹소켓) roomId 파싱 중 오류 발생: " + e.getMessage());
		}
		return null;
	}

}
