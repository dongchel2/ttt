package com.example.catchMind.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.catchMind.domain.Room;
import com.example.catchMind.dto.CatchMindMessage;
import com.example.catchMind.dto.ReplayEntry;

import jakarta.annotation.PostConstruct;

@Service
public class GameRoomService {

    private final Map<Integer, Room> roomMap = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    
    this.messagingTemplate = messagingTemplate;
    @PostConstruct
    public void init() {
        for (int i = 1; i <= 4; i++) {
            roomMap.put(i, new Room(i));
        }
    }

    public Room getRoom(int roomId) {
        return roomMap.get(roomId);
    }

    public Collection<Room> getAllRooms() {
        return roomMap.values();
    }

    public boolean isFull(int roomId) {
        Room room = getRoom(roomId);
        return room != null && room.getUsers().size() >= room.getMaxUsers();
    }

    public boolean isUserInRoom(int roomId, String loginId) {
        Room room = getRoom(roomId);
        return room != null && room.getUsers().contains(loginId);
    }

    public void enterRoom(int roomId, String loginId) {
        Room room = getRoom(roomId);
        if (room == null) return;

        room.addUser(loginId);

        // 최초 입장자라면 출제자로 지정
        if (room.getCurrentDrawer() == null) {
            room.setCurrentDrawer(loginId);
        }

        // 입장 시간 저장 (게임 시작 조건 확인용)
        if (room.getUsers().size() == 1) {
            room.setEnterTimestamp(System.currentTimeMillis());
        }
    }

    public void leaveRoom(int roomId, String loginId) {
        Room room = getRoom(roomId);
        if (room == null) return;

        room.removeUser(loginId);
        room.removeReady(loginId);

        // 출제자가 나간 경우
        if (loginId.equals(room.getCurrentDrawer())) {
            room.selectNextDrawer();
        }

        // 유저 없으면 초기화
        if (room.getUsers().isEmpty()) {
            room.resetGame();
            room.clearReplayData();
        }
    }

    public void markReady(int roomId, String loginId) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.markReady(loginId);
        }
    }

    public boolean allReady(int roomId) {
        Room room = getRoom(roomId);
        if (room == null) return false;
        return room.getUsers().equals(room.getReadyUsers());
    }

    public void addScore(int roomId, String loginId, int delta) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.addScore(loginId, delta);
        }
    }
    
    
    public void playReplay(String loginId, int roomId) {
        Room room = getRoom(roomId);
        if (room == null) return;

        List<ReplayEntry> replayEntries = room.getReplayEntries();
        for (ReplayEntry entry : replayEntries) {
            messagingTemplate.convertAndSendToUser(loginId, "/queue/replay", entry);
            try {
                Thread.sleep(100); // 부드러운 애니메이션용
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

	public void saveReplayEntry(int roomId, CatchMindMessage msg) {
		// TODO Auto-generated method stub
		
	}

	public void replaySync(String loginId, int roomId) {
		// TODO Auto-generated method stub
		
	}

}
