// ========================================================
//  🌏 travel_review.js (최종 안정 버전)
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
    return; // 이하 코드 중단
  }

  // ===== 전체 여행 평점 (1개) =====
  function initTravelRating() {
    const container = document.querySelector('.rating-container[data-category="travel"]');
    if (!container) return;

    const stars = container.querySelectorAll('.rating-stars');
    const radios = container.querySelectorAll('input[type="radio"]');
    const ratingText = container.parentElement.querySelector('.rating-text');
    let currentRating = 0;

    const texts = ['', '별로예요', '그냥 그래요', '괜찮아요', '좋아요', '최고예요'];

    stars.forEach((star, index) => {
      star.addEventListener('mouseenter', () => {
        stars.forEach((s, i) => s.classList.toggle('active', i <= index));
      });

      star.addEventListener('click', () => {
        currentRating = index + 1;
        if (radios[index]) radios[index].checked = true;
        stars.forEach((s, i) => s.classList.toggle('active', i < currentRating));
        if (ratingText) ratingText.textContent = texts[currentRating];
      });

      star.addEventListener('mouseleave', () => {
        stars.forEach((s, i) => s.classList.toggle('active', i < currentRating));
      });
    });
  }

  // ===== 동행자별 평점 (여러 명) =====
  function initUserRatings() {
    const userContainers = document.querySelectorAll('.rating-container[data-category="user"]');
    if (!userContainers.length) return;

    const texts = ['', '별로예요', '그냥 그래요', '괜찮아요', '좋아요', '최고예요'];

    userContainers.forEach(container => {
      const stars = container.querySelectorAll('.rating-stars');
      const radios = container.querySelectorAll('input[type="radio"]');
      let currentRating = 0;

      stars.forEach((star, index) => {
        star.addEventListener('mouseenter', () => {
          stars.forEach((s, i) => s.classList.toggle('active', i <= index));
        });

        star.addEventListener('click', () => {
          currentRating = index + 1;
          if (radios[index]) radios[index].checked = true;
          stars.forEach((s, i) => s.classList.toggle('active', i < currentRating));

          // 별 아래 설명 문구
          let textEl = container.querySelector('.user-rating-text');
          if (!textEl) {
            textEl = document.createElement('div');
            textEl.classList.add('user-rating-text', 'text-muted', 'mt-1');
            container.appendChild(textEl);
          }
          textEl.textContent = texts[currentRating];
        });

        star.addEventListener('mouseleave', () => {
          stars.forEach((s, i) => s.classList.toggle('active', i < currentRating));
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

    // ✅ 새로 추가할 파일들을 합쳤을 때 5장을 초과하는지 확인
    const totalCount = uploadedImages.length + newFiles.length;
    if (totalCount > 5) {
      SwalDefault.fire({
        icon: 'info',
        title: '최대 5장까지만 업로드 가능합니다.',
        text: `현재 ${uploadedImages.length}장 업로드되어 있습니다.`
      });
      e.target.value = ''; // 파일 선택 초기화
      return;
    }

    // ✅ 중복 파일 방지 (같은 파일 두 번 선택 시)
    newFiles.forEach(file => {
      const isDuplicate = uploadedImages.some(f => f.name === file.name && f.size === file.size);
      if (isDuplicate) return; // 이미 업로드된 파일이면 무시

      const reader = new FileReader();
      reader.onload = (e) => {
        const imageContainer = document.createElement('div');
        imageContainer.className = 'image-preview';
        imageContainer.innerHTML = `
          <img src="${e.target.result}" alt="업로드된 이미지">
          <button type="button" class="remove-btn" onclick="removeImage(this)">x</button>
        `;
        imagePreview.appendChild(imageContainer);
        uploadedImages.push(file); // ✅ 실제 누적
      };
      reader.readAsDataURL(file);
    });

    e.target.value = ''; // ✅ input 비워서 다음 선택 시 파일 중복 방지
  });

  window.removeImage = function (button) {
    const imageContainer = button.parentElement;
    const index = Array.from(imagePreview.children).indexOf(imageContainer);
    if (index >= 0) uploadedImages.splice(index, 1);
    imageContainer.remove();
  };

  // ===== 폼 제출 =====
  const form = document.getElementById('reviewForm');
  form?.addEventListener('submit', (e) => {
    e.preventDefault();
    e.stopImmediatePropagation();

    const title = document.getElementById('reviewTitle').value.trim();
    const content = document.getElementById('reviewContent').value.trim();

    // 제목 검사
    if (!title) {
      SwalDefault.fire({
        icon: 'warning',
        title: '제목을 입력해주세요.',
        text: '후기 제목은 필수 항목입니다.',
        confirmButtonText: '확인'
      });
      return;
    }

    // 내용 검사
    if (content.length < 10) {
      SwalDefault.fire({
        icon: 'info',
        title: '후기 내용을 최소 10자 이상 작성해주세요.',
        text: '조금만 더 자세히 적어주세요 😊',
        confirmButtonText: '확인'
      });
      return;
    }

    // travel 별점 검사
    const travelRating = document.querySelector('.rating-container[data-category="travel"] input:checked');
    if (!travelRating) {
      SwalDefault.fire({
        icon: 'warning',
        title: '여행 평점을 선택해주세요.',
        text: '별점은 필수 항목입니다 ⭐',
        confirmButtonText: '확인'
      });
      return;
    }

    // user별 별점 검사
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
        confirmButtonText: '확인'
      });
      return;
    }

    // 최종 등록 확인
    SwalDefault.fire({
      icon: 'question',
      title: '리뷰를 등록하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '등록',
      cancelButtonText: '취소'
    }).then((result) => {
      if (!result.isConfirmed) return;

      SwalDefault.fire({
        icon: 'success',
        title: '후기가 성공적으로 등록되었습니다!',
        text: '확인을 누르면 여행 상세 페이지로 이동합니다.',
        confirmButtonText: '확인'
      }).then(() => {
        form.submit();
      });
    });
  });

  // ===== 실행 =====
  initTravelRating();
  initUserRatings();
});
