package com.example.catchMind.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class GameTimerService {

    private final GameService gameService;
    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

    private final Map<Integer, ScheduledFuture<?>> timerMap = new ConcurrentHashMap<>();

    {
        scheduler.initialize();
    }

    public void startTimer(int roomId, int seconds) {
        cancelTimer(roomId); // 이전 타이머가 있으면 종료

        ScheduledFuture<?> future = scheduler.schedule(
            () -> gameService.nextTurn(roomId),
            triggerContext -> {
                long delay = seconds * 1000L;
                return new java.util.Date(System.currentTimeMillis() + delay);
            }
        );

        timerMap.put(roomId, future);
    }

    public void cancelTimer(int roomId) {
        ScheduledFuture<?> future = timerMap.get(roomId);
        if (future != null) {
            future.cancel(true);
            timerMap.remove(roomId);
        }
    }
}
