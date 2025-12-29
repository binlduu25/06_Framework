package edu.kh.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) 
// exclude = {SecurityAutoConfiguration.class}
// Spring Security에서 기본제공하는 로그인 페이지 사용하지 않음
// 직접 구축하기 위해서
public class BoardProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectApplication.class, args);
	}

}
