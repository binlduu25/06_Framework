package edu.kh.project.mypage.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;

@Mapper
public interface MyPageMapper {

	int updateInfo(Member inputMember);

	String selectPw(int memberNo);

	int changePw(Map<String, Object> paramMap);

	/** 회원 탈퇴 SQL
	 * @param memberNo
	 * @return
	 */
	int secession(int memberNo);

	/** 파일 정보 DB에 삽입
	 * @param uf
	 * @return
	 * @throws Exception
	 */
	int insertUploadFile(UploadFile uf);

	List<UploadFile> fileList(int memberNo);

	/** 프로필 이미지 변경 sql
	 * @param member
	 * @return
	 */
	int profile(Member member);

}
