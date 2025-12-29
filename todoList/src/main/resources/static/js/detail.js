// 목록으로 버튼 동작(메인페이지로 이동)
const goToList = document.querySelector("#goToList");
goToList.addEventListener("click", () => {
    location.href = "/"; // 메인페이지로 요청
});

// 삭제 버튼 클릭 시 동작
const deleteBtn = document.querySelector("#deleteBtn");
deleteBtn.addEventListener("click", (e) => {
    if(confirm("삭제가 확실합니까")) { // 삭제가 확실한 경우
        location.href = `/todo/delete?todoNo=${e.target.dataset.todoNo}`; // 현재 이벤트가 일어난 target 의 dataset 에서 todoNo 을 가져옴
    }
    
});

// 완료 여부 변경 버튼 동작
 // 요소.dataset : data-* 속성에 저장된 값 반환
 // data-todo-no 세팅한 속성값 얻어오기
 // (html) data-todo-no -> js(카멜케이스) dataset.todoNo

const completeBtn = document.querySelector(".complete-btn");
completeBtn.addEventListener("click", (e) => {
	const todoNo = e.target.dataset.todoNo;
	
	let complete = e.target.innerText; 
	// 기존의 완료 여부 값을 얻어온다("Y" 또는 "N") 현재 버튼 안의 값(innerText)
	// 값을 바꾸어야 하기 때문에 let 으로 설정
	
	complete = (complete === 'Y') ? 'N' : 'Y';
	location.href = `/todo/changeComplete?todoNo=${todoNo}&complete=${complete}`;
				//   /todo/changeComplete?todoNo=1&complete=Y
});

// 수정 버튼 클릭 시 동작
const updateBtn = document.querySelector("#updateBtn");
updateBtn.addEventListener("click", (e) => {
	location.href = `/todo/update?todoNo=${e.target.dataset.todoNo}`;	
});
