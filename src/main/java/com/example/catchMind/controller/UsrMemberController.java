package com.example.catchMind.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usr/member")
public class UsrMemberController {

    @GetMapping("/login")
    public String showLogin() {
        return "usr/member/login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam String loginId, HttpSession session) {
        if (loginId == null || loginId.trim().isEmpty()) {
            return "redirect:/usr/member/login?error=1";
        }

        session.setAttribute("loginId", loginId.trim());
        return "redirect:/usr/room/list";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usr/member/login";
    }
}
