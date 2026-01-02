package edu.kh.project.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;

@Mapper
public interface EditBoardMapper {

	/** 게시글 작성 부분 SQL 생성(BoardNo 얻어오기)
	 * @param inputBoard
	 * @return
	 */
	int boardInsert(Board inputBoard);

	/** 게시글 이미지 모두 삽입
	 * @param uploadList
	 * @return
	 */
	int insertUploadList(List<BoardImg> uploadList);

}
