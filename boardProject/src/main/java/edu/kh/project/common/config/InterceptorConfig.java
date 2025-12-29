package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.BoardTypeInterceptor;

// 인터셉터가 어떤 요청을 가로챌지 설정하는 클래스(언제 이 클래스가 일하는가)

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    InterceptorConfig(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

	@Bean 
	// 직접 만든 BoardTypeInterceptor 클래스를 Bean으로 등록하여
	// 관리를 Spring Container 가 수행하도록 한다. (개발자가 수동으로 만든 객체를 Bean 으로 등록
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
	}
	
	// 동작할 인터셉터 객체를 추가하는 메서드
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// bean 으로 등록된 BoardTypeInterceptor를 얻어와서 등록하기 위한 절차
		registry	
			.addInterceptor(boardTypeInterceptor()) // boardTypeInterceptor 등록
			.addPathPatterns("/**") // 어느 요청을 가로챌 것인가 = 언제 동작할 것인가
									// 가로챌 요청 주소 : /** : / 이하 모든 요청, 즉, 모든 페이지
			.excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon/**"); 
									// 가로채고 싶지 않을 요청주소 지정(정적리소스)
									 
	}
	
}
