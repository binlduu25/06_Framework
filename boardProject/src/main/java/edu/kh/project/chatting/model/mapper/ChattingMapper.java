package edu.kh.project.chatting.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.chatting.model.dto.ChattingRoom;
import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.member.dto.Member;

@Mapper
public interface ChattingMapper {

	List<ChattingRoom> selectRoomList(int memberNo);

	List<Member> selectTarget(Map<String, Object> map);

	int checkChattingRoomNo(Map<String, Integer> map);

	int createChattingRoom(Map<String, Integer> map);

	List<Message> selectMessageList(Object chattingRoomNo);

	/** 채팅 메시지 모두 읽음 처리 SQL
	 * @param paramMap
	 * @return
	 */
	int updateReadFlag(Map<String, Object> paramMap);

	/** 채팅 메시지 insert SQL
	 * @param msg
	 * @return
	 */
	int insertMessage(Message msg);


}
