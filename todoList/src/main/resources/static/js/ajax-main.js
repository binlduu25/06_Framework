// html 상 요소 얻어와 변수 저장

// 할 일 개수 관련 요소
const totalCount = document.querySelector("#totalCount");
const completeCount = document.querySelector("#completeCount");
const reloadBtn = document.querySelector("#reloadBtn");

// 할 일 추가 관련 요소
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");
const addBtn = document.querySelector("#addBtn");

// 할 일 목록 조회 관련 요소
const tbody = document.querySelector("#tbody");

// 할 일 상세 조회 관련 요소
const popupLayer = document.querySelector("#popupLayer");
const popupTodoNo = document.querySelector("#popupTodoNo");
const popupTodoTitle = document.querySelector("#popupTodoTitle");
const popupComplete = document.querySelector("#popupComplete");
const popupRegDate = document.querySelector("#popupRegDate");
const popupTodoContent = document.querySelector("#popupTodoContent");
const popupClose = document.querySelector("#popupClose");

// 상세 조회 팝업레이어 관련 버튼 요소
const changeComplete = document.querySelector("#changeComplete");
const updateView = document.querySelector("#updateView");
const deleteBtn = document.querySelector("#deleteBtn");

// 수정 레이어 관련 요소
const updateLayer = document.querySelector("#updateLayer");
const updateTitle = document.querySelector("#updateTitle");
const updateContent = document.querySelector("#updateContent");
const updateBtn = document.querySelector("#updateBtn");
const updateCancel = document.querySelector("#updateCancel");

/*
fetch() API
- 비동기 요청을 수행하는 최신 Javascript API 중 하나.

- Promise(객체) 는 비동기 작업의 결과를 처리하는 방법으로 어떤 결과가 올지는 모르지만 반드시 결과를 보내주겠다는 약속.
-> 비동기 작업이 맞이할 완료 또는 실패와 그 결과값을 나타냄.
-> 비동기 작업이 완료되었을 때 실행할 콜백함수를 지정하고 해당 작업의 성공 또는 실패 여부를 처리할 수 있도록 함.

Promise 객체는 세 가지 상태를 가짐
- Pending(대기 중) : 비동기 작업이 완료되지 않은 상태
- Fulfilled(이행됨) : 비동기 작업이 성공적으로 완료된 상태
- Rejected(거부됨) : 비동기 작업이 실패한 상태
*/

// 전체 Todo 개수 조회 및 html 화면에 출력할 함수
function getTotalCount(){
    // 비동기 방식으로 서버에 전체 Todo 개수를 조회하는 요청
    // fetch() API로 코드 작성
    fetch("/ajax/totalCount")
        .then((response) => {
            return response.text();
            })
            // 두번째 then (첫번째에서 return 된 데이터를 활용하는 역할)
            .then((result) => {
                totalCount.innerText = result;
                });   
    // 서버로 "/ajax/totalCount" 로 GET 요청
    // 첫번째 then (서버에서 온 응답을 처리하는 역할)
     // then 안에 콜백함수가 넘어온다
     // 즉 비동기 처리가 완료되었을 때 어떤 함수를 처리할 것인가
     // response 는 응답 변수명이고, 괄호()는 생략 가능)
     // 서버    에서 응답을 받으면, 해당 응답(response)를 텍스트 형식으로 변환하는 콜백함수
    // 매개변수 response : 비동기 요청에 대한 응답이 담긴 객체
     // response.text() : 응답데이터를 문자열/숫자 형태로 변환한 결과를 가지는 Promise 객체 반환
    // 두번째 then
     // 매개변수로 전달되어진 데이터(result)를 받아서 어떤 식으로 처리할지 정의
     // 여기선 #totalCount인 span 태그의 내용으로 result값 삽입
}
getTotalCount(); // 함수 정의 후 바로 읽혀서 전역에서 호출되어야 화면에 출력될 것


// 완료된 할 일 갯수 조회 및 html 화면 상 출력
function aCompleteCount(){
	
	fetch("/ajax/aCompleteCount")
		.then(response => response.text()) 
		// 매개변수 1개일 시(response) 소괄호 생략 가능, 중괄호 안 return 값 한 줄일 시 return 및 중괄호 생략 가능
			.then((result) => {
				completeCount.innerText = result;
			});
}
aCompleteCount();


