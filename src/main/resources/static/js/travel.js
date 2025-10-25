$(document).ready(function () {
  // 사용 가능한 태그 동적 생성
  initializeAvailableTags();

  // 태그 클릭 이벤트 (동적 생성된 태그에도 적용되도록 이벤트 위임 사용)
  $(document).off("click", ".tag-item").on("click", ".tag-item", function (e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).toggleClass("active");
    updateSelectedTags();
  });

  // ✅ 체크박스·셀렉트 변경 시 필터 업데이트 (존재할 때만)
  // ✅ 체크박스·셀렉트 변경 시 필터 업데이트 (존재할 때만)
  // groupSize 체크박스는 서버 필터링을 위해 클라이언트 측 updateSearch 호출을 제거합니다.
  if ($("select").length > 0) {
    $("select").on("change", updateSearch);
  }

  function updateSearch() {
    const selectedTags = $(".tag-item.active")
      .map(function () {
        return $(this).data("tag");
      })
      .get();

    const searchQuery = $("#searchInput").val() || "";

    // Get selected group sizes
    const selectedGroupSizes = $("input[name='groupSize']:checked")
      .map(function () {
        return $(this).val();
      })
      .get();

    $(".schedule-card").each(function () {
      const cardText = $(this).text().toLowerCase();
      const cardGroupSize = $(this).data("group-size"); // Get the group size from data attribute

      const matchesQuery =
        searchQuery.length === 0 || cardText.includes(searchQuery.toLowerCase());

      const matchesTags =
        selectedTags.length === 0 ||
        selectedTags.some((tag) => cardText.includes(tag.toLowerCase()));

      // Check if card's group size matches any selected group size
      const matchesGroupSize =
        selectedGroupSizes.length === 0 ||
        selectedGroupSizes.includes(cardGroupSize);

      $(this).toggle(matchesQuery && matchesTags && matchesGroupSize);
    });

    $(".search-result-count").text(
      `총 ${$(".schedule-card:visible").length}개`
    );
  }

  // ✅ "Load More" 버튼 (존재할 때만)
  function initLoadMore(sectionId, buttonId, step = 5) {
    const container = document.getElementById(sectionId);
    const btn = document.getElementById(buttonId);
    if (!container || !btn) return;

    const cards = Array.from(container.querySelectorAll(".schedule-card"));
    if (cards.length <= step) {
      cards.forEach((c) => c.closest(".col-12")?.classList.remove("hidden-card"));
      btn.style.display = "none";
      return;
    }

    cards.forEach((c) => c.closest(".col-12")?.classList.add("hidden-card"));

    let visible = 0;
    function showMore() {
      const next = Math.min(visible + step, cards.length);
      for (let i = visible; i < next; i++) {
        cards[i].closest(".col-12")?.classList.remove("hidden-card");
      }
      visible = next;
      btn.style.display = visible < cards.length ? "inline-block" : "none";
    }

    showMore();
    btn.addEventListener("click", showMore);
  }

  initLoadMore("my-registered-trips", "loadMoreRegistered", 4);
  initLoadMore("my-applied-trips", "loadMoreApplied", 4);

  // ✅ 신청 관리 버튼 (동적 요소 대응)
  $(document).on("click", ".btn-manage", function () {
    const tripArticleId = $(this).data("trip-id");
    const container = $(`#applicant-container-${tripArticleId}`);
    if (!container.length) return;

    if (container.hasClass("show")) {
      container.slideUp(200, function () {
        $(this).empty().removeClass("show");
      });
      return;
    }

    fetchApplicants(tripArticleId, container);
  });

  function fetchApplicants(tripArticleId, container) {
    if (!container) return;
    container
      .html(`
        <div class="card border p-3 text-center text-muted">
          <div class="spinner-border spinner-border-sm me-2" role="status">
            <span class="visually-hidden">Loading...</span>
          </div> 신청자 목록을 불러오는 중입니다...
        </div>
      `)
      .slideDown(200, function () {
        $(this).addClass("show");
      });

    $.ajax({
      url: `/schedule/api/schedule/${tripArticleId}/applicants`,
      type: "GET",
      dataType: "json",
      success: function (applicants) {
        renderApplicants(applicants, container);
      },
      error: function () {
        container.html(`
          <div class="alert alert-danger mt-2" role="alert">
            신청자 목록을 불러오는 데 실패했습니다.
          </div>
        `);
      },
    });
  }

  function renderApplicants(applicants, container) {
    if (!container) return;
    if (!applicants || applicants.length === 0) {
      container.html(`
        <div class="card border p-3 text-center text-muted">
          현재 대기 중인 신청자가 없습니다.
        </div>
      `);
      return;
    }

    const rows = applicants
      .map(
        (app) => `
        <div class="applicant-row d-flex justify-content-between align-items-center mb-2 p-2 border-bottom" data-request-id="${app.requestId}">
          <div class="applicant-info d-flex align-items-center me-3">
            <img src="${app.profileUrl || "/image/default_profile.png"}"
                 alt="${app.nickname}"
                 class="rounded-circle me-2"
                 style="width: 30px; height: 30px; object-fit: cover;">
            <div>
              <strong>${app.nickname}</strong>
              <span class="text-muted ms-2 small">(${app.gender === "M" ? "남성" : "여성"}, ${app.age}세)</span>
              <div class="small text-break">${app.introduce || "자기소개 없음"}</div>
            </div>
          </div>
          <div class="applicant-actions d-flex flex-shrink-0">
            <button class="btn btn-success btn-sm me-2 btn-applicant-action" data-request-id="${app.requestId}" data-action="ACCEPT">수락</button>
            <button class="btn btn-danger btn-sm btn-applicant-action" data-request-id="${app.requestId}" data-action="REJECT">거절</button>
          </div>
        </div>
      `
      )
      .join("");

    container.html(`<div class="card border p-3 mt-2">${rows}</div>`);
  }

  // ✅ 수정 버튼 제어 (여러 개)
  document.querySelectorAll(".editBtn").forEach((editBtn) => {
    editBtn.addEventListener("click", function (e) {
      e.preventDefault();
      const tripId = editBtn.getAttribute("data-id");
      const tripStatus = editBtn.getAttribute("data-status")?.toUpperCase();

      const statusMap = {
        MATCHED: "모집완료",
        CLOSED: "여행종료",
        CANCELED: "여행취소",
      };

      if (["MATCHED", "CLOSED", "CANCELED"].includes(tripStatus)) {
        if (typeof SwalDefault !== "undefined") {
          SwalDefault.fire({
            icon: "warning",
            title: "수정 불가",
            text: `해당 여행은 [${statusMap[tripStatus]}] 상태이므로 수정할 수 없습니다.`,
            confirmButtonText: "확인",
          });
        } else {
          alert(`해당 여행은 [${statusMap[tripStatus]}] 상태이므로 수정할 수 없습니다.`);
        }
        return;
      }

      location.href = `/schedule/edit?id=${tripId}`;
    });
  });

  // ✅ 가격 슬라이더 (존재할 때만)
  const slider = document.getElementById("slider");
  const minInput = document.getElementById("minPrice");
  const maxInput = document.getElementById("maxPrice");

  if (slider && typeof noUiSlider !== "undefined" && !slider.noUiSlider) {
    noUiSlider.create(slider, {
      start: [0, 2000000],
      connect: true,
      range: { min: 0, max: 2000000 },
	  // 👇 이 부분을 추가하거나 1000으로 수정합니다.
	      step: 1000, 
	      // 👇 숫자 포맷팅 (선택 사항)
	      /*format: wNumb({
	          decimals: 0,
	          thousand: ',',
	          suffix: '원'
	      })*/
    });

    if (minInput && maxInput) {
      slider.noUiSlider.on("update", function (values, handle) {
        const value = Math.round(values[handle]);
        if (handle === 0) minInput.value = value || "";
        else maxInput.value = value || "";
      });

      minInput.addEventListener("change", function () {
        slider.noUiSlider.set([this.value || 0, null]);
      });

      maxInput.addEventListener("change", function () {
        slider.noUiSlider.set([null, this.value || 2000000]);
      });
    }
  }

  // 🏷️ 태그 매핑 테이블 (detail.js와 동일)
  function initializeAvailableTags() {
    const tagsContainer = document.getElementById("availableTags");
    
    if (!tagsContainer) {
      return;
    }
    
    // 이미 태그가 생성되어 있으면 건너뜀 (중복 방지)
    if (tagsContainer.children.length > 0) {
      return;
    }

    const tagMapping = {
      foodie: "🍜 식도락",
      activity: "🏃 액티비티", 
      nature: "🌲 자연",
      otaku: "🎮 오타쿠",
      shopping: "🛍️ 쇼핑",
      smallGroup: "👤 소수팟",
      largeGroup: "👥 다인팟",
      indoor: "🏠 실내파",
      outdoor: "🌞 실외파",
    };

    const raw = tagsContainer.getAttribute("data-tags");
    
    if (raw) {
      const list = raw.split(",").map((t) => t.trim()).filter(Boolean);
      tagsContainer.innerHTML = "";
      
      list.forEach((key) => {
        const span = document.createElement("span");
        span.className = "badge tag-item px-3 py-2 rounded-pill";
        span.setAttribute("data-tag", key);
        span.textContent = tagMapping[key] || key;
        tagsContainer.appendChild(span);
      });
    }
  }

  // 선택된 태그들을 hidden input에 업데이트
  function updateSelectedTags() {
    const selectedTags = $(".tag-item.active")
      .map(function () {
        return $(this).data("tag");
      })
      .get();
    
    $("#tagsInput").val(selectedTags.join(","));
  }

  // 🗾 지역 버튼 클릭 시 active 토글 및 regionInput 값 반영
  $(document).on("click", ".region-item", function (e) {
    e.preventDefault();
    e.stopPropagation();
    // 모든 버튼에서 active 제거
    $(".region-item").removeClass("active");
    // 클릭한 버튼에 active 추가
    $(this).addClass("active");
    // hidden input에 값 반영
    $("#regionInput").val($(this).val());
    
    // 선택된 지역명을 아코디언 헤더에 표시 ("지역" 텍스트를 지역명으로 대체)
    const selectedRegionName = $(this).text();
    $("#headingRegion button").html(selectedRegionName +
        ' <span id="selectedRegionText" class="ms-2 text-primary"></span>');
    
    // 아코디언 닫기 - 강제로 닫기
    const collapseElement = $('#collapseRegion');
    const accordionButton = $('#headingRegion button');
    
    // Bootstrap collapse 이벤트 발생시키기
    collapseElement.removeClass('show').addClass('collapse');
    accordionButton.addClass('collapsed').attr('aria-expanded', 'false');
  });

  // 필터 폼 제출 시 태그/지역 값 최신화 보장
  $("#filterForm").on("submit", function(e) {
    // 태그 값 최신화
    updateSelectedTags();
    // 지역 값 최신화 (이미 버튼 클릭 시 반영되지만 혹시 몰라 재설정)
    var selectedRegion = $(".region-item.active").val() || "";
    $("#regionInput").val(selectedRegion);
    // groupSize 값 최신화
    const selectedGroupSizes = $("input[name='groupSize']:checked")
      .map(function () {
        return $(this).val();
      })
      .get();

    // 기존 URL 파라미터 유지
    const currentUrl = new URL(window.location.href);
    const params = new URLSearchParams(currentUrl.search);

    // groupSize 파라미터 제거 후 다시 추가
    params.delete('groupSize');
    selectedGroupSizes.forEach(size => params.append('groupSize', size));

    // 폼 액션 URL 업데이트
    $(this).attr('action', '/schedule/search?' + params.toString());

    // 폼은 그대로 제출
  });
  
  // 필터 초기화 버튼 기능
  $(document).on("click", "#resetFilters", function(e) {
    e.preventDefault(); // 폼 제출 방지
    e.stopPropagation();
    
    // 모든 태그 선택 해제
    $(".tag-item").removeClass("active");
    $("#tagsInput").val("");
    
    // 지역 선택 해제
    $(".region-item").removeClass("active");
    $("#regionInput").val("");
    // 아코디언 헤더를 "지역"으로 되돌리기
    $("#headingRegion button").html('지역 <span id="selectedRegionText" class="ms-2 text-primary"></span>');
    
    // 가격 초기화
    $("#minPrice").val("");
    $("#maxPrice").val("");
    
    // 날짜 초기화
    $("#startDate").val("");
    $("#endDate").val("");
    
    // 체크박스 초기화
    $("input[type='checkbox']").prop("checked", false);
    $("input[type='radio']").prop("checked", false);
    
    // 가격 슬라이더 초기화 (존재하는 경우)
    const slider = document.getElementById("slider");
    if (slider && slider.noUiSlider) {
      slider.noUiSlider.set([0, 2000000]);
    }
  });
});
