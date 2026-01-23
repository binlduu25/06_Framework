package edu.kh.project.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/*
스프링 예외 처리 방법 (우선순위별)

 1. 메서드에서 직접 처리
 	- (try-catch, throws)
 
 2. controller 클래스에서 클래스 단위로 모아서 처리
 	- @ExceptionHandler 어노테이션을 지닌 각각의 메서드를 해당 클래스에서 처리
 
 3. 별도 클래스를 만들어 프로젝트 단위로 모아 처리
 	- @ControllerAdvice 어노테이션을 지닌 클래스를 작성
 
  > 3번 방식 사용해보기
 
  */

@ControllerAdvice // 전역적 예외처리 활성화 어노테이션
public class ExceptionController {

	// @ExceptionHandler(예외 종류) : 어떤 예외를 다룰 건지 작성
	// 예외 종류 
	// SQLException.class - SQL 관련 예외만 처리
	// IOException.class - 입출력 관련 예외만 처리
	// ... 
	
	// 404에러와 500에러 처리해보기
	
	
	/** 404 에러 처리
	 * @return
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public String notFound() {	
		return "error/404";
	}
	
	/** 프로젝트에서 발생하는 모든 종류 예외를 500 처리
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public String allExceptionHandler(Model model, Exception e) {
		
		e.printStackTrace();
		model.addAttribute("e", e);
		
		return "error/500";
	}
	
}
