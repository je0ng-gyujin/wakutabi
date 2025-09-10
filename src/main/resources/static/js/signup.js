$(document).ready(function () {
  // ✅ 아이디 중복 체크
  $("#username").on("blur", function () {
    const username = $(this).val().trim();

    if ($("#id-check-msg").length === 0) {
      $(this).after('<small id="id-check-msg" class="text-danger"></small>');
    }

    if (username === "") {
      $("#id-check-msg").text("아이디를 입력해주세요").css("color", "red");
      return;
    }

    $.ajax({
      url: "/user/check-username",
      type: "get",
      data: { username: username },
      success: function (result) {
        if (result === "exist") {
          $("#id-check-msg").text("이미 사용중인 아이디입니다").css("color", "red");
        } else if (result === "ok") {
          $("#id-check-msg").text("사용 가능한 아이디입니다").css("color", "green");
        } else {
          $("#id-check-msg").text("중복 확인 중 오류 발생").css("color", "red");
        }
      },
      error: function () {
        $("#id-check-msg").text("AJAX 오류 발생").css("color", "red");
      }
    });
  });

  // ✅ 비밀번호 빈칸 검사
  $("#password").on("blur", function () {
    const password = $(this).val().trim();

    if ($("#pw-check-msg").length === 0) {
      $(this).after('<small id="pw-check-msg" class="text-danger"></small>');
    }

    if (password === "") {
      $("#pw-check-msg").text("비밀번호를 입력해주세요").css("color", "red");
    } else {
      $("#pw-check-msg").text("");
    }
  });

  // ✅ 닉네임 빈칸 검사
  $("#nickname").on("blur", function () {
    const nickname = $(this).val().trim();

    if ($("#nickname-check-msg").length === 0) {
      $(this).after('<small id="nickname-check-msg" class="text-danger"></small>');
    }

    if (nickname === "") {
      $("#nickname-check-msg").text("닉네임을 입력해주세요").css("color", "red");
    } else {
      $("#nickname-check-msg").text("");
    }
  });

  // ✅ 이메일 빈칸 검사
  $("#email").on("blur", function () {
    const email = $(this).val().trim();

    if ($("#email-check-msg").length === 0) {
      $(this).after('<small id="email-check-msg" class="text-danger"></small>');
    }

    if (email === "") {
      $("#email-check-msg").text("이메일을 입력해주세요").css("color", "red");
    } else {
      $("#email-check-msg").text("");
    }
  });
  
  
  $("#birth").on("blur", function () {
    const birth = $(this).val().trim();

    if ($("#birth-check-msg").length === 0) {
      $(this).after('<small id="birth-check-msg" class="text-danger"></small>');
    }

    if (birth === "") {
      $("#birth-check-msg").text("생일을 선택해주세요").css("color", "red");
    } else {
      $("#birth-check-msg").text("");
    }
  });
});
