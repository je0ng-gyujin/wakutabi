package com.wakutabi.handler;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ChatMsgDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler{
	
	private static final Set<WebSocketSession> sessions = new HashSet<>();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	// 클라이언트가 서버에 접속을 성공했을 때 호출
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
		System.out.println("새로운 클라이언트 접속: " + session.getId());
	}
	
	// 클라이언트가 메시지를 보낼 때마다 호출
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		String payload = message.getPayload();
		System.out.println("수신 JSON 메시지: " + payload);
		
		try {
			ChatMsgDto chatMsgDto = objectMapper.readValue(payload, ChatMsgDto.class);
			String jsonMessage = objectMapper.writeValueAsString(chatMsgDto);
			
			for (WebSocketSession s : sessions) {
				try {
					// 특정 세션으로 메시지 전송
					s.sendMessage(new TextMessage(jsonMessage));
				} catch (Exception e) {
					// 특정 세션 전송 실패는 무시하고 계속 진행
					System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("메시지 처리 중 오류 발생: " + e.getMessage());
			// 예외 발생 시 연결 종료
			session.close();
		}
		
	}
}
