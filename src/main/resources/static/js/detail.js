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