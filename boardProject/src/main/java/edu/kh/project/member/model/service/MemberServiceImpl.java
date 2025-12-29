package edu.kh.project.member.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class MemberServiceImpl implements MemberService{
	
	// BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private MemberMapper mapper;
	
	// 로그인 서비스 
	@Override
	public Member login(Member inputMember) throws Exception {
		
		// 데이터 가공 필요
		 /* 비밀번호 암호화 필요
		  
		  - 개인정보 및 비밀번호 등은 암호화 하여 DB에 저장
		 	
		 	1) sha 암호화 방식
		 	   : A회원과 B회원이 비밀번호를 똑같이 설정했을 시 암호화된 결과가 같기에 요즘엔 잘 사용 X
		 	   : 레인보우 테이블
		 	
		 	2) Bcrypt 암호화 패턴
		 	   : salt
		 	   : 암호화된 버전을 DB에 저장
		 	   : 하지만 이 경우 DB에 저장된 것과 클라이언트가 입력한 평문의 비밀번호가 일치하지 않게 되기 때문에 구동에 오류 발생
		 	   : 따라서 DB에서 암호화된 비밀번호를 JAVA 에 가져와 Bcrypt 라는 객체가 둘을 비교하여야 한다. */
		
		// 암호화 진행
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
		
		// user01 의 비밀번호 변경 겸 테스트
		// String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		// log.debug("tt1 : " + bcryptPassword);
		
		
		
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원의 (+비밀번호) 조회 
		 // 다른 정보는 굳이 가져갈 필요 없이 email 정보만 가져간다
		Member loginMember =  mapper.login(inputMember.getMemberEmail());
		
		// 2. 일치하는 이메일이 없어서 결과가 null 일 경우
		if(loginMember == null) return null;
		
		// 3. 클라이언트로 입력받은 평문 비밀번호(inputMember)와 DB에서 가져온 암호화된 비밀번호가 같은지 일치하는지 확인
		 // bcrypt.matches(평문, 암호화된 문자) : 일치하면 true, 불일치하면 false
			
			// 3-1) 일치하지 않을 시
			if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) return null;
			
			// 3-2) 일치할 시
			 // 로그인된 회원 정보를 session 담을 예정이고, 이때 암호화된 pw 가 client 에게 전달되지 않도록 주의해야 한다.
			 // 따라서 저달 전 pw 에 null 대입
			loginMember.setMemberPw(null);
			
			
			
			return loginMember;
		
	}
	
	
	@Override
	public int checkEmail(String memberEmail) {
		
		return mapper.checkEmail(memberEmail);
	}
	
	@Override
	public int checkNickname(String memberNickname) {
		return mapper.checkNickname(memberNickname);
	}
	
	/**
	 * 회원가입 최종 서비스
	 */
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		// 1. 주소 배열을 하나의 문자열로 가공
		 // 현재 주소는 inputMember 와 memberAddress 에서 둘 다 받고 있다.

		 // 주소가 입력되지 않았다면, 
		 // input.getMemberAddress() 는 ",," 로, 
		 // memberAddress 는 [,,] 로 넘어온다.
		
		// 주소가 입력된 경우
		if(!inputMember.getMemberAddress().equals(",,")) {
			
			// String.join("구분자", 배열)
			// 전달된 배열의 모든 요소 사이에 "구분자"를 추가하여 하나의 문자열로 만들어 반환하는 메서드
			
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else { // 주소가 입력되지 않은 경우
			inputMember.setMemberAddress(null);
		}
		
		// 2. 비밀번호 암호화
		// inputMember 안의 memberPw : 평문
		// 암호화 후 inputMember 에 세팅
		
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// 회원가입 매퍼 호출
		return mapper.signup(inputMember);
	}
	
	@Override
	public List<Member> selectMember() {
	
		return mapper.selectMember();
	}
	
}