// 할 일 추가버튼 클릭 시 동작
addBtn.addEventListener("click", () => {
	// 먼저 제목과 상세내용에 null(아무것도 입력하지 않았을 시) 
	
	if(todoTitle.value.trim().length === 0 || todoContent.value.trim().length === 0){
		alert("제목이나 내용은 비어있을 수 없습니다");
		return;
	}
	
	// post 방식 fetch() 비동기 요청 보내기
	 // 요청 주소 : "ajax/add"
	 // 데이터 전달방식 : POST
	 // 전달 데이터 (파라미터) : todoTitle 값, todoContent 값
	 // JS <-> JAVA
	  // 원래 JS 와 JAVA 간 객체는 호환될 수 없다
	  // 'JSON'을 이용하면 가능하다(k,v 로 구성됨)
	  // JSON : JavaScript Object Notation : 데이터 표현문법 
	  /* ex) 전부 String 형임
	  	{ 	
		"name"	: "홍길동",
		"age"	: "20",
		"skill" : ["javaScript, "java"]		
		}
	   */
	 
		
 // todoTitle 과 todoContent 를 저장할 객체 생성(JS 객체)
 const param ={ "todoTitle":todoTitle.value, 
				"todoContent":todoContent.value
			  };
	
// JAVA 로 보내기 위해 JSON 으로 변환하여 서버로 제출
fetch("/ajax/add", 
	  {method : "POST",
	   headers : {"Content-Type" : "application/json"}, 	
	   body : JSON.stringify(param)
	  }) 
	  // POST 방식의 요청(아무것도 입력하지 않을 시 GET)
	  // headers ~  : 요청의 머리부분, 즉 요청 데이터의 형식을 JSON 으로 지정한다
	  // body ~ : param 이라는 js 객체형태를 JSON 에 STRING 형으로 변환
	.then((response) => {return response.text()}) // 중괄호 생략하지 않을 시 return 필요
	.then((result) => {
		if (result > 0){
			alert("추가 성공");
			
			//  추가 성공 시 작성했던 내용과 제목 비우기
			todoTitle.value = "";
			todoContent.value = "";
			
			// 할 일 추가되었으므로 todo 갯수 조회하는 함수 재호출
			getTotalCount();
			
			// 전체 todo 목록 조회하는 함수 재호출
			selectTodoList();
			
		}else{
			alert("추가 실패");
		}
	})			  
	
});

// 비동기로 할 일 전체 목록 조회 & html 화면에 출력까지 하는 함수

const selectTodoList = () => {
	fetch("/ajax/selectList")
	.then((response) => {return response.json()}) // response 에 단일값이 아닌 배열 등이 있으므로 응답 결과를 json 으로 받아 해석해야 함
	.then((todoList) => {
		// 매개변수 todoList
		// 첫번째 then 에서 response.text() 인지, response.json 인지에 따라 단순 텍스트인지, JS OBJECT 인지 달라진다.
		// 확인 : console.log(todoList);
		
		// 기존에 출력되어 있던 할 일 목록 모두 비우기
		tbody.innerHTML = "";
		
		// tobdy에 tr, td 요소 생성하여 내용 추가
		for(let todo of todoList){ // 향상된 for 문 이용해 json으로 받아온 todoList 의 요소 1개씩 가져옴
			  
			// tr 태그 생성 (한 행마다, 즉 요소 1개마다)
			const tr = document.createElement("tr"); // <tr></tr>
		
			// JS 객체에 존재하는 key 모음 배열 생성(화면에서 표시될 내용) (한 행마다, 즉 요소 1개마다)
			const arr = ['todoNo', 'todoTitle', 'complete', 'regDate'];
				
			for(let key of arr){
				const td = document.createElement("td"); // <td></td>
				
				// 제목인 경우 a태그 생성하여 넘어가게 함
				if (key === 'todoTitle'){
					const a = document.createElement("a");
					a.innerText = todo[key]; // todo["todoTitle"]
					a.href = "/ajax/detail?todoNo=" + todo.todoNo; // <a href="/ajax/detail?todoNo=1">테스트 1 제목</a>
					td.append(a); // td 에 a 태그 추가
					tr.append(td); // tr 에 td 추가
					
					a.addEventListener("click", (e) => { 
					// a 태그 클릭 시 페이지 이동 막기 : 비동기 요청을 위해 동기식 요청 수행 방지
					// 사실 비동기식 요청에서 a 태그를 굳이 사용할 필요는 없다
					// button 등 사용 가능
					e.preventDefault();
					// 대신에 a 태그 클릭 시 할 일 상세 조회 비동기 요청 함수 호출
					selectTodo(e.target.href); // 클릭 시, selectTodo 함수 수행하고, 매개변수로 e.target 의 href 속성 값을 보내겠다.
					});
					continue; // ??
				}
				
				// 제목이 아닌 경우
				td.innerText = todo[key]; // todo['todoNo']
				tr.append(td); // tr의 마지막 요소 현재 td 에 추가
				
				
			}
			tbody.append(tr);
		}

	});	
		
};

