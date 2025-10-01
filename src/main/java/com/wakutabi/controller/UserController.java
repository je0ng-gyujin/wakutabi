package com.wakutabi.controller;

import java.security.Principal;

import com.wakutabi.domain.*;
import com.wakutabi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	public String signRegister(SignUpDto user) {
		userService.register(user);
		// 회원가입 완료 후 이메일 확인 페이지로 리디렉션
		return "redirect:/user/signup-complete";
	}	
	
	// 회원가입 완료 후 이메일 확인 안내 페이지
	@GetMapping("/signup-complete")
	public String enterSignupComplete() {
		return "infos/signup-complete";
	}
	
	// 이메일 인증 링크를 처리하는 엔드포인트
	// UserController.java
	// UserController.java
	@GetMapping("/verify-email")
	public String verifyEmail(@RequestParam("username") String username,
	                          @RequestParam("token") String token,
	                          Model model) { // RedirectAttributes -> Model
	    boolean isVerified = userService.verifyEmail(username, token);
	    
	    if (isVerified) {
	        model.addAttribute("message", "이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다."); // RedirectAttributes.addFlashAttribute -> Model.addAttribute
	        return "infos/verification-success"; // redirect:/user/verification-success -> infos/verification-success
	    } else {
	        model.addAttribute("error", "잘못된 인증 링크입니다.");
	        return "infos/verification-failure";
	    }
	}
	
	
	@GetMapping("/check-username")
	@ResponseBody
	public String checkUsername(@RequestParam("username") String username) {
		try {
			int count = userService.countByUsername(username);
			return count > 0 ? "exist" : "ok";
		} catch (Exception e) {
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

    @GetMapping("/survey")
    public String enterSurvey() {
    	return "infos/survey";
    }
}
