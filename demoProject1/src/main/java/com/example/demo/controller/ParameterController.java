package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.dto.Member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청 및 응답 제어 역할 명시 + Bean 등록

@RequestMapping("param") // /param 으로 시작하는 모든 요청을 현재 컨트롤러로 매핑

@Slf4j // log 를 이용한 메시지를 콘솔창에서 출력할 때 사용하는 어노테이션으로써, lombok 에서 제공함
public class ParameterController {

	// 1.
	
	@GetMapping("main")
	public String paramMain() { // /param/main Get 방식 요청 매핑
		
		// src/main/resources/templates/param/param-main.html 로 forward
		return "param/param-main";
	}
 	
	
	// 2.
	
	/* HttpServletRequest :			
	 * - 요청 클라이언트의 정보, 제출된 파라미터 등을 저장한 객체
	 * - 클라이언트 요청 시 생성
	 * 
	 * Spring 의 Controller
	 * 단 메서드 작성 시 매개변수에 원하는 객체를 작성하면 존재하는 객체를 바인딩, 없다면 생성해서 바인딩
	 * --> ArgumentResolver(전달인자 해결사) 가 있어서 해당 방식이 가능함
	 * */
	@PostMapping("test1") // /param/test1 으로 요청 온 POST 방식 요청 매핑
	public String paramTest1(HttpServletRequest req) {
		
		String inputName = req.getParameter("inputName");
		int inputAge = Integer.parseInt(req.getParameter("inputAge"));
		String inputAddress = req.getParameter("inputAddress");
		
		log.debug("이름 : " + inputName);
		log.debug("주소 : " + inputAddress);
		log.debug("나이 : " + inputAge);
		
		/*
		 * Spring 에서 Redirect(재요청) 하는 방법
		 * - Controller 메서드 반환값에 "redirect:재요청주소"; 작성
		 * resp.sendRedirect 처럼
		 * redirect는 get 방식
		 */
		
		return "redirect:/param/main";
	}

	/* 
	 
	 2. @RequestParam 어노테이션 - 낱개 파라미터 얻어오기
	 
	 - request 객체를 이용한 파라미터 전달 어노테이션
	 - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨.
	 - 주입되는 데이터는 매개변수의 타입에 맞게 형변환이 자동으로 수행됨.
	 
	 [기본 작성법]
	 @RequestParam("key") 자료형 매개변수명
	 
	 [속성 추가 작성법]
	 @RequestParam(value="key", required=false, defaultValue="1")
	 
	 value : 전달받은 input 태그의 name 속성값(파라미터 key)
	 required : 입력된 name 속성값 파라미터 필수 여부 지정(기본값 true)
	 -> required=true인 파라미터가 존재하지 않는다면 400(Bad Request) 에러 발생
	 -> "" (빈문자열)일 때는 에러 발생 X
	 (파라미터가 존재하지 않는것이 아니라 name속성값="" 로 넘어오기 때문에)
	 
	 defaultValue : 파라미터 중 일치하는 name속성값이 없을 경우에 대입할 값 지정.
	 -> required=false 인 경우 사용
	 
	 */
	
	@PostMapping("test2") // /param/test2 로 오는 POST 방식 요청을 매핑
	public String paramTest2(@RequestParam("title") String title,
							 @RequestParam("author") String author,
							 @RequestParam("price") int price, 
							 // int 형은 빈문자열이 들어올 시 형변환 오류가 발생
							 //빈문자 -> int 로 자동형변환이 불가능하기 때문.
							 // 즉, 매개변수의 자동형변환이 사용된 경우 파라미터값(value)이 필수로 작성되어야 함
							 @RequestParam(value="publisher", required=false, defaultValue="라라출판") String publisher // 미입력 시 기본값 사용
							 ) {
		
		log.debug("title : " + title);
		log.debug("author : " + author);
		log.debug("price : " + price);
		log.debug("publisher : " + publisher);
		
		
		return "redirect:/param/main";
	}
	
	// 3. @RequestParam 여러 개 파라미터 얻어오기
	// - 같은 name 속성값을 지닌 파라미터 얻어오는 방법(String[], List<String>)
	// - 제출된 파라미터 한번에 묶어서 얻어오기 (Map<String, Object>)
	@PostMapping("test3")
	public String paramTest3(@RequestParam("color") String [] colorArr,
							 @RequestParam("fruit") List<String> fruitList,
							 @RequestParam Map<String, Object> paramMap // Map으로 가져오게 되면 name 입력할 필요 없다.
			) {
		
		log.debug("colorArr : " + Arrays.toString(colorArr));
		log.debug("fruitList : " + fruitList);
		log.debug("paramMap : " + paramMap); 
		// @RequestParam Map<String, Object>
		// -> 제출된 모든 파라미터가 Map으로 넘어오지만 파라미터는 배열이나 List 가 아님. 
		// -> 첫번째로 제출된 value값만 저장된다
		
		return "redirect:/param/main";
	}
	
	// @ModelAttribute 를 이용한 파라미터 얻어오기
	// - DTO(또는 VO)와 같이 사용하는 어노테이션
	// 전달받은 파라미터의 name 속성값이 함께 사용되는 DTO의 필드명과 같다면 자동으로 setter를 호출해서 필드에 값을 저장
	@PostMapping("test4")
	public String paramTest4(@ModelAttribute Member inputMember) {
		
		/*
		 
		 ModelAttribute 가 하는 일
		 
		 Member inputMember = new Member();
		 inputMember.setMemberId("memberId에 작성된 값"
		 ....그외 나머지 매개변수들에 대한 setter 작업
		  
		 위 코드를 어노테이션 하나로 해결
		 해당 어노테이션이 작동하기 위해선 반드시 getter/setter 및 기본생성자가 필수로 작성되어 있어야 함
		 
		 또한,
		 @ModelAttribute 를 통해 만들어진 객체(여기서 inputMember)를 **'커맨드객체'** 라고 호칭함
		 
		 그리고 아래와 같이 어노테이션 부분을 생략 가능
		 public String paramTest4(Member inputMember)
		 
		 */
		
		log.debug("inputMember : " + inputMember);
		
		return "redirect:main"; 
		// 상대경로 작성법
		// 상대경로에서 가장 중요한 부분은 현재 위치
		// '/' 를 붙이게 되면 절대경로가 되어 아예 다른 곳으로 가니 주의
		// -> 현재 경로의 가장 마지막 레벨의 주소값을 "redirect:해당 주소" 로 변경하기 때문
	}
	
}
