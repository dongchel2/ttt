package com.example.catchMind.service;

import com.example.catchMind.domain.ReplayEntry;
import com.example.catchMind.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplayService {

    private final GameRoomService gameRoomService;

    public void recordDrawing(int roomId, ReplayEntry entry) {
        Room room = gameRoomService.getRoom(roomId);
        if (room != null) {
            room.addReplayEntry(entry);
        }
    }

    public List<ReplayEntry> getReplay(int roomId) {
        Room room = gameRoomService.getRoom(roomId);
        if (room != null) {
            return new ArrayList<>(room.getReplayEntries());
        }
        return new ArrayList<>();
    }

    public void clearReplay(int roomId) {
        Room room = gameRoomService.getRoom(roomId);
        if (room != null) {
            room.clearReplayData();
        }
    }
}
