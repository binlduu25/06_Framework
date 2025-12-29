package edu.kh.project.common.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * Filter : 요청, 응답 시 걸러내거나 추가할 수 있는 객체
 * 
 * [필터 클래스 생성 방법]
 * 1. jakarta.servlet.Filter 인터페이스 상속 받기
 *  - doFilter() 메서드 오버라이딩
 *  
 *  
 * 
 * */

// 로그인이 되어 있지 않은 경우 특정 페이지 접근 불가하도록 피렅링
public class LoginFilter implements Filter{

	// 필터 동작을 정의하는 메서드
	@Override
	public void doFilter(ServletRequest request, 
						 ServletResponse response, 
						 FilterChain chain) throws IOException, ServletException {
	
		// ServletRequest : HttpServletRequest 부모타입
		// ServletResponse : HttpServletResponse 부모타입
		
		// FilterChain : 다음 필터 또는 DispatcherServlet 으로 체인
		
		// 먼저, 로그인 정보가 담긴 session 이 필요하다.
		// 로그인이 되어 있는지 여부에 따라 페이지 접근을 제한해야 하기 때문
		
		HttpServletRequest req = (HttpServletRequest)request; // HttpServletRequest 형태(자식형태)로 다운캐스팅하여 req 에 담기
		HttpServletResponse resp = (HttpServletResponse)response; // HttpServletResponse 형태(자식형태)로 다운캐스팅하여 resp 에 담기
		
		// 현재 들어온 요청의 URI 를 가져옴
		String path = req.getRequestURI(); // /myPage/profile
	
		// 요청 URI가 "/myPage/profile" 로 시작하는지 확인
		 // -> 프로필로 들어오는 경로는 통과시켜줄 예정
		 // -> 프로필 내 이미지를 등록할 수 있고, 해당 이미지는 위 경로를 통해 삽입되며, 
		 //    해당 경로가 차단될 경우 로그인하지 않은 다른 회원들이 해당 사진을 볼 수 없어 깨지기 때문
		
		if(path.startsWith("/myPage/profile/")) { // true라면 통과한다 (**** 경로 끝에 '/'를 붙였기 때문에 myPage/profile/ 이하로 들어오는 요청을 일컫는다
			
			chain.doFilter(request, response); // 필터를 통과한 후 return
			return;
			
		}
		
		// session 객체 얻어와 로그인된 정보 파악
		 // loginMember 가 있는지 혹은 NULL 인지 파악
		HttpSession session = req.getSession();
		
		if(session.getAttribute("loginMember") == null) { // 로그인되어 있지 않은 상태라면
			
			// /loginError 재요청 (즉, redirect) -> MainController 에서 처리
			resp.sendRedirect("/loginError");
			
		} else { // 로그인되어 있다면, 다음 필터로 넘어가거나 다음 필터 없다면 해당 요청을 DispatcherServlet으로 통과
			
			chain.doFilter(request, response);
			
		}
		
	}
	
}
