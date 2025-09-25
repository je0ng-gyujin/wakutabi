/* 채팅창 js 시작 */
const chatListId = document.getElementById("chatListPanel");
const chatRoomId = document.getElementById("chatRoomPanel");
const chatReceiveList = document.getElementById('messageList');
const myUserId = sessionStorage.getItem('userId');

let socket = null;

/**
 * 주어진 메시지 데이터로 채팅 메시지 HTML 요소를 생성하고 반환합니다.
 * @param {object} messageData - 메시지 데이터 객체 (예: {userId: '...', message: '...', ...})
 * @param {string} myUserId - 현재 로그인한 사용자의 ID
 * @returns {HTMLElement} - 생성된 메시지 컨테이너 요소
 */
function createMessageElement(messageData, myUserId) {
	const isMyMessage = String(messageData.userId) === String(myUserId);

	// 1. 가장 바깥쪽 컨테이너 생성
	const messageContainer = document.createElement('div');
	messageContainer.classList.add('d-flex', 'mb-3', 'align-items-end');

	// 내 메시지일 경우 우측 정렬
	if (isMyMessage) {
		messageContainer.classList.add('justify-content-end');
	}

	// 2. 메시지 버블 및 관련 정보 컨테이너
	const contentContainer = document.createElement('div');
	contentContainer.classList.add('d-flex', 'flex-column', 'align-items-start');

	// 3. 상대방 메시지일 경우에만 프로필 이미지와 닉네임 추가
	if (!isMyMessage) {
		const profileImageElement = document.createElement('img');
		profileImageElement.setAttribute('src', messageData.userImagePath || '/image/testuser.jpg'); // DTO에 imagePath 필드가 있다고 가정
		profileImageElement.style.cssText = 'width: 40px; height: 40px;';
		profileImageElement.classList.add('rounded-circle', 'align-self-start', 'm-1');
		messageContainer.appendChild(profileImageElement);

		const usernameElement = document.createElement('div');
		usernameElement.classList.add('small', 'fw-bold', 'text-secondary');
		usernameElement.textContent = messageData.nickname;
		contentContainer.appendChild(usernameElement);
	}

	// 4. 메시지 내용 버블
	const messageBubbleElement = document.createElement('div');
	messageBubbleElement.classList.add('p-2', 'rounded-3');
	if (isMyMessage) {
		messageBubbleElement.classList.add('bg-wakutabi-primary', 'text-white');
	} else {
		messageBubbleElement.classList.add('bg-light');
	}

	// 5. 실제 메시지 텍스트
	const messageTextElement = document.createElement('p');
	messageTextElement.classList.add('mb-0');
	messageTextElement.textContent = messageData.message;

	// 6. 시간 표시
	const timeElement = document.createElement('div');
	timeElement.classList.add('small', 'text-secondary');
	if (isMyMessage) {
		timeElement.classList.add('mx-1', 'order-1');
	} else {
		timeElement.classList.add('mx-1');
	}
	// DTO에서 받은 시간 형식을 가공하여 표시할 수 있습니다 (예: moment.js 사용)
	timeElement.textContent = messageData.createdAt ? new Date(messageData.createdAt).toLocaleTimeString('ko-KR', {
	    hour: '2-digit', 
	    minute: '2-digit', 
	    hour12: false
	}) : '99:99';

	// 7. 요소 조합
	messageBubbleElement.appendChild(messageTextElement);
	contentContainer.appendChild(messageBubbleElement);

	messageContainer.appendChild(contentContainer);
	messageContainer.appendChild(timeElement);

	return messageContainer;
}

/**
 * 메시지 목록을 렌더링하는 함수
 * @param {Array} messages - DB에서 불러온 메시지 목록 배열
 */
function renderMessages(messages) {
	// 기존 채팅 목록 초기화
	chatReceiveList.innerHTML = '';

	// 메시지 목록을 오래된 순으로 정렬 (필요시)
	messages.reverse().forEach(msg => {
		const messageElement = createMessageElement(msg, myUserId);
		chatReceiveList.appendChild(messageElement);
	});

	// 스크롤을 맨 아래로 이동
	setTimeout(() => {
		chatReceiveList.scrollTop = chatReceiveList.scrollHeight;
	}, 0);
}

