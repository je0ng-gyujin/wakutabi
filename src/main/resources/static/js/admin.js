// 답변이 존재하는 경우 true
const hasAnswer = true;

window.onload = function() {
  const btn = document.getElementById("answerBtn");
  if (btn && hasAnswer) {
    btn.textContent = "수정하기";
    btn.classList.remove("btn-primary");
    btn.classList.add("btn-warning");
    btn.setAttribute("onclick", "editAnswer()");
  }
};

function toggleAnswerForm() {
  const form = document.getElementById("answerForm");
  const defaultBtns = document.getElementById("defaultBtns"); // 답변하기/목록으로
  const answerBtns = document.getElementById("answerBtns");   // 등록/취소

  // form이 닫혀있는 경우 → 답변 폼 열기
  if (form.style.display === "none" || form.style.display === "") {
    form.style.display = "block";      // 답변 폼 열기
    defaultBtns.style.display = "none"; // 답변하기 + 목록으로 숨김
    answerBtns.style.display = "flex";  // 등록 + 취소 표시
  } 
  // form이 열려있는 경우 → 닫기
  else {
    form.style.display = "none";       // 답변 폼 닫기
    defaultBtns.style.display = "flex"; // 답변하기 + 목록으로 표시
    answerBtns.style.display = "none";  // 등록 + 취소 숨김
  }
}
