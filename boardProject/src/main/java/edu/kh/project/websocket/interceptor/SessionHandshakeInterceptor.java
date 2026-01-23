package edu.kh.project.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;

// SessionHandshakeInterceptor
// WebSocketHandler 가 동작하기 전/후 연결된 클라이언트의 세션을 가로채는 동작 설정할 클래스

// Handshake란 : 클라이언트와 서버가 WebSocket 연결을 수립하기 위해 HTTP 프로토콜을 통해 수행하는 초기 단계
// 				 즉, 기존 HTTP 연결을 WebSocket 연결로 변경한다.

@Component // Bean 으로 등록하여 Config에서 사용할 수 있게 함
public class SessionHandshakeInterceptor implements HandshakeInterceptor{ // 상속 필요
	
	/** 
	 * Handler 가 동작하기 전에 수행
	 * handshakeInterceptor 상속 시 필수 메서드로써, 반드시 작성 필요함
	 * 만약 특정한 기능을 수행할 필요가 없다 하더라도 상속은 받아야 함
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		// ServletHttpRequest : HttpServletRequest 의 부모 인터페이스
		// ServerHttpResponse : HttpServletResponse 의 부모 인터페이스
		
		// attributes : 해당 맵에 세팅된 속성(데이터)은 다음에 동작할 Handler 객체에 전달됨
		 // HandshakeInterceptor 에서 Handler 로 데이터 전달하는 역할
		
		// request가 참조하는 객체가 ServletServerHttpRequest로 다운캐스팅이 가능한가? -> 안전하게 다운캐스팅하기
		// instanceof : 두 객체가 부모 자식 관계인지, 같은 타입인지 따진다. -> T/F 로 반환
		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			
			// 웹소켓 동작을 요청한 클라이언트의 세션 얻어오기
			HttpSession session = servletRequest.getServletRequest().getSession();
			
			// 가로챈 session 을 Handler 에 전달할 수 있게 세팅
			attributes.put("session", session);
			
		}
		
		return true; // 가로채기 성공 여부 : true여야 session 을 가로채 Handler에게 전달 가능
	}
	
	/** 
	 * Handler 가 동작하기 후에 수행
	 * handshakeInterceptor 상속 시 필수 메서드로써, 반드시 작성 필요함
	 * 만약 특정한 기능을 수행할 필요가 없다 하더라도 상속은 받아야 함
	 */
	@Override
		public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
				Exception exception) {
			// TODO Auto-generated method stub
			
		}
	
}
