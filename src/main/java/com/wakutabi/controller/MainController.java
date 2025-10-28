package com.wakutabi.controller;

import com.wakutabi.domain.SignUpDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {

	@GetMapping("/") 
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/signup")
	public String signupForm(Model model) {
		model.addAttribute("SignUpDto", new SignUpDto());
		return "users/signup";
	}

	@GetMapping("/login")
	public String loginForm() {
		return "users/login";
	}

}
