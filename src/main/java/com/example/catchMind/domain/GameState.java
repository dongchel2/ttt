//package com.example.catchMind.domain;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.stereotype.Component;
//
//import lombok.Getter;
//
//@Component
//@Getter
//public class GameState {
//
//    private final List<String> loginIdList = new ArrayList<>();
//    private final Map<String, Integer> scoreMap = new ConcurrentHashMap<>();
//    private String currentDrawer;
//    private String currentAnswer;
//    private boolean timerRunning = false;
//    private List<String> playerOrder = new ArrayList<>();
//    private int currentDrawerIndex = -1;
//    
//    
//    private int round = 1; // 현재 라운드
//    private int turnCount = 0; // 전체 턴 수 (모든 유저의 출제 턴 포함)
//    private final int maxRounds = 2; // 최대 라운드
//    
//
//  //  준비 상태 저장 구조 만들기
//    private final Set<String> readyUsers = ConcurrentHashMap.newKeySet();
//    private long firstJoinTimestamp = 0; // 최초 접속자 기준 시간 기록
//
//    
//    
//    public void addUser(String loginId) {
//        if (!loginIdList.contains(loginId)) {
//            loginIdList.add(loginId);
//            scoreMap.putIfAbsent(loginId, 0);
//        }
//    }
//
//    public void removeUser(String loginId) {
//        loginIdList.remove(loginId);
//        scoreMap.remove(loginId);
//
//        // 출제자였을 경우 순환
//        if (Objects.equals(currentDrawer, loginId) && !loginIdList.isEmpty()) {
//            rotateDrawer();
//        }
//
//        // 아무도 없으면 상태 초기화
//        if (loginIdList.isEmpty()) {
//            currentDrawer = null;
//            currentAnswer = null;
//            timerRunning = false;
//        }
//    }
//
//    public void setDrawer(String loginId) {
//        this.currentDrawer = loginId;
//    }
//
//    public void rotateDrawer() {
//        if (loginIdList.isEmpty()) return;
//        int nextIndex = (loginIdList.indexOf(currentDrawer) + 1) % loginIdList.size();
//        currentDrawer = loginIdList.get(nextIndex);
//        currentAnswer = null;
//        timerRunning = false;
//    }
//
//    public void setAnswer(String answer) {
//        this.currentAnswer = answer;
//    }
//
//    public boolean isCorrectAnswer(String input) {
//        return currentAnswer != null && currentAnswer.equalsIgnoreCase(input.trim());
//    }
//
//    public void increaseScore(String loginId, int point) {
//        scoreMap.computeIfPresent(loginId, (k, v) -> v + point);
//    }
//
//    public List<String> getAllLoginIds() {
//        return new ArrayList<>(loginIdList);
//    }
//
//    public boolean isTimerRunning() {
//        return timerRunning;
//    }
//
//    public void setTimerRunning(boolean running) {
//        this.timerRunning = running;
//    }
//    
//    
//    public void addScore(String loginId, int point) {
//        scoreMap.put(loginId, scoreMap.getOrDefault(loginId, 0) + point);
//    }
//    
//    
//    public void reset() {
//        this.currentAnswer = null;
//        // 필요하다면 다른 상태도 초기화 가능 (예: isRoundActive 등)
//    }
//    
//    
//    
//    public String getNextDrawer() {
//        if (playerOrder.isEmpty()) return null;
//        currentDrawerIndex = (currentDrawerIndex + 1) % playerOrder.size();
//        return playerOrder.get(currentDrawerIndex);
//    }
//    
//    public void setPlayerOrder(List<String> players) {
//        this.playerOrder = new ArrayList<>(players);
//        this.currentDrawerIndex = -1;
//    }
//    
//    
//
//	public long getFirstJoinTimestamp() {
//	    return firstJoinTimestamp;
//	}
//	
//	public void setFirstJoinTimestamp(long firstJoinTimestamp) {
//	    this.firstJoinTimestamp = firstJoinTimestamp;
//	}
//    
//}
