package edu.kh.project.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.mapper.EditBoardMapper;
import edu.kh.project.common.util.Utility;
import lombok.extern.slf4j.Slf4j;

@PropertySource("classpath:/config.properties")
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class EditBoardServiceImpl implements EditBoardService{
	
	@Autowired
	private EditBoardMapper mapper;
	
	@Value("${my.board.web-path}")
	private String webPath;
	
	@Value("${my.board.folder-path}")
	private String folderPath;
	
	/** 게시글 작성 서비스
	 * @throws IOException 
	 * @throws IllegalStateException 
	 *
	 */
	@Override
	public int boardInsert(Board inputBoard, List<MultipartFile> images) throws IllegalStateException, IOException {
		
		// 0. DB는 어떻게 생겼는지 먼저 다시 한번 확인
		// 게시글 제목과 게시글 상세내용을 먼저 DB에 등록하고
		// 이에 따라 boardNo이 도출되면 해당 boardNo를 가지고 가져온 이미지를 해당 boardNo을 가진 게시글에 등록한다(BOARD_IMG DB에 저장한다)
		
		// 1. 게시글 부분(inputBoard) 게시글 DB에 INSERT
		// > iNSERT 된 게시글의 번호 (시퀀스 번호) 반환 받기
		int result = mapper.boardInsert(inputBoard);
		// 결과는 int 형이므로 삽입된 행의 갯수가 들어가있을 것
		// sql 상(mapper.xml)에서 <selectKey> 통해서 발급받은 boardNo은 inputBoard 안에 들어가 있다. (얕은 복사)
		
		// 삽입 실패 시
		if(result == 0) {return 0;}
		
		// 삽입 성공 시
		// 삽입된 게시글의 번호를 변수로 저장
		int boardNo = inputBoard.getBoardNo();
		
		// 2. 업로드된 이미지가 실제로 존재할 경우
		//    업로드된 이미지만 별도로 저장하여 BOARD_IMG 테이블에 삽입하는 코드 작성
		
		// > images 는 길이 5의 MultipartFile로 이루어진 list이고 반복문 통해 있는지 없는지 검사
		// > 실제로 업로드된 이미지만 모아둘 list 생성하기
		List<BoardImg> uploadList = new ArrayList<>();
		
		// 실제 파일 있는지 검사
		for(int i = 0; i < images.size(); i++) {
			
			// 실제 파일이 제출된 경우
			if(!images.get(i).isEmpty()) {
				// 원본명 구하고
				String originalName = images.get(i).getOriginalFilename();
				
				// 변경명 구하고
				String rename = Utility.fileRename(originalName);
				
				// 모든 값 저장할 BoardImg DTO 객체 생성(builder 패턴 이용)
				BoardImg img = BoardImg.builder()
								.imgOriginalName(originalName)
								.imgRename(rename)
								.imgPath(webPath)
								.boardNo(boardNo)
								.imgOrder(i)
								.uploadFile(images.get(i))
								.build();
				
				// uploadList에 추가
				uploadList.add(img);
			}
		}
		
		// uploadList 가 비어 있는 경우(파일이 없어서 위 for문에서 아무것도 저장되지 않았을 것)
		// 코드가 여기까지 도달했다는 것은 게시글과 제목은 있다는 뜻이므로 return 에 boardNo를 전달해준다.
		if(uploadList.isEmpty()) {
			return boardNo;
		}
		
		// 제출된 파일이 존재할 경우
		// > "BOARD_IMG" 테이블 INSERT로 DB에 저장하고 서버 컴퓨터에 파일 저장(transferTo())
		result = mapper.insertUploadList(uploadList);
		
		// 이제 result 에 삽입된 행의 갯수가 들어가 있을 것
		// 여기서 만약, 사용자가 3개의 그림을 업로드했고, 결과가 2만 반환되었다면 1개는 제대로 업로드하지 못했다는 걸 뜻함
		// 따라서 이 경우는 전체 실패로 간주해야 함
		
		// 성공 시
		if(result == uploadList.size()) {
			
			// 서버에 파일 저장
			for(BoardImg img : uploadList) {
				
				img.getUploadFile().transferTo(new File(folderPath + img.getImgRename()));
				
				
				// File은 늘 IO Exception 발생시키므로 예외처리 필요
				
			}
			
		}else { // 실패 시
			// 부분적 삽입 실패 시(2개 중 1개만 실패 등)
			// 전체 서비스 실패로 판단
			// -> 성공한 1개도 rollback 필요
			// springboot에서 rollback 하는 법 : 이때까지는 spring Container 가 관리하고 있었음
			// 이제 강제로 rollback 하기 위해선 Exception 처리 필요
			// 클래스 상단 (@Transactional 의 기본 예외처리는 RuntimeException 이다)
			// 이후 spring container 가 알아서 rollback 처리 해줄 것
			
			throw new RuntimeException();
			
		}
		
		return boardNo;
	}
	
	/**
	 * 게시글 수정 서비스
	 */
	@Override
	public int boardUpdate(Board inputBoard, List<MultipartFile> images, String deleteOrderList) throws Exception{
		
		// 제목이나 내용 등은 Board 테이블에 있지만, 이미지는 Board_img 테이블에 있으므로 이를 나누어 생각해야 한다.
		
		// 1. 게시글 제목/내용 수정
		
		int result = mapper.boardUpdate(inputBoard);
		
		// 수정 실패 시 바로 리턴
		if(result == 0) return 0;
		
		// 2. 기존 0 -> 삭제된 이미지(deleteOrderList)가 있는 경우
		if(deleteOrderList != null && !deleteOrderList.equals("")) {
			
			// mapper 로 전달할 매개변수 만들기 위해 map 에 세팅
			Map<String, Object> map = new HashMap<>();
			map.put("deleteOrderList", deleteOrderList); // deleteOrderList 는 String으로 0,1,2 형태로 들어 있다.
			map.put("boardNo", inputBoard.getBoardNo());
			
			// BOARD_IMG 에 존재하는 행을 삭제하는 SQL 호출
			result = mapper.deleteImg(map);
			
			// result = 0~4 값을 가짐(boardImg)
			 // 조금 더 service 품질을 올리기 위해서는 삭제한 img 갯수와 결과가 일치하지 않을 때를 고려하는 코드를 작성하는 것이 좋다.
			 // 하지만 현 단계에선 x
			
			if(result == 0) { // 실패할 경우 rollback
				throw new RuntimeException();
			}
		}
		
		// 3. 제출된 이미지가 있을 경우(클라이언트가 실제 업로드한 이미지가 있는 경우)
		 // 위 boardInsert 쪽 부분 복사해서 가져옴
		
		List<BoardImg> uploadList = new ArrayList<>();
		for(int i = 0; i < images.size(); i++) {
			if(!images.get(i).isEmpty()) {
				String originalName = images.get(i).getOriginalFilename();
				String rename = Utility.fileRename(originalName);
				
				BoardImg img = BoardImg.builder()
								.imgOriginalName(originalName)
								.imgRename(rename)
								.imgPath(webPath)
								.boardNo(inputBoard.getBoardNo())
								.imgOrder(i)
								.uploadFile(images.get(i))
								.build();
				
				uploadList.add(img);
				
				// 4. 업로드 하려는 이미지 정보(img) 이용하여 수정 또는 삽입 수행
				 // 94 18:00
			}
		}
		
		
		
		return 0;
	}
}
