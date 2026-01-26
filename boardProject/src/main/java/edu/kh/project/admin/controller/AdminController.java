package edu.kh.project.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.kh.project.admin.model.service.AdminService;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.dto.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
@RequestMapping("admin")
@RequiredArgsConstructor
@SessionAttributes({"loginMember"})
public class AdminController {
	
	private final AdminService service;
	
	@PostMapping("login")
	public Member login(@RequestBody Member inputMember, Model model) {
		
		Member loginMember = service.login(inputMember);
		
		if(loginMember == null) return null;
		
		model.addAttribute("loginMember", loginMember);
		return loginMember;
		
	}
	
	@GetMapping("logout")
	public ResponseEntity<String> logout(HttpSession session){
		
		// ResponseEntity
		// 비동기 요청 시 서버에서 응답값으로 주로 사용
		// spring에서 제공하는 Http 응답 데이터를 커스터마이징할 수 있도록 지원하는 클래스
		// > Http 상태 코드, 헤더, 응답 본문(body)을 모두 설정 가능
		
		try {
			session.invalidate(); // 세션 무효화 처리
			
			// status : http 상태코드 설정 부분
			// HttpStatus.OK : 상태코드 200으로 반환
			return ResponseEntity.status(HttpStatus.OK).body("로그아웃 완료");
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 에러
					.body("로그아웃 중 예외 발생" + e.getMessage());
		}
			
	}
	
	/** 관리자계정 발급 메서드
	 * @return
	 */
	@PostMapping("createAdminAccount")
	public ResponseEntity<String> createAdminAccount(@RequestBody Member member){
		
		try {
			
			// 1. 기존에 있는 이메일인지 검사
			int checkEmail= service.checkEmail(member.getMemberEmail());
			
			// 2. 있으면 발급 안 함	
			if(checkEmail > 0) {
				// HttpStatus.CONFLICT(409) : 요청이 서버의 현재 상태와 충돌할 때 사용
				// == 이미 존재하는 리소스(이메일) 때문에 새 리소스 생성 불가
				return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다");
			}
			
			// 3. 없으면 발급 > 비밀번호 반환
			 // HttpStatus.OK (200) : 요청이 정상 처리되었으나 기존 리소스에 대한 단순 처리 결과를 응답하는 것일 뿐임
			 // HttpStatus.CREATED(201) : 자원이 성공적으로 생성되었음을 나타냄
			
			String accountPw = service.createAdminAccount(member);
			return ResponseEntity.status(HttpStatus.CREATED).body(accountPw);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
						.body("관리자 계정 생성 중 	문제 발생(서버 문의 바람)");
		}	
	}
	
	/** 관리자 계정 목록 조회
	 * @return
	 */
	@GetMapping("adminAccountList")
	public ResponseEntity<Object> adminAccountList(){
		
		try {
			List<Member> adminList = service.adminAccountList();
			return ResponseEntity.status(HttpStatus.OK).body(adminList);
		
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	/** 최대 조회수 게시글 조회
	 * @return
	 */
	@GetMapping("maxReadCount")
	public ResponseEntity<Object> maxReadCount(){
		
		try {
			Board board = service.maxReadCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	/** 최대 좋아요 게시글 조회
	 * @return
	 */
	@GetMapping("maxLikeCount")
	public ResponseEntity<Object> maxLikeCount(){
		
		try {
			Board board = service.maxLikeCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		
	}
	
	/** 최대 댓글수 게시글 조회
	 * @return
	 */
	@GetMapping("maxCommentCount")
	public ResponseEntity<Object> maxCommentCount(){
		
		try {
			Board board = service.maxCommentCount();
			return ResponseEntity.status(HttpStatus.OK).body(board);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
		
	}
	
	@GetMapping("withdrawnMemberList")
	public ResponseEntity<Object> selectWithdrawnMemberList(){
		
		try {
			// 성공 시 List<Member> 반환, 에러 발생 시 String 반환 > ResponseEntity<Object> 
			List<Member> withdrawnMemberList = service.selectWithdrawnMemberList();
			return ResponseEntity.status(HttpStatus.OK).body(withdrawnMemberList);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴한 회원 목록 조회 중 에러 발생 : " + e.getMessage());
		}
		
	}
	
	@PutMapping("restoreMember")
	public ResponseEntity<String> restoreMember(@RequestBody Member member){
		
		try {
			int result = service.restoreMember(member.getMemberNo());
			if(result > 0) {
				return ResponseEntity.status(HttpStatus.OK).body(member.getMemberNo() + "번 회원 복구 완료");
			}else {
				// 400번 : BAD_REQUEST - 잘못된 요청 요청 구문이 잘못되었거나 유효하지 않음
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 memberNo : " + member.getMemberNo());
			}
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴한 회원 복구 중 에러 발생 : " + e.getMessage());
		}
		
	}
	
	
}
