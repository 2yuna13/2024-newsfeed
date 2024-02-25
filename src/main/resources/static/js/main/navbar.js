$(document).ready(function () {
  $("a.nav-link[href='#home']").click(function () {
    window.location.href = "/home";
  });

  $("a.nav-link[href='#posts']").click(function () {
    window.location.href = "/posts";
  });

  $("a.nav-link[href='#myPage']").click(function () {
    window.location.href = "/myPage";
  });

  // 로그아웃 버튼 변수화
  const LogoutBtn = $("#logout-btn");

  // 로그아웃 클릭 이벤트 감지!
  LogoutBtn.on("click", function () {
    // 액세스 토큰을 쿠키에서 가져오기
    const accessToken = Cookies.get("accessToken");

    // 액세스 토큰이 있다면 서버에 로그아웃 요청 보내기
    if (accessToken) {
      const url = "/api/auth/logout";
      $.ajax({
        url: url,
        type: "POST",
        headers: { Authorization: "Bearer " + accessToken },
        success: function (response) {
          // 쿠키에서 액세스 토큰 제거
          Cookies.remove("accessToken", { path: "/" });

          alert("로그아웃 성공");
          window.location.href = "/login-page";
        },
        error: function (jqXHR) {
          alert(`로그아웃 실패: ${jqXHR.responseJSON.message}`);
        },
      });
    } else {
      alert("이미 로그아웃된 상태입니다.");
    }
  });
});