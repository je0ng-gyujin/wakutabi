$(document).ready(function () {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
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
          $("#id-check-msg").text("사용 가능한 아이디입니다").attr("style", "color: green !important;");
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
      $("#pw-check-msg").text("비밀번호를 적어주세요").css("color", "red");;
    } else if (password.length < 9 || password.length > 15) {
      $("#pw-check-msg").text("비밀번호는 9자 이상 15자 이하로 적어주세요.").css("color", "red");;
    } else if (!/[!@#$%^(),.?":{}|<>]/.test(password)) {
      $("#pw-check-msg").text("비밀번호에는 최소 1개의 특수문자가 포함되어야 합니다.").css("color", "red");;
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
      return
    }
    if(!emailRegex.test(email)){
      $("#email-check-msg").text("올바른 이메일 형식이 아닙니다.").css("color", "red");
      return
    }

    // Ajax로 email 중복 검사
    $.ajax({
        url : "/user/check-email",
        type : "GET",
        data : {email : email},
        success : function (response) {
            if(response === "exist"){
                $("#email-check-msg").text("중복된 이메일입니다.").css("color","red");
            } else if (response === "ok"){
                $("#email-check-msg").text("사용가능한 이메일입니다.").attr("style", "color: green !important;");
            } else {
                $("#email-check-msg").text("이메일 확인 중 오류가 발생했습니다.").css("color","red");
            }
        },
        error : function(){
            $("#email-check-msg").text("서버와 통신할 수 없습니다.").css("color","red");
        }
    });
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
  $("#signupForm").on("submit", function(e) {
    let isValid = true;

    if($("#id-check-msg").text().includes("이미 사용중인")) isValid = false;
    if($("#pw-check-msg").text() !== "") isValid = false;
    if($("#email-check-msg").text().includes("중복된")) isValid = false;
    if($("#birth").val().trim() === "") isValid = false;

    if(!isValid){
     e.preventDefault();
     alert("다시 한 번 확인해주세요.");
     }
  })
});
