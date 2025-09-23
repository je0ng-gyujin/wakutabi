/* 채팅 관련 js 시작 */
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
/* 채팅 관련 js 끝 */