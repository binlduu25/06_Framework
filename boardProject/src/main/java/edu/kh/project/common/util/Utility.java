package edu.kh.project.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

// 프로그램 전반적으로 사용될 만한 유용한 기능 모음
 // 메서드는 static으로 작성

public class Utility {
	
	// Sequence 번호로 쓸 필드 선언
	public static int seqNum = 1; // 1 ~ 99999 반복
	
	public static String fileRename(String originalFileRename) { 
		// 해당 메서드 호출 시 호출부에서 원본 파일 이름 넘겨줄 것. 매개변수로 원본파일명 받을 준비
		// 반환 시 확장자 부분을 추출해서 String 형태로 넘겨줄 것
		
		// SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 연, 월, 일, 시, 분, 초
		
		// 현재 시간 얻어오기
		 // java.util.Date() : 현재 시간을 저장한 자바 객체(Date 타입)
		 // SimpleDateFormat.format() : Date 타입을 받아 String 으로 변환
		String date = sdf.format(new Date());
			
		String number = String.format("%05d", seqNum); 
		// String.format : printf 와 비슷하게 사용하는 메서드
		// "%05d" : 다섯 칸 마련하고 0으로 채워둔다
		 // "-%05d"로 작성 시, "10000" 처럼 앞자리부터 작성되고, "%05d"처럼 작성 시 "00001" 처럼 뒷자리부터 값을 채운다
		// seqNum 을 넣었으므로, 1일 때는 00001, 2일 때는 00002 ... 처럼 될 것 		
		
		// 해당 메서드 호출되면 순서대로 코드 실행 후 다음 호출 위해 카운트 하나 더
		seqNum++;
		
		// 99999 까지 도달한다면 처음 번호로 초기화
		if(seqNum == 100000) seqNum = 1;
		
		// 확장자 구하기
		 // String.substring(인덱스) 
		  // 문자열을 인덱스부터 끝까지 잘라낸 결과 반환
		 // String.lastIndexOf("찾을 문자열")
		  // 문자열에서 "찾을 문자열"의 인덱스 반환
		
		String str = originalFileRename.substring(originalFileRename.lastIndexOf("."));
		// str > .jpg 등으로 반환될 것
	
		return date + "_" + number + str;
		//20251216135630_00001.jpg 같은 형태로 반환될 것
	}
	

}
