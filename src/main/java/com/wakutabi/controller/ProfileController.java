package com.wakutabi.controller;

import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    // 회원프로필 페이지
    @GetMapping("/my")
    public String enterProfile(Principal principal, Model model){
        if (principal == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "profile/profile";
    }

}