selectTodoList();


// 비동기로 할 일 상세 조회하는 함수
// 먼저 각 할 일의 제목 클릭 시 숨겨져 있는 popuplayer 보이게 해야 함
const selectTodo = (url) => { // 182번 라인에서 받은 매개변수를 url 이라는 변수명으로 받겠다? (주석 설명 맞는지 확인 필요)
	// fetch()요청 보내기 
	fetch(url)
	.then((resp) => resp.json())
	.then((todo) => {
		
		// popuplayer 에 조회해 온 값을 출력
		popupTodoNo.innerText = todo.todoNo;
		popupTodoTitle.innerText = todo.todoTitle;
		popupComplete.innerText  = todo.complete;
		popupRegDate.innerText   = todo.regDate;
		popupTodoContent.innerText = todo.todoContent;
		
		// popuplayer 보이게 하기(숨김 처리(display:none) 하는 클래스 삭제)
		popupLayer.classList.remove("popup-hidden");
	});
};

// popuplayer의 x 클릭 시 popuplayer 숨기기
popupClose.addEventListener("click", () => {
	popupLayer.classList.add("popup-hidden");
});

/*
GET, POST 외에도 DELETE, PUT 이 있다. 
HTTP METHOD : GET, POST, DELETE, PUT

1. GET : 조회(R) - 서버에 있는 데이터 가져올 때
2. POST : 생성(C) - 새로운 데이터 서버에 등록 시
3. PUT : 수정(U) - 서버에 있는 데이터를 통째로 바꿀 때 사용(덮어쓰기)
				   -> 같은 요청 여러 번 보내도 결과가 항상 같음(멱등성)
4. DELETE : 삭제(D) - 서버에 있는 데이터 삭제
					-> 같은 요청 여러 번 보내도 결과가 항상 같음(멱등성)

여태까지는 POST 방식을 이용해 삭제 및 수정 요청 보냈었고, 실제로도 가능하다
만약 해당 방식으로 삭제 요청을 한번 보내고 다시 같은 삭제 요청을 보낼 때 에러가 발생함(멱등성을 띄지 않는다)
하지만 DELETE 나 PUT 등은 멱등성을 띄기 때문에 여러번 같은 요청을 보내도 에러가 발생하지 않음
또한 비동기식 요청에서는 GET/POST 밖에 없기 때문에 사용이 제한되나 동기식 요청에서는 PUT, DELETE 가 가능하다.

----------------------------------------------------------

*** REST ***

REST: 웹의 자원을 URL로 표현하고, 동사의 역할을 HTTP Method로 나누는 방식

요청 주소 만들 때,

/selectTodoList
/getMemberList
/addTodo

-> REST하지 못하다

사용자 목록 조회 : /users (GET)
사용자 새로 등록 : /user (POST)
사용자 수정 : /user/3 (PUT)
사용자 삭제 : /user/4 (DELETE)

-> REST 규칙 준수했다(RestFul)
-> RestAPI : REST 규칙 준수(RestFul)해서 만든 API

백엔드 쪽에서는 REST 를 지향하는 편

*/


// 삭제 버튼 클릭 시
deleteBtn.addEventListener("click", () => {
	// 취소 클릭 시 해당 함수 종료
	if(!confirm("정말 삭제하시겠습니까?")){
		return;
	}
	// 삭제할 할 일 번호 얻어오기
	const todoNo = popupTodoNo.innerText;
	
	// 확인 버튼 클릭 시 삭제 비동기 요청 (DELETE 방식)
	fetch("/ajax/delete", {
		method : "DELETE",
		headers : {"Content-Type" : "application/json"},
		body : JSON.stringify(todoNo)
		// 단일값은 JSON 형태로 자동변환되어 전달되기 때문에 아래와 같이 작성하는 것도 허용됨
		// body : todoNo
	})
	.then((response) => {return response.text()})
	.then((result) => {
		if(result > 0){
			alert("삭제 성공");
			
			//상세 조회 팝업 레이어 닫기
			popupLayer.classList.add("popup-hidden");

			// 전체, 완료된 할 일 갯수 다시 조회
			// 할 일 목록 다시 조회
			getTotalCount();
			aCompleteCount();
			selectTodoList();
			
		}else{
			alert("삭제 실패");
			
		}
	});
});

