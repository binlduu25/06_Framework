package edu.kh.project.mypage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.common.config.EmailConfig;
import edu.kh.project.member.controller.MemberController;
import edu.kh.project.member.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;
import edu.kh.project.mypage.model.service.MyPageService;
import jakarta.mail.Multipart;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@SessionAttributes({"loginMember"}) // 로그인할 때 session scope 에 실어두었던 loginMember 를 가져와 해당 컨트롤러단에서 사용하게 한다
// @SessionAttributes 의 역할
 // - Model에 추가된 속성 중 key 값이 일치하는 속성을 session scope 로 변경하는 어노테이션 
 // - 클래스 상단에 @SessionAttributes({"session scope로 이동하고 싶은 key 값"})

// @SessionAttribute 의 역할
 // SessionAttributes 를 통해 session 에 등록된 속성을 꺼내올 때 사용하는 어노테이션
 // 메서드 매개변수 부분에 작성 후 저장할 타입 지정

@Controller
@RequestMapping("myPage")
@Slf4j
public class MyPageController {

    private final MultipartResolver multipartResolver;

    private final MemberController memberController;

    private final EmailConfig emailConfig;
    
	@Autowired	
	private MyPageService service;

    MyPageController(EmailConfig emailConfig, MemberController memberController, MultipartResolver multipartResolver) {
        this.emailConfig = emailConfig;
        this.memberController = memberController;
        this.multipartResolver = multipartResolver;
    }
	
	@GetMapping("info") // /myPage/info
	public String info(@SessionAttribute("loginMember") Member loginMember, // session 에 실린 loginMember 를 해당 경로로 접근할 시 불러와 Member타입에 저장
					   Model model) { // Model 이용하여 request scope에 값 실을 것
		
		// 현재 로그인한 회원의 정보는 session scope 에 등록되어 loginMember에 저장된 상태
		// 이 안에 주소도 있음(memberAddress)
		// 구분자(^^^)로 설정해두었고, 입력했다면 해당 구분자로 구분되어 있을 것
		// 주소를 입력하지 않았다면 NULL 로 저장
		
		String memberAddress = loginMember.getMemberAddress();
		
		if(memberAddress != null) { // 주소가 있을 경우
			
			// 구분자(^^^) 를 기준으로 memberAddress 값을 쪼깨어 String[] 배열로 반환
			String [] arr = memberAddress.split("\\^\\^\\^"); // split 은 정규표현식 관련 메서드이고 ^ 와 \ 모두 역할이 있기 때문에 이스케이프 문자를 사용한다
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
			
		}
		
		return "myPage/myPage-info";
	}
	
	@GetMapping("profile")
	public String profile() {
		return "myPage/myPage-profile";
	}
	
	@GetMapping("changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
	}
	
	@GetMapping("secession")
	public String secession() {
		return "myPage/myPage-secession";
	}
	
	@GetMapping("fileTest")
	public String fileTest() {
		return "myPage/myPage-fileTest";
	}
	
