package edu.kh.project.common.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.kh.project.common.filter.LoginFilter;

@Configuration
public class FilterConfig {

	
	/** 만들어 놓은 loginFilter 가 언제 적용될지 설정하는 메서드
	 * @return
	 */
	@Bean // 반환된 객체를 Bean 으로 등록
		  // LoginFilter 로 타입을 제한한 객체를 Bean으로 등록
	public FilterRegistrationBean<LoginFilter> loginFilter(){
		
		FilterRegistrationBean<LoginFilter> filter = new FilterRegistrationBean<>();
		
		// LoginFilter 에서 작성한 filter 를 객체화하여 등록
		filter.setFilter(new LoginFilter()); 
		
		// 필터가 동작하 URL 세팅
		// ex -  myPage/* : /myPage로 시작하는 모든 요청
		String[] filteringURL = {"/myPage/*", "/chatting/*", "/editBoard/*"};
		
		// String[] 을 List로 변환
		 // Arrays.asList(배열) : 배열을 List로 전환하는 메서드
		filter.setUrlPatterns(Arrays.asList(filteringURL));
		
		// 필터 이름 지정
		filter.setName("loginFilter");
		
		// 필터 순서 지정
		filter.setOrder(1);
		
		return filter; // 반환된 filter 객체가 Bean으로 등록되어 @Configuration 에 따라 필요한 곳에서 동작할 것
		
	};
	
}
