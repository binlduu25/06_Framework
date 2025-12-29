package edu.kh.project.member.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.dto.Member;

@Mapper
public interface MemberMapper {

	/** 로그인 SQL 실행
	 * @param memberEmail
	 * @return loginMember
	 */
	Member login(String memberEmail) throws Exception;

	/** 이메일 중복 검사 SQL 실행
	 * @param memberEmail
	 * @return
	 */
	int checkEmail(String memberEmail);

	int checkNickname(String memberNickname);

	int signup(Member inputMember);

	List<Member> selectMember();
	
}