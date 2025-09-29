document.addEventListener('DOMContentLoaded', () => {
    // 전역 변수
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    const uploadedImages = document.getElementById('uploadedImages');
    const orderNumberInput = document.getElementById('orderNumber');
    // ⭐ 추가: 삭제된 기존 이미지 ID를 저장할 Hidden 필드
    const deletedImageIdsInput = document.getElementById('deletedImageIds'); 
    const newImageFiles = new Map(); // key: uuid, value: File 객체

    
    // 날짜 입력 필드와 에러 메시지 요소
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const startDateError = document.getElementById('startDateError');
    const endDateError = document.getElementById('endDateError');
    
    // 태그 입력 필드와 태그 영역
    const tagInput = document.getElementById('tag');
    const tagListArea = document.querySelector('.tag-list');
    const tagSection = document.querySelector('.form-section.p-3.rounded.mb-4:nth-child(4)'); 
    const allTagItems = document.querySelectorAll('.tag-item');

    // 선택된 태그를 추적하는 Set
    const selectedTags = new Set(); 
    // ⭐ 추가: 삭제된 이미지 ID를 추적하는 Set
    const deletedImageIds = new Set(); 

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
    let imageOrderData = []; // 이미지 순서와 UUID/ID 저장

    function handleFiles(files) {
        Array.from(files).forEach(file => {
            if (!file.type.startsWith('image/')) return;
            const reader = new FileReader();
            // 파일별 고유 식별자 (새로운 파일만 UUID 가짐)
            const fileUuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                var r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
            
            newImageFiles.set(fileUuid, file);

            reader.onload = (e) => {
                const imageDiv = document.createElement('div');
                imageDiv.className = 'uploaded-image new-image'; // 새 이미지 구분
                imageDiv.dataset.uuid = fileUuid;
                
                // 순서 설정 (단순히 현재 개수 + 1)
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
                    // ⭐ 삭제 로직: 기존 이미지(data-id)인 경우 삭제 Set에 추가, 새 이미지는 그냥 제거
                    if (imageDiv.dataset.id) {
                        deletedImageIds.add(imageDiv.dataset.id);
                        deletedImageIdsInput.value = Array.from(deletedImageIds).join(',');
                    }
                    // ⭐ 추가: 새 이미지인 경우 Map에서 파일 제거
                    if (imageDiv.dataset.uuid) { 
                        newImageFiles.delete(imageDiv.dataset.uuid);
                        }
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

    // ⭐ 이미지 순서 업데이트 함수 (ID와 UUID 모두 전송)
    function updateImgOrder() {
        const imgs = uploadedImages.querySelectorAll('.uploaded-image');
        imageOrderData = [];
        imgs.forEach((imgDiv, index) => {
            const order = index + 1;
            imgDiv.dataset.order = order;
            
            const uuid = imgDiv.dataset.uuid || null; // 새 이미지 UUID
            const id = imgDiv.dataset.id || null; // 기존 이미지 DB ID
            
            imageOrderData.push({
                order: order,
                uuid: uuid,
                id: id
            });
        });
        orderNumberInput.value = JSON.stringify(imageOrderData);
        // console.log("Image Order Data:", orderNumberInput.value);
    }
    
    // ⭐ 기존 이미지의 삭제 버튼에 이벤트 리스너 추가
    document.querySelectorAll('#uploadedImages .uploaded-image').forEach(imgDiv => {
        const removeBtn = imgDiv.querySelector('.remove-image');
        // 이미 핸들러가 있다면 건너뛰거나, 안전하게 제거 후 추가
        if (removeBtn && !removeBtn._hasListener) {
            removeBtn.addEventListener('click', function handler() {
                if (imgDiv.dataset.id) {
                    deletedImageIds.add(imgDiv.dataset.id);
                    deletedImageIdsInput.value = Array.from(deletedImageIds).join(',');
                }
                imgDiv.remove();
                updateImgOrder();
            });
            removeBtn._hasListener = true; // 중복 추가 방지 플래그
        }
    });

    // 페이지 로드 후 기존 이미지 및 태그 상태 초기화
    updateImgOrder(); 

    // ----------------------------------------------------
    // 태그 선택 기능
    // ----------------------------------------------------
    function initializeTags() {
        // Thymeleaf에서 바인딩된 기존 태그 문자열을 가져옵니다 (예: "food,nature")
        const existingTagsString = tagListArea.getAttribute('data-selected-tag') || '';
        const existingTags = existingTagsString.split(',').filter(tag => tag.trim() !== '');
    
        existingTags.forEach(tagValue => {
            const tagElement = document.querySelector(`.tag-item[data-tag="${tagValue}"]`);
            if (tagElement) {
                tagElement.classList.add('selected');
                selectedTags.add(tagValue);
            }
        });
        
        // Hidden input 값도 초기화 시점에 설정합니다.
        tagInput.value = Array.from(selectedTags).join(',');
    }
    
    allTagItems.forEach(tag => {
        tag.addEventListener('click', () => {
            const tagValue = tag.dataset.tag;
            tag.classList.toggle('selected');
    
            if (tag.classList.contains('selected')) {
                selectedTags.add(tagValue);
            } else {
                selectedTags.delete(tagValue);
            }
            
            // Hidden input 값 업데이트
            tagInput.value = Array.from(selectedTags).join(',');
        });
    });
    
    initializeTags(); 
    
    // ----------------------------------------------------
    // 날짜 유효성 검증 함수
    // ----------------------------------------------------
    function validateDates() {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        let isValid = true;

        startDateError.textContent = '';
        endDateError.textContent = '';

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
    
    endDateInput.addEventListener('change', validateDates);

    // ----------------------------------------------------
    // ✅ submit 이벤트: 이미지 + 데이터 전송
    // ----------------------------------------------------
    document.getElementById("travelRegistrationForm").addEventListener("submit", (e) => {
        e.preventDefault();
        // 제출 전에 날짜 유효성 검사
        if (!validateDates()) {
            endDateInput.focus();
            return;
        }

        if (selectedTags.size === 0) {
            alert('여행과 관련된 태그를 하나 이상 선택해주세요.');
            tagSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
            tagSection.style.outline = '2px solid #007bff';
            setTimeout(() => {
                tagSection.style.outline = '';
            }, 2000);
            return;
        }

        const form = e.target;
        const formData = new FormData(form);

        // ⭐ Hidden Fields의 최신 값을 FormData에 수동으로 추가/업데이트
        formData.set('orderNumber', orderNumberInput.value);
        formData.set('deletedImageIds', deletedImageIdsInput.value); 
        formData.set('tag', tagInput.value); 

        // ⭐ 새 이미지 파일들을 FormData에 추가
        newImageFiles.forEach((file, uuid) => {
            // orderNumberInput의 JSON 문자열을 파싱하여 현재 순서를 찾습니다.
            const orderData = JSON.parse(orderNumberInput.value);
            const item = orderData.find(d => d.uuid === uuid);
            
            // 순서가 변경되었을 수 있으므로, 파일명에 순서를 포함하여 전송 (옵션)
            // Spring에서 `List<MultipartFile> images`로 받을 경우, 순서가 중요하므로 파일을 순서대로 추가하는 것이 좋습니다.
            // 여기서는 간단히 모든 파일을 'images'라는 동일한 키로 추가합니다.
            // Spring 서버에서 순서 정보를 담고 있는 `orderNumber` 값(JSON)과 매핑하여 처리하게 됩니다.
            formData.append('images', file, file.name); 
        });

        fetch(form.action, {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(msg => {
            alert(msg);
            // 성공 시 상세 페이지로 이동하도록 리다이렉션 로직을 추가하는 것이 좋습니다.
            // 예: window.location.href = '/schedule/detail?id=' + travelId; 
        })
        .catch(err => {
            console.error(err);
            alert("업로드 실패!");
        });
    });
});