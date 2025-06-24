package com.example.catchMind.service;

import com.example.catchMind.dto.ReplayEntry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DrawingStorageService {

    // roomId -> round -> replay entries
    private final Map<Integer, Map<Integer, List<ReplayEntry>>> replayStorage = new HashMap<>();

    public void saveEntry(int roomId, int round, ReplayEntry entry) {
        replayStorage
            .computeIfAbsent(roomId, k -> new HashMap<>())
            .computeIfAbsent(round, k -> new ArrayList<>())
            .add(entry);
    }

    public List<ReplayEntry> getReplay(int roomId, int round) {
        return replayStorage.getOrDefault(roomId, Collections.emptyMap())
                            .getOrDefault(round, Collections.emptyList());
    }

    public void clearReplay(int roomId) {
        replayStorage.remove(roomId);
    }
}
