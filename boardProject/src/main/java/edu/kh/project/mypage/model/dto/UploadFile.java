package edu.kh.project.mypage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본생성자
@AllArgsConstructor // 매개변수 생성자
@Builder

public class UploadFile {

	private int fileNo;
	private String filePath;
	private String fileOriginalName;
	private String fileRename;
	private String fileUploadDate;
	private int memberNo;
	
	// DTO 작성 시 관련된 테이블 컬럼과 반드시 동일하게 이름을 일치시키지 않아도 된다.
	// 필요에 따라 필드 추가 및 삭제가 가능하다
	
	private String memberNickname;
	// Member DTO 
	
	
}
