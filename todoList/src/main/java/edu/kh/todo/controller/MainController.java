package edu.kh.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
@Slf4j // log 객체 자동생성 - lombok 라이브러리

public class MainController {
	
	@RequestMapping("/")
	public String MainPage() {
		
		// 접두사 : src/main/resources/templates/
		// 접미사 : .html
		return "common/main";
	}
	
}
