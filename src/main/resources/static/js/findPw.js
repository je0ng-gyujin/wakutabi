const sendCodeButton = document.getElementById('sendCodeButton');
       const emailInput = document.getElementById('email');
       const emailMessage = document.getElementById('emailMessage');
       const verificationCodeGroup = document.getElementById('verificationCodeGroup');
       
       // 비밀번호 그룹 추가
       const passwordGroup = document.getElementById('passwordGroup');
       const passwordMessage = document.getElementById('passwordMessage');
       const newPassword = document.getElementById('newPassword');
       const confirmNewPassword = document.getElementById('confirmNewPassword');

       const submitButton = document.getElementById('submitButton');
       const timerDisplay = document.getElementById('timer');
       const sendCodeUrl = sendCodeButton.getAttribute('data-url');

       let intervalId; // 타이머 ID

       function startTimer(durationInSeconds) {
           let timer = durationInSeconds;
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
                   sendCodeButton.disabled = false;
                   sendCodeButton.textContent = "인증번호 다시 받기";
                   submitButton.disabled = true;
               }
           }, 1000);
       }

       // '인증번호 받기' 버튼 클릭 이벤트
       sendCodeButton.addEventListener('click', function() {
           const email = emailInput.value;
           if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
               emailMessage.textContent = '올바른 이메일 주소를 입력하세요.';
               return;
           }

           emailMessage.textContent = '인증번호를 발송 중입니다...';
           sendCodeButton.disabled = true;

           fetch(sendCodeUrl, { 
               method: 'POST',
               headers: {
                   'Content-Type': 'application/x-www-form-urlencoded',
                   // 'X-CSRF-TOKEN': '[[${_csrf.token}]]' // CSRF 사용 시
               },
               body: 'email=' + encodeURIComponent(email)
           })
           .then(response => {
               if (response.ok) return response.text();
               return response.text().then(text => { throw new Error(text || '가입되지 않은 이메일입니다.'); });
           })
           .then(message => {
               emailMessage.textContent = message;
               emailMessage.style.color = 'blue';
               
               // 인증번호, 비밀번호 입력칸 모두 보이기
               verificationCodeGroup.style.display = 'flex'; // .form-group이 flex-direction: column이므로
               passwordGroup.style.display = 'flex';

               submitButton.disabled = false;
               emailInput.readOnly = true;
               startTimer(60 * 5); // 5분
           })
           .catch(error => {
               emailMessage.textContent = error.message;
               emailMessage.style.color = 'red';
               sendCodeButton.disabled = false;
           });
       });

       // 폼 제출 전 유효성 검사
       function validatePasswordForm() {
           const codeInput = document.getElementById('code');
           
           // 1. 인증번호 입력 확인
           if (verificationCodeGroup.style.display === 'flex' && !codeInput.value) {
               alert('인증번호를 입력하세요.');
               return false;
           }

           // 2. 비밀번호 일치 여부 확인
           if (newPassword.value !== confirmNewPassword.value) {
               passwordMessage.textContent = '새 비밀번호가 일치하지 않습니다.';
               confirmNewPassword.focus();
               return false; // 폼 제출 중단
           }

           // 3. (선택) 비밀번호 정책 검사 (예: 8자 이상)
           if (newPassword.value.length < 8) {
                passwordMessage.textContent = '비밀번호는 8자 이상이어야 합니다.';
                newPassword.focus();
                return false;
           }

           passwordMessage.textContent = ''; // 에러 없음
           return true; // 폼 제출 진행
       }

       // 실시간 비밀번호 일치 검사 (선택 사항)
       confirmNewPassword.addEventListener('keyup', function() {
           if (newPassword.value !== confirmNewPassword.value) {
               passwordMessage.textContent = '비밀번호가 일치하지 않습니다.';
           } else {
               passwordMessage.textContent = '';
           }
       });