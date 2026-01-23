package edu.kh.project.common.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.kh.project.member.dto.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Component
@Aspect
@Slf4j
public class LoggingAspect {
	
	/** 컨트롤러 수행 전 로그 출력 (클래스/메서드/ip)
	 * 
	 */
	
	@Before("PointcutBundle.controllerPointcut()")
	public void beforeController(JoinPoint jp) {
		
		String className = jp.getTarget().getClass().getSimpleName();
		String methodName = jp.getSignature().getName();
		
		// 요청한 클라이언트 ip 로그 출력
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
		// Controller 에서 얻어온 게 아니라 우회해서 얻어오는 방식이라 조금 복잡하다.
		// > 스프링에서 HTTP 요청을 처리하는 스레드에 접근하는 방식임
		
		String ip = getRemoteAddr(req);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("[%s.%s] 요청 / ip : %s", className, methodName, ip));
		
		// 로그인 상태인 경우
		if(req.getSession().getAttribute("loginMember") != null){
			String memberEmail = ((Member)req.getSession().getAttribute("loginMember")).getMemberEmail();
			// Session 에서 Attribute로 꺼내오면 Object 이기 때문에 DTO 객체로 형변환 필수
			
			// 문자열에 추가
			sb.append(String.format(", 요청 회원 : %s", memberEmail)); 
		}
		
		log.info(sb.toString());
		
	}
	
	// 1) @Around 사용 시 메서드의 반환형은 반드시 Object
	// 2) @Around 메서드 종료 시 proceed 반환값을 return 해야함
	
	// ProceedingJoinPoint
	 // - JoinPoint 의 자식 객체
	 // - @Around 가 붙은 메서드에서 사용 가능하며, proceed 메서드 제공
	 // proceed 메서드 호출 전/후로 Before/After 가 구분되어지는 역할
	
	/** 모든 서비스 수행 전/후로 동작하는 코드(클래스/메서드/파라미터/실행시간)
	 * @return
	 * @throws Throwable 
	 */
	@Around("PointcutBundle.serviceImplPointcut()")  
	public Object aroundServiceImpl(ProceedingJoinPoint pjp) throws Throwable {
		
		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();
		
		// 중괄호 안에 값 들어갈 수 있다
		log.info("======= {}.{} 서비스 호출 =======", className, methodName);
		
		// 파라미터를 로그로 출력
		log.info("Parameter : {}", Arrays.toString(pjp.getArgs())); // 매개변수(args)를 모두 가져오면 배열형태 > toString
		
		// 서비스 주요 비즈니스 로직 코드 실행 소요 시간 기록
		long startMs = System.currentTimeMillis(); // 시스템 상 현재 시간 ms 형태로 반환
		
		// ------------- 여기까지 Before ----------------------
		
		Object obj = pjp.proceed();
		 // : 이대로만 쓰고 return obj 까지 입력 시 예외처리 필요하다는 에러 발생
		 // : 하지만 메서드 선언부에 throws Exception 해도 예외처리 안 됨
		 // 왜? 
		 // Exception 의 상위 클래스인 Throwable 이기 때문
		 // Throwable 자식 : Exception, Error..
		
		// ------------- 여기부터 After -----------------------
		
		long endMs = System.currentTimeMillis();
		
		log.info("RunningTime : {}ms", endMs - startMs);
		log.info("=========== service 종료 =============");
		
		return obj;
	}
	
	
	/** 접속자 IP 얻어오는 메서드
	 * @param request
	 * @return
	 */
	private String getRemoteAddr(HttpServletRequest request) {
		
		// 클라이언트(사용자)의 실제 IP 주소를 찾아내기 위한 로직
		String ip = null;
		ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-RealIP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	

}
