package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wakutabi.domain.UserDto;
import com.wakutabi.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/signup")
	public String signRegister(UserDto user) {
		userService.register(user);
		return "redirect:/";
	}
	@GetMapping("/check-username")
	@ResponseBody
	public String checkUsername(@RequestParam("username") String username) {
		try {
			int count = userService.countByusername(username);
			return count > 0 ? "exist" : "ok";
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
	}
	
}
