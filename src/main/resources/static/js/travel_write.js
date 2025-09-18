document.addEventListener('DOMContentLoaded', () => {

    // 전역 변수
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    const uploadedImages = document.getElementById('uploadedImages');
    const orderNumberInput = document.getElementById('orderNumber');

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

                // 삭제 버튼 이벤트
                const removeBtn = imageDiv.querySelector('.remove-image');
                removeBtn.addEventListener('click', () => {
                    imageDiv.remove();
                    updateImgOrder();
                });

                // Sortable 초기화
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
    const tagInput = document.getElementById('tag');

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

    // ✅ submit 이벤트: 이미지 + 데이터 전송
    document.getElementById("travelRegistrationForm").addEventListener("submit", (e) => {
        e.preventDefault();
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