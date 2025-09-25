  function hideButtons(form) {
    // form 부모(div.action-buttons)를 숨김
    const container = form.closest(".action-buttons");
    if (container) {
      container.style.display = "none";
    }
    // 폼은 그대로 submit → 서버에 요청 감
  }
