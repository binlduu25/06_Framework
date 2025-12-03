package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

// Spring EL 같은 경우 DTO 객체 출력 시 getter 가 필수로 작성되어 있어야 함
public class Student {
	
	private String studentNo;
	private String name;
	private int age;

}