	/** 회원 정보 수정 처리
	 * 
	 * @param inputMember : 커맨드객체 (@ModelAttribute는 생략 가능), 제출된 MemberNickname, memberTel 세팅된 상태
	 * @param 주소는 따로 받아올 memberAddress 에 배열로 받아옴
	 * @param loginMember : 로그인한 회원 정보 (현재 로그인한 회원번호(PK)사용 예정
	 * @return
	 */
	@PostMapping("info") 
	// 위 info 화면을 출력하는 get 방식 요청이 있고, 
	// 해당 메서드는 post 방식으로 수정요청을 보내는 controller
	// myPage/info 로 오는 POST 방식 처리
	public String updateInfo(@ModelAttribute Member inputMember, // 클라이언트로 제출받은 파라미터(Member객체)
							 @RequestParam("memberAddress") String[] memberAddress,
							 @SessionAttribute("loginMember") Member loginMember, // session 에 저장된 로그인회원 정보(Member객체)
							 RedirectAttributes ra) {
		
		// inputMember 에 loginMember 에 담긴 현재 회원번호 정보 추가
		inputMember.setMemberNo(loginMember.getMemberNo());
		
		// 주소 부분은 controller 에서 안 하고 service 에서 수행, 여기선 service 로 inputMember와 memberAddress 2개
		
		// 회원 정보 수정 서비스 호출
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		
		if(result > 0) {
			message = "회원 정보 수정 성공";
			// loginMember에 DB상 업데이트된 내용으로 세팅
			// -> loginMember는 세션에 저장된 로그인할 당시의 기존 회원 정보가 세팅되어 있음
			// -> loginMember 수정 시 세션에 저장된 로그인한 회원의 정보가 업데이트됨
			// 즉, session 의 정보와 DB 의 정보를 동기화할 필요 있다.
			loginMember.setMemberNickname(inputMember.getMemberNickname());
			loginMember.setMemberTel(inputMember.getMemberTel());
			loginMember.setMemberAddress(inputMember.getMemberAddress());
			
		}else {
			message = "회원 정보 수정 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info"; // 재요청 경로 : /myPage/info 로 GET 요청
	}
	
	/**
	 * @param paramMap : 모든 파라미터를 맵으로 저장
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("changePw")
	public String changePw(@RequestParam Map<String, Object> paramMap,
						   @SessionAttribute("loginMember") Member loginMember,
						   RedirectAttributes ra) {
		
		int memberNo = loginMember.getMemberNo();
		
		int result = service.changePw(paramMap, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "비밀번호 변경 성공";
			path = "/myPage/info";
			
		}else {
			message = "현재 비밀번호가 일치하지 않습니다";
			path = "/myPage/changePw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
	}
	
	
	/**
	 * @param memberPw : 제출받은(입력받은) 비밀번호
	 * @param loginMember : 로그인한 회원 정보 객체, sql 에 사용하기 위함
	 * @param status : @SessionAttributes 와 함께 사용
	 * @return
	 */
	@PostMapping("secession") // /myPage/secession POST
	public String secession(@RequestParam("memberPw") String memberPw, 
							@SessionAttribute("loginMember") Member loginMember,
							SessionStatus status,
							RedirectAttributes ra) {
		
		// 로그인한 회원의 회원번호 꺼내오기
		int memberNo = loginMember.getMemberNo();
		
		// 서비스 호출
		int result = service.secession(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		// 탈퇴 성공 : 메인페이지로
		// 탈퇴 실패 : 탈퇴 페이지 재요청 
		 // > 둘 모두 redirect
		
		if(result > 0) {
			message = "탈퇴되었습니다";
			path = "/";
			
			status.setComplete(); // 세션 비우기 (로그아웃 상태 변경)
		}else {
			message = "비밀번호가 일치하지 않습니다";
			path = "secession";
		}
		
		ra.addFlashAttribute(message);
		
		return "redirect:" + path;
	
	}
	
	/* 
	 * Spring 에서 파일을 처리하는 방법
	 * 
	 * - enctype="multipart/form-data" 로 클라이언트의 요청을 받으면(문자, 숫자, 파일 등이 섞여 있는 요청)
	 * 
	 * 이렇게 섞여 있는 파라미터를 MultipartResolver를 이용하여 분리하는 작업 필요
	 *  > FileConfig 에 MultipartResolver 를 정의한다.
	 *   : 문자열, 숫자 > String
	 *   : 파일			> MultipartFile

	 * */
	@PostMapping("file/test1") // /myPage/file/test1
	public String fileUpload1(@RequestParam("uploadFile") MultipartFile uploadFile,
							  RedirectAttributes ra) { // redirectattributes 통해 파일의 '경로'(path) 전달
		
		try {
			String path = service.fileUpload1(uploadFile); // 경로를 받아야 하기에 String 으로 설정
			
			// 파일이 실제로 서버 컴퓨터에 저장이 되어 웹에서 접근할 수 있는 경로가 반환되었을 때
			if(path != null) {
				ra.addFlashAttribute("path", path);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("파일 업로드 예제 1 중 예외 발생");
		}
		
		return "redirect:/myPage/fileTest";
	}
	
	@PostMapping("file/test2")
	public String fileUpload2(@RequestParam("uploadFile") MultipartFile uploadFile,
							  @SessionAttribute("loginMember") Member loginMember,
							  RedirectAttributes ra) {

		try {
			// 로그인한 회원의 번호 얻어오기
			int memberNo = loginMember.getMemberNo();
			
			// 업로드된 파일 정보를 DB에 INSERT 후 결과 행의 갯수 반환 받아야 함
			int result = service.fileUpload2(uploadFile, memberNo);
			
			String message = null;
			
			if(result > 0) {
				message = "파일 업로드 성공";
			}else {
				message = "파일 업로드 실패";
			}
			
			ra.addFlashAttribute("message", message);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("파일 업로드 예제 2 중 예외 발생");
		}
		
		return "redirect:/myPage/fileTest";
	}
	
	// 파일 목록 조회 화면
	@GetMapping("fileList")
	public String fileList(Model model,
						   @SessionAttribute("loginMember") Member loginMember) {
		
		// 파일 목록 조회 서비스 호출(현재 로그인한 회원이 올린 이미지만)
		int memberNo = loginMember.getMemberNo();
		List<UploadFile> list = service.fileList(memberNo);
		
		// model 활용해 list 담아서 forward
		model.addAttribute("list", list);
		
		return "myPage/myPage-fileList";
	}
	
	@PostMapping("file/test3")
	public String fileUpload3(@RequestParam("aaa") List<MultipartFile> aaaList,
							  @RequestParam("bbb") List<MultipartFile> bbbList,
							  @SessionAttribute("loginMember") Member loginMember,
							  RedirectAttributes ra) throws Exception{
		
		log.debug("aaaList" + aaaList); // 파일 미제출 시, 인덱스에 2개가 넘어온다 / 제출 시 2개
		log.debug("bbbList" + bbbList); // 파일 미제출 시, 인덱스에 1개가 넘어온다 / 3개 제출 시 3개

		// 여러 파일 업로드 서비스 호출
		int memberNo = loginMember.getMemberNo();
		
		int result = service.fileUpload3(aaaList, bbbList, memberNo);
		
		// result 안에는 aaaList 와 bbbList에 업로드된 파일 갯수가 들어 있을 것
		String message = null;
		
		if(result == 0) {
			message = "업로드된 파일이 없습니다";
		
		}else {
			message = result + "개의 파일이 업로드되었습니다";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest";
	}
	
	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg,
						  @SessionAttribute("loginMember") Member loginMember,
						  RedirectAttributes ra) throws Exception{
		
		// 서비스 호출
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) {
			message = "변경 성공";
		}else {
			message = "변경 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile"; // 상대경로 > /myPage/profile 로 GET 요청
	}
	
}
