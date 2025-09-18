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
    
    // 태그 입력 필드와 태그 영역
    const tagInput = document.getElementById('tag');
    const tagSection = document.querySelector('.form-section.p-3.rounded.mb-4:nth-child(4)'); // 태그 섹션 선택
    // nth-child(4)를 사용하거나, 태그 섹션에 별도의 id를 추가하는 것을 추천합니다. (e.g., id="tagSection")

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
    }

    // 태그 선택 기능
    const selectedTags = new Set();
    document.querySelectorAll('.tag-item').forEach(tag => {
        tag.addEventListener('click', () => {
            const tagValue = tag.dataset.tag;
            tag.classList.toggle('selected');
            if (tag.classList.contains('selected')) {
                selectedTags.add(tagValue);
            } else {
                selectedTags.delete(tagValue);
            }
            tagInput.value = Array.from(selectedTags).join(',');
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
    
    // 출발일과 귀국일 필드에 change 이벤트 리스너 추가
    endDateInput.addEventListener('change', validateDates);

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
            alert('여행과 관련된 태그를 하나 이상 선택해주세요.');
            tagSection.scrollIntoView({ behavior: 'smooth', block: 'center' }); // 태그 섹션으로 스크롤 이동
            tagSection.style.outline = '2px solid #007bff'; // 시각적 강조
            setTimeout(() => {
                tagSection.style.outline = ''; // 강조 효과 제거
            }, 2000);
            return;
        }

        // 유효성 검사를 모두 통과하면 폼 제출
        const form = e.target;
        const formData = new FormData(form);

        fetch(form.action, {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(msg => {
            alert(msg);
        })
        .catch(err => {
            console.error(err);
            alert("업로드 실패!");
        });
    });
});