function openSidebar() {
    document.getElementById("notificationSidebar").classList.add("open");
    document.getElementById("sidebarOverlay").classList.add("show");
  }

  function closeSidebar() {
    document.getElementById("notificationSidebar").classList.remove("open");
    document.getElementById("sidebarOverlay").classList.remove("show");
  }

  // 알림 버튼에 이벤트 연결
  document.addEventListener("DOMContentLoaded", function () {
    const bellButton = document.querySelector(".btn-wakutabi-outline .bi-bell-fill");
    if (bellButton) {
      bellButton.addEventListener("click", openSidebar);
    }
  });