package edu.kh.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// dto
// 데이터 전달용 객체
// DB 에 조회된 결과 또는 SQL 구문에 사용할 값을 전달하는 용도
// 관련성 있는 데이터를 한번에 묶어서 다룸


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Member {

	private int memberNo;
	private String memberEmail;
	private String memberPw;
	private String memberNickname;
	private String memberTel;
	private String memberAddress;
	private String profileImg;
	private String enrollDate;
	private String memberDelFl;
	private int authority;
	
}
