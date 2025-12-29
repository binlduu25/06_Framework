package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;

@Controller // 요청 + 응답 + Bean 등록
@RequestMapping("todo")
public class TodoController {

	@Autowired // DI : 의존성 주입(같은 타입, 상속관계 등의 Bean 타입 의존성 주입)
	private TodoService service;
	
	
	// 입력값이 null 일 때 오류 발생함. 해결 방법?
	@PostMapping("add") // todo/add 의 post 방식 mapping
	public String addTodo(@RequestParam("todoTitle") String todoTitle,
						  @RequestParam("todoContent") String todoContent,
						  RedirectAttributes ra){
		
		// 받아오는 방법
		 // 방법 1. HttpServletRequest req 통해 getParameter() 로 받아오기
		 // 방법 2. @RequestParam() 이용해서 얻어오기
		 // 방법 3. @ModelAttribute 와 DTO 이용해서 얻어오기 (DTO 객체의 변수명과 넘어오는 Parameter 값이 일치해야 할 것)
		
		// 서비스 메서드 호출 후 반환 받기
		int result = service.addTodo(todoTitle, todoContent);
		
		// 결과에 따라 message 값 지정
		String message = null;
		
		
		
		if (result > 0) {
			message = "할 일 추가 성공!";
		}else { message = "할 일 추가 실패!";
		}
		
		// message 를 어느 scope 에 담아야 할까를 결정하기 위해서 forward/redirect 할지를 먼저 결정해야 함
		// 여기서는 main.html로 '재요청'을 해야 하기 때문에 redirect 해야 함
		
		// request Scope 에 실어둔 값은 redirect 할 시 살아있지 않다. 다시 생성되기 때문에
		// 따라서 session Scope 에 실어야 함
		
		// RedirectAttributes : 전달인자 부분에 작성한다
		// 리다이렉트 시 값을 1회성으로 전달하는 객체이다.
		// request -> session -> request 로 변환되는 과정을 거친다
		// RedirectAttributes.addFlashAttribute('key", value) 라는 메서드가 있다.
		// 위 메서드를 이용하면 원래 응답 전 request Scope 인 RedirectAttributes 가 잠시 session Scope 로 변환(이동, 사용)된다.
		// 그 후 redirect 가 끝난 후(응답 후) 기존의 request Scope 로 복귀한다. 
		
		// redirect 시 1회성으로 사용할 데이터를 속성으로 추가
		// 원래 req지만 redirect 할 때 session 으로 변하여 응답 수행 후 다시 req로 복귀 후 소멸
		ra.addFlashAttribute("message", message);
		
		// 메인페이지로 재요청
		return "redirect:/";
	}
	
	
	/** 할 일 상세 조회
	 * @return
	 */
	@GetMapping("detail") // a 태그로부터 온 요청이므로 get
	public String todoDetail(@RequestParam("todoNo") int todoNo, Model model, RedirectAttributes ra) { 
		// todoNo 을 받아온다. 해당 parameter 로 연계하여 상세 내용 조회할 것 
		// parameter 로 들어온 값은 String 이지만 int 형으로 받을 경우 자동형변환 된다
		
		Todo todo = service.todoDetail(todoNo); 
		// todoNo 을 전달해 sql 구문 상에서 WHERE 절의 조건문에 쓰일 것이고
		// 결과는 todoNo 에 해당하는 todoTitle 등 todo 객체에 상응하는 값들, 즉 1행이 조회될 것
		// 따라서 Todo 객체로 받는다
		
		String path = null;
		
		// 조회 결과가 있을 경우 detail.html 로 forward
		if(todo != null) {
			
			path = "todo/detail";
			model.addAttribute("todo", todo); // request scope 값 세팅
			
		// 조회 결과가 없을 경우 main.html 로 redirect
		} else {
			
			path = "redirect:/";
			ra.addFlashAttribute("message", "할 일이 존재하지 않음");
		}
		
		return path;
		
	}
	
	@GetMapping("delete")
	public String todoDelete(@RequestParam("todoNo") int todoNo, RedirectAttributes ra) {
		
		int result = service.todoDelete(todoNo);
		
		String path = null;
		String message = null;
		
		if(result > 0) {
			path = "/";
			message = "삭제 성공";
		} else {
			path = "/todo/detail?todoNo=" + todoNo; // /todo/detail 에 todoNo이 필요하다
			message = "삭제 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		
	}
	
	/**
	 * @param todo : 커맨드 객체
	 * ModelAttribute 어노테이션과 함께 DTO 클래스를 사용하는 방식
	 * 파라미터의 KEY 와 Todo 객체의 필드명 일치할 시 일치하는 필드값을 파라미터의 value 값으로 세팅
	 * 즉, todo 객체의 todoNo와 complete 필드가 세팅 완료된 상태
	 * ModelAttribute 부분은 생략이 가능하다
	 * @return
	 */
	@GetMapping("changeComplete")
	public String changeComplete(@ModelAttribute Todo todo, RedirectAttributes ra) {
		
		// 변경 서비스 호출
		int result = service.changeComplete(todo);
		
		String message = null;
		
		if (result > 0) message = "변경 성공";
		else			message = "변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		// 상대경로로 작성해보기
		// 현재 주소 : /todo/changeComplete
		// 목표 주소 : /todo/detail?todoNo=
		return "redirect:detail?todoNo=" + todo.getTodoNo();
	}
	
	/** 
	 * 수정 화면 전환 요청
	 * @return
	 */
	@GetMapping("update")
	public String todoUpdate(@RequestParam("todoNo") int todoNo, Model model) {
		
		// 상세 조회 서비스 재활용
		 // 수정 화면에 출력할 기존 내용 필요
		Todo todo = service.todoDetail(todoNo);
		
		model.addAttribute("todo", todo);
		
		return "todo/update";
	}
	
	@PostMapping("update")
	public String todoUpdate(@ModelAttribute Todo todo, RedirectAttributes ra) {
		
		// 수정 서비스 호출 후 반환 받기
		int result = service.todoUpdate(todo);
		
		String path = "redirect:";
		String message = null;
		
		if (result > 0) {
			// 해당 Todo 의 상세 조회로 redirect
			path += "/todo/detail?todoNo=" + todo.getTodoNo();
			message = "수정 성공";
		}else {
			path += "/todo/update?todoNo=" + todo.getTodoNo();
			message = "수정 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
}
