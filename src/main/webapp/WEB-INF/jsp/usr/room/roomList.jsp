<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>방 선택</title>
    <link rel="stylesheet" href="/css/catch.css">
</head>
<body>
<div class="room-list-container">
    <h2>🎮 게임 방 목록</h2>

    <c:if test="${not empty errorMsg}">
        <p class="error-msg">${errorMsg}</p>
    </c:if>

    <table>
        <thead>
        <tr><th>방 번호</th><th>인원</th><th>입장</th></tr>
        </thead>
        <tbody>
        <c:forEach var="room" items="${rooms}">
            <tr>
                <td>${room.roomId}</td>
                <td>${room.users.size()} / 2</td>
                <td>
                    <c:choose>
                        <c:when test="${room.users.size() < 2}">
                            <form method="post" action="/usr/room/enter/${room.roomId}">
                                <button type="submit">입장</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <button disabled>입장 불가</button>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form method="post" action="/usr/member/logout">
        <button type="submit">로그아웃</button>
    </form>
</div>
</body>
</html>
