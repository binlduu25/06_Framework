package edu.kh.project.chatting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.kh.project.chatting.model.dto.ChattingRoom;
import edu.kh.project.chatting.model.dto.Message;
import edu.kh.project.chatting.model.service.ChattingService;
import edu.kh.project.member.dto.Member;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("chatting")
public class ChattingController {

	@Autowired
	private ChattingService service;
	
	/** 채팅 목록 조회 및 페이지 전환
	 * @return
	 */
	@GetMapping("list")
	public String chatting(@SessionAttribute("loginMember") Member loginMember,
			Model model) {
		
		List<ChattingRoom> roomList = service.selectRoomList(loginMember.getMemberNo());
		model.addAttribute("roomList", roomList);
	
		return "chatting/chatting";
	}
	
	
	/** 채팅 상대 검색 메서드 - 비동기
	 * @return
	 */
	@GetMapping("selectTarget")
	@ResponseBody
	public List<Member> selectTarget(@RequestParam("query") String query,
									 @SessionAttribute("loginMember") Member loginMember){
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", loginMember.getMemberNo());
		map.put("query", query);

		return service.selectTarget(map);
		
	}
	
	/** 채팅방 입장 및 생성 - 비동기
	 * @return
	 */
	@ResponseBody
	@GetMapping("enter")
	public int chattingEnter(@RequestParam("targetNo") int targetNo, @SessionAttribute("loginMember") Member loginMember) {
		
		Map<String, Integer> map = new HashMap<>();
		map.put("targetNo", targetNo);
		map.put("loginMemberNo", loginMember.getMemberNo());
		
		// 채팅방 번호 체크 서비스 호출 및 반환 (기존 생성된 방이 있는지)
		int chattingNo = service.checkChattingNo(map);
		// 반환받은 채팅방 결과값 0일 시 기존의 채팅방 없다는 뜻이므로 생성
		if(chattingNo == 0) {
			chattingNo = service.createChattingRoom(map);
		}
		
		return chattingNo;
	}
	
	/** 채팅방 목록 조회 - 비동기
	 * @return
	 */
	@ResponseBody
	@GetMapping("roomList")
	public List<ChattingRoom> selectRoomList(@SessionAttribute("loginMember") Member loginMember){
		return service.selectRoomList(loginMember.getMemberNo());
		
	}
	
	// 메시지 목록 조회 - 비동기
	@GetMapping("selectMessage")
	@ResponseBody
	public List<Message> selectMessageList(@RequestParam Map<String, Object> paramMap){
		return service.selectMessageList(paramMap);
		
		// *** @RequestParam을 이용한 Map 사용시 K,V 무조건 String ***
		// -> 컴파일 오류가 없어도 형변환 불가하여 오류 발생
		
		//log.debug("test : {}", paramMap.get("chattingRoomNo").getClass());
		// -> class java.lang.String
		
		//int chattingRoomNo = Integer.parseInt(paramMap.get("chattingRoomNo"));
		// 형변환 불가 : 바인딩 충돌 관련 오류 때문에
		// Integer.parseInt() 은 무조건 String 을 원함
		// -> 하지만 paramMap.get("chattingRoomNo") 은 
		// 컴파일 시 Integer / 런타임시 String형임
		
		// 무조건 형변환하고싶다면 아래처럼 가능하긴함
		// int chattingRoomNo 
		// 		= Integer.parseInt(String.valueOf(paramMap.get("chattingRoomNo")));
		
	}
	
	
	/** 채팅 읽음 표시 메서드 - 비동기
	 * @return
	 */
	@PutMapping("updateReadFlag")
	@ResponseBody
	public int updateReadFlag(@RequestBody Map<String, Object> paramMap) {
		return service.updateReadFlag(paramMap);
	}
	
	
	
}
