package com.example.catchMind.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 하나의 DRAW 동작을 저장하는 리플레이용 엔트리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplayEntry {
    private String type;        // "DRAW"
    private int x;
    private int y;
    private String color;
    private int size;
    private String loginId;
    private long timestamp;
}
