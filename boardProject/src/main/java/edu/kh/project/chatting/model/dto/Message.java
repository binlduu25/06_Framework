package edu.kh.project.chatting.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {

	private int messageNo; // 메시지 번호 
	private String messageContent; // 메시지 내용
	private String readFl; // 읽음 여부
	private int senderNo; // 보낸 회원 번호
	private int chattingRoomNo; // 채팅방 번호
	private String sendTime; // 메시지 발송 시간
	
}
