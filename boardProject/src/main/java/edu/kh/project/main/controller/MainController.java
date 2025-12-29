package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
	
	@RequestMapping("/")
	public String mainPage() {
		return "common/main"; // forward
	}
	
	
	/** LoginFilter에서 로그인하지 않았을 때 리다이렉트로 요청
	 * 로그인되어 있지 않을 시에 접근 제한 메뉴에 접근할 때 차단 후 메인페이지로 돌려보내고 메시지 출력
	 * @return
	 */
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		
		ra.addFlashAttribute("message", "로그인 후 이용해주세요");
		
		return "redirect:/";
	}
}
