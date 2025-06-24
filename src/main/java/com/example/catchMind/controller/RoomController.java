package com.example.catchMind.controller;

import com.example.catchMind.domain.Room;
import com.example.catchMind.dto.CatchMindMessage;
import com.example.catchMind.service.GameRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usr/room")
public class RoomController {

    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/list")
    public String roomList(Model model, HttpSession session) {
        model.addAttribute("rooms", gameRoomService.getAllRooms());
        model.addAttribute("loginId", session.getAttribute("loginId"));
        return "/WEB-INF/jsp/usr/room/roomList.jsp";
    }

    @PostMapping("/enter")
    public String enterRoom(@RequestParam int roomId, HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        if (loginId == null) return "redirect:/usr/home/login";

        if (gameRoomService.isFull(roomId)) {
            model.addAttribute("errorMessage", "⚠ 이미 방이 가득 찼습니다.");
            return roomList(model, session);
        }

        if (gameRoomService.isUserInRoom(roomId, loginId)) {
            model.addAttribute("errorMessage", "⚠ 이미 해당 방에 참여 중입니다.");
            return roomList(model, session);
        }

        gameRoomService.enterRoom(roomId, loginId);
        session.setAttribute("roomId", roomId);

        return "/WEB-INF/jsp/usr/home/game.jsp";
    }

    @PostMapping("/leave")
    public String leaveRoom(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        Integer roomId = (Integer) session.getAttribute("roomId");

        if (loginId != null && roomId != null) {
            gameRoomService.leaveRoom(roomId, loginId);

            Room room = gameRoomService.getRoom(roomId);
            if (room != null) {
                String newDrawer = room.getCurrentDrawer();
                if (newDrawer != null) {
                    messagingTemplate.convertAndSend("/topic/room/" + roomId,
                            CatchMindMessage.drawer(newDrawer));
                }
            }
        }

        session.removeAttribute("roomId");
        return "redirect:/usr/room/list";
    }
}