// 완료 여부 변경 클릭 시
changeComplete.addEventListener("click", () => { 
	
	// 현재 완료 여부를 반대값으로 변경한 값, 변경할 할 일 번호
	const complete = popupComplete.innerText === 'Y' ? 'N' : 'Y';
	const todoNo   = popupTodoNo.innerText;
	
	// SQL 실행에 필요한 두 값을 JS 객체로 묶음 > JSON 으로 전달하기 위함
	const obj = {"todoNo" : todoNo, "complete" : complete};
	// ex. todoNo = 2, complete = "Y"
	
	// 비동기로 완료 여부 변경 요청 (PUT 요청 방식)
	fetch("/ajax/changeComplete", {
		
		method : "PUT",
		headers : {"Content-Type" : "application/json"},
		body : JSON.stringify(obj)
		
		
	})
	.then((resp) => {return resp.text()})
	.then((result) => {
		if(result > 0){
			
			// 단순히 selectTodo를 호출해도 됨
			// 하지만 이는 서버 부하가 큼
			// 따라서 화면상의 표기만 바꾸고 DB 는 따로 저장만 시켜주는 게 효율적
			
			popupComplete.innerText = complete;
			
			// 마찬가지로 aCompleteCount 재호출해도 되지만 비효율적
			// 기존 완료된 todo 개수에 +- 1 
			const count = Number(completeCount.innerText);
			if(complete === 'Y') completeCount.innerText = count + 1;
			else				 completeCount.innerText = count - 1;
			
			selectTodoList();
			// 서버 부하 줄이기 가능! -> 코드가 복잡해서 오히려 시간비용 증가
			// 서버 부하 줄이는 방법으로 코드 작성해보기
			
		}else{
			alert("완료 여부 변경 실패..");
			
		}
	});
	
	//  
});

// 상세조회 팝업에서 수정(#UpdateView) 버튼 클릭 시
updateView.addEventListener("click", () => {
	
	// 기존 팝업 레이어 숨기고 수정 팝업 레이어는 보이게
	popupLayer.classList.add("popup-hidden");
	updateLayer.classList.remove("popup-hidden");
	
	// 상세 조회 팝업 레이어에 작성된 제목, 내용 얻어와 세팅
	updateTitle.value = popupTodoTitle.innerText;
	updateContent.value = popupTodoContent.innerHTML.replaceAll("<br>", "\n");
	// textarea 내부의 값은 개행 처리 되어 있는 텍스트를 처리해주어야 하고,
	// innerHTML로 불러오고 html 상 <br>로 되어 있는 개행 처리를 "\"로 바꾸어주어야 한다. 
	
	// 수정 레이어 상 수정 버튼에 data-todo-no 속성 추가(아래 메소드에서 수정 버튼 눌렀을 시 구동하기 위함)
	updateBtn.setAttribute("data-todo-no", popupTodoNo.innerText);
	// <button id="updateBtn" data-todo-no="">수정</button>
});


// 수정 레이어에서 취소 버튼 클릭 시,  수정 팝업 레이어 숨기고 상세 팝업 레이어 표시
updateCancel.addEventListener("click", () => {
  updateLayer.classList.add("popup-hidden");
  popupLayer.classList.remove("popup-hidden");
});

// 수정레이어에서 수정 버튼 클릭 시
updateBtn.addEventListener("click", (e) => {
	
	// 서버로 전달해야 하는 값을 JS 객체로 묶기
	const obj = {
		"todoNo" : e.target.dataset.todoNo,
		"todoTitle" : updateTitle.value,
		"todoContent" : updateContent.value
	};
	 
	// 비동기 요청
	fetch("/ajax/update", {
		method : "PUT",
		headers : {"Content-Type" : "application/json"},
		body : JSON.stringify(obj)
	})
	.then((response) => {return response.text()})
	.then((result) => {
		if(result > 0){
			alert("수정 성공");	
			updateLayer.classList.add("popup-hidden");
			
			// 수정한 내용이 출력되도록 해야 함
			popupTodoTitle.innerText = updateTitle.value;
			popupTodoContent.innerHTML = updateContent.value.replaceAll("\n", "<br>");
			
			popupLayer.classList.remove("popup-hidden");
			
			selectTodoList(); // 전체 목록 다시 조회
			
			updateTitle.value = "";
			updateContent.value = "";
			updateBtn.removeAttribute("data-todo-no"); // 속성 제거
		}else{
			alert("수정 실패");
		}
	});
});

