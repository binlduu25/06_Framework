package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor 
public class EmailServiceImpl implements EmailService{

	private final EmailMapper mapper;
	private final JavaMailSender mailSender; // JavaMailSender : 실제 메일 발송을 담당하는 객체(EmailConfig 참조)
	private final SpringTemplateEngine templateEngine; // SpringTemplateEngine : 타임리프를 이용해서 html 코드 -> java코드 변환

	@Override
	public String sendEmail(String type, String email) {
		
		// 1. 인증키 생성 및 DB저장
		String authkey = createAuthkey(); // 아래 createAuthkey() 메서드 생성
		
		// 2. sql 쪽에 email과 authkey를 전달해주어야 하고, mybatis 에서는 매개변수를 1개밖에 전달하지 못하기 때문에 Map 이용
		Map<String, String> map = new HashMap<>(); // authkey 와 email 모두 String
		map.put("authkey", authkey);
		map.put("email", email);
		
		
		
		// 3. DB 저장 시도 - 실패 시 해당 메서드 종료
		 // null 을 가지고 controller 로 돌아감
		if(!storeAuthkey(map)) return null; 
		
		// 4. DB에 저장이 성공된 경우에 메일 발송 시도
		MimeMessage mimeMessage = mailSender.createMimeMessage(); // 메일 발송 시 사용하는 객체(누가, 누구에게, 어떤 내용 보낼지 등)
		
		try { 
			
			// 메일 발송을 도와주는 Helper 클래스(파일첨부, 템플릿 설정 등 쉽게 처리)
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// mimeMessage : MimeMessage 객체로, 이메일 메시지의 내용을 담고 있음(이메일 본문, 제목, 수신자 정보 등 포함)
			// true : 파일 첨부를 사용할 것인지 여부 지정(파일첨부 및 내부 이미지 삽입 가능)
			// UTF-8 : 이메일 내용 UTF-8로 인코딩
			
			helper.setTo(email); // 받는 사람(수신자)
			helper.setSubject("[boardProject]회원가입 인증 메일");
			// 단순히 내용만 보내기 위해서는 helper.setText("내용"); 등으로 설정하면 될 것
			helper.setText( loadHtml(authkey, type), true); // HTML 내용 설정, loadHtml 메서드 필요
			helper.addInline("logo", new ClassPathResource("static/images/logo.jpg"));
			
			// 만약 비밀번호 찾기 같은 내용으로 메일을 보내야 한다면 html 을 하나 만들어서 일련의 과정들 수행하면 될 것
			
			// 실제로 메일 발송
			mailSender.send(mimeMessage);
			
			return authkey;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null; // 실패 시 null 반환
		}
	
	}
	
	/** 전달받은 email 과 authKey로 확인하는 메서드
	 * 
	 */
	@Override
	public int checkAk(Map<String, String> map) {
		return mapper.checkAk(map);
	}
	
	/** // 인증키와 이메일을 DB에 저장하는 메서드
	 //  DB에 저장 : INSERT(DML)을 하겠다 > Transactional 필요
	 //  메서드 레벨에서도 transactional 어노테이션 사용 가능
	 * @param map
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean storeAuthkey(Map<String, String> map) {
		
		// 1. 기존 이메일에 대한 인증키 update 수행
		 // 만약 사용자가 인증번호 받기를 여러번 눌렀을 때 첫번째로 update 를 진행해야 함
		 // db 에 저장된 email 이 있으면 UPDATE 될 것이고
		 // 없다면 INSERT 수행 
		int result = mapper.updateAuthKey(map);
		
		// 2. update 실패 시 insert 수행
		if(result == 0) {
			result = mapper.insertAuthKey(map);
		}
		
		// 3. 성공 여부 반환(0 이상(성공) TRUE, 실패 FALSE)
		return result > 0;
		
	}

	/** 인증번호 발급 메서드
	 * UUID 를 사용하여 인증키 생성
	 * UUID : (Universally Unique IDentifier) 
	 * > 전세계에서 고유한 식별자를 생성하기 위한 표준
	 * > 매우 낮은 확률로 중복되는 식별자 생성(중복되는 값이 거의 없다)
	 * > 주로 데이터베이스 기본키, 고유 식별자를 생성해야 할 때 사용
	 * > 실제로 업무할 때는 DB 기본키를 난수를 생성해서 암호화하여 배정해야 함
	 * > 이때 사용하는 것이 UUID  
	 *	@return
	 */
	private String createAuthkey() {
		String uuid = UUID.randomUUID().toString().substring(0, 6); 
		// log.debug("tttttttttttt: " + uuid);
		return uuid;
		 // UUID 의 randomUUID 메서드 불러와 toString으로 변환한 다음, UUID 값이 길기 때문에 substring 으로 잘라준다(앞에서부터 6글자)
	}
	
	/** HTML 템플릿에 데이터를 넣어 최종HTML 생성하는 메서드
	 * @param authkey
	 * @param type
	 * @return
	 */
	private String loadHtml(String authkey, String type) {
		
		// Context(org.thymeleaf.context.Context)
		// 타임리프에서 제공하는 Html 템플릿에 데이터를 전달하기 위해 사용하는 클래스
		Context context = new Context();
		context.setVariable("authkey", authkey);
		
		return templateEngine.process("email/" + type, context);
		// src/main/resources/templates/email/signup.html 에 있는 html 파일로 context 를 전달
	}		

}
