document.addEventListener("DOMContentLoaded", function () {
    // 🔹 1. 프로필 이미지 미리보기 (요소가 존재할 경우만 실행)
    const profileInput = document.getElementById('profileImage');
    const profileImg = document.querySelector('.profile-img');
    if (profileInput && profileImg) {
        profileInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (!file) return;

            const reader = new FileReader();
            reader.onload = function (e) {
                profileImg.src = e.target.result;
            };
            reader.readAsDataURL(file);
        });
    }

    // 🔹 2. 이메일 유효성 검사 (이메일 input이 있는 경우만 실행)
    const emailInput = document.querySelector("input[name=email]");
    const form = document.querySelector("form");
    if (emailInput && form) {
        form.addEventListener("submit", function (e) {
            const email = emailInput.value;
            if (!email || !email.includes('@')) {
                alert("유효한 이메일을 입력해주세요.");
                e.preventDefault(); // 제출 방지
            }
        });
    }

    // 🔹 3. 기타 사유 선택 시, 사유 입력 칸 보여주기 (탈퇴 폼에만 적용)
    const reasonSelect = document.getElementById("reason");
    const otherReasonRow = document.getElementById("other-reason-row");

    if (reasonSelect && otherReasonRow) {
        reasonSelect.addEventListener("change", function () {
            if (this.value === "other") {
                otherReasonRow.style.display = "flex";
            } else {
                otherReasonRow.style.display = "none";
            }
        });
    }
});
