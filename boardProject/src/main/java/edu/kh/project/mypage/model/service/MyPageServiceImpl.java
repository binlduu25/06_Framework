package edu.kh.project.mypage.model.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.dto.Member;
import edu.kh.project.mypage.model.dto.UploadFile;
import edu.kh.project.mypage.model.mapper.MyPageMapper;
import lombok.extern.slf4j.Slf4j;

@PropertySource("classpath:/config.properties")

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MyPageServiceImpl implements MyPageService {
	
	@Autowired
	private MyPageMapper mapper;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath;
	
	@Value("${my.profile.folder-path}")
	private String profileFolderPath;
	
	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		
		if(!inputMember.getMemberAddress().equals(",,")) {
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
			
		} else { // 주소가 입력되지 않았을 때
			inputMember.setMemberAddress(null);
		}
		
		return mapper.updateInfo(inputMember);
	}
	
	
	// 비밀번호 변경 서비스
	@Override
	public int changePw(Map<String, Object> paramMap, int memberNo) {
		
		// 먼저, 기존의 비밀번호와 입력한 비밀번호가 일치하는지 확인한다.
		String originPw = mapper.selectPw(memberNo);
		
		// 입력받은 현재 비밀번호는 평문이고, DB에서 가져온 비밀번호는 암호문
		
		if(!bcrypt.matches((String)paramMap.get("currentPw"), originPw)) { 
			// bcrypt를 통해 DB에 저장되어 불러진 originPw 와 String으로 paramMap 에 저장되어(사용자가 입력한) 현재 pw 가 일치하는지 확인
			// 일치하지 않는다면 메서드 종료
			return 0;	
		}
		
		// 일치할 시, 새 비밀번호 암호화
		
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
			
		// 암호화된 encPw 를 DB에 저장
		
		// SQL 전달해야하는 데이터 2개 (암호화한 새 비밀번호, 회원번호)
		// -> SQL 전달 인자 1개뿐!
		// -> 묶어서 전달 (paramMap 재활용)
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		return mapper.changePw(paramMap);
	}
	
	/**
	 * 회원 탈퇴 서비스
	 */
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 1. 현재 로그인한 회원과 비밀번호 DB에서 조회
		String encPw = mapper.selectPw(memberNo);
		
		// 2. 입력받은 비번 & 암호화된 DB 비번 비교
		if(!bcrypt.matches(memberPw, encPw)) { // 일치하지 않을 떄
			return 0;
		}
		
		// 3. 같은 경우
		return mapper.secession(memberNo);
		
	}
	
	// 파일 업로드 테스트1 (서버 저장)
	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception{
		
		// 먼저 클라이언트가 실제로 파일을 올렸는지 확인한다.
		
		if(uploadFile.isEmpty()) { // 업로드한 파일이 없을 경우(true일 때)
			return null;
		}
		
		// 업로드한 파일이 있을 경우
		// D:/Programming/0_uploadFiles/test/파일명 으로 서버에 사용자가 올린 파일 이름으로 저장
		 // new File 생성 시 throws Exception 처리 필요
		uploadFile.transferTo(new File("D:/Programming/0_uploadFiles/test/" + uploadFile.getOriginalFilename()));
		
		// 웹에서 해당 파일에 접근할 수 있는 경로를 만들어 반환
		 // 클라이언트가 브라우저에 해당 이미지를 보기 위해 요청하는 경로
		 // <img src = "경로">
		 // /myPage/file/파일명.jpg 
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
		
	}
	
	// 파일 업로드 테스트2 (서버 저장 + DB 저장)
	@Override
	public int fileUpload2(MultipartFile uploadFile, int memberNo) throws Exception {
		
		// 업로드된 파일 없을 시
		if(uploadFile.isEmpty()) { 
			return 0;
		}
		
		// multipartFile 이 제공하는 메서드
		 // - isEmpty() : 업로드 파일 있을 때 t, 없을 때 f
		 // - getSize() : 파일 크기
		 // - getOriginalFileName() : 원본 파일명
		 // - transferTo(경로) : 메모리 또는 임시 저장 경로에 업로드된 파일을 원하는 경로에 실제로 전송
		 // 					 어떤 서버 폴더에 저장할지 지정한다
		
		
		// 업로드 된 파일이 있을 때
		 // 1. 서버에 저장될 서버 폴더 경로 만들기
		  // 1-1. 파일이 저장될 서버 경로 지정하여 변수 담기
		String folderPath = "D:/Programming/0_uploadFiles/test/";
		
		  // 1-2. 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소(요청 주소) 
		String webPath = "/myPage/file/";
		
		 // 2. DB 에 전달할 데이터를 DTO로 묶어서 INSERT
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		
		 // 2-1. Builder 이용해 uploadFile 객체 생성
		  // Builder 장점 : 반복되는 참조변수명, set 구문 생략
		  // 				메서드 체이닝 이용해 한 줄로 작성 가능
		UploadFile uf = UploadFile.builder()
						.memberNo(memberNo)
						.filePath(webPath)
						.fileOriginalName(uploadFile.getOriginalFilename())
						.fileRename(fileRename)
						.build();
		
		int result = mapper.insertUploadFile(uf);
		
		 // 3. 삽입 성공 시 서버 폴더에 저장
		
		if(result == 0) {
			return 0;
		}
		
		 // 변경된 파일명으로 서버 컴퓨터에 저장
		 uploadFile.transferTo(new File(folderPath + fileRename));
		 
		 return result;
	}
	
	@Override
	public List<UploadFile> fileList(int memberNo) {
		
		return mapper.fileList(memberNo);
	}
	
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, List<MultipartFile> bbbList, int memberNo) throws Exception {
		
		// 1. aaaList 처리
		
		 // 업로드된 파일이 없을 경우를 제외하고 업로드
		int result1 = 0;
		
		for(MultipartFile file : aaaList) {
			
			if(file.isEmpty()) { // 파일이 없다면 다음 반복으로 넘어가야 함
				continue;
			}
			
			// 파일이 있을 경우 DB에 저장
			 // 위 fileUpload2 메서드 재사용
			
			result1 += fileUpload2(file, memberNo); // 2개 모두 성공했다면 2 반환, 1개만 성공했다면 1반환
			
		}
		
		// 2. bbbList 처리 - 1.과 방식 같게
		int result2 = 0;
		
		for(MultipartFile file : bbbList) {
			if(file.isEmpty()) {
				continue;
			}
			
			result2 += fileUpload2(file, memberNo);
			
		}
		
		return result1 + result2;
	}
	
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
	
		// 프로필 이미지 경로
		String updatePath = null;
		
		// 업로드한 이미지가 없는데 요청이 온 경우도 있음(파일 삭제하기 누르고(NULL 값으로) 변경하기 요청)
		
		// 변경명 저장
		String rename = null;
		
		// 업로드한 이미지가 있을 경우 (js 에서 막아놨지만 서버에서 한번 더 해준다)
		if(!profileImg.isEmpty()) {
			
			// updatePath 경로 조합
			 // 1. 파일명 변경
			 rename = Utility.fileRename(profileImg.getOriginalFilename());
			 
			 // 2. /myPage/profile/변경된파일명
			  // config.properties 에 설정
			 updatePath = profileWebPath + rename;
		}
		
		// 수정된 프로필 이미지 경로 + 회원 번호 저장할 DTO 객체
		Member member = Member.builder()
						.memberNo(loginMember.getMemberNo())
						.profileImg(updatePath)
						.build();
		
		// update 수행
		int result = mapper.profile(member);
		
		if(result > 0) { // db 업데이트 성공
			// 서버폴더 경로에 클라이언트가 보낸 이미지 저장
			
			// 프로필 이미지를 없앤 경우 (NULL 로 수정한 경우)를 제외
			 // -> 업로드한 이미지가 있을 경우
			 if(!profileImg.isEmpty()) {
				 // 파일을 서버 지정된 폴더에 저장
				 profileImg.transferTo(new File(profileFolderPath + rename));
				 // 서버 폴더에 변경된 이름으로 저장됨
			 }
			
			 // 세션에 등록된 현재 로그인한 회원 정보에서
			 // 프로필 이미지 경로를 DB에 업데이트한 경로로 변경
			 loginMember.setProfileImg(updatePath);
			
		}
		
		return result;
	}
	
	
	
	
}
