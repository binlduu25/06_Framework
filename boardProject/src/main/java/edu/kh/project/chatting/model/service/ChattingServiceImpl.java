package edu.kh.project.chatting.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.chatting.model.dto.ChattingRoom;
import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.chatting.model.mapper.ChattingMapper;
import edu.kh.project.member.dto.Member;

@Service
@Transactional(rollbackFor = Exception.class)
public class ChattingServiceImpl implements ChattingService{
	
	@Autowired
	private ChattingMapper mapper;
	
	/**
	 * 채팅방 목록 조회
	 */
	@Override
	public List<ChattingRoom> selectRoomList(int memberNo) {
		return mapper.selectRoomList(memberNo);
	}
		
	/**
	 * 채팅 상대 검색 서비스
	 */
	@Override
	public List<Member> selectTarget(Map<String, Object> map) {
		return mapper.selectTarget(map);
	}
	
	/**
	 * 기존 채팅방 no 체크 서비스
	 */
	@Override
	public int checkChattingNo(Map<String, Integer> map) {
		return mapper.checkChattingRoomNo(map);
	}
	
	/**
	 * 새 채팅방 생성 서비스
	 */
	@Override
	public int createChattingRoom(Map<String, Integer> map) {
		
		// 생성된 채팅방의 번호를 받아올 것
		int result = mapper.createChattingRoom(map);
		
		if(result > 0) {
			return map.get("chattingRoomNo");
		}
		
		return 0;
	}
	
	
	/**
	 * 채팅 메시지 조회 서비스
	 */
	@Override
	public List<Message> selectMessageList(Map<String, Object> paramMap) {
		
		List<Message> messageList = mapper.selectMessageList(paramMap.get("chattingRoomNo"));
		
		if(!messageList.isEmpty()) { // 해당 채팅방에 나눈 메시지가 있다면
			int result = mapper.updateReadFlag(paramMap); // 메시지 모두 읽음처리
			
		}
		
		return messageList;
	}

	
	/**
	 * 채팅 읽음 표시 업데이트
	 */
	@Override
	public int updateReadFlag(Map<String, Object> paramMap) {
		return mapper.updateReadFlag(paramMap);
	}
	
	/**
	 * 채팅 입력 서비스
	 */
	@Override
	public int insertMessage(Message msg) {
		return mapper.insertMessage(msg);
	}
	
}
