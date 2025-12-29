package edu.kh.todo.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.todo.model.dto.Todo;

// 5. Mapper 작성


/*

@Mapper

- MyBatis에서 제공하는 어노테이션
- dependencies 에 mybatis 추가하지 않으면 사용할 수 없다
- MyBatis에서 sql 과 java 메서드를 연결해주는 인터페이스의 구현체를 
  Spring 의 Bean으로 등록할 수 있게 해주는 어노테이션
- TodoMapper 는 인터페이스이기에 Mapper 어노테이션을 상속받는 구현체(class)를 mybatis 내부적으로 생성해
  Bean으로 등록까지 한다.
- 즉, Spring 이 Mapper 인터페이스를 인식하여, 자동으로 구현체 생성, Bean 등록
- 또한 해당 어노테이션이 작성된 인터페이스는 namespace 에 해당 인터페이스가 작성된 xxx-mappers.xml 파일과 연결되어 
  SQL 호출/수행/결과반환 가능

*/

@Mapper
public interface TodoMapper {

	/**7-6. TodoMapper는 인터페이스고 아래 메소드는 추상메소드
	 * 이를 상속받은 구현체 클래스 역시 직접 만들지 않음
	 * 여기서 해야 할 일은 TodoMapper 와 namespace로 연결되어 있는 xml 처리
	 * @return
	 */
	String testTitle();

	/** A-5.
	 * @return
	 */
	List<Todo> selectAll();
	
	int getCompleteCount();

	int addTodo(Todo todo); // service 로부터 1개의 변수만 담아서 가져옴

	Todo todoDetail(int todoNo);

	int todoDelete(int todoNo);

	int changeComplete(Todo todo);

	int todoUpdate(Todo todo);

	int getTotalCount();

}
