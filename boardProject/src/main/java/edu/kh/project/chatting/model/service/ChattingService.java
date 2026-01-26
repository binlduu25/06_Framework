package edu.kh.project.chatting.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.chatting.model.dto.ChattingRoom;
import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.member.dto.Member;

public interface ChattingService {

	List<ChattingRoom> selectRoomList(int memberNo);

	List<Member> selectTarget(Map<String, Object> map);

	int checkChattingNo(Map<String, Integer> map);

	int createChattingRoom(Map<String, Integer> map);

	List<Message> selectMessageList(Map<String, Object> paramMap);

	int updateReadFlag(Map<String, Object> paramMap);

	int insertMessage(Message msg);



}
