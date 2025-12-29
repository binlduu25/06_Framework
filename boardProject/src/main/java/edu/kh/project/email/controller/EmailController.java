package edu.kh.project.email.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.kh.project.email.model.service.EmailService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 필드에 자동으로 의존성(autowired) 주입(Autowired 사용 불필요). lombok 제공
@RequestMapping("email")
@Controller

public class EmailController {
	
	private final EmailService service; // final 선언

	@ResponseBody
	@PostMapping("signup")
	public int signup(@RequestBody String email) {
		
		String authKey = service.sendEmail("signup", email);
		
			if(authKey != null) { // 인증번호가 반환되어 돌아옴 > 이메일 보내기 성공
				return 1;
			} else {
				return 0;
			}
	}
	
	/**
	 * @param map : email, authKey
	 * @return
	 */
	@ResponseBody
	@PostMapping("checkAuthkey")
	public int checkAuthkey(@RequestBody Map<String, String> map) {
		
		return service.checkAk(map);
		
	}
	
}
