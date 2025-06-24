package com.example.catchMind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatchMindMessage {

    private String type;          // "CHAT", "DRAW", "READY", "ANSWER", "SCORE", "NEXT", "REPLAY", ...
    private String loginId;       // 유저 식별 ID
    private String message;       // 일반 채팅 또는 시스템 메시지
    private int roomId;           // 방 번호
    private Object payload;       // 확장용 필드 (점수 Map, 정답, 그림 데이터 등)

    // 채팅 메시지
    public static CatchMindMessage chat(String message, String from) {
        return CatchMindMessage.builder()
                .type("CHAT")
                .message(message)
                .loginId(from)
                .build();
    }

    // 정답 전송
    public static CatchMindMessage answer(String answer) {
        return CatchMindMessage.builder()
                .type("ANSWER")
                .message(answer)
                .build();
    }

    // 점수판 전송
    public static CatchMindMessage score(Map<String, Integer> scores) {
        return CatchMindMessage.builder()
                .type("SCORE")
                .payload(scores)
                .build();
    }

    // 출제자 지정
    public static CatchMindMessage drawer(String loginId) {
        return CatchMindMessage.builder()
                .type("DRAWER")
                .loginId(loginId)
                .build();
    }

    // 다음 라운드 시작 알림
    public static CatchMindMessage next(int round) {
        return CatchMindMessage.builder()
                .type("NEXT")
                .message("ROUND_" + round)
                .build();
    }

    // 리플레이용 메시지
    public static CatchMindMessage replay(Object replayData) {
        return CatchMindMessage.builder()
                .type("REPLAY")
                .payload(replayData)
                .build();
    }
}
