// 쿠키에 저장된 이메일을 input 창에 뿌리기

// 쿠키에서 매개변수로 전달받은 key 가 일치하는 value 얻어오는 함수 작성
const getCookie = (key) => { // 메서드 호출 시 전달된 값이 key 가 될 것(아래에서 호출한다면 saveId)
    const cookies = document.cookie; // "K=V; K=V; ....." // 문서상 쿠키를 전부 얻어온다
    // 콘솔에서 확인해보면,
    // console.log(cookies); // saveId=user01@kh.or.kr; testKey=testValue
    
    // cookies 문자열을 배열 형태로 변환
     // 구분자를 이용해 자른다.
     // ("; ") <- 구분자
    const cookieList = cookies.split("; ") // ["K=V", "K=V"...] 
 				                .map( el => el.split("=") );  // ["K", "V"].. // key, value 형태로 구분

    // 콘솔로 확인(cookieList)
    // console.log(cookieList);
     // ['saveId', 'user01@kh.or.kr'], 
     // ['testKey', 'testValue']

    // 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후 결과 값으로 새로운 배열을 만들어서 반환
	 // 배열 -> 객체로 변환 (다루기 쉽도록)

    const obj = {}; // 비어있는 객체 선언

    // 현재 cookieList 는 2차원 배열 형태임
     // 0번 인덱스 : [a, b], 1번 인덱스 : [c, d], ....
     // 아래 반복문을 통해 i번째 인덱스의 0번째 값을 key로, i번째 인덱스의 1번째 값을 value 로설정
    for(let i = 0; i < cookieList.length; i++) {
		const k = cookieList[i][0]; // key 값
		const v = cookieList[i][1]; // value 값
		obj[k] = v; // 객체에 추가
		// obj["saveId"] = "user01@kh.or.kr";
		// obj["testKey"]  = "testValue";
	}
    
    // 콘솔로 확인
	 // console.log(obj); 
     // {saveId: 'user01@kh.or.kr', testKey: 'testValue'}

    return obj[key]; 
    // 매개변수로 전달받은 key(ex: saveId)와 obj 객체에 저장된 key가 일치하는 요소의 value값 반환

}    

// 이메일 작성 input 태그 요소(name 속성 이용)
const loginEmail = document.querySelector("#loginForm input[name='memberEmail']");

if(loginEmail != null){ // 로그인 창의 이메일 input 태그가 화면상에 존재할 때, 즉 로그인이 안 되어 있는 화면일 때

    // 쿠키 중 key 값이 "saveId"인 쿠키의 value 얻어오기
    const saveId = getCookie("saveId"); 

    // saveId 값이 있을 경우
    if(saveId != undefined){
        loginEmail.value = saveId; // 쿠키에서 얻어온 email 값을 input 요소 내 value로 세팅
        document.querySelector("input[name='saveId']").checked = true; // '아이디저장' 부분 체크박스에 체크해두기
    }
    // saveId 값이 없을 경우 따로 지정하지 않음
}

// ---------------------------------- 비동기요청 연습 -------------------------------------

// 1. 전체 회원 조회

const memberList = document.querySelector("#memberList");

const selectBtn = document.querySelector("#selectMemberList").addEventListener("click",() => {
	
	fetch("/member/selectMember")
		.then((resp) => {return resp.json})
		.then((resultList) => { // return 된 resp.json 의 내용이 resultList 로 저장됨??
			
			memberList.innerHTML = ""; // 이전 내용 삭제(버튼 누를 때마다 이전 조회된 데이터 누적되는 것 방지)
			
			resultList.forEach(member, index)
			
		})
		
});


