package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getter + setter + toString
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 초기화용 매개변수 생성자

public class Member { // paramTest4 의 DTO
	
	private String memberId;
	private String memberPw;
	private String memberName;
	private int memberAge;

}
