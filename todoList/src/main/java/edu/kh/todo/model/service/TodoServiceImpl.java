package edu.kh.todo.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.todo.model.dao.TodoDAO;
import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.mapper.TodoMapper;

// 3. Service 인터페이스 작성

@Transactional(rollbackFor = Exception.class)
// @Transactioncal
// 트랜잭션 처리를 수행하라고 지시하는 어노테이션
// 정상 코드 수행 시 commit 될 것

// 기본 설정 : Service 내부 코드 수행 중 RuntimeException 발생 시 rollback
// 하지만 이렇게 하면 runtimeException 외 예외 발생 시에는 문제가 있음에도 rollback 하지 않기에, 아래와 같이 추가함(모든 예외 발생 시 rollback)
// rollbackFor = Exception.class 

@Service 
// 비즈니스 로직 (데이터 가공, 트랜잭션 처리 등) 역할 명시 + Bean 등록
public class TodoServiceImpl implements TodoService{

	// 7-3. TodoDAO 와 같은 타입이거나 상속관계 Bean 의존성 주입(DI)
	@Autowired
	private TodoDAO dao;
	
	// A-4.
	@Autowired
	private TodoMapper mapper;
	
	/** 7-2.테스트
	 */
	@Override
	public String testTitle() {
		return dao.testTitle(); // 그동안은 변수명을 따로 설정해주고 그에 맞는 값을 반환해야 했지만 그럴 필요 없다(이유?)
	}
	
	
	/** A-2.
	 *a
	 */
	@Override
	public Map<String, Object> selectAll() {
		
		// 1) 할 일 목록 조회
		// 2) 완료된 할 일 갯수 조회
		// -> DAO 2번 호출해야 함 -> 하지만 DAO 이제부터 사용할 필요X
		// -> Service 단에서 Mapper 바로 호출하면 됨
		
		List<Todo> todoList = mapper.selectAll(); // 1)
		int completeCount = mapper.getCompleteCount(); //2
		
		Map<String, Object> map = new HashMap();
		map.put("todoList", todoList);
		map.put("completeCount", completeCount);
		
		return map;
	}
	
	
	@Override
	public int addTodo(String todoTitle, String todoContent) {
		
		// service 단에서 데이터 가공 필요
		// 헤당 메서드에서 mapper 호출해야 할 것(myBatis 에서 제공하는 어노테이션으로 만들어진 mapper) 
		// mybatis 에서는 sql에 전달할 수 있는 parameter 는 단 1개
		// 즉, TodoMapper에 생성될 추상메서드의 매개변수도 1개
		// 따라서 controller 에서 넘어온 todoTitle 과 todoContent 를 전달하기 위해선 Todo 객체 생성 후 Todo DTO로 묶어서 전달해야 함
		// (만약 DTO 객체와 관련없는 값이라면 Map 을 사용하면 될 것)
		
		Todo todo = new Todo();
		todo.setTodoTitle(todoTitle);
		todo.setTodoContent(todoContent);
			
		
		return mapper.addTodo(todo);
	}
	
	/**
	 * 할 일 상세 조회
	 */
	@Override
	public Todo todoDetail(int todoNo) {
	
		return mapper.todoDetail(todoNo);
	}
	
	@Override
	public int todoDelete(int todoNo) {
		
		return mapper.todoDelete(todoNo);
	}
	
	/**
	 * 완료 여부 변경
	 */
	@Override
	public int changeComplete(Todo todo) {
		return mapper.changeComplete(todo);
	}
	
	@Override
	public int todoUpdate(Todo todo) {
		return mapper.todoUpdate(todo);
	}
	
	@Override
	public int getTotalCount() {
		return mapper.getTotalCount();
	}
	
	@Override
	public int getCompleteCount() {
		return mapper.getCompleteCount(); // 위에 이미 생성했으니 재활용하면 됨
	}

	@Override
	public List<Todo> selectList() {
		return mapper.selectAll();
	}
}


