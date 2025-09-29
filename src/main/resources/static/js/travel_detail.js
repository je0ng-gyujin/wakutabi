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

document.addEventListener("DOMContentLoaded", function () {
    const btn = document.getElementById("joinBtn");
    const form = document.getElementById("joinForm");

    btn.addEventListener("click", function (e) {
        // 폼의 기본 제출 동작을 막습니다.
        e.preventDefault();

        // 사용자에게 확인 메시지를 먼저 보여줍니다.
        if (confirm("이 여행에 참가하시겠습니까?")) {
            // FormData 객체를 생성하여 폼 데이터를 가져옵니다.
            const formData = new FormData(form);

            // Fetch API를 사용하여 서버에 데이터를 전송합니다.
            fetch(form.action, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json()) // JSON 파싱
            .then(data => {
                if (data.status === "success") {
                    alert(data.message); // "참가 신청이 완료되었습니다."
                    // location.reload();
                } else {
                    alert(data.message); // "호스트는 참가신청 할 수 없습니다."
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("네트워크 오류가 발생했습니다.");
            });
        }
    });
});