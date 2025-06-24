package com.example.catchMind.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Room {
    private int id;
    private String name;
    private Set<String> users = new LinkedHashSet<>();
    private String currentDrawer;
    private String currentAnswer = "-";
    private boolean answerActive = false;
    private int currentRound = 1;
    private int maxRound = 2;
    private long enterTimestamp = System.currentTimeMillis();
    private boolean started = false;

    private Map<String, Integer> scores = new HashMap<>();
    private Set<String> readyStatus = new HashSet<>();
    private Set<String> alreadyAnswered = new HashSet<>();

    private List<ReplayEntry> replayEntries = new ArrayList<>();
    private int maxUsers = 2;

    public void addUser(String loginId) {
        users.add(loginId);
        scores.putIfAbsent(loginId, 0);
    }

    public void removeUser(String loginId) {
        users.remove(loginId);
        scores.remove(loginId);
        readyStatus.remove(loginId);
        alreadyAnswered.remove(loginId);
        if (loginId.equals(currentDrawer)) {
            selectNextDrawer();
        }
    }

    public void markReady(String loginId) {
        readyStatus.add(loginId);
    }

    public void removeReady(String loginId) {
        readyStatus.remove(loginId);
    }

    public boolean isReady(String loginId) {
        return readyStatus.contains(loginId);
    }

    public boolean allReady() {
        return readyStatus.containsAll(users) && users.size() > 1;
    }

    public void activateAnswer(String word) {
        this.currentAnswer = word;
        this.answerActive = true;
        this.alreadyAnswered.clear();  // 정답 맞힌 사람 초기화
    }

    public void deactivateAnswer() {
        this.answerActive = false;
    }

    public void addScore(String loginId, int delta) {
        scores.put(loginId, scores.getOrDefault(loginId, 0) + delta);
    }

    public void selectNextDrawer() {
        List<String> userList = new ArrayList<>(users);
        if (userList.isEmpty()) {
            currentDrawer = null;
            return;
        }
        int index = userList.indexOf(currentDrawer);
        index = (index + 1) % userList.size();
        currentDrawer = userList.get(index);
    }

    public void nextRound() {
        currentRound++;
    }

    public void clearReplayData() {
        replayEntries.clear();
    }

    public void addReplayEntry(ReplayEntry entry) {
        replayEntries.add(entry);
    }

    public boolean hasUser(String loginId) {
        return users.contains(loginId);
    }

    public boolean hasAlreadyAnswered(String loginId) {
        return alreadyAnswered.contains(loginId);
    }

    public void markAnswered(String loginId) {
        alreadyAnswered.add(loginId);
    }
}
