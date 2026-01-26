package edu.kh.project.admin.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import edu.kh.project.admin.model.mapper.AdminMapper;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.common.util.Utility;
import edu.kh.project.member.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

	private final AdminMapper mapper;
	private final BCryptPasswordEncoder bcrypt;
	
	/**
	 * 관리자 로그인 서비스
	 */
	@Override
	public Member login(Member inputMember) {
		
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		if(loginMember == null) return null;
		
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;
		
		loginMember.setMemberPw(null);
		return loginMember;
	}
	
	/**
	 * 관리자 이메일 생성 시 기존 이메일과 중복 여부 검사
	 */
	@Override
	public int checkEmail(String memberEmail) {
		return mapper.checkEmail(memberEmail);
	}
	
	/**
	 * 관리자 계정 발급 서비스
	 */
	@Override
	public String createAdminAccount(Member member) {
		
		// 1. 영어(대소문자) + 숫자 포함 6자리 난수로 만든 비밀번호 평문 / 암호화 한 값 구하기
		String rawPw = Utility.generatePassword(); // 평문 비밀번호
		
		// 2. 평문 비밀번호 암호화 후 저장
		String encPw = bcrypt.encode(rawPw);
		
		// 3. member에 암호화된 비밀번호 세팅
		member.setMemberPw(encPw);
		
		// 4. DB에 암호화된 비밀번호가 세팅된 MEMBER 전달하여 계정 발급
		int result = mapper.createAdminAccount(member);
		
		// 5. 계정 발급(insert) 정상 처리되었으면 발급된 비밀번호(평문) 리턴
		if(result > 0) {
			return rawPw;
		}else {
			return null;
		}
		
	}
	
	/**
	 * 관리자 계정 목록 조회 서비스
	 */
	@Override
	public List<Member> adminAccountList() {
		return mapper.adminAccountList();
	}
	
	/**
	 * 최대 조회수 게시글 조회
	 */
	@Override
	public Board maxReadCount() {
		return mapper.maxReadCount();
	}
	
	/**
	 * 최대 좋아요 게시글 조회
	 */
	@Override
	public Board maxLikeCount() {
		return mapper.maxLikeCount();
	}
	
	/**
	 * 최대 댓글수 게시글 조회
	 */
	@Override
	public Board maxCommentCount() {
		return mapper.maxCommentCount();
	}
	
	/**
	 * 탈퇴한 회원 목록 조회
	 */
	@Override
	public List<Member> selectWithdrawnMemberList() {
		return mapper.selectWithdrawnMemberList();
	}
	
	/**
	 * 탈퇴 회원 복구
	 */
	@Override
	public int restoreMember(int memberNo) {
		return mapper.restoreMember(memberNo);
	}
	

	
}
