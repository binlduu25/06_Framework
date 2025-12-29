package edu.kh.project.email.model.service;

import java.util.Map;

import org.springframework.stereotype.Service;

public interface EmailService {

	/** 이메일 보내기 서비스
	 * 이메일을 보내야 되는 경우가 많다.
	 * @param type : 무슨 이메일을 발송할지 구분할 key로 쓰임
	 * @param email
	 * @return
	 */
	String sendEmail(String type, String email); // 매개변수를 전달 시 바로 "signup"을 전달했기 때문에 String type 으로 수정

	int checkAk(Map<String, String> map);



}
