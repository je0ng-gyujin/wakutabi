/* 채팅창 js 시작 */
const chatListId = document.getElementById("chatListPanel");
const chatRoomId = document.getElementById("chatRoomPanel");

document.querySelectorAll("#chatListPanel a").forEach(link => {
	link.addEventListener('click', (e) => {
		e.preventDefault();
		chatListId.classList.add("d-none");
		chatRoomId.classList.remove("d-none");
	});
});

const chatBackBtn = document.querySelector(".btn-wakutabi-back");
chatBackBtn.addEventListener("click", (e) => {
	e.preventDefault();
	chatRoomId.classList.add("d-none");
	chatListId.classList.remove("d-none");
});
/* 채팅창 js 끝 */

/* 웹소켓 js 시작 */
// 웹소켓 서버의 URL을 지정하여 연결 객체 생성
const socket = new WebSocket("ws://192.168.0.55:8088/ws/chat");

// 웹소켓 연결이 성공하면 호출되는 이벤트
socket.onopen = function(e) {
	console.log('웹소켓 채팅 연결 성공!');
};

const chatReceiveList = document.getElementById('messageList');

// 서버로부터 메시지를 수신하면 호출되는 이벤트
socket.onmessage = function(e) {
	const receivedObject = JSON.parse(e.data);
	const receivedMessage = receivedObject.message; // 메시지 내용
	const senderId = receivedObject.userId; // 발신자 ID

	const isAtBottom = (messageList.scrollHeight - messageList.scrollTop <= messageList.clientHeight + 1);
	const messageContainer = document.createElement('div');
	if (String(senderId) === String(myUserId)) { // 내 메시지
		// 1. 가장 바깥쪽 컨테이너
		messageContainer.classList.add('d-flex', 'justify-content-end', 'mb-3', 'align-items-end');

		// 2. 메시지 내용 버블
		const messageBubbleElement = document.createElement('div');
		messageBubbleElement.classList.add('p-2', 'bg-wakutabi-primary', 'text-white', 'rounded-3');

		// 3. 실제 메시지 텍스트
		const messageTextElement = document.createElement('p');
		messageTextElement.classList.add('mb-0');
		messageTextElement.textContent = receivedMessage; // 웹 소켓으로 가져온 데이터

		// 4. 시간 표시
		const timeElement = document.createElement('div');
		timeElement.classList.add('me-2', 'small', 'text-secondary', 'order-1');
		timeElement.textContent = '99:99';

		// 5. 요소들 조합
		messageBubbleElement.appendChild(messageTextElement);
		messageContainer.appendChild(messageBubbleElement);
		messageContainer.appendChild(timeElement);
	} else { // 다른 사람 메시지
		// 1. 메시지 컨테이너 (가장 바깥쪽)
		messageContainer.classList.add('d-flex', 'mb-3', 'align-items-end');
	
		// 2. 프로필 이미지
		const profileImageElement = document.createElement('img');
		profileImageElement.setAttribute('src', '/image/testuser.jpg'); // 실제 프로필사진 주소로 수정
		profileImageElement.style.cssText = 'width: 40px; height: 40px;';
		profileImageElement.classList.add('rounded-circle', 'align-self-start', 'm-1');
	
		// 3. 이름과 메시지 버블을 담을 컨테이너
		const contentContainer = document.createElement('div');
	
		// 4. 사용자 이름
		const usernameElement = document.createElement('div');
		usernameElement.classList.add('small', 'fw-bold', 'text-secondary');
		usernameElement.textContent = myUserId; // 실제 사용자 이름로 수정
	
		// 5. 메시지 내용 버블
		const messageBubbleElement = document.createElement('div');
		messageBubbleElement.classList.add('p-2', 'bg-light', 'rounded-3');
	
		// 6. 실제 메시지 텍스트
		const messageTextElement = document.createElement('p');
		messageTextElement.classList.add('mb-0');
		messageTextElement.textContent = receivedMessage; // 웹 소켓으로 가져온 데이터
	
		// 7. 시간 표시
		const timeElement = document.createElement('div');
		timeElement.classList.add('ms-2', 'small', 'text-secondary');
		timeElement.textContent = '99:99' // 실제 시간으로 수정
	
		// 8. 자식 요소들 조합해 출력할 메시지 요소 완성
		messageBubbleElement.appendChild(messageTextElement);
		contentContainer.appendChild(usernameElement);
		contentContainer.appendChild(messageBubbleElement);
		messageContainer.appendChild(profileImageElement);
		messageContainer.appendChild(contentContainer);
		messageContainer.appendChild(timeElement);
	}
		
	chatReceiveList.appendChild(messageContainer);
	
	if (isAtBottom) {
		messageList.scrollTop = messageList.scrollHeight;
	}
};

// 웹소켓 연결이 닫히면 호출되는 이벤트
socket.onclose = function(e) {
	console.log('연결 종료');
};

// 에러 발생 시 호출되는 이벤트
socket.onerror = function(e) {
	console.error('에러 발생:', e);
};

// 입력 필드와 버튼 요소
const chatSendText = document.getElementById('chatSendText');
const chatSendBtn = document.getElementById('chatSendBtn');
const myUserId = sessionStorage.getItem('userId');
chatSendBtn.addEventListener('click', (e) => {
	
	if (chatSendText.value.trim() !== '') {
		const messageObject = {
			userId: myUserId,
			message: chatSendText.value
		};
		socket.send(JSON.stringify(messageObject));
		chatSendText.value = '';
	}
});

chatSendText.addEventListener('keydown', (e) => {
	if (e.key === 'Enter') {
		e.preventDefault();
		if (chatSendText.value.trim() !== '') {
			const messageObject = {
				userId: myUserId,
				message: chatSendText.value
			};
			socket.send(JSON.stringify(messageObject));
			chatSendText.value = '';
		}
	}
});

