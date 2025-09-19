package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	
	@PostMapping("/signup")
	public String signRegister(SignUpDto user) {
		userService.register(user);
		return "redirect:/";
	}	
	@GetMapping("/check-username")
	@ResponseBody
	public String checkUsername(@RequestParam("username") String username) {
		try {
			int count = userService.countByUsername(username);
			return count > 0 ? "exist" : "ok";
		}catch(Exception e){
			e.printStackTrace();
			return "error";
		}
	}

	@GetMapping("/mypage")
    public String enterMypage(){
        return "infos/mypage";
    }

    @GetMapping("/update")
    public String userInfoUpdateForm(){
        return "infos/info";
    }

    @PostMapping("/update")
    public String userInfoUpdate(){
        return "infos/mypage";
    }

     @GetMapping("/my-inquiries")
    public String enterMyInquiries(){
        return "infos/my-inquiries";
    }
    
     @GetMapping("/user-inquiry")
    public String enterUserInquiry(){
        return "infos/user-inquiry";
    }

     @GetMapping("/inquiry-read")
    public String enterInquiryRead(){
        return "infos/inquiry-read";
    }
    
     @GetMapping("/change-password")
    public String enterChangePassword(){
        return "infos/change-password";
    }

    @PostMapping("/passwordUpdate")
    public String passwordUpadte(){
        return "infos/change-password";
    }

     @GetMapping("/delete")
    public String enterDelete(){
        return "infos/delete";
    }
    
	
	
}
