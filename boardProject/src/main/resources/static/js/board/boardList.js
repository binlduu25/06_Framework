/* 글쓰기 버튼 클릭 시 */
const insertBtn = document.querySelector("#insertBtn");

// 글쓰기 버튼이 존재할 때 (로그인 상태인 경우)
if(insertBtn != null) {
    insertBtn.addEventListener('click', () => {

        // get 방식 요청(location : 동기식)
        // /editBoard/1/insert
        location.href = `/editBoard/${boardCode}/insert`;
		
		// html 상 타임리프
		// "${}" : {} 안에 java에서 가지고 온 값 (request scope, session scope 등)
		
		// java script 상 "" 
		// `${}` : 무조건 백틱(`) 사용해야 함. "", '' 사용 불가. 사용 시 따옴표 안의 내용을 그저 문자로 인식할 뿐
		// {} 안에 JS 변수를 쓸 수 있다. 따라서 이어쓰기 형태가 됨 
		// `/editBoard/${boardCode}/insert`
    });
}