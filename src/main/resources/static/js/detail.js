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
            .then(response => {
                // 서버의 응답 상태를 확인합니다.
                if (response.ok) {
                    // 성공적으로 처리되었을 때 알림을 띄웁니다.
                    alert("참가 신청되었습니다.");
                    // 필요하다면 페이지를 새로고침하거나 다른 동작을 수행할 수 있습니다.
                    // location.reload();
                } else {
                    // 오류가 발생했을 때 사용자에게 알립니다.
                    alert("참가 신청에 실패했습니다.");
                }
            })
            .catch(error => {
                // 네트워크 오류 등 예외 발생 시 알립니다.
                console.error('Error:', error);
                alert("네트워크 오류가 발생했습니다.");
            });
        }
    });
});