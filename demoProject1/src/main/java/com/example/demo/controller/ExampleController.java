package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
// 요청 및 응답 제어 역할 명시 + Bean 등록(객체화하여 Spring container 안에 넣어두었다)
public class ExampleController {
	
	// 1) @RequestMapping("/example")
	
	// 2) @GetMapping("주소") : get 방식 요청 매핑 -> 조회
	//    @PostMapping("주소") : post 방식 요청 매핑 -> 삽입
	//    @PutMapping("주소") : put 방식 요청 매핑(form, a 태그 요청은 불가) -> 수정
	//    @DeleteMapping("주소") : Delete 방식 요청 매핑(form, a 태그 요청은 불가) -> 삭제
	//	: CRUD 와 관련 있다
	
	
	@GetMapping("example")
	// "/" 제거해도 무방 (서버단의 WebServlet 에서는 무조건 붙여야한다.)
	
	// Spring Boot에서는 요청 주소 앞에 '/' 없어도 요청이 에러 없이 수행 가능
	// 또한, 추후 프로젝트 배포 시 AWS 와 같은 호스팅 서비스 이용 시 Linux 서버에서 구동하게 되며, 그때 '/'가 있으면 오류 발생
	
	public String exampleMethod() {
		
		// forward 하려는 html 파일 경로를 return 에 작성
		// 단, ViewResolver 가 제공하는 타임리프의 접두사, 접미사는 제외하고 작성
		// 접두사 : classpath: /templates/
		// 접미사 : .html
		
		return "example";
		// src/main/resources/templates/example.html 로 forward
	}
	
	
}