// 채팅방 클릭 시 이벤트
document.querySelectorAll("#chatListPanel a").forEach(link => {
	link.addEventListener('click', (e) => {
		e.preventDefault();

		const roomId = e.currentTarget.dataset.roomId;
		const tripId = e.currentTarget.dataset.tripId;
		const title = e.currentTarget.dataset.title;

		// DB에서 메시지를 가져오는 Ajax 요청
		$.ajax({
			url: "/chat/messages/" + roomId,
			type: "GET",
			dataType: "json",
			success: function (messages) {
				console.log("채팅 메시지 가져오기 성공 : ", messages);

				// 화면 렌더링
				renderMessages(messages); // <-- AJAX 성공 시 렌더링 함수 호출

				chatListId.classList.add("d-none");
				chatRoomId.classList.remove("d-none");

				// 기존 웹소켓 연결이 있다면 닫고 새로 연결
				if (socket && socket.readyState === WebSocket.OPEN) {
					socket.close();
				}

				socket = new WebSocket("ws://localhost:8088/ws/chat?roomId=" + roomId);

				// 웹소켓 연결이 성공하면 호출되는 이벤트
				socket.onopen = function (e) {
					console.log('웹소켓 채팅 연결 성공! 채팅방 ID : ', roomId);
					document.getElementById('chatRoomTitle').textContent = title;
					document.getElementById('chatRoomTitle').setAttribute('href', '/schedule/detail?id=' + tripId);
				};

				// 웹소켓 서버로부터 메시지를 수신하면 호출되는 이벤트
				socket.onmessage = function (e) {
					const receivedObject = JSON.parse(e.data); // <-- JSON 데이터 파싱
					const newMessageElement = createMessageElement(receivedObject, myUserId); // 새로운 메시지 요소를 생성
					const isAtBottom = (chatReceiveList.scrollHeight - chatReceiveList.scrollTop <= chatReceiveList.clientHeight + 1); // 채팅방 스크롤이 최하단인지 확인
					// 채팅 목록에 추가
					chatReceiveList.appendChild(newMessageElement);

					if (isAtBottom) {
						chatReceiveList.scrollTop = chatReceiveList.scrollHeight;
					}
				};

				// 웹소켓 연결이 닫히면 호출되는 이벤트
				socket.onclose = function (e) {
					console.log('연결 종료');
				};

				// 에러 발생 시 호출되는 이벤트
				socket.onerror = function (e) {
					console.error('에러 발생:', e);
				};
			} // success: function end
		}) // $.ajax end
	});
});

const chatBackBtn = document.querySelector(".btn-wakutabi-back");
chatBackBtn.addEventListener("click", (e) => {
	e.preventDefault();
	chatRoomId.classList.add("d-none");
	chatListId.classList.remove("d-none");

	// 채팅방을 나갈 때 웹소켓 연결을 종료합니다.
	if (socket) {
		socket.close();
		socket = null;
	}
});
/* 채팅창 js 끝 */

/* 웹소켓 js 시작 */
// 입력 필드와 버튼 요소
const chatSendText = document.getElementById('chatSendText');
const chatSendBtn = document.getElementById('chatSendBtn');

chatSendBtn.addEventListener('click', (e) => {
	// socket이 연결된 상태인지 확인합니다.
	if (socket && socket.readyState === WebSocket.OPEN) {
		if (chatSendText.value.trim() !== '') {
			const messageObject = {
				"type": "TEXT",
				"roomId": roomId,
				"userId": myUserId,
				"message": chatSendText.value
			};
			socket.send(JSON.stringify(messageObject));
			chatSendText.value = '';
		}
	}
});

chatSendText.addEventListener('keydown', (e) => {
	if (e.key === 'Enter') {
		e.preventDefault();
		// socket이 연결된 상태인지 확인합니다.
		if (socket && socket.readyState === WebSocket.OPEN) {
			if (chatSendText.value.trim() !== '') {
				const messageObject = {
					"type": "TEXT",
					"userId": myUserId,
					"message": chatSendText.value
				};
				socket.send(JSON.stringify(messageObject));
				chatSendText.value = '';
			}
		}
	}
});
/* 웹소켓 js 끝 */