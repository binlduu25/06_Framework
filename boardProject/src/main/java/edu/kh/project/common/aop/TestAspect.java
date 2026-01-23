package edu.kh.project.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
AOP : Aspect-Oriented-Programming
- 분산된 관심사/관점을 모듈화시키는 기법
- 주요 비즈니스 로직과 관련이 없는 부가적인 기능을 추가할 때 유용
 ex) 코드 중간중간 로그 찍을 때, 트랜잭션 처리, 보안 처리 추가 등..
 
 주요 어노테이션
 - @Aspect : Aspect 정의하는 데 사용되는 어노테이션. 클래스 상단에 작성
 - @Before(포인트컷) : 대상 메서드(포인트컷) 실행 전에 Advice 실행
 - @After(포인트컷) : 대상 메서드 실행 후 Advice 실행
 - @Around(포인트컷) : 대상 메서드 실행 전/후 Advice 실행
*/

@Component // Bean으로 등록
@Slf4j // Logger 생성 코드
// @Aspect // 공통 관심사 작성된 클래스임을 명시(AOP 동작용 클래스), 테스트 종료 후 막아둠
public class TestAspect {
	
	// Advice : 끼워넣을 코드(메서드)
	// PointCut : 실제로 Advice가 적용될 JointPoint 지정
	 // > 클래스명은 패키지명부터 모두 작성
	 
	// pointcut 패턴 : execution(* edu.kh.project..*Controller*.*(..))
	 // * : 모든 리턴 타입을 나타냄
	 // edu.kh.project : 패키지명
	 // .. : 0개 이상의 하위 패키지 
	 // *Controller* : 이름에 Controller라는 문자열이 포함된 모든 클래스 대상
	 // .* : 모든 메서드
	 // (..) : 0개 이상의 파라미터(매개변수)를 나타냄
	
	@Before("execution(* edu.kh.project..*Controller*.*(..))")
	public void testAdvice() {
		log.info("------------------- testAdvice() 수행됨 -----------------------");
	}
	
	@After("execution(* edu.kh.project..*Controller*.*(..))")
	public void controllerEnd(JoinPoint jp) { // JoinPoint : AOP 기능이 적용된 대상
		
		// AOP가 적용된 클래스 이름 얻어오기
		 // ex) MainController, MemberController..
		String className = jp.getTarget().getClass().getSimpleName();
		
		// 실행된 컨트롤러의 메서드명 얻어오기
		String methodName = jp.getSignature().getName();
		
		log.info("---------------------{}.{} 수행완료--------------------",className, methodName);
		
	}
	
	
}
