package edu.kh.project.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@SessionAttributes({"loginMember"})
/*
 * @SessionAttributes({"key", "key", ...})
 * - Model에 추가된 속성 중 key 값이 일치하는 속성을 session scope로 변경
 * */

@RequestMapping("member")
@Controller
@Slf4j
public class MemberController {
	
	@Autowired // 의존성 주입 DI
	private MemberService service;
	
	
	/** [로그인]
	 * - 특정 사이트에 아이디(이메일)/비밀번호 등을 입력하여 해당 정보가 있으면 조회/서비스 이용
	 * - 로그인 한 회원 정보를 session 에 기록하여 로그아웃 또는 브라우저 종료 시까지 해당 정보를 계속 이용
	 * @param inputMember : 커맨드 객체(memberEmail, memberPw 세팅된 상태)
	 * @return
	 */
	@PostMapping("login")  // /member/login POST방식 요청 응답 
	public String login(/*@ModelAttribute*/Member inputMember, 
										   RedirectAttributes ra,
										   Model model,
										   @RequestParam(value ="saveId", required = false) String saveId,
										   // 아이디 저장 기능 위해 saveId 값을 받아온다(DTO에는 없기에 Model 이용 불가, required 기본값은 true 이기에 false로 반드시 설정)
										   HttpServletResponse resp)
										   {
		
		// 로그인 서비스 호출
		try {
			Member loginMember = service.login(inputMember); 
			
			// 테스트용 log.debug("tt2 : " + loginMember); 
						
			// 로그인 실패 시와 로그인 성공 구분
			
			if(loginMember == null) {
			
				ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다");
				// footer.html 에 미리 message 세팅됨
				
			}else { // 로그인 성공
				
				// 로그인된 정보는 브라우저가 활성화된 동안 정보가 그대로 담겨 있어야 함
				
				// 1단계 requestScope 에 세팅
				model.addAttribute("loginMember", loginMember);
				
				// 2단계 클래스 상단부에 @SessionAttributes() 어노테이션 작성하여 session Scope에 담는다.
				
				// ************** cookie ***************
				// 이하 아이디 저장을 위한 부분
				// 쿠키, 캐시, 세션 구분
				
				// 1) 쿠키를 이용하여 이메일 저장
				 // 쿠키 객체 생성해서 saveId 키에 로그인된 memberEmail 저장
				Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
				
				// 2) 쿠키 적용될 경로 설정
				 // 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될지 지정
				 // 즉, 메인페이지를 봤을 때 첨부되어야 함
				cookie.setPath("/");
				// "/" : IP 또는 도메인 또는 localhost
				//       즉, 메인페이지 + 하위 모든 주소
				
				// 3) 쿠키 만료 기간 설정
				if(saveId != null) { // 아이디 저장 체크함
					cookie.setMaxAge(60 * 60 * 24 * 30); // 30일로 초단위 지정
					
				}else { // 아이디 저장 체크 안 함
					cookie.setMaxAge(0); // 클라이언트의 쿠키 삭제	
				} 
				
				// 응답객체에 쿠키를 추가해서 클라이언트에게 전달해야 함
				 // 메서드 매개변수에 HttpServletResponse 추가
				resp.addCookie(cookie);
				
				
				
			}
			
		} catch (Exception e) {
			log.info("로그인 중 예외 발생");
			e.printStackTrace();
		}
		
		return "redirect:/";
	}
	
	
	/** 로그아웃
	 * @Param SessionStatus : @SessionAttributes 로 지정된 특정 속성을 세션에서 
	 * 						  제거할 수 있는 기능을 제공하는 객체 
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		// session scope 에 저장된 로그인된 회원 정보를 초기화 해주면 됨
		
		status.setComplete(); // 세션을 완료 시킴
		
		return "redirect:/";
		
	}
	
	@GetMapping("signup")
	public String signupPage() {
		return "member/signup";
	}
	
	/** 이메일 중복검사(비동기 요청)
	 * @return
	 */
	@ResponseBody // 응답 본문으로 응답값 돌려보냄
	@GetMapping("checkEmail") // GET 방식 /member/checkEmail 요청 매핑
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail); 
		
	}
	
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}
	
	
	
	/** 회원가입 최종
	 * @param inputMember : 입력된 정보 얻어와 MemberDTO에 저장, memberAddress 제외
	 * @param memberAddress : 입력한 주소 input 3개의 값을 배열로 전달 [우편번호, 도로명/지번주소, 상세주소]
	 * @param ra : RedirectAttributes 로 리다이렉트 시 1회성으로 req -> session -> req 로 전달되는 객체
	 * @return
	 */
	@PostMapping("signup")
	public String signup(@ModelAttribute Member inputMember,
						 @RequestParam("memberAddress") String[] memberAddress,
						 RedirectAttributes ra) { // 동기식 요청
	
		// 회원 가입 서비스 호출
		int result = service.signup(inputMember, memberAddress);
		
		String path = null;
		String message = null;
		
		if(result > 0) { // 성공
			message = inputMember.getMemberNickname() + "님의 가입을 환영합니다";
			
			path = "/";
			
		}else { // 실패
			message = "회원 가입 실패";
			path = "signup";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		// 성공 시 : redirect:/ (메인페이지 재요청)
		// 실패 시 : redirect:signup (상대경로)
		// 현재주소 : /member/signup
		// 목표경로 : /member/signup(get 방식)
		
	}
	
	@ResponseBody
	@GetMapping("selectMember")
	public List<Member> selectMember(){
	
		return service.selectMember();
		
		
		
	}
	
}
