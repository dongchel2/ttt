package com.example.catchMind.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsrHomeController {

    @GetMapping("/")
    public String showHome() {
        return "usr/home/main";
    }

    @GetMapping("/usr/home/main")
    public String showMain(Model model, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        if (loginId != null) {
            model.addAttribute("loginedMember", true);
            model.addAttribute("loginId", loginId);
        } else {
            model.addAttribute("loginedMember", false);
        }

        return "usr/home/main";
    }

    @GetMapping("/usr/home/game")
    public String showGame(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("loginId");
        Integer roomId = (Integer) session.getAttribute("roomId");

        if (loginId == null || roomId == null) {
            return "redirect:/usr/room/roonList";
        }

        model.addAttribute("loginId", loginId);
        model.addAttribute("roomId", roomId);

        return "usr/home/game";
    }
}