package com.example.catchMind.controller;

import com.example.catchMind.service.GameRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RoomController {

    private final GameRoomService gameRoomService;

    @GetMapping("/usr/room/roomList")
    public String showRoomList(Model model) {
        model.addAttribute("rooms", gameRoomService.getAllRooms());
        return "usr/room/roomList";
    }

    @PostMapping("/usr/room/enter")
    public String enterRoom(@RequestParam int roomId, HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");

        boolean success = gameRoomService.enterRoom(roomId, loginId);
        if (!success) {
            model.addAttribute("errorMsg", "방 입장 실패: 인원 초과 또는 중복 입장입니다.");
            return "usr/room/roomList";
        }

        session.setAttribute("roomId", roomId);
        return "redirect:/usr/room/game?roomId=" + roomId;
    }

    @PostMapping("/usr/room/leave")
    public String leaveRoom(@RequestParam int roomId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        if (loginId == null) {
            // 세션 만료 또는 비정상 접근
            return "redirect:/usr/member/login";
        }

        gameRoomService.leaveRoom(roomId, loginId);
        session.removeAttribute("roomId");

        return "redirect:/usr/room/roomList";
    }


    @GetMapping("/usr/room/game")
    public String showGame(@RequestParam int roomId, Model model, HttpSession session) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("loginId", session.getAttribute("loginId"));
        return "usr/room/game";
    }
    
    
}
