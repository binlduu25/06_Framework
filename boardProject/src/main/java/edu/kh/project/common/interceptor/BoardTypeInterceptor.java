package edu.kh.project.common.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.kh.project.board.model.service.BoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/* 인터셉터(Interceptor)란?
 * 
 * 요청 / 응답 / 뷰 완성 후 가로채는 객체 (Spring 에서 지원)
 * - 인터셉터 역할 수행하게끔 하기 위해 인터페이스 상속 필요
 *  > HandlerInterceptor 상속받기
 *   > preHandle(전처리) : DispatcherServlet -> Controller 사이에서 수행
 *   > postHandle(후처리) : Controller -> DispatcherServlet 응답 보낼 때 수행
 *   > afterCompletion(뷰 완성 후) : ViewResolver -> DispatcherServlet 사이에서 응답 되돌려줄 때 수행
 * 
 * 
 * */

// 참고
// HandlerInterceptor 들어가서 메서드를 살펴보면 default 라는 접근제한자가 붙어 있다.
// 해당 메서드를 보면 중괄호가 붙어 있고 추상메서드 형태가 아닌 것처럼 보이지만, js 버전이 올라가며 새로 생긴 방식임
// default 접근제한자를 붙이면 중괄호 붙여 사용 가능

@Slf4j
public class BoardTypeInterceptor implements HandlerInterceptor{
	
	@Autowired
	private BoardService service;
	
	// 1. 전처리
	 // : 요청이 Controller로 들어오기 전 실행되는 메서드
	 // 이때 DB에 접근하여 원하는 요청 처리 가능
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 해당 프로젝트에서는 BoardType 을 가져오기로 한다
		 // BoardTypeList 로 가져올 것
		 // applicationScope 에 세팅할 것. 
		  // 즉, 서버 구동 시 바로 실행되도록 할 것
		  // 서버 내에 딱 1개만 존재하고 모든 클라이언트가 공용으로 사용되어야 하므로
		
		// application scope 객체 얻어오기
		 // 본인보다 범위가 작은 객체에서 얻어올 수 있다
		 // session 또는 request
		 // 매개변수로 들어온 request 활용
		
		ServletContext application = request.getServletContext(); // ServletContext : 어플리케이션 scope 이름
		
		// 이제 application scope 에 boardType 객체를 담아야 하고, 해당 작업 수행 위해 db 접근 필요
		// preHandle 이기 때문에 controller 에 접근 전이며, DB 접근 요청을 위해 controller 가 아닌 바로 service 단으로 호출
		
		// 또한 application scope 는 서버 내 1개 뿐이고 이를 매번 서버를 접속할 때마다 불러올 DB에 접근할 필요는 없음
		// appilication scope 에 "boardTypeList" 가 없을 때만 요청을 처리한다.
		if(application.getAttribute("boardTypeList") == null){
			
			// BoardTypeList 조회 서비스 호출
			List<Map<String, Object>> boardTypeList = service.selectBoardTypeList();
			
			log.debug("boardTypeList :" + boardTypeList);
			
			// 조회 결과 application scope에 추가
			application.setAttribute("boardTypeList", boardTypeList);
		}
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	// 2. 후처리
	 // 요청 처리 후, 뷰가 렌더링 되기 전 실행되는 메서드
	 // 응답을 가지고 DispatcherServlet에게 돌아가기 전임
	 // 일단 지금은 수행해줄 작업이 없기 때문에 오버라이딩한 그대로 놔둔다.
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	// 3. 뷰 완성 후
	 // 뷰 렌더링이 끝난 후 실행되는 메서드
	 // 일단 지금은 수행해줄 작업이 없기 때문에 오버라이딩한 그대로 놔둔다.
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
