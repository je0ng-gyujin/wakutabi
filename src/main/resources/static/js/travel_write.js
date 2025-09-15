document.addEventListener('DOMContentLoaded', () => {

	// 전역 변수
	const imageUploadArea = document.getElementById('imageUploadArea');
	const imageInput = document.getElementById('imageInput');
	const uploadedImages = document.getElementById('uploadedImages');
	const orderNumberInput = document.getElementById('orderNumber');

	// 이미지 업로드 기능
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

	imageInput.addEventListener('change', (e) => {
		handleFiles(e.target.files);
	});

	let imageSortable = null;
	
	function handleFiles(files) {
		Array.from(files).forEach(file => {
			if (file.type.startsWith('image/')) {
				const reader = new FileReader();
				
				const fileUuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
					var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
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
                    <button class="remove-image" onclick="this.parentElement.remove()">×</button>
                `;
					uploadedImages.appendChild(imageDiv);
					if (imageSortable === null) {
						imageSortable = new Sortable(uploadedImages, {
							animation: 150,
							ghostClass: 'sortable-ghost',
							handle: 'img', // <img> 태그를 드래그 핸들로 지정
							onEnd: function(evt) {
								// 드래그가 끝난 후 순서 재정렬
								updateImgOrder();
							}
						});
					}
				};
				
				
				reader.readAsDataURL(file);
				updateImgOrder();
			}
		});
	}
	
	let imageOrderData = []; // 이미지 순서와 src를 저장할 배열
	
	function updateImgOrder() { // 이미지 순서 바꾸는 함수

		const imgs = uploadedImages.querySelectorAll('.uploaded-image');
		imageOrderData = [];

		
		imgs.forEach((imgDiv, index) => {
			const order = index + 1;
			const uuid = imgDiv.dataset.uuid;

			// 각 이미지의 순서, ID, src를 배열에 객체로 저장합니다.
			imgDiv.dataset.order = order;

			imageOrderData.push({
				order: order,
				uuid: uuid
			});
		});
		
		// 배열을 JSON 문자열로 변환하여 hidden input에 할당
		orderNumberInput.value = JSON.stringify(imageOrderData);
		
		console.log(orderNumberInput.value);
	}

	// 미리보기 업데이트 기능
	function updatePreview() {
	}

	const selectedTags = new Set();
	const tagInput = document.getElementById('tag');

	document.querySelectorAll('.tag-item').forEach(tag => {
	    tag.addEventListener('click', () => {
	        const tagValue = tag.dataset.tag; // data-tag 속성 값 가져오기
	        tag.classList.toggle('selected'); // 클래스 토글

	        if (tag.classList.contains('selected')) {
	            // 'selected' 클래스가 있으면 Set에 값 추가
	            selectedTags.add(tagValue);
	        } else {
	            // 'selected' 클래스가 없으면 Set에서 값 삭제
	            selectedTags.delete(tagValue);
	        }

	        // Set 객체의 값들을 쉼표로 연결하여 input 태그에 할당
	        tagInput.value = Array.from(selectedTags).join(',');
	    });
	});

}); // document.addEventListener('DOMContentLoaded', () => {}); 닫음