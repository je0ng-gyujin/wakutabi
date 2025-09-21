package com.wakutabi.controller;


import java.security.Principal;
import java.util.List;

import com.wakutabi.domain.*;
import jakarta.validation.Valid;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    // 회원정보 페이지
	@GetMapping("/mypage")
    public String enterMypage(Principal principal, Model model){
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/mypage";
    }
    // 회원정보 수정 페이지
    @GetMapping("/update")
    public String userInfoUpdateForm(Principal principal, Model model){
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/info";
    }
    // 회원정보 수정 처리
    @PostMapping("/update")
    public String userInfoUpdate(UserUpdateDto user){
       userService.userInfoUpdate(user);
        return "redirect:/user/mypage";
    }
    // 문의 내역

    //패스워드 수정 페이지
    @GetMapping("/change-password")
    public String enterChangePassword(){
        return "infos/change-password";
    }
    //패스워드 수정 처리
    @PostMapping("/passwordUpdate")
    public String passwordUpadte(Principal principal, 
                                @Valid UserPasswordUpdateDto newPassword,
                                BindingResult result,
                                Model model){
        String username = principal.getName();

        if(result.hasErrors()){
            model.addAttribute("error", result.getAllErrors().get(0).getDefaultMessage());
            return "infos/change-password";
        }
        if (!userService.checkCurrentPassword(username, newPassword.getCurrentPassword())) {
        model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
        return "infos/change-password";
        }

        if(!newPassword.getNewPassword().equals(newPassword.getConfirmNewPassword())){
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "infos/change-password";
        }

        newPassword.setUsername(username);
        userService.userPasswordUpdate(newPassword);

        model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
        return "infos/change-password";
    }
    //회원 탈퇴 페이지
    @GetMapping("/delete")
    public String enterDelete(){
        return "infos/delete";
    }
    //회원 탈퇴 처리
    @PostMapping("/delete")
    public String enterDelete(Principal principal,
                              @RequestParam("currentPassword") String currentPassword,
                              Model model){

        String username = principal.getName();

        if (!userService.checkCurrentPassword(username, currentPassword)) {
             model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
             return "infos/delete";
        }

        userService.userWithdrawal(username);
        model.addAttribute("message", "탈퇴되었습니다.");
        return "infos/delete";
        }

}
