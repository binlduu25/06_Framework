package edu.kh.project.board.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.controller.MemberController;
import edu.kh.project.member.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("board")
@Controller
@Slf4j
public class BoardController {

    private final MemberController memberController;

	@Autowired
	private BoardService service;


    BoardController(MemberController memberController) {
        this.memberController = memberController;
    }
	
	
	// /board/1
	// /board/2
	// /board/3 
	// 이처럼 boardType의 숫자만 변경될 뿐 해야 하는 일은 board 목록을 조회하는 게 같다
	// > path variable 이용
	
	
	/** 게시글 목록 조회 메서드
	 * getMapping 
	 * 
	 * {boardCode}
	 *  - /board/xxx : /board 이하 1레벨 자리에 어떤 주소값이 들어오든 모두 이 메서드에 매핑
	 * 
	 * {boardCode:[0-9]+}
	 *  - /board 이하 1레벨 자리에 숫자로 된 요청 주소가 작성되어 있을 때만 동작하게 한다
	 *  - 정규표현식을 이용
	 *   - [0-9] : 1자리 숫자만 이용 가능
	 *   - [0-9]+ : 모든 숫자 이용 가능
	 *   
	 *   > 매개변수에 @pathVariable 을 작성해주어야 받을 수 있다.
	 *   > board/1 로 요청이 온다면 boardCode 에 1이,
	 *   > board/2 로 요청이 온다면 boardCOde 가 2가 들어간다.
	 *   > 또한 pathVariable 사용 시 자동으로 requestScope 에 담긴다. 
	 *   
	 *   @RequestParam(value = "cp", required = false, defaultValue = "1") int cp)
	 *   - 게시판 제목을 바로 클릭해서 들어갈 시 /board/1 형태로 끝이 나지만
	 *   - 해당 화면에서 2페이지, 또는 2페이지에서 1페이지 클릭 시
	 *   - /board/1?cp=2, /board/1?cp=1 등으로 주소가 들어오게 한다.
	 *    - 따라서 requestParam 이용하여 cp 라는 parameter(현재 조회 요청한 페이지) 를 받아오고, 필수는 아니며, 
	 *    - 기본값은 1로 지정하여 1페이지가 나오도록 하며, int형 변수 cp로 설정한다.
	 *   
	 *   @Model 
	 *   - 포워드할 때 게시판 목록을 담아야 하기 때문에 dto 객체를 활용해야 하므로 사용
	 *   
	 *   @RequestParam Map<String, Object> paramMap
	 *   - 제출된 parpameter 가 모두 저장됨(검색 시 key 와 query 담겨 있다)
	 *   - 검색 시 url : /board/1?key=t&query=폭탄
	 *   - 위 형태로 나올 것이며 이를 위해 한번에 묶어서 가져올 수 있도록 Map 사용
	 *   - 검색하지 않으면 board/1 그대로 있음 
	 *   - 검색 시 key=t, query=폭탄
	 *   - 검색 x 시 {}
	 *   
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}")  
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
								  @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
								  Model model,
								  @RequestParam Map<String, Object> paramMap) {
		
		// 조회 서비스 호출 후 결과 반환
		
		// 먼저 Map 변수 설정
		 // 하단에서 조건문 결과에 따라 map 이 호출할 servie 메서드 다르게 설정할 예정
		Map <String, Object> map = null;
		 
		// 1. 검색이 아닌 경우 : 매개변수로 들어온 paramMap 이 비어 있을 것(key와 query 없음(NULL)) : {} 형태
		if(paramMap.get("key") == null) { 
		
			// 게시글 목록 조회 서비스 호출
			map = service.selectBoardList(boardCode, cp);
			
		// 2. 검색인 경우
		}else {
			
			// --> paramMap 에서 검색 정보를 2개 가져옴
			// --> ex) 
			 // key(key)=w(value) : 검색할 종류(제목, 작성자 등)
			 // query(key)=짱구(value) : 사용자가 입력한 검색값
			
			// boardCode 를 paramMap 에 추가해야 함
			paramMap.put("boardCode", boardCode);
			// -> 이제 paramMap은 다음과 같은 형식임 {key=w, query=짱구, boardCode=1}
			
			// 검색 서비스 호출 (검색하고 싶은 게시글 조회)
			map = service.searchList(paramMap, cp); // 검색 목록에 맞춘 pagination 객체 생성하기 위해 cp도 전달
		}
		
		// model 에 결과값 등록
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		
		// src/main/resources/templates/board/boardList.html 로 forward
		return "board/boardList"; 
	}
	
	
	// 상세 조회 요청 주소
	// board/1/212
	// board/2/500 ...
	/** 게시글 상세 조회 화면 controller
	 * !!!!!!! 현재 작동은 되지만 썸네일 이미지 1개가 일반 이미지로도 들어가 있음
	 * @param boardCode
	 * @param boardNo
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo,
							  @SessionAttribute(value = "loginMember", required = false) Member loginMember,
							  // 게시글은 로그인하지 않아도 접근이 가능하기 때문에 required 를 false 로 한다.
							  // 만약 해당 설정 해주지 않을 시 로그인하지 않은 회원은 게시글 접근 불가
							  Model model,
							  RedirectAttributes ra,
							  HttpServletRequest req, // 요청에 담긴 쿠키 얻어오기
							  HttpServletResponse resp // 새로운 쿠키 만들어서 응답할 때 사용할 resp
							  ) {
		
		// 게시글 상세 조회 서비스 호출
		
		// 전달할 파라미터가 무엇일까?
		// 1) Map으로 전달할 파라미터 묶기
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// 2) 로그인 상태인 경우에만 map 에 memberNo 추가
		 // LIKE_CHECK 시 이용(로그인한 사람이 좋아요 누른 게시글인지 체크하기 위해서)
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		// 3) 서비스 호출
		 // 게시글 상세 조회 페이지에 뿌려줄 데이터 전체를 가지고 와야 한다
		Board board = service.selectOne(map);
		
		// log.debug("조회된 board : " + board);
		
		String path = null;
		
		// 조회 결과가 없는 경우
		if(board == null) {
			
			path = "redirect:/board/" + boardCode;
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
			
		} else { // 조회 결과가 있는 경우
			
			/* @@@@@@@@@@@@@@@@@ 쿠키를 이용한 조회수 증가 시작 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
			
			// 내가 쓴 글은 조회수 증가할 수 없도록 설정해보기
			// 비회원 또는 로그인한 회원의 글이 아닌 경우. 즉, 글쓴이를 제외한 다른 사람만 조회수 증가하도록 
			if(loginMember == null || board.getMemberNo() != loginMember.getMemberNo()) {
				
				// 요청에 담긴 모든 쿠키 얻어오기
				 // jakarta.servlet.http.cookie
				Cookie[] cookies = req.getCookies(); // 여러 가지 값이 들어 있을 것.
				
				Cookie c = null; // 요청에 담긴 cookies 배열 중 readBoardNo 만을 가져와 c 에 저장할 용도로 변수 선언
				
				// if(cookies != null) { // 브라우저에 쿠키가 없을 경우에 대한 예외처리?? : 수업시간엔 XXX
				 // > js 파일 없어서 작동안된 거 같음. 확인 필요
				 // > js 파일 있다면 해당 코드 필요 없음
					for(Cookie temp : cookies) {
						
						// cookie 중에 "readBoardNo"가 존재할 때
						if(temp.getName().equals("readBoardNo")) {
							c = temp;
							break;
						}
					}
				// }
				
				// 이 상태에서 c 는 여전히 null 이거나 담겨 있거나
				
				int result = 0; // 조회수 증가 결과 저장 변수
				
				// "readBoradNo" 가 쿠키에 없을 때
				
				if(c == null) {
					
					// 새 쿠키 생성("readBoardNo", [게시글번호])
					c = new Cookie("readBoardNo", "[" + boardNo + "]");
					result = service.updateReadCount(boardNo);
					
				}
				
				else { // "readBoardNo" 가 쿠키에 있을 때
					   //       k         :          v
					   //  readboardNo    :   [5][24][201][432]....
					
					// 현재 글을 처음 읽는 경우
					 // getValue 통해 readBoardNo(문자열) 꺼내오고 indexOf() 매개변수 안의 문자가 몇 번째에 있는지 반환
					 // 못 찾으면 -1 반환
					if(c.getValue().indexOf("[" + boardNo + "]") == -1) {
						
						// 해당 글 번호를 쿠키에 누적 + 서비스 호출
						c.setValue(c.getValue() + "[" + boardNo + "]");
						// readboardNo : [5][24][201][432].... + [현재 boardNo]
						result = service.updateReadCount(boardNo);
					}
					
				}
				
				// 조회 수 증가 / 조회 성공 시
				if(result > 0) {
					
					// 앞서 조회했던 board의 readCount 값을 얻어온 result 값으로 다시 세팅
					board.setReadCount(result);
					
					// 쿠키 적용 경로 설정
					 // "/" 이하 경로 요청 시 쿠키 서버로 전달 
					c.setPath("/");
					
					// 쿠키 수명 지정 (여기선 현재 시간 기준 다음날 자정까지)
					 // 현재 시간 얻어오기
					LocalDateTime now = LocalDateTime.now();
					
					 // 다음날 자정 지정(현재 시간 기준 하루 후의 자정)
					LocalDateTime nextDayMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
					
					// 다음날 자정까지 남은 시간 계산 (초단위)
					long secondsUntilNextDay = Duration.between(now, nextDayMidnight).getSeconds();
					
					// 쿠키 수명 설정
					c.setMaxAge((int)secondsUntilNextDay); 
					
					resp.addCookie(c); // 응답 객체를 이용해서 클라이언트에게 전달
					
				}
				
			}
			
			
			/* @@@@@@@@@@@@@@@@@ 쿠키를 이용한 조회수 증가 끝 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
			
			path = "board/boardDetail";
			
			// src/main/resources/templates/board/boardDetail.html 로 forward
			
			// board 객체 안에 전부 들어있다.
			// model 통해 requestScope로 실어 전달
			model.addAttribute("board", board);
		
			// 몇 가지 생각해야 함
			// 썸네일 없이 이미지만 있는 경우, 썸네일 포함 이미지가 있는 경우, 썸네일 및 사진 전부 없이 내용만 있는 경우 등
			
			// 1) 조회된 이미지 목록(imageList)이 있을 경우
			if(!board.getImageList().isEmpty()) {
				
				BoardImg thumbnail = null;
				
				// imageList의 0번 인덱스는 IMG_ORDER가 가장 빠른 순서
				
				// 만약 이미지 목록 0번째 요소의 IMG_ORDER == 0 일 시, 해당 이미지는 썸네일임
				if(board.getImageList().get(0).getImgOrder() == 0) {
					thumbnail = board.getImageList().get(0);
				}
				
				// thumbnail 변수에는,
				// 이미지 목록의 0번째 요소가 썸네일이라면 썸네일 이미지의 BoardImg 객체
				// 썸네일이 아니라면 null
				model.addAttribute("thumbnail", thumbnail);
				model.addAttribute("start", thumbnail != null ? 1 : 0); // thumbnail 이 null 이 아니라면 1, null 이라면 0을 start 에다 담아 보냄
				
				// 썸네일 없는 경우 : 일반 이미지만 있거나, 등록된 이미지가 아예 없을 때임 : start = 0
				
			}
			
		}
		
		return path;
	}
	
	
	/** 게시글 좋아요 체크/해제 (비동기)
	 * 
	 */
	@ResponseBody
	@PostMapping("like")
	public int boardLike(@RequestBody Map<String, Integer> map){
		return service.boardLike(map); 
	}
	
	
	
	
	
}
