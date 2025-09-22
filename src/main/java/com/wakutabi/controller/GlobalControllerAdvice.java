package com.wakutabi.controller;

import com.wakutabi.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    public GlobalControllerAdvice(UserService userService){
        this.userService = userService;
    }

    @ModelAttribute("userId")
    public Long addUserId(Principal principal){
        if(principal != null){
            String username = principal.getName();
            return userService.getUserId(username);
        }
        return null;
    }
}
