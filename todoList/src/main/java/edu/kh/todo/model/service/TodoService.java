package edu.kh.todo.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.todo.model.dto.Todo;

public interface TodoService {

	/** 7-1.
	 * (TEST) todoNo가 1인 할 일 제목 조회
	 * @return title
	 */
	String testTitle();

	/** A-1. 할 일 목록 + 완료된 할 일 갯수 조회
	 * @return map
	 */  
	Map<String, Object> selectAll();

	/** 할 일 추가
	 * @param todoTitle
	 * @param todoContent
	 * @return
	 */
	int addTodo(String todoTitle, String todoContent);

	/** 할 일 상세 조회
	 * @param todoNo
	 * @return
	 */
	Todo todoDetail(int todoNo);

	/** 할 일 삭제
	 * @param todoNo
	 * @return
	 */
	int todoDelete(int todoNo);

	int changeComplete(Todo todo);

	int todoUpdate(Todo todo);

	/** 전체 todo 개수 조회 : ajax
	 * @return
	 */
	int getTotalCount();

	/** 완료된 todo 개수 조회
	 * @return
	 */
	int getCompleteCount();

	/** 전체 할 일 목록 조회(ajax)
	 * @return
	 */
	List<Todo> selectList();
	
		
}
