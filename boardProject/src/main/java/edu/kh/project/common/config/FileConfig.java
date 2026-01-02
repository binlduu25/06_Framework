package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@PropertySource("classpath:/config.properties")
@Configuration
public class FileConfig implements WebMvcConfigurer{


	// WebMvcConfigurer : SpringMVC 프레임워크에서 제공하는 인터페이스 중 하나로, 스프링 구성을 커스터마이징하고 
	// 					  확장하기 위한 메서드 제공. 주로 웹 애플리케이션의 설정을 조정하거나 추가하는 데 사용됨
	
	// 파일 업로드 임계값
	@Value("${spring.servlet.multipart.file-size-threshold}")
	private long fileSizeThreshold;
	
	// 임계값 초과 시 파일의 임시 저장 경로
	@Value("${spring.servlet.multipart.location}")
	private String location;
	
	// 요청 당 파일 최대 크기
	@Value("${spring.servlet.multipart-max-request-size}")
	private long maxRequestSize;
	
	// 개별 파일 당 최대 크기 
	@Value("${spring.servlet.multipart-max-file-size}")
	private long maxFileSize;
	
	// ----------------------------------
	
	// 프로필 이미지 관련 경로
	@Value("${my.profile.resource-handler}")
	private String profileResourceHandler;
	
	@Value("${my.profile.resource-location}")
	private String profileResourceLocation;

	// ----------------------------------
	
	// 게시판 이미지 경로
	@Value("${my.board.resource-handler}")
	private String boardResourceHandler;
	
	@Value("${my.board.resource-location}")
	private String boardResourceLocation;
	
	/* 아래 코드 잘못 눌러서 생긴 듯
	    private final BoardController boardController;

	private final MultipartConfigElement configElement;
    
    FileConfig(MultipartConfigElement configElement) {
        this.configElement = configElement;
    }
    
        FileConfig(BoardController boardController) {
        this.boardController = boardController;
    }
    
    */
    
	
	
	
	// 요청 주소에 따라 서버 컴퓨터의 어떤 경로로 접근할지 설정하는 메서드
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// ResourceHandlerRegistry : 
		 // Spring MVC에서 정적 리소스(이미지, CSS, JS 등)의 요청을 처리하기 위해 사용하는 클래스
		 // URL 요청 패턴을 서버의 실제 파일 경로와 연결하여 클라이언트가 특정 경로로 정적파일에 접근할 수 있도록 설정
		
		registry.addResourceHandler("/myPage/file/**").addResourceLocations("file:///D:/Programming/0_uploadFiles/test/");
		// 어떤 경로로 접근했는가?(Handler)
		// 정적파일 경로는 어디인가?(location)
		 // 즉, 클라이언트가 "/myPage/file/**" 패턴으로 이미지 요청 시, 서버 폴더 경로 중 "D:/Programming/0_uploadFiles/test/" 로 연결할 것
		
		// 프로필 이미지 경로
		registry.addResourceHandler(profileResourceHandler).addResourceLocations(profileResourceLocation);
		// 사용자가 /myPage/profile/** 경로로 요청 시, D:/Programming/0_uploadFiles/profile/ 로 연결
		
		registry.addResourceHandler(boardResourceHandler).addResourceLocations(boardResourceLocation);
		// 사용자가 
	}
	
	@Bean // 이 메서드를 Bean으로 등록하여 원하는 곳에서 사용
	public MultipartConfigElement configElement() {
		
		// 위 필드들을 가지고 multipart 객체를 만들기 위한 설정을 한다
		// MultipartResolver 설정
		 // MultipartConfigElement
		 // : 파일 업로드를 처리하는 데 사용되는 MultipartConfigElement 를 구성하고 반환(옵션 설정에 사용)
		 // : 업로드 파일 최대 크기, 임시 저장 경로 등을 설정하는 객체
			
		MultipartConfigFactory factory = new MultipartConfigFactory();
			
		factory.setFileSizeThreshold(DataSize.ofBytes(fileSizeThreshold)); // 파일 업로드 임계값 설정
		factory.setLocation(location); 									   // 임시 저장 경로
		factory.setMaxRequestSize(DataSize.ofBytes(maxRequestSize));  	   // HTTP 요청 당 파일 최대 크기
		factory.setMaxFileSize(DataSize.ofBytes(maxFileSize)); 			   // 개별 파일 당 최대 크기
		
		// 세팅을 한 공장을 하나 만들었고, 이 공장에서 multipartElement 를 찍어내서 제공
		return factory.createMultipartConfig();
		
	}
	
	// MultipartResolver 생성하여 Bean으로 등록 
	// 위에서 만든 MultipartConfigElement "자동"으로 이용
	@Bean
	public MultipartResolver multipartResolver() {
		// MultipartResolver 란?
		 // 클라이언트로부터 파일을 전달 받으면 MultipartFile 이라는 객체를 이용해야 한다. 이를 처리해주는 역할
		 // 즉, Resolver
		 // 클라이언트로부터 받은 multipart 요청 처리, 업로드된 파일을 추출하여 multipartFile 객체로 제공
		 // 여기까지 세팅해놔야 controller 단에서 multipartFile 객체 이용 가능
		
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		
		return multipartResolver;
	}
}
