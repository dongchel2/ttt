package com.example.catchMind.service;

import com.example.catchMind.domain.Room;
import com.example.catchMind.dto.CatchMindMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final WordProvider wordProvider;
    private final GameTimerService gameTimerService;

    public void startGame(int roomId) {
        Room room = gameRoomService.getRoom(roomId);
        if (room == null) return;

        room.resetGame();
        room.selectNextDrawer(); // 출제자 선정
        room.nextRound();

        startTurn(room);
    }

    public void startTurn(Room room) {
        int roomId = room.getRoomId();

        // 새 정답 설정
        String answer = wordProvider.getRandomWord();
        room.activateAnswer(answer);
        room.clearAlreadyAnswered();

        // 출제자에게 정답 전송
        sendAnswerToDrawer(room);

        // 출제자 브로드캐스트
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                CatchMindMessage.drawer(room.getCurrentDrawer()));

        // 라운드 시작 알림
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                CatchMindMessage.chat("📢 라운드 " + room.getCurrentRound() + " 시작!", "SYSTEM"));

        // 타이머 시작
        gameTimerService.startTimer(roomId, 60, () -> {
            room.nextRound();
            room.selectNextDrawer();
            room.clearReplayData();

            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    CatchMindMessage.chat("⏱️ 라운드 종료!", "SYSTEM"));

            // 게임 종료 조건 확인
            if (room.getCurrentRound() > room.getMaxRound()) {
                messagingTemplate.convertAndSend("/topic/room/" + roomId,
                        CatchMindMessage.chat("🎉 게임 종료! 점수판을 확인하세요.", "SYSTEM"));

                messagingTemplate.convertAndSend("/topic/room/" + roomId,
                        CatchMindMessage.score(room.getScores()));

                return;
            }

            startTurn(room); // 다음 라운드로
        });
    }

    public String getNewWordAndSet(int roomId) {
        Room room = gameRoomService.getRoom(roomId);
        if (room == null) return "";

        String newAnswer = wordProvider.getRandomWord();
        room.activateAnswer(newAnswer);
        room.clearAlreadyAnswered();
        sendAnswerToDrawer(room);
        return newAnswer;
    }

    public void sendAnswerToDrawer(Room room) {
        String drawer = room.getCurrentDrawer();
        if (drawer != null) {
            messagingTemplate.convertAndSendToUser(drawer, "/queue/answer",
                    CatchMindMessage.answer(room.getCurrentAnswer()));
        }
    }
}
