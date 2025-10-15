document.addEventListener('DOMContentLoaded', () => {
    // 전역 변수
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    const uploadedImages = document.getElementById('uploadedImages');
    const orderNumberInput = document.getElementById('orderNumber');

    // 중복 제출 방지를 위한 전역 플래그
    let isSubmitting = false;

    // 날짜 입력 필드와 에러 메시지 요소
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const recruitEndDateInput = document.getElementById('recruitEndDate');
    const startDateError = document.getElementById('startDateError');
    const endDateError = document.getElementById('endDateError');
    const recruitEndDateError = document.getElementById('recruitEndDateError');

    // 예상 비용 입력 필드
    const estimatedCostInput = document.getElementById('estimatedCost');

    // 태그 입력 필드와 태그 영역
    const tagInput = document.getElementById('tagsInput');
    const tagHiddenInput = document.getElementById('tag');
    const tagSection = document.querySelector('.form-section.p-3.rounded.mb-4:nth-child(4)');

    // 미리보기 섹션 요소 가져오기
    const previewImage = document.querySelector('.card-img-top');
    const previewTitle = document.getElementById('previewTitle');
    const previewRegion = document.getElementById('previewRegion');
    const previewDates = document.getElementById('previewDates');
    const previewDescription = document.getElementById('previewDescription');
    const previewParticipants = document.getElementById('previewParticipants');
    const previewTags = document.getElementById('previewTags');

    // ⭐ 미리보기 텍스트 요소들을 한 줄로 표시되도록 스타일 적용
    if (previewTitle) {
        previewTitle.style.whiteSpace = 'nowrap';
        previewTitle.style.overflow = 'hidden';
        previewTitle.style.textOverflow = 'ellipsis';
    }
    if (previewDescription) {
        previewDescription.style.whiteSpace = 'nowrap';
        previewDescription.style.overflow = 'hidden';
        previewDescription.style.textOverflow = 'ellipsis';
    }
    if (previewDates) {
        previewDates.style.whiteSpace = 'nowrap';
        previewDates.style.overflow = 'hidden';
        previewDates.style.textOverflow = 'ellipsis';
    }
    if (previewRegion) {
        previewRegion.style.whiteSpace = 'nowrap';
        previewRegion.style.overflow = 'hidden';
        previewRegion.style.textOverflow = 'ellipsis';
    }

    // 삭제된 이미지 ID를 저장할 hidden input 필드
    const deletedImageIdsInput = document.getElementById('deletedImageIds');
    // 남아있는 이미지 ID를 저장할 hidden input 필드
    const remainImageIdsInput = document.getElementById('remainImageIds');

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
    endDateInput.min = getTodayFormatted();
    recruitEndDateInput.min = getTodayFormatted();

    // ⭐ 날짜 유효성 검사 함수 (전역 스코프에 노출)
    window.validateDates = function() {
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        let isValid = true;

        // 출발일 검사
        if (startDateInput.value) {
            const startDate = new Date(startDateInput.value);
            startDate.setHours(0, 0, 0, 0);
            
            if (startDate < today) {
                startDateError.textContent = '출발일은 오늘 이후로 선택해주세요.';
                startDateError.style.display = 'block';
                isValid = false;
            } else {
                startDateError.style.display = 'none';
            }
        } else {
            startDateError.style.display = 'none';
        }

        // 도착일 검사
        if (endDateInput.value) {
            const endDate = new Date(endDateInput.value);
            endDate.setHours(0, 0, 0, 0);
            
            if (endDate < today) {
                endDateError.textContent = '도착일은 오늘 이후로 선택해주세요.';
                endDateError.style.display = 'block';
                isValid = false;
            } else if (startDateInput.value) {
                const startDate = new Date(startDateInput.value);
                startDate.setHours(0, 0, 0, 0);
                
                if (endDate < startDate) {
                    endDateError.textContent = '도착일은 출발일 이후로 선택해주세요.';
                    endDateError.style.display = 'block';
                    isValid = false;
                } else {
                    endDateError.style.display = 'none';
                }
            } else {
                endDateError.style.display = 'none';
            }
        } else {
            endDateError.style.display = 'none';
        }

        // 모집종료일 검사
        if (recruitEndDateInput.value) {
            const recruitEndDate = new Date(recruitEndDateInput.value);
            recruitEndDate.setHours(0, 0, 0, 0);
            
            if (recruitEndDate < today) {
                recruitEndDateError.textContent = '모집종료일은 오늘 이후로 선택해주세요.';
                recruitEndDateError.style.display = 'block';
                isValid = false;
            } else if (startDateInput.value) {
                const startDate = new Date(startDateInput.value);
                startDate.setHours(0, 0, 0, 0);
                
                if (recruitEndDate >= startDate) {  // 수정: 모집종료일이 출발일과 같거나 늦으면 에러
                    recruitEndDateError.textContent = '모집종료일은 출발일 이전으로 선택해주세요.';
                    recruitEndDateError.style.display = 'block';
                    isValid = false;
                } else {
                    recruitEndDateError.style.display = 'none';
                }
            } else {
                recruitEndDateError.style.display = 'none';
            }
        } else {
            recruitEndDateError.style.display = 'none';
        }

        return isValid;
    }

    // ⭐ 날짜 입력 필드에 이벤트 리스너 추가 (여러 이벤트 추가로 실시간 검증 보장)
    startDateInput.addEventListener('change', window.validateDates);
    startDateInput.addEventListener('input', window.validateDates);
    startDateInput.addEventListener('blur', window.validateDates);
    startDateInput.addEventListener('keyup', window.validateDates);
    
    endDateInput.addEventListener('change', window.validateDates);
    endDateInput.addEventListener('input', window.validateDates);
    endDateInput.addEventListener('blur', window.validateDates);
    endDateInput.addEventListener('keyup', window.validateDates);
    
    recruitEndDateInput.addEventListener('change', window.validateDates);
    recruitEndDateInput.addEventListener('input', window.validateDates);
    recruitEndDateInput.addEventListener('blur', window.validateDates);
    recruitEndDateInput.addEventListener('keyup', window.validateDates);

    // ⭐ 추가: 주기적으로 날짜 값 변화 체크 (date picker 사용 시를 위해)
    let lastStartDate = startDateInput.value;
    let lastEndDate = endDateInput.value;
    let lastRecruitEndDate = recruitEndDateInput.value;
    
    setInterval(() => {
        if (startDateInput.value !== lastStartDate || 
            endDateInput.value !== lastEndDate || 
            recruitEndDateInput.value !== lastRecruitEndDate) {
            
            lastStartDate = startDateInput.value;
            lastEndDate = endDateInput.value;
            lastRecruitEndDate = recruitEndDateInput.value;
            
            window.validateDates();
        }
    }, 500); // 0.5초마다 체크

    // 이미지 업로드 클릭/드래그 이벤트 ... (생략) ...
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
    let imageOrderData = []; // 이미지 순서와 id 저장

    // 기존 이미지도 드래그 앤 드롭 가능하도록 Sortable 초기화
    if (uploadedImages && uploadedImages.children.length > 0) {
        imageSortable = new Sortable(uploadedImages, {
            animation: 150,
            ghostClass: 'sortable-ghost',
            handle: 'img',
            onEnd: () => updateImgOrder()
        });
    }

    function handleFiles(files) {
        Array.from(files).forEach(file => {
            // ⭐ 최대 5개 이미지 제한
            const currentImageCount = uploadedImages.querySelectorAll('.uploaded-image').length;
            if (currentImageCount >= 5) {
                alert('이미지는 최대 5개까지만 업로드할 수 있습니다.');
                return;
            }
            
            if (!file.type.startsWith('image/')) return;
            const reader = new FileReader();
            // 기존 이미지 처리 로직 ... (생략)
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
                    // Sortable 초기화 로직 ... (생략)
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
            const id = imgDiv.dataset.id;
            imgDiv.dataset.order = order;
            if (id) {
                imageOrderData.push({
                    id: id,
                    order: order
                });
            }
        });
    // 기존 이미지 순서 정보 JSON으로 저장
    orderNumberInput.value = JSON.stringify(imageOrderData);
        // ⭐ 이미지 삭제 시 미리보기 업데이트
        if (uploadedImages.children.length > 0) {
            previewImage.src = uploadedImages.children[0].querySelector('img').src;
        } else {
            // 이미지가 없으면 기본 이미지로 되돌리기
            previewImage.src = 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=400&h=200&fit=crop';
        }
    }

    // 태그 선택 기능
    // Set에 객체 대신 안전하게 태그 값(value)만 저장합니다.
    const selectedTagValues = new Set(); 
    const allTagItems = document.querySelectorAll('.tag-item');

    // 🏷️ 기존 태그를 불러와서 설정하는 함수
    function initializeSelectedTags() {
        const selectedTagListContainer = document.querySelector('.tag-list');
        const initialTagsString = selectedTagListContainer ? selectedTagListContainer.dataset.selectedTag : '';
        
        if (!initialTagsString) return;
        
        const initialTags = initialTagsString.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);
        
        initialTags.forEach(tagValue => {
            const tagItem = document.querySelector(`.tag-item[data-tag="${tagValue}"]`);
            if (tagItem) {
                // 1. 시각적으로 'selected' 클래스 추가
                tagItem.classList.add('selected');
                
                // 2. selectedTagValues Set에 값(value) 추가
                selectedTagValues.add(tagValue);
            }
        });
    
        // 3. Hidden Input 필드 값 최신화
        tagHiddenInput.value = Array.from(selectedTagValues).join(',');
    
        // ⭐⭐ 여기를 추가하여 미리보기를 초기화 시점에 업데이트합니다! ⭐⭐
        updatePreviewTags();
    }
    
    // ----------------------------------------------------------------------
    
    allTagItems.forEach(tag => {
        tag.addEventListener('click', () => {
            const tagValue = tag.dataset.tag;
            tag.classList.toggle('selected');

            if (tag.classList.contains('selected')) {
                selectedTagValues.add(tagValue); // 값(value)만 저장
            } else {
                selectedTagValues.delete(tagValue); // 값(value)으로 삭제
            }

            // Hidden Input과 미리보기 업데이트
            tagHiddenInput.value = Array.from(selectedTagValues).join(',');
            updatePreviewTags();
        });
    });

    // 🗓️ 날짜 유효성 검증 함수 ... (생략) ...
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

    // ⭐ 여행 기간 자동 계산 함수 ... (생략) ...
    function calculateTravelPeriod() {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        const travelPeriodElement = document.getElementById('travelPeriod');

        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);
            const diffInTime = end.getTime() - start.getTime();
            const diffInDays = Math.ceil(diffInTime / (1000 * 60 * 60 * 24));

            if (diffInDays < 0) {
                travelPeriodElement.textContent = "날짜를 다시 확인해주세요";
                updatePreviewDates('날짜를 다시 확인해주세요');
                return;
            }

            travelPeriodElement.textContent = `${diffInDays}박 ${diffInDays + 1}일`;
            updatePreviewDates(`${startDate.replace(/-/g, '.')} ~ ${endDate.replace(/-/g, '.')}`);
        } else {
            travelPeriodElement.textContent = '자동 계산됩니다';
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

    // ✅ 예상 비용 입력 필드에 쉼표 포맷팅 기능 추가
    estimatedCostInput.addEventListener('input', (e) => {
        const cleanedValue = e.target.value.replace(/[^0-9]/g, '');
        const formattedValue = cleanedValue.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        e.target.value = formattedValue;
    });

    // ✅ submit 이벤트: 이미지 + 데이터 전송
    document.getElementById("travelRegistrationForm").addEventListener("submit", (e) => {
        e.preventDefault(); // 기본 폼 제출 방지

        // 중복 제출 방지 - 이미 제출 중이면 무시
        if (isSubmitting) {
            console.log("이미 제출 중입니다. 요청이 무시됩니다.");
            return false;
        }

        if (!window.validateDates()) {
            // 첫 번째 에러가 있는 필드로 포커스 이동
            if (startDateError.style.display === 'block') {
                startDateInput.focus();
            } else if (endDateError.style.display === 'block') {
                endDateInput.focus();
            } else if (recruitEndDateError.style.display === 'block') {
                recruitEndDateInput.focus();
            }
            return;
        }

        // ⭐ 태그 선택 여부 유효성 검사 추가
        if (selectedTagValues.size === 0) { // Set을 selectedTagValues로 변경
            alert('여행과 관련된 태그를 하나 이상 선택해주세요.');
            tagSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
            tagSection.style.outline = '2px solid #007bff';
            setTimeout(() => {
                tagSection.style.outline = '';
            }, 2000);
            return;
        }
        
        // 제출 상태를 true로 설정
        isSubmitting = true;
        // 폼 제출 전에 남아있는 이미지 ID 업데이트
        updateRemainImageIds();
        
        // 유효성 검사를 모두 통과하면 폼 제출
        const form = e.target;
        // 중복 제출 방지
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton.disabled) {
            return; // 이미 제출 중이면 무시
        }
        submitButton.disabled = true;
        submitButton.textContent = '수정 중...';
        // 예상 비용 필드의 값에서 쉼표 제거 후 전송
        estimatedCostInput.value = estimatedCostInput.value.replace(/,/g, '');
        // 기존 이미지 순서 정보 최신화
        updateImgOrder();
        const formData = new FormData(form);
        fetch(form.action, {
            method: "POST",
            body: formData
        })
        .then(res => res.text())
        .then(msg => {
            if (msg.includes("수정 완료")) {
                alert("수정 성공!");
                // 상세 페이지로 리다이렉트
                const travelId = document.querySelector('input[name="id"]').value;
                window.location.href = `/schedule/detail?id=${travelId}`;
            } else {
                alert("수정 실패: " + msg);
                // 버튼 상태 복구
                submitButton.disabled = false;
                submitButton.textContent = '수정하기';
                isSubmitting = false; // 제출 상태 초기화
            }
        })
        .catch(err => {
            console.error(err);
            alert("수정 실패!");
            // 버튼 상태 복구
            submitButton.disabled = false;
            submitButton.textContent = '수정하기';
            isSubmitting = false; // 제출 상태 초기화
        });
    });

    // ----------------------------------------------------
    // ⭐⭐ 미리보기 업데이트 함수들 ⭐⭐
    // ----------------------------------------------------
    
    // 여행 제목 및 설명 미리보기 업데이트 ... (생략) ...
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

    // 지역 미리보기 업데이트 ... (생략) ...
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

    // 참가자 미리보기 업데이트 ... (생략) ...
    function updatePreviewParticipants() {
        const maxParticipants = document.getElementById('maxParticipants').value;
        if (maxParticipants) {
            previewParticipants.textContent = `0/${maxParticipants}명`;
        } else {
            previewParticipants.textContent = '0/0명';
        }
    }
    document.getElementById('maxParticipants').addEventListener('change', updatePreviewParticipants);

    // 태그 미리보기 업데이트 (selectedTagValues Set을 사용하도록 수정)
    function updatePreviewTags() {
        previewTags.innerHTML = ''; // 기존 태그 제거
        if (selectedTagValues.size === 0) { // Set을 selectedTagValues로 변경
            previewTags.innerHTML = '<span class="badge bg-secondary me-1">태그</span>';
        } else {
            selectedTagValues.forEach(tagValue => {
                // 저장된 값(value)을 사용하여 해당 태그 아이템을 찾아 이름(name)을 가져옵니다.
                const tagItem = document.querySelector(`.tag-item[data-tag="${tagValue}"]`);
                const tagName = tagItem ? tagItem.textContent.trim() : tagValue; // 혹시 못 찾을 경우를 대비

                const tagSpan = document.createElement('span');
                tagSpan.className = 'badge bg-primary me-1';
                tagSpan.textContent = tagName;
                previewTags.appendChild(tagSpan);
            });
        }
    }

    // ----------------------------------------------------
    // 🌟🌟 초기화 호출 부분 🌟🌟
    // ----------------------------------------------------
    // 페이지 로드 시 초기 태그 상태를 설정합니다. (⭐ 새로 추가된 부분)
    initializeSelectedTags();

    // 페이지 로드 시 초기 미리보기 상태 설정
    calculateTravelPeriod();
    updatePreviewRegion();
    updatePreviewParticipants();
    // updatePreviewTags()는 initializeSelectedTags()에서 이미 호출됩니다.

    // ⭐ 서버에서 렌더링된 이미지의 X버튼에 삭제 이벤트 추가
    document.querySelectorAll('.remove-image').forEach(btn => {
        btn.addEventListener('click', () => {
            const imgDiv = btn.parentElement;
            const imgId = imgDiv.dataset.id; // 서버에 저장된 이미지 ID
            let deletedIds = deletedImageIdsInput.value ? deletedImageIdsInput.value.split(',') : [];
            deletedIds.push(imgId);
            deletedImageIdsInput.value = deletedIds.join(',');

            imgDiv.remove();
            updateImgOrder();
            updateRemainImageIds(); // 남아있는 이미지 ID 업데이트
        });
    });

    // 폼 제출 전에 남길 이미지 id를 모두 hidden에 저장
    function updateRemainImageIds() {
        const remainIds = [];
        document.querySelectorAll('.uploaded-image').forEach(div => {
            if (div.dataset.id) remainIds.push(div.dataset.id);
        });
        remainImageIdsInput.value = remainIds.join(',');
    }

});