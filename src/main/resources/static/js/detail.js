document.addEventListener("DOMContentLoaded", function () {
    const btn  = document.getElementById("joinBtn");
    const form = document.getElementById("joinForm");

    if (!btn || !form) {
        console.error("폼이나 버튼을 찾지 못했습니다.");
        return;
    }

    btn.addEventListener("click", function () {
        console.log("버튼 클릭됨"); 
        if (confirm("이 여행에 참가하시겠습니까?")) {
            alert("참가 신청되었습니다.");
            form.submit();
        }
    });
});