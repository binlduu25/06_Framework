package edu.kh.project.admin.model.service;

import java.util.List;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.member.dto.Member;

public interface AdminService {

	Member login(Member inputMember);

	int checkEmail(String memberEmail);

	String createAdminAccount(Member member);

	List<Member> adminAccountList();

	Board maxReadCount();

	Board maxLikeCount();

	Board maxCommentCount();

	List<Member> selectWithdrawnMemberList();

	int restoreMember(int memberNo);

}
