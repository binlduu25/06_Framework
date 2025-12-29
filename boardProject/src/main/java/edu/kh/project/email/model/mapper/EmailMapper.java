package edu.kh.project.email.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailMapper {

	
	// --------------------------------------
	/** 기존 이메일에 대한 인증키 수정
	 * @param map
	 * @return int 행 갯수
	 */
	int updateAuthKey(Map<String, String> map);

	/** 이메일과 인증번호 새로 삽입
	 * @param map
	 * @return int 행 갯수
	 */
	int insertAuthKey(Map<String, String> map);
	// --------------------------------------

	/** 이메일과 인증번호로 확인 
	 * @param email
	 * @return
	 */
	int checkAk(Map<String, String> map);
}
