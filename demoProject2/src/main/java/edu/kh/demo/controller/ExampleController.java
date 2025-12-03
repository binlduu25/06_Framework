package edu.kh.demo.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.demo.model.dto.Student;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("example") // /example 로 시작하는 모든 요청을 해당 컨트롤러에 매핑
@Slf4j // lombok 라이브러리가 제공하는 로그 객체 자동생성 어노테이션

public class ExampleController {

	/* Servlet 내장 객체 범위
	 * page < request < session < application
	 * 
	 * Model 객체 : (org.springframework.ui.Model)
	 * > Spring 에서 데이터 전달 역할하는 객체 (기본 scope 가 request임)
	 * - @SessionAttribute 와 함께 사용 시 session scope 로 변환
	 * 
	 * [기본 사용법]
	 * model.addAttribute(key, value);
	 * 
	 * */
	@GetMapping("ex1") // /example/ex1 으로 get 방식 요청 매핑
	public String ex1(HttpServletRequest req, Model model) {
		
		req.setAttribute("test1", "HttpServletRequest로 전달한 값");
		model.addAttribute("test2", "Model로 전달한 값");
		
		// 단일 값(숫자, 문자열)을 Model 이용하여 html로 전달
		model.addAttribute("productName", "커피");
		model.addAttribute("price", 20000);
		
		// 복수 값(배열, List)을 Model을 이용하여 html로 전달
		List<String> fruitList = new ArrayList();
		fruitList.add("사과");
		fruitList.add("딸기");
		fruitList.add("바나나");
		model.addAttribute("fruitList", fruitList);
		
		// DTO 객체 Model을 이용하여 html로 전달
		Student std = new Student();
		std.setStudentNo("123");
		std.setName("김김");
		std.setAge(24);	
		model.addAttribute("std", std);
		
		// List<Student> 객체 Model 이용하여 html로 전달
		List<Student> stdList = new ArrayList<>();
		stdList.add(new Student("124", "라라", 20));
		stdList.add(new Student("125", "김라", 30));
		stdList.add(new Student("127", "오라", 50));
		model.addAttribute("stdList", stdList);
		
		// src/main/resources/templates/example/ex1.html 로 forward
		return "example/ex1";	
		
	}
	
	@PostMapping("ex2") // /example/ex2 POST 방식 요청하는 매핑
	public String ex2(Model model) {
		
		model.addAttribute("str", "<h1>테스트 중 &times; </h1>"); // request scope
		
		// src/main/resources/templates/example/ex2.html 로 forward
		return "example/ex2";
		
	}
	
	
	@GetMapping("ex3")
	public String ex3(Model model) {
		
		model.addAttribute("key", "제목");
		model.addAttribute("query", "검색어");
		model.addAttribute("boardNo", 10);
		
		return "example/ex3";
	}
	
	@GetMapping("ex3/{path}") // path: 임의의 변수명 
	public String pathVariableTest(@PathVariable("path") int path) {
		
		// Controller 에서 해야 하는 로직이 동일한 경우
		// : example/ex3/1, example/ex3/2, example/ex3/3 .... 등
		// 해당 주소 중 {path} 부분의 값을 가져와서 매개변수로 저장,
		// 이 매개변수 값을 controller 단 메서드에서 사용할 수 있도록 함(해당 값을 Service -> DAO -> DB)
		// + Request scope 에 자동 세팅됨 
		// 변수명 = 값
		
		return "example/testResult";
	}
	
	
	@GetMapping("ex4")
	public String ex4(Model model) {
		
		Student std = new Student("3333", "유비", 44);
		model.addAttribute("std", std);
		model.addAttribute("num", 100);
		
		return "example/ex4";
	}
	
	@GetMapping("ex5")
	public String ex5(Model model) {
		
		model.addAttribute("message", "타임리프 + JS 사용 연습");
		model.addAttribute("num", 12345);
		
		Student std = new Student();
		std.setStudentNo("123");
		std.setName("김김");

		model.addAttribute("std", std);
		
		return "example/ex5";
	}
	
	
}
