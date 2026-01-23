package edu.kh.project.common.aop;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

public class PointcutBundle {

	// Pointcut 을 모아두는 클래스로서, 매번 복잡한 패턴의 Pointcut을 미리 작성해두고
	// 필요한 곳에서 클래스명.메서드명()으로 호출해서 사용
	 // @Before("execution(* edu.kh.project..*Controller*.*(..))")
	
	@Pointcut("execution(* edu.kh.project..*Controller*.*(..))")
	public void controllerPointcut() {}
	// 메서드 내용은 작성 불필요
	// 위 패턴은 프로젝트 내 전역의 Controller를 가리키는 Pointcut이고, 이를 사용하고 싶다면
	// @Before("PointcutBundle.controllerPointcut()") 같은 방법으로 사용 가능	

	@Pointcut("execution(* edu.kh.project..*ServiceImpl*.*(..))")
	public void serviceImplPointcut() {}
	
}
