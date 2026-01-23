// 웹소켓 테스트 js

// 클라이언트 단에서 필요한 설정
// 1. sockJS 라이브러리 추가 필요(없어도 가능은 하다, 하지만 사용하는 게 쉽고 편하고, 안정성이 있음, 실무에서도 이용)
//    > common.html 에 추가

// 2. SockJS 객체 생성
const testSock = new SockJS("/testSock"); 
// - WebSocket 통신을 할 수 있게끔 해주는 객체를 생성함과 동시에 자동으로
//   http:://localhost/testSock으로 연결 요청을 보냄

// 3. 생성된 SockJS 객체 이용해 서버에 메시지 전달
const sendMessageFn = (name, str) => {
    // JSON 이용하여 데이터를 TEXT 형태로 전달
    const obj = {"name" : name, "str" : str};
    testSock.send(JSON.stringify(obj));
}

// 4. 서버로부터 클라이언트에게 웹소켓을 이용한 메시지가 전달된 경우
testSock.addEventListener("message", e => {
    // e.data : 서버로부터 전달받은 message
    const msg = JSON.parse(e.data); // JSON 객체 -> JS 객체로 변환
    console.log(`${msg.name} 의 메시지 : ${msg.str}`);
});

