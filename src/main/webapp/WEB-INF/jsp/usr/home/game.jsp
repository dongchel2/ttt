<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>CatchMind Game</title>
    <link rel="stylesheet" href="/css/catch.css">
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
</head>
<body>
<div class="game-container">
    <div class="top-panel">
        <div class="info-box">
            <span>출제자: <span id="drawerArea">-</span></span>
            <span>남은 시간: <span id="timer">60</span>s</span>
        </div>
        <div class="answer-box">
            <span id="answerArea"></span>
        </div>
    </div>

    <canvas id="canvas" width="500" height="400"></canvas>

    <div class="toolbar">
        <label>색상:
            <input type="color" id="colorPicker" value="#000000">
        </label>
        <label>굵기:
            <input type="range" id="brushSize" min="1" max="10" value="2">
        </label>
        <button id="clearBtn">전체 지우기</button>
    </div>

    <div class="chat-container">
        <div id="chatBox" class="chat-box"></div>
        <input type="text" id="chatInput" placeholder="채팅 입력..." maxlength="20">
        <button id="sendBtn">전송</button>
    </div>

    <div class="side-panel">
        <div class="score-box">
            <h3>점수판</h3>
            <ul id="scoreList"></ul>
        </div>
        <div class="ready-box">
            <button id="readyBtn">준비</button>
            <button id="leaveBtn">게임 나가기</button>
        </div>
    </div>

    <div class="replay-box" style="display:none;">
        <h3>📺 리플레이</h3>
        <button id="replayStartBtn">▶ 재생</button>
        <button id="replayCloseBtn">❌ 닫기</button>
    </div>
</div>

<script>
    const loginId = "${sessionScope.loginId}";
    const roomId = "${sessionScope.roomId}";
    let stompClient = null;
    let isDrawing = false;
    let prevX = 0, prevY = 0;
    let replayData = [];
    let timerInterval;

    const canvas = document.getElementById("canvas");
    const ctx = canvas.getContext("2d");

    function connectWebSocket() {
        const socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/room/" + roomId, (msg) => {
                const data = JSON.parse(msg.body);
                if (data.type === "DRAW") drawLine(data);
                else if (data.type === "CHAT") appendChat(data);
                else if (data.type === "SCORE") updateScores(data.scores);
                else if (data.type === "DRAWER") document.getElementById("drawerArea").innerText = data.drawer;
                else if (data.type === "NEXT") resetCanvas();
            });

            stompClient.subscribe("/user/queue/answer", (msg) => {
                const data = JSON.parse(msg.body);
                if (data.type === "ANSWER") {
                    document.getElementById("answerArea").innerText = "🧠 정답: " + data.answer;
                }
            });

            stompClient.send("/game/enter", {}, JSON.stringify({ roomId, loginId }));
        });
    }

    function sendDraw(x, y, color, size) {
        const msg = { type: "DRAW", roomId, loginId, x, y, color, size };
        stompClient.send("/game/send", {}, JSON.stringify(msg));
    }

    canvas.addEventListener("mousedown", (e) => {
        isDrawing = true;
        prevX = e.offsetX;
        prevY = e.offsetY;
    });

    canvas.addEventListener("mouseup", () => { isDrawing = false; });

    canvas.addEventListener("mousemove", (e) => {
        if (!isDrawing) return;
        const x = e.offsetX, y = e.offsetY;
        const color = document.getElementById("colorPicker").value;
        const size = document.getElementById("brushSize").value;
        drawLine({ x1: prevX, y1: prevY, x2: x, y2: y, color, size });
        sendDraw(x, y, color, size);
        prevX = x; prevY = y;
    });

    function drawLine({ x1, y1, x2, y2, x, y, color, size }) {
        ctx.strokeStyle = color || "#000";
        ctx.lineWidth = size || 2;
        ctx.lineCap = "round";
        ctx.beginPath();
        ctx.moveTo(x1 ?? prevX, y1 ?? prevY);
        ctx.lineTo(x ?? prevX, y ?? prevY);
        ctx.stroke();
    }

    function resetCanvas() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    }

    document.getElementById("clearBtn").addEventListener("click", resetCanvas);

    document.getElementById("sendBtn").addEventListener("click", () => {
        const msg = document.getElementById("chatInput").value;
        if (msg.trim()) {
            stompClient.send("/game/send", {}, JSON.stringify({ type: "CHAT", roomId, loginId, message: msg }));
            document.getElementById("chatInput").value = "";
        }
    });

    function appendChat(data) {
        const chatBox = document.getElementById("chatBox");
        const div = document.createElement("div");
        div.innerText = `[${data.loginId}] ${data.message}`;
        chatBox.appendChild(div);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    function updateScores(scores) {
        const list = document.getElementById("scoreList");
        list.innerHTML = "";
        const sorted = Object.entries(scores).sort((a, b) => b[1] - a[1]);
        for (const [user, score] of sorted) {
            const li = document.createElement("li");
            li.innerText = `${user}: ${score}점`;
            list.appendChild(li);
        }
    }

    document.getElementById("readyBtn").addEventListener("click", () => {
        stompClient.send("/game/send", {}, JSON.stringify({ type: "READY", roomId, loginId }));
        document.getElementById("readyBtn").disabled = true;
    });

    document.getElementById("leaveBtn").addEventListener("click", () => {
        if (confirm("게임에서 나가시겠습니까?")) {
            fetch("/usr/room/leave", { method: "POST" })
                .then(() => location.href = "/usr/room/list");
        }
    });

    // 타이머 갱신 수신
    stompClient?.subscribe("/topic/timer/" + roomId, (msg) => {
        const time = JSON.parse(msg.body).time;
        document.getElementById("timer").innerText = time;
    });

    connectWebSocket();
</script>
</body>
</html>
