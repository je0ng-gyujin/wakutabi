$(document).ready(function () {
  // ✅ 태그 클릭 이벤트
  $(".tag-item").on("click", function () {
    $(this).toggleClass("active");
  });

  // ✅ 체크박스·셀렉트 변경 시 필터 업데이트 (존재할 때만)
  if ($("input[type='checkbox'], select").length > 0) {
    $("input[type='checkbox'], select").on("change", updateSearch);
  }

  function updateSearch() {
    const selectedTags = $(".tag-item.active")
      .map(function () {
        return $(this).data("tag");
      })
      .get();

    const searchQuery = $("#searchInput").val() || "";

    $(".schedule-card").each(function () {
      const cardText = $(this).text().toLowerCase();
      const matchesQuery =
        searchQuery.length === 0 || cardText.includes(searchQuery.toLowerCase());

      const matchesTags =
        selectedTags.length === 0 ||
        selectedTags.some((tag) => cardText.includes(tag.toLowerCase()));

      $(this).toggle(matchesQuery && matchesTags);
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

  initLoadMore("registeredTrips", "loadMoreRegistered", 5);
  initLoadMore("appliedTrips", "loadMoreApplied", 5);

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

  if (slider && typeof noUiSlider !== "undefined") {
    noUiSlider.create(slider, {
      start: [0, 2000000],
      connect: true,
      range: { min: 0, max: 2000000 },
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
});
