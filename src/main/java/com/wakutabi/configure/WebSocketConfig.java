package com.wakutabi.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.wakutabi.handler.MyWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		"/ws/chat": 클라이언트가 웹소켓에 접속할 때 사용할 URL(예: ws://localhost:8080/ws/chat)
//		.setAllowedOrigins("*"): 모든 도메인에서의 접속을 허용. 개발 단계에서 편리하지만, 실제 서비스에서는 특정 도메인만 허용.
		registry.addHandler(new MyWebSocketHandler(), "/ws/chat").setAllowedOrigins("*");
	}

}
