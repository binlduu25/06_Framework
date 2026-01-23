package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;
import edu.kh.project.board.model.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardMapper mapper;
	
	/** 인터셉터에서 사용하는 boardType 조회 서비스
	 *
	 */
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}

	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {
		
		// 해당 service 메서드에서 할 일
		 
		 // 1. 지정된 게시판(boardCode)에서 '삭제되지 않은' 게시글 수 조회
		 // 2. 1번의 결과 + cp를 이용하여 Pagination 객체 생성
		  // Pagination : 게시글 목록 구성에 필요한 값을 저장한 객체
		  // > 따라서 1번 의 결과는 List로, 2번의 결과는 Pagination 객체 즉, Object 로 반환하여야 함
		  // > Map 으로 반환타입을 지정한 이유임
		// 3. 특정(boardType) 게시판의 지정된 페이지 목록 조회	
		// 4. Pagination 객체 + 목록 조회 결과를 Map으로 묶어 Controller 에 반환
		
		
		// 1. 삭제되지 않은 게시글 수 조회
		int listCount = mapper.getListCount(boardCode);
		 
		// 2. pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		// 3. 특정 게시판의 지정된 페이지 목록 조회
		 /* ROWBOUNDS 객체 (MyBatis 제공 객체)
		  * - RowBounds(offset, limit)
		  * - 지정된 크기 만큼 건너뛰고(offset)
		  *   제한된 크기 만큼(limit)의 행을 조회하는 객체
		  *   > 페이징 처리에 유용 */
		  
		int limit = pagination.getLimit(); // 10개
		int offset = (cp - 1) * limit; // 몇 개 건너뛰고 조회할 것인지, 즉, cp 가 2면 10개 건너뛰고 10개 조회. cp가 3이면 2개 건너뛰고 10개 조회
		RowBounds rowBounds = new RowBounds(offset, limit); 
		
		// mapper에 boardCode 와 rowBounds 를 전달하여 해당 게시판 종류(boardCode)의 게시글(Board) 목록을 List로 받는다.
		// ** 원래 mapper에 전달할 수 있는 값은 오직 1개지만, 만약 2번째 전달인자에 rowBonds 객체를 전달하는 경우 2개가 전달 가능하다.
		// rowBounds 는 무조건 2번째 자리에서 전달해야 하며, 전달인자가 rowBounds 외 없다면 1번째 자리에 null 등을 전달하는 방법으로 한다.
		List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);
		
		// 4. pagination 객체 + 목록 조회 결과 Map으로 묶기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		return map;
	}
	
	@Override
	public Map<String, Object> searchList(Map<String, Object> paramMap, int cp) {
		
		// 1. 지정된 게시판(boardCode)에서 검색조건에 맞으면서 삭제되지 않은 게시글 조회
		int listCount = mapper.getSearchCount(paramMap);
		
		// 2. Pagination 객체 생성
		Pagination pagination = new Pagination(cp, listCount);
		
		// 3. 특정 게시판의 검색 결과가 포함된 페이지 목록 조회
		int limit = pagination.getLimit();
		int offset = (cp - 1) * limit;
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		List<Board> boardList = mapper.selectSearchList(paramMap, rowBounds);
		
		// 4. 검색 목록 조회 결과 Map 으로 묶기
		Map<String, Object> map = new HashMap<>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
	
		return map;
	}
	
	@Override
	public Board selectOne(Map<String, Integer> map) {
		
		// 여러 SQL 을 실행하는 방법
		
		// 1. 하나의 service 메서드에서 여러 mapper 메서드를 호출하는 방법
		
		// 2. mybatis 기능 이용 
		 // 2-1. 모두 SELECT 이면서, 먼저 조회된 결과 중 일부 이용하여 나중에 수행되는 SQL의 조건문으로 삼을 수 있는 경우
		 // -> <resultMap>, <collection> 태그 이용해서 Mapper 메서드 1회 호출만으로 여러 SELECT 한번에 수행 가능
		return mapper.selectOne(map); 
		
	}
	
	/**
	 * 조회수 1 증가
	 */
	@Override
	public int updateReadCount(int boardNo) {
		
		// 1. 조회 수 1 증가(UPDATE)
		int result = mapper.updateReadCount(boardNo);
		
		// 2. 현재 조회 수 조회
		if(result > 0) {
			return mapper.selectReadCount(boardNo);
		}
		
		// UPDATE 실패한 경우 -1 반환 
		return -1;
	}
	
	/**
	 * 게시글 좋아요 체크/해제 서비스
	 */
	@Override
	public int boardLike(Map<String, Integer> map) {
	
		int result = 0;
		
		// 1. 좋아요가 체크된 상태일 경우 likeCheck == 1
		 // -> BOARD_LIKE 테이블에 DELETE
		
		if (map.get("likeCheck") == 1) {
			result = mapper.deleteBoardLike(map);
		}
		else {
		// 2. 좋아요가 해제된 상태인 경우 likeCheck == 0
		 // -> BOARD_LIKE 테이블에 INSERT
			result = mapper.insertBoardLike(map);
		}
		
		// 3. INSERT/DELETE 성공 시 해당 게시글 좋아요갯수 조회수 반환
		if(result > 0) {
			return mapper.selectLikeCount(map.get("boardNo"));
		}
		
		// 4. 실패 시
		return -1;
		
		
	}
	
	/**
	 * DB에 있는 이미지 조회 서비스 (SCHEDULING 에서 사용)
	 */
	@Override
	public List<String> selectDbImageList() {
		return mapper.selectDbImageList();
	}
	
	
}
