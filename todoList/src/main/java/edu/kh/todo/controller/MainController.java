package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청/응답 제어 역할 명시 + Bean 등록
@Slf4j // log 객체 자동생성 - lombok 라이브러리


// 1. Controller 클래스 작성
public class MainController {
	
	
	
	// 6. @Autowired로 TodoService '의존성 주입' (타입이 같거나 상속관계인 것만(TodoServiceImpl))
	// > private TodoService service = new TodoServiceImpl(); 과 같은 방식으로 흘러갈 것
	
	@Autowired
	private TodoService service;
	
	// 2. 메인 화면 전송 처리
	@RequestMapping("/")
	public String MainPage(Model model) {
		
		// 7. 전송 테스트
		
		log.debug("service : " +  service);
		// log에서 다음과 같이 확인 가능
		// ...[0;39m service : edu.kh.todo.model.service.TodoServiceImpl@3d2aee95
		
		// todoNo가 1인 todo의 제목 조회하여 request scope 에 추가
		
		String testTitle =  service.testTitle();
		
		// 8.
		model.addAttribute("testTitle", testTitle);
		
		// ----------------------------------------------------------------------
		
		// A. TB_TODO 테이블에 저장된 전체 할 일 목록 조회 + 완료된 할 일 갯수
		 // service 메서드 호출 후 결과 반환 받기
		 // 결과값을 어떤 타입에 저장해야 할까?
		
		Map<String, Object> map = service.selectAll(); 
		
		// map 에 담긴 내용 추출해서 scope 객체에 담아야 함
		
		List<Todo> todoList = (List<Todo>)map.get("todoList"); // map 을 object 객체로 담아왔기 때문에 다운캐스팅 필요하다
		int completeCount = (int)map.get("completeCount"); // 상동
		
		// Model 이용하여 request Scope 에 담기
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		
		// 불러온 객체 
		
		
		// 접두사 : src/main/resources/templates/
		// 접미사 : .html
		return "common/main";
	}
	
}
