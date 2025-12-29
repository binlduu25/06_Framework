package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;


@Controller
@RequestMapping("ajax")
public class AjaxController {

	// 등록된 Bean 중 상속 타입 또는 상속관계인 Bean 을 찾아 해당 필드에 의존성 주입
	@Autowired
	private TodoService service;	
	
	@GetMapping("main")
	public String ajaxMain() {
		return "ajax/main";
	}
	
	/*
	 * @ResponseBody
	 * - 컨트롤러 메서드의 반환값을 Http 응답 본문에 직접 바인딩하는 역할임을 명시
	 * -> 컨트롤러 메서드의 반환값을 비동기 요청했던 HTML/JS 파일 부분에 값을 그대로 돌려 보낼 것임을 명시. 
	 * -> 해당 어노테이션이 붙은 컨트롤러의 메서드는 return 에 작성된 값이 forward/redirect 로 인식 X
	 * 
	 * @RequestBody
	 * - 비동기 요청시 전달되는 데이터 중 body 부분에 포함된 요청 데이터를 알맞은 Java 객체 타입으로 바인딩하는 어노테이션
	 * - 기본적으로 JSON 형식을 기대함.
	 * 

	 * 
	 * 
	 * */
	
	
	// 그동안 반환형을 String을 한 이유는 동기식 요청 시 forward 또는 redirect 시 경로를 작성해야 했기 때문
	// 하지만 @ResponseBody 라는 어노테이션을 사용하게 되면 js에서 요청한 값을 그대로 돌려보내게 됨?
	// 따라서 더 이상 return 에 작성하는 값은 forward, redirect 값이 아니다.
	 // 아래 메서드는 전체 Todo 개수를 비동기 조회하는 메서드이고, 반환되어야 하는 결과값은 int이다 (행의 개수)
	 
	@ResponseBody // 반환값을 HTTP 응답 봄문으로 직접 전송(값 그대로 돌려보냄)
	@GetMapping("totalCount")
	public int getTotalCount() {
		
		// 전체 할 일 갯수 조회 서비스 호출 결과 반환받기
		// 결과 JS로 반환
		
		int totalCount = service.getTotalCount();
		return totalCount;
		
	}
	
	@ResponseBody
	@GetMapping("aCompleteCount")
	public int aCompleteCount() {
		
		return service.getCompleteCount();	
		
	}
	
	@ResponseBody
	@PostMapping("add")
	public int addTodo(@RequestBody Todo todo) {
		return service.addTodo(todo.getTodoTitle(), todo.getTodoContent());
		// 요청 body(본문)에 담긴 값을 Todo 라는 DTO 에 저장
		// 할 일 추가 서비스 호출 후 결과값 리턴
	}
	
	@ResponseBody
	@GetMapping("selectList")
	public List<Todo> selectList() {
		return service.selectList();
		// List 는 java 전용 객체이기 때문에 JS 가 인식할 수 없다
		// -> JSON 으로 변환!
		// -> HttpMessageConverter 가 알아서 변환해줄 것임
		
 		/* [HttpMessageConvertor]
 		 *
		 * Spring에서 비동기 통신 시,
		 * - 전달받은 데이터의 자료형
		 * - 응답하는 데이터의 자료형
		 * 위 두가지를 알맞은 형태로 가공(변환)해주는 객체
		 * 
		 *    Java                       JS
		 * 문자열,숫자 <--------------> TEXT
		 *    Map      <->   JSON   <-> JS Object
		 *    DTO      <->   JSON   <-> JS Object
		 *    
		 * (참고)
		 * Spring에서 HttpMessageConvertor 작동하기 위해서는
		 * jackson-data-bind 라이브러리가 필요한데 Spring boot에는 모듈에 내장되어 있음
		 * */
	}
	
	
	@ResponseBody
	@GetMapping("detail")
	public Todo selectTodo(@RequestParam("todoNo") int todoNo) {
		return service.todoDetail(todoNo);
	}
	
	@ResponseBody
	@DeleteMapping("delete")
	public int todoDelete(@RequestBody int todoNo) {
		return service.todoDelete(todoNo);
	}
	
	@ResponseBody
	@PutMapping("changeComplete")
	public int changeComplete(@RequestBody Todo todo) {
		return service.changeComplete(todo);
	}
	
	
	@ResponseBody
	@PutMapping("update")
	public int todoUpdate(@RequestBody Todo todo) {
		return service.todoUpdate(todo);
	}
}
