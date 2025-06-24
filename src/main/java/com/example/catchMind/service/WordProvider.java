package com.example.catchMind.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class WordProvider {

    private final List<String> words = List.of(
            "사과", "자동차", "고양이", "강아지", "컴퓨터",
            "바나나", "텔레비전", "의자", "책상", "하늘",
            "바다", "비행기", "전화기", "냉장고", "모자",
            "칠가이", "트랄랄레로 트랄랄라", "푸바오", "피카츄", "꼬부기",
            "로그함수", "안유진", "차은우", "도라에몽", "아구몬", "해피캣"
    );

    private final Random random = new Random();

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }
}
