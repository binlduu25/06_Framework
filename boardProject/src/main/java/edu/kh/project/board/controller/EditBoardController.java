package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import edu.kh.project.BoardProjectApplication;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.BoardServiceImpl;
import edu.kh.project.board.model.service.EditBoardService;
import edu.kh.project.member.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("editBoard")
@Slf4j
@RequiredArgsConstructor
public class EditBoardController {

    private final BoardServiceImpl boardServiceImpl;

    private final BoardProjectApplication boardProjectApplication;

    private final BoardController boardController;

	private final EditBoardService service;
	
	private final BoardService boardService;
	
	/** 글쓰기 버튼 눌렀을 시 단순히 글쓰기 페이지로 이동시켜주는 controller
	 * @param boardCode
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/insert")
	// @PathVariable : 주소에 들어오는 값을 변수로 사용하겠다.
	
	public String boardInsert(@PathVariable ("boardCode") int boardCode){
		return "board/boardWrite";
	}
	
	/** 게시글 작성 메서드
	 *  - 오버로딩
	 * @param boardCode : 게시판번호
	 * @param inputBoard : 입력된 값, 제목 등이 세팅된 Board객체(커맨드객체)
	 * @param loginMember : 로그인된 회원 정보(세션에 등록되어 있음)
	 * @param images : 제출된 file 타입 input 태그가 전달한 데이터들 (이미지 파일..)
	 * @param ra : 메시지 실어보낼 객체
	 * 
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode,
							  @ModelAttribute Board inputBoard,
							  @SessionAttribute("loginMember") Member loginMember,
							  @RequestParam("images") List<MultipartFile> images,
							  RedirectAttributes ra
							  ) throws IllegalStateException, IOException {
		/*
		먼저 inputBoard 와 images 를 어떻게 처리할지가 관건
		이미지가 제출되지 않았을 경우 List 안에 MultipartFile 객체가 5개 들어가 있지만 비어있을 뿐
		[MultipartFile, MultipartFile, MultipartFile, MultipartFile, MultipartFile]
		즉, 등록 시 실제 제출 여부와 무관하게 길이 5개(0~4) MultipartFile 의 List 제출됨
		 - ex1) 5개 모두 업로드 x : 0~4번까지 인덱스는 있으나 비어 있는 list
		 - ex2) 2번 인덱스(3번째) 만 제출 시 : 2번 인덱스에만 MultipartFile, 나머지 0,1,3,4번 인덱스에는 비어있음
		따라서 무작정 서버에 저장할 게 아니라
		실제로 제출된 파일이 있는지 확인하는 로직 구성 필요
		list 요소의 index 번호 == DB 상 IMG_ORDER와 같다
		
		log.debug("images" + images); */
		
		// 1-1. 어느 게시판에 들어갈 글인가?
		 // > boardCode 를 inputBoard에 넣기
		// 1-2. 누가 쓴 글인가?
		 // loginMember의 memberNo 꺼내와서 inputBoard 에 넣기 
		
		// 따라서
		inputBoard.setBoardCode(boardCode);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		// > inputBoard 에 총 4가지가 세팅됨(boardTitle, boardContent, boardCode, memberNo)
		
		// 2. 서비스 호출 후 결과 반환 받기
		// 성공 시, 상세 조회 페이지로 redirect
		 // 삽입된 '게시글 번호(boardNo)'를 받아와야 재요청이 가능
		int boardNo = service.boardInsert(inputBoard, images);
		
		// 3. 서비스 결과에 따라 message, redirect 경로 지정
		String path = null;
		String message = null;
		
		if(boardNo > 0){
			
			path = "/board/" + boardCode + "/" + boardNo;
			message = "게시글 작성 완료";
			
		}else {
			
			path = "insert"; // 현재 경로는 /editBoard/1/insert 이런 식임
							 // 해당 경로가 상대경로 작성방식이기에 맨 뒤의 주소 레벨을 insert로 갈아끼움
			 				 // 즉, 경로 변화 없음. => Get 방식 redirect (게시글 작성 다시 시도)
			
			message = "게시글 작성 실패";
			
		}
		
		ra.addFlashAttribute("message", message);	
		return "redirect:" + path;
		
	}
	
	/** 게시글 수정 화면으로 들어가는 요청 처리(GET)
	 * @param boardCode
	 * @param boardNo
	 * @param loginMember
	 * @param model
	 * @param ra
	 * @return
	 */
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo,
							  @SessionAttribute("loginMember") Member loginMember,
							  Model model,
							  RedirectAttributes ra) {
		
		// 수정 전 화면(게시글) 페이지의 정보를 가져와 뿌려주어야 함
		// -> 게시글 상세 조회 서비스 이요
		
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// BoardService.selectOne(map) 호출 결과값 반환 받기(게시판 상세 조회 화면)
		Board board = boardService.selectOne(map);
		
		String message = null;
		String path = null;
		
		if(board == null) { // boardNo와 boardCode 를 사용자가 주소창에서 임의로 조작할 수 있기에 해당 조건 필요(게시글이 아예 없는 경우)
			message = "해당 게시글이 존재하지 않습니다";
			path = "redirect:/";
			
			ra.addFlashAttribute("message", message);
		}
		
		else if(board.getMemberNo() != loginMember.getMemberNo()) { // boardNo와 boardCode가 일치하는 게시글이 있지만 사용자가 작성한 글이 아닌 경우로 접근하는 경우
			message = "자신이 작성한 글만 수정할 수 있습니다.";
			
			// 해당 글 상세조회 화면으로 리다이렉트
			path = String.format("redirect:/board/%d/$d", boardCode, boardNo);
			
			ra.addFlashAttribute("message", message);
			
		} else {
			
			// forward : templates/board/boardUpdate.html
			path = "board/boardUpdate";
			model.addAttribute("board", board);
			
		}
		
		return path;
	}
	
	/** 게시글 수정화면에서 나오는 요청 처리(POST)
	 *  게시글 수정 서비스
	 * @return
	 */
	/**
	 * @param boardCode
	 * @param boardNo
	 * @param inputBoard : 커맨드 객체의 역할(html에서 넘어온 데이터를 Board dto 를 통해 처리
	 * @param images : 제출된 type = "file" 인 모든 요소
	 * @param deleteOrderList : js 단에서 deleteOrderList 배열을 String 으로 변화해서 가져옴, 삭제된 이미지 순서가 기록된 문자열
	 * @return
	 */
	@PostMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo,
							  @ModelAttribute Board inputBoard,
							  @RequestParam("images") List<MultipartFile> images,
							  @RequestParam(value = "deleteOrderList", required = false) String deleteOrderList, // 없어도 되지만 없으면 null
							  @RequestParam(value = "cp", required = false, defaultValue = "1") int cp, // 없어도 되지만 없으면 기본값은 1
							  @SessionAttribute("loginMember") Member loginMember,
							  RedirectAttributes ra) throws Exception{
							  
		// 1. inputBoard 에 boardCode, boardNo, memberNO 세팅
		inputBoard.setBoardCode(boardCode);
		inputBoard.setBoardNo(boardNo);
		inputBoard.setMemberNo(loginMember.getMemberNo());
		
		// 2. 게시글 수정 서비스 호출 후 결과 반환 받기
		int result = service.boardUpdate(inputBoard, images, deleteOrderList);
		
		
		return "redirect:";
	}
	
	
}
