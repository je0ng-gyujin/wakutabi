		const sendCodeButton = document.getElementById('sendCodeButton');
        const emailInput = document.getElementById('email');
        const emailMessage = document.getElementById('emailMessage');
        const verificationCodeGroup = document.getElementById('verificationCodeGroup');
        const submitButton = document.getElementById('submitButton');
        const timerDisplay = document.getElementById('timer');
        const sendCodeUrl = sendCodeButton.getAttribute('data-url'); // th:data-url 값 가져오기

        let intervalId; // 타이머 ID

        // 5분 타이머 시작 함수
        function startTimer(durationInSeconds) {
            let timer = durationInSeconds;
            
            // 기존 타이머가 있다면 중지
            if (intervalId) {
                clearInterval(intervalId);
            }

            intervalId = setInterval(function () {
                let minutes = parseInt(timer / 60, 10);
                let seconds = parseInt(timer % 60, 10);

                minutes = minutes < 10 ? "0" + minutes : minutes;
                seconds = seconds < 10 ? "0" + seconds : seconds;

                timerDisplay.textContent = minutes + ":" + seconds;

                if (--timer < 0) {
                    clearInterval(intervalId);
                    timerDisplay.textContent = "인증 시간이 만료되었습니다.";
                    sendCodeButton.disabled = false; // 버튼 다시 활성화
                    sendCodeButton.textContent = "인증번호 다시 받기";
                    submitButton.disabled = true; // 아이디 찾기 버튼 비활성화
                }
            }, 1000);
        }

        // '인증번호 받기' 버튼 클릭 이벤트
        sendCodeButton.addEventListener('click', function() {
            const email = emailInput.value;

            // 간단한 이메일 유효성 검사
            if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                emailMessage.textContent = '올바른 이메일 주소를 입력하세요.';
                return;
            }

            emailMessage.textContent = '인증번호를 발송 중입니다...';
            sendCodeButton.disabled = true; // 버튼 비활성화

            // fetch API를 사용해 서버에 AJAX 요청 (CSRF 토큰이 필요하면 headers에 추가해야 함)
            fetch(sendCodeUrl, { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    // 'X-CSRF-TOKEN': '[[${_csrf.token}]]' // Spring Security CSRF 사용 시
                },
                body: 'email=' + encodeURIComponent(email)
            })
            .then(response => {
                if (response.ok) {
                    return response.text(); // 성공 시 텍스트 응답 받기
                } else {
                    return response.text().then(text => { // 실패 시 텍스트 응답 받기
                        throw new Error(text || '가입되지 않은 이메일이거나 오류가 발생했습니다.');
                    });
                }
            })
            .then(message => {
                // 성공
                emailMessage.textContent = message; // "인증번호가 발송되었습니다."
                emailMessage.style.color = 'blue';
                verificationCodeGroup.style.display = 'block'; // 인증번호 입력칸 보이기
                submitButton.disabled = false; // '아이디 찾기' 버튼 활성화
                emailInput.readOnly = true; // 이메일 수정 방지
                startTimer(60 * 5); // 5분 타이머 시작
            })
            .catch(error => {
                // 실패
                emailMessage.textContent = error.message;
                emailMessage.style.color = 'red';
                sendCodeButton.disabled = false; // 버튼 다시 활성화
            });
        });

        // 폼 제출 전 인증번호 입력 여부 확인
        function validateForm() {
            const codeInput = document.getElementById('code');
            if (verificationCodeGroup.style.display === 'block' && !codeInput.value) {
                alert('인증번호를 입력하세요.');
                return false; // 폼 제출 중단
            }
            return true; // 폼 제출 진행
        }