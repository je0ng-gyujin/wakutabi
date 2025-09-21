package com.wakutabi.controller;

import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    // 회원프로필 페이지
    @GetMapping("/profile")
    public String enterProfile(Principal principal, Model model){
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/profile";
    }

}