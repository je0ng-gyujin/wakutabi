package com.wakutabi.controller;


import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
import com.wakutabi.domain.UserUpdateDto;
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
    public String enterMypage(Principal principal, Model model){
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/mypage";
    }

    @GetMapping("/update")
    public String userInfoUpdateForm(Principal principal, Model model){
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/info";
    }

    @PostMapping("/update")
    public String userInfoUpdate(UserUpdateDto user){
       userService.userInfoUpdate(user);
        return "redirect:/user/mypage";
    }

     @GetMapping("/my-inquiries")
    public String enterMyInquiries(){
        return "infos/my-Inquiries";
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
    public String passwordUpadte(Principal principal, 
                                UserPasswordUpdateDto newPassword,
                                Model model){

        String username = principal.getName();
        if (!userService.checkCurrentPassword(username, newPassword.getCurrentPassword())) {
        model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
        return "infos/change-password";
        }

        if(newPassword.getNewPassword() == null || newPassword.getNewPassword().isBlank()){
            model.addAttribute("error", "비밀번호를 입력하세요.");
            return "infos/change-password";
        }

        if(!newPassword.getNewPassword().equals(newPassword.getConfirmNewPassword())){
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "infos/change-password";
        }

        if(newPassword.getNewPassword().length() < 3){
            model.addAttribute("error", "비밀번호는 최소 3자 이상이어야 합니다.");
            return "infos/change-password";
        }

        
        newPassword.setUsername(username);
        userService.userPasswordUpdate(newPassword);

        model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
        return "infos/change-password";
    }

     @GetMapping("/delete")
    public String enterDelete(){
        return "infos/delete";
    }
    
	
	
}
