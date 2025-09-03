package com.wakutabi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login"; // templates/login.html 을 렌더링함
    }
    
    @GetMapping("/signup")
    public String showSignupPage() {
    	return "user/signup";
    }
}