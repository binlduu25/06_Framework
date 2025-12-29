package edu.kh.todo.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.todo.model.mapper.TodoMapper;

// 4. DAO 작성

// Spring 에서는 DAO 에서 Mapper를 사용하기 때문에 해당 클래스는 한번 작성해보는 용도임

@Repository 
// DAO 계층 역할 명시 + Bean 등록

public class TodoDAO {

	
	/**7-5.
	 * mapper 단계 추가
	 * mapper 에는 TodoMapper 인터페이스 구현체가 의존성 주입됨
	 * -> 해당 구현체가 sqlSessionTemplate 이용
	 */
	@Autowired
	private TodoMapper mapper;
	
	/**7-4.
	 * @return
	 */
	public String testTitle() {
		return mapper.testTitle();
	}

	
	
	
}
