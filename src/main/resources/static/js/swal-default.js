// ✅ SweetAlert2 기본 설정 (WakuTabi 전체 공용)
const SwalDefault = Swal.mixin({
  position: "top", // ✅ 강제 위쪽 정렬
  customClass: {
    popup: "wakutabi-alert",
  },
  showClass: {
    popup: "swal2-show",
  },
  hideClass: {
    popup: "swal2-hide",
  },
  didOpen: (popup) => {
    // 팝업이 열린 후 살짝 아래쪽으로 내림 (너무 상단에 붙지 않게)
    const container = popup.closest(".swal2-container");
    if (container) {
      container.style.paddingTop = "8vh"; // 화면 위에서 약간 떨어뜨림
    }
  },
});
