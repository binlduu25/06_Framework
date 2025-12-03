package com.example.demo.controller;

import com.example.demo.DemoProject1Application;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
// 요청/응답을 제어하는 역할 + Bean 등록 역할
// instance : 개발자가 직접 new 연산자를 통해 만든 객체
// bean : Spring Container 가 만들고 관리하는 객체

// IOC(제어의 반전) : 객체의 생성 및 새염ㅇ주기의 권한이 개발자가 아닌, 프레임워크에게 있다.

// @RequestMapping("/test")
public class TestController {

    private final DemoProject1Application demoProject1Application;

    TestController(DemoProject1Application demoProject1Application) {
        this.demoProject1Application = demoProject1Application;
    }

	// 기존의 Servlet : 클래스 단위로 하나의 요청만 처리 가능
	 // -> GET/POST 에 따라 한 클래스 당 최대 2개만 가능했음(doGet, doPost)
	
	// 하지만 Spring 에서는 메서드 단위로 요청을 처리할 수 있다.
	// @RequestMapping("요청주소") : 요청주소를 처리할 클래스 or 메서드를 매핑하는 어노테이션
	
		// 아래는 예시 
		
		/*
		
		@RequestMapping("/insert")
		public void methodA() {}
		-> /test/insert 요청을 methodA 가 처리
		
		@RequestMapping("/update")
		public void methodB() {}
		-> /test/update 요청을 methodB 가 처리
		
		*/
		
	// 작성법
	
	// 1) 클래스와 메서드에 함께 작성 : 공통 주소를 매핑(13번 라인 참고)
	 // ex) /test/insert, /test/update, /test/select ...
	
	// 2) 메서드에 작성 : 요청 주소와 해당 메서드를 매핑
	 // get/post 가리지 않고도 가능하며, 속성 지정 또는 다른 어노테이션을 이용해서 get/post 구분 역시 가능
	 
	
	// 메인화면(index) 에서 넘어온 "/test" 의 요청 받아줄 메소드 작성
	
	// @RequestMapping(value="/test", method=RequestMethod.GET) 
	// : test 요청 시 testMethod가 매핑하여 처리
	// -> get 또는 post 여부 상관 없음
	// -> get 방식로 들어오는 요청만 처리하고 싶다면? 속성 부여 가능
	
    @RequestMapping("/test")
	public String testMethod() {
    	// 반환 타입이 String 인 이유?
    	// Controller 의 반환형은 보통은 String
    	// -> 메소드에서 반환되는 문자열이 forward 할 html 파일의 경로가 되기 때문
    	
    	// ThymeLeaf : JSP 대신 사용하는 템플링 ㄴ엔진(HTML 형태
    	
    	// classpath == src/main/resources
    	// 접두사 : classpath:/templates/
    	// 접미사 : .html
    	//-> src/main/resources/templates/test.html
    	
    	
		System.out.println("/test 요청 받음");
		return "test";
	}
	
	
}
