// ========================================================
// 🌏 travel_review.js (최종 안정 버전 - 2025.10 수정)
// ========================================================

document.addEventListener('DOMContentLoaded', () => {

  // ===== 리뷰 중복 알람 =====
  if (window.alreadyReviewed) {
    SwalDefault.fire({
      icon: 'info',
      title: '이미 리뷰를 작성한 여행입니다.',
      text: '리뷰는 한 번만 작성할 수 있습니다.',
      confirmButtonText: '확인'
    }).then(() => history.back());
    return;
  }

  // ===== 여행 평점 (1개) =====
  function initTravelRating() {
    const container = document.querySelector('.rating-container[data-category="travel"]');
    if (!container) return;

    const stars = Array.from(container.querySelectorAll('.rating-stars'));
    const radios = Array.from(container.querySelectorAll('input[type="radio"]'));
    const ratingText = container.parentElement.querySelector('.rating-text');
    const texts = ['', '별로예요', '그냥 그래요', '괜찮아요', '좋아요', '최고예요'];
    const totalStars = stars.length;
    let currentRating = 0;

    stars.forEach((star, index) => {
      star.addEventListener('mouseenter', () => {
        stars.forEach((s, i) => s.classList.toggle('active', i >= index)); // 반전된 표시
      });

      star.addEventListener('click', () => {
        currentRating = totalStars - index;
        const radio = radios.find(r => parseInt(r.value) === currentRating);
        if (radio) radio.checked = true;
        stars.forEach((s, i) => s.classList.toggle('active', i >= index));
        if (ratingText) ratingText.textContent = texts[currentRating];
      });

      star.addEventListener('mouseleave', () => {
        stars.forEach((s, i) => s.classList.toggle('active', i >= index));
      });
    });
  }

  // ===== 동행자 평점 (여러 명) =====
  function initUserRatings() {
    const userContainers = document.querySelectorAll('.rating-container[data-category="user"]');
    if (!userContainers.length) return;
    const texts = ['', '별로예요', '그냥 그래요', '괜찮아요', '좋아요', '최고예요'];

    userContainers.forEach(container => {
      const stars = Array.from(container.querySelectorAll('.rating-stars'));
      const radios = Array.from(container.querySelectorAll('input[type="radio"]'));
      const totalStars = stars.length;
      let currentRating = 0;

      stars.forEach((star, index) => {
        star.addEventListener('mouseenter', () => {
          stars.forEach((s, i) => s.classList.toggle('active', i >= index));
        });

        star.addEventListener('click', () => {
          currentRating = totalStars - index;
          const radio = radios.find(r => parseInt(r.value) === currentRating);
          if (radio) radio.checked = true;
          stars.forEach((s, i) => s.classList.toggle('active', i >= index));

          let textEl = container.querySelector('.user-rating-text');
          if (!textEl) {
            textEl = document.createElement('div');
            textEl.classList.add('user-rating-text', 'text-muted', 'mt-1');
            container.appendChild(textEl);
          }
          textEl.textContent = texts[currentRating];
        });

        star.addEventListener('mouseleave', () => {
          stars.forEach((s, i) => s.classList.toggle('active', i >= index));
        });
      });
    });
  }

  // ===== 이미지 업로드 기능 =====
  const imageInput = document.getElementById('imageInput');
  const imagePreview = document.getElementById('imagePreview');
  let uploadedImages = [];

  imageInput?.addEventListener('change', (e) => {
    const newFiles = Array.from(e.target.files);
    const totalCount = uploadedImages.length + newFiles.length;

    // 최대 5장 제한
    if (totalCount > 5) {
      SwalDefault.fire({
        icon: 'info',
        title: '최대 5장까지만 업로드 가능합니다.',
        text: `현재 ${uploadedImages.length}장 업로드되어 있습니다.`
      });
      e.target.value = '';
      return;
    }

    // ✅ 중복 방지
    newFiles.forEach(file => {
      const isDuplicate = uploadedImages.some(f =>
        f.name === file.name && f.size === file.size && f.lastModified === file.lastModified
      );
      if (isDuplicate) return; // 이미 추가된 파일이면 무시

      const reader = new FileReader();
      reader.onload = (ev) => {
        const imageContainer = document.createElement('div');
        imageContainer.className = 'image-preview';
        imageContainer.innerHTML = `
          <img src="${ev.target.result}" alt="업로드된 이미지">
          <button type="button" class="remove-btn" onclick="removeImage(this)">x</button>
        `;
        imagePreview.appendChild(imageContainer);
      };
      reader.readAsDataURL(file);
      uploadedImages.push(file); // 실제 파일 배열에 추가
    });

    e.target.value = ''; // 동일 파일 재선택 방지
  });

  // 이미지 제거
  window.removeImage = function (button) {
    const imageContainer = button.parentElement;
    const index = Array.from(imagePreview.children).indexOf(imageContainer);
    if (index >= 0) uploadedImages.splice(index, 1);
    imageContainer.remove();
  };

  // ===== 폼 제출 =====
  const form = document.getElementById('reviewForm');
  let isSubmitting = false; // 🚫 중복 제출 방지

  form?.addEventListener('submit', (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();
    if (isSubmitting) return;

    const title = document.getElementById('reviewTitle').value.trim();
    const content = document.getElementById('reviewContent').value.trim();

    // === 유효성 검사 ===
    if (!title) {
      SwalDefault.fire({ icon: 'warning', title: '제목을 입력해주세요.', text: '후기 제목은 필수 항목입니다.' });
      return;
    }
    if (content.length < 10) {
      SwalDefault.fire({ icon: 'info', title: '후기 내용을 최소 10자 이상 작성해주세요.', text: '조금만 더 자세히 적어주세요 😊' });
      return;
    }
    const travelRating = document.querySelector('.rating-container[data-category="travel"] input:checked');
    if (!travelRating) {
      SwalDefault.fire({ icon: 'warning', title: '여행 평점을 선택해주세요.', text: '별점은 필수 항목입니다 ⭐' });
      return;
    }

    // 동행자 평가 확인
    const userContainers = document.querySelectorAll('.rating-container[data-category="user"]');
    const unselectedUsers = [];
    userContainers.forEach(container => {
      const checked = container.querySelector('input[type="radio"]:checked');
      const name = container.closest('.col-md-6')?.querySelector('span')?.textContent?.trim();
      if (!checked) unselectedUsers.push(name || '동행자');
    });
    if (unselectedUsers.length > 0) {
      SwalDefault.fire({
        icon: 'info',
        title: '모든 동행자 평가를 완료해주세요.',
        html: `<b>${unselectedUsers.join(', ')}</b>님이 아직 평가되지 않았습니다.`,
      });
      return;
    }

    // === 최종 등록 ===
    SwalDefault.fire({
      icon: 'question',
      title: '리뷰를 등록하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '등록',
      cancelButtonText: '취소'
    }).then((result) => {
      if (!result.isConfirmed) return;

      isSubmitting = true; // 중복 방지 시작

      SwalDefault.fire({
        icon: 'success',
        title: '후기가 성공적으로 등록되었습니다!',
        text: '확인을 누르면 여행 상세 페이지로 이동합니다.',
        confirmButtonText: '확인'
      }).then(() => {
        const formData = new FormData(form);

        // ✅ 중복 제거 후 append
        const uniqueImages = Array.from(new Map(
          uploadedImages.map(f => [f.name + f.size + f.lastModified, f])
        ).values());
        uniqueImages.forEach(file => formData.append('imageFiles', file));

        fetch(form.action, {
          method: "POST",
          body: formData
        })
          .then(response => {
            if (response.redirected) {
              window.location.href = response.url;
            } else {
              isSubmitting = false;
              SwalDefault.fire('서버 오류', '리뷰 저장 중 오류가 발생했습니다.', 'error');
            }
          })
          .catch(() => {
            isSubmitting = false;
            SwalDefault.fire('네트워크 오류', '잠시 후 다시 시도해주세요.', 'error');
          });
      });
    });
  });

  // ===== 실행 =====
  initTravelRating();
  initUserRatings();
});
