// 이미지 업로드 기능
const imageUploadArea = document.getElementById('imageUploadArea');
const imageInput = document.getElementById('imageInput');
const uploadedImages = document.getElementById('uploadedImages');

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

function handleFiles(files) {
	Array.from(files).forEach(file => {
		if (file.type.startsWith('image/')) {
			const reader = new FileReader();
			reader.onload = (e) => {
				const imageDiv = document.createElement('div');
				imageDiv.className = 'uploaded-image';
				imageDiv.innerHTML = `
                    <img src="${e.target.result}" alt="업로드된 이미지">
                    <button class="remove-image" onclick="this.parentElement.remove()">×</button>
                `;
				uploadedImages.appendChild(imageDiv);
			};
			reader.readAsDataURL(file);
		}
	});
}

// 미리보기 업데이트 기능
function updatePreview() {
}


// 폼 제출 처리
/*document.getElementById('travelRegistrationForm').addEventListener('submit', function(e) {
	e.preventDefault();

	// 유효성 검사
	const requiredFields = document.querySelectorAll('[required]');
	let isValid = true;

	requiredFields.forEach(field => {
		if (!field.value.trim()) {
			isValid = false;
			field.classList.add('is-invalid');
		} else {
			field.classList.remove('is-invalid');
		}
	});

	if (!isValid) {
		alert('필수 항목을 모두 입력해주세요.');
		return;
	}

});*/


document.querySelectorAll('.tag-item').forEach(tag => {
	tag.addEventListener('click', () => {
		tag.classList.toggle('selected');
	});
});


document.addEventListener("DOMContentLoaded", () => {
  const tagItems = document.querySelectorAll(".tag-item");
  const tagInput = document.getElementById("tag");
  let selectedTags = [];

  tagItems.forEach(item => {
    item.addEventListener("click", () => {
      const tagValue = item.getAttribute("data-tag");

      // 선택/해제 토글
      if (selectedTags.includes(tagValue)) {
        selectedTags = selectedTags.filter(tag => tag !== tagValue);
        item.classList.remove("selected"); // 선택된 스타일 제거
      } else {
        selectedTags.push(tagValue);
        item.classList.add("selected"); // 선택된 스타일 적용
      }

      // hidden input 값 업데이트 (콤마 구분)
      tagInput.value = selectedTags.join(",");
      console.log("현재 선택된 태그:", tagInput.value);
    });
  });
});
