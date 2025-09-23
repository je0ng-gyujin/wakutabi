document.addEventListener("DOMContentLoaded", () => {
    const deleteBtn = document.getElementById("deleteBtn");

    if (deleteBtn) {
        deleteBtn.addEventListener("click", () => {
            const tripId = deleteBtn.getAttribute("data-id");
            if (!confirm("정말 삭제하시겠습니까?")) return;

            // CSRF 토큰과 헤더를 가져올 때, 요소가 존재하는지 먼저 확인
            const csrfMeta = document.querySelector('meta[name="_csrf"]');
            const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

            // meta 태그가 없으면 함수 실행 중단
            if (!csrfMeta || !csrfHeaderMeta) {
                console.error("CSRF meta tags not found.");
                alert("삭제 기능을 사용할 수 없습니다. (보안 설정 오류)");
                return;
            }

            const token = csrfMeta.getAttribute('content');
            const header = csrfHeaderMeta.getAttribute('content');

            fetch("/schedule/traveldelete", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    [header]: token
                },
                body: JSON.stringify({ id: tripId })
            })
            .then(res => res.text())
            .then(msg => {
                alert(msg);
                if (msg.includes("완료")) {
                    location.href = "/schedule/list";
                }
            })
            .catch(err => console.error("삭제 요청 실패", err));
        });
    }
});