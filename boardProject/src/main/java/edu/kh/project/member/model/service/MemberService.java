package edu.kh.project.member.model.service;

import java.util.List;

import edu.kh.project.member.dto.Member;

public interface MemberService {

	/** 로그인 서비스
	 * @param inputMember
	 * @return loginMember
	 */
	Member login(Member inputMember) throws Exception;

	/** 이메일 중복검사 서비스
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return 중복 1, 아님 0
	 */
	int checkNickname(String memberNickname);

	/** 회원가입 최종 서비스
	 * @param inputMember
	 * @param memberAddress
	 * @return
	 */
	int signup(Member inputMember, String[] memberAddress);

	List<Member> selectMember();

}