$(document).ready(function () {
  // 로그인한 사용자의 정보 조회
  $.ajax({
    url: "/api/users/profile",
    type: "GET",
    success: function (response) {
      // 사용자의 정보를 화면에 표시합니다.
      $("#user-info").html(
        "Email: " +
          response.email +
          "<br>" +
          "Nickname: " +
          response.nickname +
          "<br>" +
          "Description: " +
          response.description +
          "<br>" +
          "ProfileImage: " +
          response.profileImage +
          "<br>"
      );
    },
    error: function (error) {
      console.error(error);
    },
  });

  // 로그인한 사용자가 작성한 게시물 조회
  $.ajax({
    url: "/api/users/posts",
    type: "GET",
    success: function (response) {
      // 사용자가 작성한 게시물을 화면에 표시합니다.
      for (var i = 0; i < response.content.length; i++) {
        $("#user-posts").append("<li>" + response.content[i].title + "</li>");
      }
    },
    error: function (error) {
      console.error(error);
    },
  });
});