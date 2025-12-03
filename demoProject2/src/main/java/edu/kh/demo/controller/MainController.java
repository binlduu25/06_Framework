package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 요청 및 응답 제어 역할 명시 + Bean 등록
public class MainController {

	// '/' 주소로 요청 시 main.html 파일로 forward
	
	// forward : 요청 위임
	// thymleleaf : spring boot 에서 사용하는 템플릿엔진(html 사용)
	
	@RequestMapping("/")
	public String mainPage() {
		
		// classpath:/templates/common/main.html
		return "common/main";
	}
	
}
 