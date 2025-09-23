package com.wakutabi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.service.NotificationService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class MainController {

	@GetMapping("/") 
	public String index(Model model) {
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
