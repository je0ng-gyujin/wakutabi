document.addEventListener("DOMContentLoaded", () => {
  const editBtn = document.getElementById("editBtn");
  const canceledBtn = document.getElementById("canceledBtn");
  const deleteBtn = document.getElementById("deleteBtn");

  // ✏️ 여행 수정
  if (editBtn) {
    editBtn.addEventListener("click", (e) => {
      e.preventDefault();
      const tripId = editBtn.getAttribute("data-id");
      const tripStatus = editBtn.getAttribute("data-status")?.toUpperCase();

      const statusMap = {
        MATCHED: "모집완료",
        CLOSED: "여행종료",
        CANCELED: "여행취소",
      };

      if (["MATCHED", "CLOSED", "CANCELED"].includes(tripStatus)) {
        SwalDefault.fire({
          icon: "warning",
          title: "수정 불가",
          text: `해당 여행은 [${statusMap[tripStatus]}] 상태이므로 수정할 수 없습니다.`,
          confirmButtonText: "확인",
        });
        return;
      }

      location.href = `/schedule/edit?id=${tripId}`;
    });
  }

  // ❌ 여행 취소
  if (canceledBtn) {
    canceledBtn.addEventListener("click", () => {
      const tripId = canceledBtn.getAttribute("data-id");
      const tripStatus = canceledBtn.getAttribute("data-status")?.toUpperCase();
      const statusMap = { MATCHED: "모집완료", CLOSED: "여행종료" };

      if (tripStatus === "CANCELED") {
        SwalDefault.fire({
          icon: "info",
          title: "이미 취소된 여행",
          text: "이미 취소한 여행입니다.",
          confirmButtonText: "확인",
        });
        return;
      }

      if (["MATCHED", "CLOSED"].includes(tripStatus)) {
        SwalDefault.fire({
          icon: "warning",
          title: "취소 불가",
          text: `해당 여행은 [${statusMap[tripStatus]}] 상태로 취소할 수 없습니다.`,
          confirmButtonText: "확인",
        });
        return;
      }

      SwalDefault.fire({
        title: "정말 취소하시겠습니까?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "네, 취소합니다",
        cancelButtonText: "아니요",
      }).then((result) => {
        if (!result.isConfirmed) return;

        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfMeta || !csrfHeaderMeta) {
          SwalDefault.fire({
            icon: "error",
            title: "보안 설정 오류",
            text: "취소를 진행할 수 없습니다. 잠시 후 다시 시도해주세요.",
            confirmButtonText: "확인",
          });
          return;
        }

        const token = csrfMeta.getAttribute("content");
        const header = csrfHeaderMeta.getAttribute("content");

        fetch(`/schedule/travelCanceled?id=${tripId}`, {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            [header]: token,
          },
          body: JSON.stringify({ id: tripId }),
        })
          .then((res) => res.text())
          .then((msg) => {
            SwalDefault.fire({
              icon: msg.includes("완료") ? "success" : "error",
              title: msg.includes("완료") ? "취소 완료" : "취소 실패",
              text: msg,
              confirmButtonText: "확인",
            }).then(() => {
              if (msg.includes("완료")) {
                location.href = `/schedule/detail?id=${tripId}`;
              }
            });
          })
          .catch((err) => {
            console.error("취소 요청 실패", err);
            SwalDefault.fire({
              icon: "error",
              title: "요청 실패",
              text: "서버와의 통신 중 오류가 발생했습니다.",
              confirmButtonText: "확인",
            });
          });
      }); // ✅ ← 이게 누락돼 있었음!
    });
  }

  // 🗑️ 여행 삭제
  if (deleteBtn) {
    deleteBtn.addEventListener("click", () => {
      const tripId = deleteBtn.getAttribute("data-id");

      SwalDefault.fire({
        title: "정말 삭제하시겠습니까?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "네, 삭제합니다",
        cancelButtonText: "아니요",
      }).then((result) => {
        if (!result.isConfirmed) return;

        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfMeta || !csrfHeaderMeta) {
          SwalDefault.fire({
            icon: "error",
            title: "보안 설정 오류",
            text: "삭제를 진행할 수 없습니다. 잠시 후 다시 시도해주세요.",
            confirmButtonText: "확인",
          });
          return;
        }

        const token = csrfMeta.getAttribute("content");
        const header = csrfHeaderMeta.getAttribute("content");

        fetch("/schedule/traveldelete", {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            [header]: token,
          },
          body: JSON.stringify({ id: tripId }),
        })
          .then((res) => res.text())
          .then((msg) => {
            SwalDefault.fire({
              icon: msg.includes("완료") ? "success" : "error",
              title: msg.includes("완료") ? "삭제 완료" : "삭제 실패",
              text: msg,
              confirmButtonText: "확인",
            }).then(() => {
              if (msg.includes("완료")) {
                location.href = "/schedule/myTrips";
              }
            });
          })
          .catch((err) => {
            console.error("삭제 요청 실패", err);
            SwalDefault.fire({
              icon: "error",
              title: "요청 실패",
              text: "서버와의 통신 중 오류가 발생했습니다.",
              confirmButtonText: "확인",
            });
          });
      });
    });
  }

  // ✅ 참가 신청
  const joinBtn = document.getElementById("joinBtn");
  const joinForm = document.getElementById("joinForm");
  if (joinBtn && joinForm) {
    joinBtn.addEventListener("click", (e) => {
      e.preventDefault();
      SwalDefault.fire({
        title: "이 여행에 참가하시겠습니까?",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "참가",
        cancelButtonText: "취소",
      }).then((r) => {
        if (!r.isConfirmed) return;

        const formData = new FormData(joinForm);
        fetch(joinForm.action, { method: "POST", body: formData })
          .then((res) => res.json())
          .then((data) => {
            SwalDefault.fire({
              icon: data.status === "success" ? "success" : "error",
              title: data.status === "success" ? "신청 완료" : "신청 실패",
              text: data.message || "",
            });
          })
          .catch(() => {
            SwalDefault.fire({
              icon: "error",
              title: "네트워크 오류",
              text: "서버 통신 중 오류가 발생했습니다.",
            });
          });
      });
    });
  }

  // 🏷️ 태그 표시
  const tagsContainer = document.getElementById("travelTags");
  if (tagsContainer) {
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
        span.classList.add("tag-item", "me-2");
        span.textContent = tagMapping[key] || key;
        tagsContainer.appendChild(span);
      });
    }
  }
});
