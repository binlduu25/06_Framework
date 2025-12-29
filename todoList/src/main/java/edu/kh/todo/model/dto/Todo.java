package edu.kh.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// A-3.
public class Todo {
	private int todoNo;  			// TODO_NO
	private String todoTitle;		// TODO_TITLE
	private String todoContent;		// TODO_CONTENT
	private String complete;		// COMPLETE("Y"/"N")
	private String regDate;			// REG_DATE
	 
	
	
}
