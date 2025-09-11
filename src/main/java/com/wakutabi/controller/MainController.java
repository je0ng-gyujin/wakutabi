package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.wakutabi.domain.UserDto;

@Controller
public class MainController {
	
	@GetMapping("/") 
	public String index() {
		return "index";
	}
	
	@GetMapping("/signup")
	public String signupForm() {
		return "users/signup";
	}
	@GetMapping("/login")
	public String loginForm() {
		return "users/login";
	}
	
}
