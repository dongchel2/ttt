<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>게임 결과</title>
    <style>
        canvas { border: 1px solid #000; background: #fff; }
        .score-table { margin-top: 20px; }
        .score-table th, .score-table td { padding: 5px 10px; border: 1px solid #ccc; }
    </style>
</head>
<body>
    <h2>🎉 게임 종료! 점수판</h2>

    <table class="score-table">
        <thead>
        <tr>
            <th>플레이어</th>
            <th>점수</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="entry" items="${scoreMap}">
            <tr>
                <td>${entry.key}</td>
                <td>${entry.value}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <hr/>
    <h3>🖼️ 리플레이 보기</h3>
    <canvas id="replayCanvas" width="800" height="500"></canvas><br>
    <button onclick="startReplay()">▶ 재생</button>

    <script>
        const replayData = ${replayJson}; // 서버에서 전달된 JSON 배열
        const canvas = document.getElementById("replayCanvas");
        const ctx = canvas.getContext("2d");

        function startReplay() {
            let i = 0;
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            function drawStep() {
                if (i >= replayData.length) return;

                const step = replayData[i];
                ctx.beginPath();
                ctx.fillStyle = step.color || "black";
                ctx.arc(step.x, step.y, step.size || 2, 0, 2 * Math.PI);
                ctx.fill();
                i++;
                setTimeout(drawStep, 10); // 간격 조절
            }

            drawStep();
        }
    </script>
</body>
</html>
