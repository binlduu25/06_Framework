package edu.kh.project.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;

// @Controller : Controller 명시 + Bean 등록
// @ResponseBody : 응답 본문으로 응답데이터 자체를 반환 
 // 지금까지는 비동기 요청 시 컨트롤러 단에 위 두 어노테이션을 사용했지만,
 // 이번엔 위 두 어노테이션을 합친 기능의 어노테이션을 사용 

@RestController
// @RestController : RestAPI 구축을 위해 사용하는 컨트롤러
//					 > 모든 요청에 대한 응답을 응답 본문으로 반환하는 컨트롤러
//					 > 해당 클래스로 오는 모든 요청이 비동기일 시 사용 가능
@RequestMapping("comment") // comment 로 들어오는 모든 요청 매핑
public class CommentController {
	
	@Autowired
	private CommentService service;
	
	/** 댓글 목록 조회
	 * @param boardNo
	 * @return
	 */
	@GetMapping("") // 댓글 목록 조회로서, 요청이 comment 로만 들어오고 있고, 클래스 상단에 이미 comment 로 응답을 받고 있기에 ("")만 사용한다
	public List<Comment> select(@RequestParam("boardNo") int boardNo){
		
		return service.select(boardNo);
	}
	
	/** 댓글 및 답글 등록
	 * 답글일 때는 parentCommentNo도 포함
	 * @return
	 */
	@PostMapping("")
	public int insert(@RequestBody Comment comment) {

		return service.insert(comment);
	}
	
	/** 댓글 삭제
	 * @param commentNo
	 * @return
	 */
	@DeleteMapping("")
	public int delete(@RequestBody int commentNo) {
		
		return service.delete(commentNo);
	}
		
	/** 댓글 수정
	 * @return
	 */
	@PutMapping("")
	public int update(@RequestBody Comment comment) {
		
		return service.update(comment);
	}
	
	
}
