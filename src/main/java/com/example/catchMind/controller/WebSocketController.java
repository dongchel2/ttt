package com.example.catchMind.controller;

import com.example.catchMind.domain.Room;
import com.example.catchMind.dto.CatchMindMessage;
import com.example.catchMind.service.GameRoomService;
import com.example.catchMind.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomService gameRoomService;
    private final GameService gameService;

    @MessageMapping("/game/enter")
    public void handleEnter(@Payload CatchMindMessage msg) {
        int roomId = msg.getRoomId();
        String loginId = msg.getLoginId();
        Room room = gameRoomService.getRoom(roomId);
        if (room == null) return;

        // 현재 출제자 정보 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                CatchMindMessage.drawer(room.getCurrentDrawer()));

        // 점수판 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                CatchMindMessage.score(room.getScores()));

        // 출제자에게만 정답 전송
        if (loginId.equals(room.getCurrentDrawer())) {
            gameService.sendDrawerInfo(roomId);
        }

        // 리플레이 전송 (입장 시)
        gameRoomService.playReplay(loginId, roomId);
    }

    @MessageMapping("/game/send")
    public void handleMessage(@Payload CatchMindMessage msg) {
        int roomId = msg.getRoomId();
        String loginId = msg.getLoginId();
        Room room = gameRoomService.getRoom(roomId);
        if (room == null) return;

        switch (msg.getType()) {
            case "DRAW" -> {
                messagingTemplate.convertAndSend("/topic/room/" + roomId, msg);
                gameService.recordDrawing(roomId, msg); // 리플레이 저장
            }

            case "CHAT" -> {
                if (room.isAnswerActive()
                        && !room.getAlreadyAnswered().contains(loginId)
                        && msg.getMessage().equals(room.getCurrentAnswer())) {

                    room.getAlreadyAnswered().add(loginId); // 중복 정답 방지
                    gameRoomService.addScore(roomId, loginId, 10);

                    messagingTemplate.convertAndSend("/topic/room/" + roomId,
                            CatchMindMessage.chat("⭕ " + loginId + " 정답!", "SYSTEM"));

                    messagingTemplate.convertAndSend("/topic/room/" + roomId,
                            CatchMindMessage.score(room.getScores()));

                    String newAnswer = gameService.getNewWordAndSet(roomId);
                    gameService.sendDrawerInfo(roomId);
                } else {
                    messagingTemplate.convertAndSend("/topic/room/" + roomId, msg);
                }
            }

            case "READY" -> {
                gameRoomService.markReady(roomId, loginId);

                if (gameRoomService.allReady(roomId)) {
                    long elapsed = System.currentTimeMillis() - room.getEnterTimestamp();

                    if (elapsed >= 30_000) {
                        gameService.startGame(roomId);
                        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                                CatchMindMessage.chat("🎯 모든 유저 준비 완료! 게임을 시작합니다.", "SYSTEM"));
                    } else {
                        long waitMillis = 30_000 - elapsed;
                        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                                CatchMindMessage.chat("⏳ 준비 완료! " + (waitMillis / 1000) + "초 후 게임이 시작됩니다.", "SYSTEM"));

                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                gameService.startGame(roomId);
                                messagingTemplate.convertAndSend("/topic/room/" + roomId,
                                        CatchMindMessage.chat("🎮 게임 시작!", "SYSTEM"));
                            }
                        }, waitMillis);
                    }
                }
            }

            case "REPLAY_REQUEST" -> {
                gameRoomService.playReplay(loginId, roomId);
            }

            case "NEXT_TURN" -> {
                gameService.nextTurn(roomId); // 수동으로 다음 턴 요청
            }
        }
    }
}
