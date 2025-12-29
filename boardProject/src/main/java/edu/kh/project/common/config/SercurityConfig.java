package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Spring security 를 미리 Dependency 에 넣어두었다.

@Configuration // Configuration 어노테이션 설정을 통해 서버가 구동될 때 바로 시행될 수 있도록 함

public class SercurityConfig {
	
	// BCryptPasswordEncoder 라는 객체를 생성해 반환해주는 메서드
	 // BCryptPasswordEncoder : 평문을 BCrypt 패턴을 이용하여 암호화 평문과 암호화된 문자열 비교하여 서로 일치하는지 판단
	
	@Bean // Bean 으로 등록했으니 MemberService 에서 Autowired 로 사용 가능
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
