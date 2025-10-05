document.addEventListener("DOMContentLoaded", () => {
    const editBtn = document.getElementById("editBtn");
    const deleteBtn = document.getElementById("deleteBtn");

    if(editBtn){
        editBtn.addEventListener("click", (e) => {
        e.preventDefault(); // e태그 기본으로 막기
        const travelId = editBtn.getAttribute("data-id");
        const travelStatus = editBtn.getAttribute("data-status")?.toUpperCase();
        // 상태 한글로 매핑
        const statusMap = {
            "MATCHED" : "매칭완료",
            "CLOSED" : "여행종료",
            "CANCELED" : "여행취소"};
        // alert출력
        if(["MATCHED","CLOSED","CANCELED"].includes(travelStatus)){
            alert(`해당 여행은 [${statusMap[travelStatus]}] 상태로 수정할 수 없습니다.`);
            return;
        }
        // 정상일 경우에만
        location.href = `/schedule/edit?id=${travelId}`;
        });
    }
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

document.addEventListener("DOMContentLoaded", () => {
    // 1. 태그 영문명 -> 한글+이모지 매핑 객체 정의
    const tagMapping = {
        "foodie": "🍜 식도락",
        "activity": "🏃 액티비티",
        "nature": "🌲 자연",
        "otaku": "🎮 오타쿠",
        "shopping": "🛍️ 쇼핑",
        "smallGroup": "👤 소수팟",
        "largeGroup": "👥 다인팟",
        "indoor": "🏠 실내파",
        "outdoor": "🌞 실외파"
        // 필요한 다른 태그도 여기에 추가하세요.
    };

    // 2. 태그 컨테이너 요소 (id="travelTags")를 가져옵니다.
    const tagsContainer = document.getElementById('travelTags'); 

    if (tagsContainer) {
        // 3. HTML의 data-tags 속성에서 영문 태그 목록 문자열을 가져옵니다.
        const tagsRaw = tagsContainer.getAttribute('data-tags');
        
        // 4. 태그가 존재하면 처리합니다.
        if (tagsRaw) {
            const tagsList = tagsRaw.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);
            
            // 기존 "등록된 태그가 없습니다." 메시지를 지웁니다.
            tagsContainer.innerHTML = '';
            
            // 5. 각 영문 태그를 순회하며 이모지가 포함된 요소로 만들어 컨테이너에 추가합니다.
            tagsList.forEach(tagKey => {
                const displayTag = tagMapping[tagKey] || tagKey; // 변환된 한글+이모지 이름

                const tagElement = document.createElement('span');
                // 상세 페이지 디자인에 맞는 클래스를 사용해 주세요. (예: badge, text-bg-info)
                tagElement.classList.add('tag-item');
                tagElement.classList.add('me-2');
                
                tagElement.textContent = displayTag;

                tagsContainer.appendChild(tagElement);
            });
        }
    }
});
