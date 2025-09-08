// 커스텀 태그 추가 기능
document.getElementById('addTagBtn').addEventListener('click', function() {
	addCustomTag();
});

document.getElementById('customTagInput').addEventListener('keypress', function(e) {
	if (e.key === 'Enter') {
		e.preventDefault();
		addCustomTag();
	}
});

function addCustomTag() {
	const input = document.getElementById('customTagInput');
	const container = document.getElementById('customTagsContainer');
	const tagText = input.value.trim();

	if (tagText) {
		// 기존 안내 문구 제거
		const helpText = container.querySelector('.text-muted');
		if (helpText) helpText.remove();

		// 새 태그 생성
		const tagElement = document.createElement('span');
		tagElement.className = 'tag-item-input';
		tagElement.innerHTML = `
            ${tagText}
            <span class="remove-tag" onclick="this.parentElement.remove()">&times;</span>
        `;

		container.appendChild(tagElement);
		input.value = '';
		updatePreview();
	}
}

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
	const title = document.querySelector('input[placeholder*="여행 제목"]').value || '여행 제목을 입력하세요';
	const region = document.querySelector('select[required]').value || '지역';
	const description = document.querySelector('textarea[placeholder*="여행의 매력"]').value || '여행 설명을 입력하세요';
	const totalPeople = document.querySelector('select[required]:nth-of-type(2)').value || '0';

	document.getElementById('previewTitle').textContent = title;
	document.getElementById('previewRegion').textContent = region === 'hokkaido' ? '홋카이도' :
		region === 'honshu' ? '혼슈' :
			region === 'shikoku' ? '시코쿠' :
				region === 'kyushu' ? '규슈' : '지역';
	document.getElementById('previewDescription').textContent = description.length > 60 ?
		description.substring(0, 60) + '...' : description;
	document.getElementById('previewParticipants').textContent = `1/${totalPeople}명`;

	// 날짜 업데이트
	const startDate = document.querySelector('input[type="date"]').value;
	const endDate = document.querySelector('input[type="date"]:nth-of-type(2)').value;
	if (startDate && endDate) {
		const start = new Date(startDate);
		const end = new Date(endDate);
		const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
		document.getElementById('previewDates').textContent = `${start.getMonth() + 1}월 ${start.getDate()}일 ~ ${end.getMonth() + 1}월 ${end.getDate()}일 (${days}일간)`;

		// 여행 기간 자동 계산
		document.querySelector('input[readonly]').value = `${days}일`;
	}

	// 태그 업데이트
	const tagsContainer = document.getElementById('previewTags');
	tagsContainer.innerHTML = '';

	// 선택된 인기 태그
	document.querySelectorAll('.btn-check:checked').forEach(checkbox => {
		const label = document.querySelector(`label[for="${checkbox.id}"]`);
		if (label) {
			const span = document.createElement('span');
			span.className = 'badge bg-primary me-1 mb-1';
			span.textContent = label.textContent;
			tagsContainer.appendChild(span);
		}
	});

	// 커스텀 태그
	document.querySelectorAll('.tag-item-input').forEach(tag => {
		const span = document.createElement('span');
		span.className = 'badge bg-primary me-1 mb-1';
		span.textContent = tag.textContent.replace('×', '').trim();
		tagsContainer.appendChild(span);
	});

	if (tagsContainer.children.length === 0) {
		const span = document.createElement('span');
		span.className = 'badge bg-secondary me-1';
		span.textContent = '태그';
		tagsContainer.appendChild(span);
	}
}

// 실시간 미리보기 업데이트를 위한 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
	// 입력 필드들에 이벤트 리스너 추가
	document.querySelector('input[placeholder*="여행 제목"]').addEventListener('input', updatePreview);
	document.querySelector('select[required]').addEventListener('change', updatePreview);
	document.querySelector('textarea[placeholder*="여행의 매력"]').addEventListener('input', updatePreview);
	document.querySelector('select[required]:nth-of-type(2)').addEventListener('change', updatePreview);
	document.querySelector('input[type="date"]').addEventListener('change', updatePreview);
	document.querySelector('input[type="date"]:nth-of-type(2)').addEventListener('change', updatePreview);

	// 태그 체크박스 이벤트 리스너
	document.querySelectorAll('.btn-check').forEach(checkbox => {
		checkbox.addEventListener('change', updatePreview);
	});
});

// 폼 제출 처리
document.getElementById('travelRegistrationForm').addEventListener('submit', function(e) {
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

	// 성공 메시지
	alert('여행 일정이 성공적으로 등록되었습니다! 승인 후 공개됩니다.');

	// 실제 서버 전송 로직은 여기에 구현
	console.log('폼 데이터 전송 준비 완료');
});

// 임시저장 기능
document.querySelector('.btn-outline-secondary').addEventListener('click', function() {
	const formData = new FormData(document.getElementById('travelRegistrationForm'));

	// 브라우저 저장소 대신 변수로 저장 (실제로는 서버에 임시저장)
	console.log('임시저장 완료');
	alert('현재 작성 내용이 임시저장되었습니다.');
});