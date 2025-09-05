//프로필 이미지 미리보기
document.getElementById('profileImage').addEventListener('change', function (e) {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        document.querySelector('.profile-img').src = e.target.result;
    };
    reader.readAsDataURL(file);
});

//폼 유효성 검사
document.querySelector("form").addEventListener("submit", function (e) {
    const email = document.querySelector("input[name=email]").value;
    if (!email || !email.includes('@')) {
        alert("유효한 이메일을 입력해주세요.");
        e.preventDefault(); // 제출 방지
    }
});