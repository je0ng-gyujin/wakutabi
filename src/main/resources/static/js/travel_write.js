    document.addEventListener('DOMContentLoaded', () => {
        // 전역 변수
        const imageUploadArea = document.getElementById('imageUploadArea');
        const imageInput = document.getElementById('imageInput');
        const uploadedImages = document.getElementById('uploadedImages');
        const orderNumberInput = document.getElementById('orderNumber');

        // 날짜 입력 필드와 에러 메시지 요소
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');
        const startDateError = document.getElementById('startDateError');
        const endDateError = document.getElementById('endDateError');

        // 예상 비용 입력 필드
        const estimatedCostInput = document.getElementById('estimatedCost');

        // 태그 입력 필드와 태그 영역
        const tagInput = document.getElementById('tagsInput');
        const tagSection = document.querySelector('.form-section.p-3.rounded.mb-4:nth-child(4)');

        // ⭐ 미리보기 섹션 요소 가져오기
        const previewImage = document.querySelector('.card-img-top');
        const previewTitle = document.getElementById('previewTitle');
        const previewRegion = document.getElementById('previewRegion');
        const previewDates = document.getElementById('previewDates');
        const previewDescription = document.getElementById('previewDescription');
        const previewParticipants = document.getElementById('previewParticipants');
        const previewTags = document.getElementById('previewTags');
        const previewAgeLimit = document.getElementById('previewAgeLimit');
        const previewGenderLimit = document.getElementById('previewGenderLimit');

        // 오늘 날짜를 YYYY-MM-DD 형식으로 반환하는 함수
        function getTodayFormatted() {
            const today = new Date();
            const year = today.getFullYear();
            const month = (today.getMonth() + 1).toString().padStart(2, '0');
            const day = today.getDate().toString().padStart(2, '0');
            return `${year}-${month}-${day}`;
        }

        // 페이지 로드 시 출발일의 최소 날짜를 오늘로 설정
        startDateInput.min = getTodayFormatted();

        // 이미지 업로드 클릭/드래그 이벤트
        imageUploadArea.addEventListener('click', () => imageInput.click());
        imageUploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            imageUploadArea.style.background = 'rgba(96, 181, 255, 0.15)';
        });
        imageUploadArea.addEventListener('dragleave', () => {
            imageUploadArea.style.background = 'rgba(96, 181, 255, 0.05)';
        });
        imageUploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            imageUploadArea.style.background = 'rgba(96, 181, 255, 0.05)';
            const files = e.dataTransfer.files;
            handleFiles(files);
        });
        imageInput.addEventListener('change', (e) => handleFiles(e.target.files));

        let imageSortable = null;
        let imageOrderData = []; // 이미지 순서와 UUID 저장

        function handleFiles(files) {
            Array.from(files).forEach(file => {
                if (!file.type.startsWith('image/')) return;
                const reader = new FileReader();
                const fileUuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                    var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                    return v.toString(16);
                });
                reader.onload = (e) => {
                    const imageDiv = document.createElement('div');
                    imageDiv.className = 'uploaded-image';
                    imageDiv.dataset.uuid = fileUuid;
                    const currentImageCount = uploadedImages.querySelectorAll('.uploaded-image').length;
                    imageDiv.dataset.order = currentImageCount + 1;
                    imageDiv.innerHTML = `
                        <img src="${e.target.result}" alt="업로드된 이미지">
                        <button class="remove-image">×</button>
                    `;
                    uploadedImages.appendChild(imageDiv);
                    updateImgOrder(); // 이미지 추가 후 순서 업데이트
                    const removeBtn = imageDiv.querySelector('.remove-image');
                    removeBtn.addEventListener('click', () => {
                        imageDiv.remove();
                        updateImgOrder();
                    });
                    if (imageSortable === null) {
                        imageSortable = new Sortable(uploadedImages, {
                            animation: 150,
                            ghostClass: 'sortable-ghost',
                            handle: 'img',
                            onEnd: () => updateImgOrder() // 드래그 종료 후 순서 업데이트
                        });
                    }
                    // ⭐ 이미지 업로드 시 미리보기 업데이트
                    if (uploadedImages.children.length > 0) {
                        previewImage.src = uploadedImages.children[0].querySelector('img').src;
                    }
                };
                reader.readAsDataURL(file);
            });
        }

        function updateImgOrder() {
            const imgs = uploadedImages.querySelectorAll('.uploaded-image');
            imageOrderData = [];
            imgs.forEach((imgDiv, index) => {
                const order = index + 1;
                const uuid = imgDiv.dataset.uuid;
                imgDiv.dataset.order = order;
                imageOrderData.push({
                    order: order,
                    uuid: uuid
                });
            });
            orderNumberInput.value = JSON.stringify(imageOrderData);
            console.log(orderNumberInput.value);
            // ⭐ 이미지 삭제 시 미리보기 업데이트
            if (uploadedImages.children.length > 0) {
                previewImage.src = uploadedImages.children[0].querySelector('img').src;
            } else {
                // 이미지가 없으면 기본 이미지로 되돌리기
                previewImage.src = 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=400&h=200&fit=crop';
            }
        }

        // 태그 선택 기능
        const selectedTags = new Map();
        document.querySelectorAll('.tag-item').forEach(tag => {
            tag.addEventListener('click', () => {
                const tagValue = tag.dataset.tag;
                const tagName = tag.textContent.trim(); // 태그 텍스트 가져오기 (이모지 포함)
                tag.classList.toggle('selected');
                if (tag.classList.contains('selected')) {
                    selectedTags.set(tagValue, tagName);
                } else {
                    selectedTags.delete(tagValue);
                }
                tagInput.value = Array.from(selectedTags.keys()).join(',');
                updatePreviewTags(); // 태그 미리보기 업데이트
            });
        });

        // 🗓️ 날짜 유효성 검증 함수
        function validateDates() {
            const startDate = startDateInput.value;
            const endDate = endDateInput.value;
            let isValid = true;

            // 에러 메시지 초기화
            startDateError.textContent = '';
            endDateError.textContent = '';

            // 1. 귀국일이 출발일보다 빠른지 확인
            if (startDate && endDate) {
                const startDateObj = new Date(startDate);
                const endDateObj = new Date(endDate);
                if (startDateObj > endDateObj) {
                    endDateError.textContent = '귀국일은 출발일보다 빠를 수 없습니다.';
                    isValid = false;
                }
            }

            return isValid;
        }

        // ⭐ 여행 기간 자동 계산 함수
        function calculateTravelPeriod() {
            const startDate = startDateInput.value;
            const endDate = endDateInput.value;
            const travelPeriodElement = document.getElementById('travelPeriod');

            // 출발일과 귀국일이 모두 입력되었을 때만 계산
            if (startDate && endDate) {
                const start = new Date(startDate);
                const end = new Date(endDate);
                const diffInTime = end.getTime() - start.getTime();

                // 밀리초를 일수로 변환
                const diffInDays = Math.ceil(diffInTime / (1000 * 60 * 60 * 24));

                // 날짜 차이가 0보다 작으면 '날짜를 다시 확인해주세요'
                if (diffInDays < 0) {
                    travelPeriodElement.textContent = "날짜를 다시 확인해주세요";
                    updatePreviewDates('날짜를 다시 확인해주세요');
                    return;
                }

                travelPeriodElement.textContent = `${diffInDays}박 ${diffInDays + 1}일`;
                updatePreviewDates(`${startDate.replace(/-/g, '.')} ~ ${endDate.replace(/-/g, '.')}`);
            } else {
                travelPeriodElement.textContent = '자동 계산됩니다'; // 입력이 없으면 기본 문구 표시
                updatePreviewDates('날짜를 선택하세요');
            }
        }

        // 출발일과 귀국일 필드에 change 이벤트 리스너 추가
        startDateInput.addEventListener('change', () => {
            validateDates();
            calculateTravelPeriod();
        });

        endDateInput.addEventListener('change', () => {
            validateDates();
            calculateTravelPeriod();
        });

        // 페이지 로드 시 초기 계산
        calculateTravelPeriod();

        // ✅ 예상 비용 입력 필드에 쉼표 포맷팅 기능 추가 (수정된 코드)
        estimatedCostInput.addEventListener('input', (e) => {
            const cleanedValue = e.target.value.replace(/[^0-9]/g, '');
            const formattedValue = cleanedValue.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            e.target.value = formattedValue;
        });

        // ✅ submit 이벤트: 이미지 + 데이터 전송
        document.getElementById("travelRegistrationForm").addEventListener("submit", (e) => {
            e.preventDefault();

            // 폼 제출 전 최종적으로 날짜 유효성 검증
            if (!validateDates()) {
                endDateInput.focus();
                return;
            }

            // ⭐ 태그 선택 여부 유효성 검사 추가
            if (selectedTags.size === 0) {
                SwalDefault.fire({
                    icon : 'warning',
                    title : '태그 선택 필요',
                    text : '여행과 관련된 태그를 하나 이상 선택해주세요.'
                });
                tagSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
                return;
            }

            // 유효성 검사를 모두 통과하면 폼 제출
            const form = e.target;

            // ⭐ 예상 비용 필드의 값에서 쉼표 제거 후 전송
            const estimatedCostValue = estimatedCostInput.value.replace(/,/g, '');
            const formData = new FormData(form);
            formData.set('estimatedCost', estimatedCostValue); // 쉼표가 제거된 값으로 덮어쓰기

            fetch(form.action, {
                method: "POST",
                body: formData
            })
            .then(res => res.json()) // 서버에서 json 반환
            .then(data => {
                // data.status, data.message 사용가능
                if(data.status === "success"){
                    SwalDefault.fire({
                        icon: 'success',
                        title: '등록 완료!',
                        text: data.message,
                        confirmButtonText: '확인'
                    }). then(() => {
                        // 확인 누르면 myTrip으로 이동
                        window.location.href = '/schedule/myTrips';
                    });
                } else {
                    SwalDefault.fire({
                        icon: 'error',
                        title: '등록 실패',
                        text: data.message
                    });
                }
            })
            .catch(err => {
                console.error(err);
                SwalDefault.fire({
                    icon: 'error',
                    title: '업로드 실패',
                    text: '서버 오류가 발생했습니다.'
                });
            });
        });

        // ----------------------------------------------------
        // ⭐⭐ 미리보기 업데이트 함수들 ⭐⭐
        // ----------------------------------------------------

        // 여행 제목 및 설명 미리보기 업데이트
        function updatePreviewText(e) {
            const targetId = e.target.id;
            const value = e.target.value;
            if (targetId === 'title') {
                previewTitle.textContent = value || '여행 제목을 입력하세요';
            } else if (targetId === 'content') {
                previewDescription.textContent = value || '여행 설명을 입력하세요';
            }
        }
        document.getElementById('title').addEventListener('input', updatePreviewText);
        document.getElementById('content').addEventListener('input', updatePreviewText);

        // 지역 미리보기 업데이트
        function updatePreviewRegion() {
            const locationSelect = document.getElementById('location');
            const selectedOption = locationSelect.options[locationSelect.selectedIndex];
            previewRegion.textContent = selectedOption.textContent;
        }
        document.getElementById('location').addEventListener('change', updatePreviewRegion);

        // 날짜 미리보기 업데이트 함수
        function updatePreviewDates(text) {
            previewDates.textContent = text;
        }

        // 참가자 미리보기 업데이트
        function updatePreviewParticipants() {
            const maxParticipants = document.getElementById('maxParticipants').value;
            if (maxParticipants) {
                previewParticipants.textContent = `0/${maxParticipants}명`;
            } else {
                previewParticipants.textContent = '0/0명';
            }
        }
        document.getElementById('maxParticipants').addEventListener('change', updatePreviewParticipants);

        // 태그 미리보기 업데이트
        function updatePreviewTags() {
            previewTags.innerHTML = ''; // 기존 태그 제거
            if (selectedTags.size === 0) {
                previewTags.innerHTML = '<span class="badge bg-secondary me-1">태그</span>';
            } else {
                selectedTags.forEach((tagName, tagValue) => {
                    const tagSpan = document.createElement('span');
                    tagSpan.className = 'badge bg-primary me-1';
                    tagSpan.textContent = tagName;
                    previewTags.appendChild(tagSpan);
                });
            }
        }
        updatePreviewTags(); // 초기 로드 시 한 번 실행

        // 페이지 로드 시 초기 미리보기 상태 설정
        updatePreviewRegion();
        updatePreviewParticipants();
    });