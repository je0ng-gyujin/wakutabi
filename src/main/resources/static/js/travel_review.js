// 별점 기능
document.querySelectorAll('.rating-container').forEach(container => {
	const stars = container.querySelectorAll('.rating-stars');
	let currentRating = 0;

	stars.forEach((star, index) => {
		star.addEventListener('mouseenter', () => {
			stars.forEach((s, i) => {
				s.classList.toggle('active', i <= index);
			});
		});

		star.addEventListener('click', () => {
			currentRating = index + 1;
			stars.forEach((s, i) => {
				s.classList.toggle('active', i < currentRating);
			});

			// 전체 평점의 경우 텍스트 업데이트
			if (!container.dataset.category) {
				const ratingTexts = ['', '최고예요', '좋아요', '괜찮아요', '그냥 그래요', '별로예요'];
				const ratingText = container.parentElement.querySelector('.rating-text');
				if (ratingText) {
					ratingText.textContent = ratingTexts[currentRating];
				}
			}
		});
	});

	container.addEventListener('mouseleave', () => {
		stars.forEach((s, i) => {
			s.classList.toggle('active', i < currentRating);
		});
	});
});

// 태그 선택 기능
document.querySelectorAll('.tag-item').forEach(tag => {
	tag.addEventListener('click', () => {
		tag.classList.toggle('selected');
	});
});

// 이미지 업로드 기능
const imageInput = document.getElementById('imageInput');
const imagePreview = document.getElementById('imagePreview');
let uploadedImages = [];

imageInput.addEventListener('change', (e) => {
	const files = Array.from(e.target.files);

	files.forEach(file => {
		if (uploadedImages.length >= 5) {
			alert('최대 5장까지만 업로드 가능합니다.');
			return;
		}

		const reader = new FileReader();
		reader.onload = (e) => {
			const imageContainer = document.createElement('div');
			imageContainer.className = 'image-preview';

			imageContainer.innerHTML = `
                <img src="${e.target.result}" alt="업로드된 이미지">
                <button type="button" class="remove-btn" onclick="removeImage(this)">x</button>
            `;

			imagePreview.appendChild(imageContainer);
			uploadedImages.push(file);
		};
		reader.readAsDataURL(file);
	});
});

// 이미지 제거 기능
function removeImage(button) {
	const imageContainer = button.parentElement;
	const index = Array.from(imagePreview.children).indexOf(imageContainer);
	uploadedImages.splice(index, 1);
	imageContainer.remove();
}

// 폼 제출
/*document.getElementById('reviewForm').addEventListener('submit', (e) => {
	e.preventDefault();

	const title = document.getElementById('reviewTitle').value.trim();
	const content = document.getElementById('reviewContent').value.trim();

	if (!title) {
		alert('후기 제목을 입력해주세요.');
		return;
	}

	if (content.length < 10) {
		alert('후기 내용을 최소 10자 이상 작성해주세요.');
		return;
	}

	alert('후기가 성공적으로 등록되었습니다!');
	// 실제로는 서버로 데이터를 전송하는 코드가 들어갑니다.
});*/