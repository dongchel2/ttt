<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>로그인</title>
    <link rel="stylesheet" href="/css/catch.css">
</head>
<body>
<div class="login-container">
    <h2>캐치마인드 로그인</h2>
    <form method="post" action="/usr/member/doLogin">
        <label for="loginId">로그인 ID:</label>
        <input type="text" name="loginId" id="loginId" required>
        <button type="submit">로그인</button>
    </form>

    <c:if test="${param.error == '1'}">
        <p class="error-msg">로그인 ID는 필수입니다.</p>
    </c:if>
</div>
</body>
</html>
