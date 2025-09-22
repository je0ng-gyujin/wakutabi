package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


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
	
	@GetMapping("/detail")
	public String detail() {
		return "travels/detail";
	}
}
