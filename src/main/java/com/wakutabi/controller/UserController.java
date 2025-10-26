package com.wakutabi.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

import com.wakutabi.domain.*;
import com.wakutabi.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	// 1. properties의 실제 저장 경로(C:/upload/)를 주입받습니다.
    @Value("${file.upload.path}")
    private String uploadPath;

    // 2. properties의 웹 접근 경로(/upload/)를 주입받습니다.
    @Value("${uploadPath}")
    private String webPath;
    
    
    
	@PostMapping("/signup")
	public String signRegister(@Valid SignUpDto user, BindingResult bindingResult,
                               Model model) {
        if(bindingResult.hasErrors()){
            return "users/signup";
        }
		userService.register(user);
		// 회원가입 완료 후 이메일 확인 페이지로 리디렉션
		return "redirect:/user/signup-complete";
	}	
	
	// 회원가입 완료 후 이메일 확인 안내 페이지
	@GetMapping("/signup-complete")
	public String enterSignupComplete() {
		return "infos/signup-complete";
	}

    // 이메일 중복여부 확인
    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email){
        try {
            int count = userService.countByEmail(email);
            return count > 0 ? "exist" : "ok";
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
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
 
    @PostMapping("/update")
    public String userInfoUpdate(
            UserUpdateDto user,
            @RequestParam("profileImage") MultipartFile profileImage) throws IOException {

        // --- 파일 처리 로직 시작 ---
        if (profileImage != null && !profileImage.isEmpty()) {
            
            // 업로드 디렉토리가 없으면 생성합니다.
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFilename = profileImage.getOriginalFilename();
            // 3. 고유한 파일 이름 생성
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            
            // 4. 물리적인 경로에 파일을 저장합니다.
            File saveFile = new File(uploadPath, storedFilename);
            profileImage.transferTo(saveFile);

            // 5. DB에는 웹 접근 경로를 저장합니다.
            // 예: "/upload/고유한이름_파일.jpg"
            user.setImagePath(webPath + storedFilename);
        }
        // --- 파일 처리 로직 끝 ---

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
    
    
    
    // 1. 아이디 찾기 페이지
    @GetMapping("/find-id")
    public String findIdForm() {
        return "users/find-id"; // find-id.html (이메일 입력 폼)
    }

    // 2. [아이디 찾기] 인증번호 발송
    // AJAX로 요청하는 것을 권장 (ResponseEntity 사용)
    @PostMapping("/find-id/send-code")
    @ResponseBody // JSON/Text 응답
    public ResponseEntity<String> sendCodeForFindId(@RequestParam("email") String email, HttpSession session) {
        boolean isSent = userService.sendVerificationCodeForFindId(email, session);
        
        if (isSent) {
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("가입되지 않은 이메일입니다.");
        }
    }

    // 3. [아이디 찾기] 인증번호 확인 및 결과
    @PostMapping("/find-id/verify")
    public String verifyCodeAndFindId(@RequestParam("email") String email,
                                      @RequestParam("code") String code,
                                      HttpSession session,
                                      Model model) {
        
        String foundUsername = userService.verifyCodeAndFindUsername(email, code, session);

        if (foundUsername != null) {
            model.addAttribute("foundUsername", foundUsername);
        } else {
            model.addAttribute("errorMessage", "인증에 실패했습니다. 다시 시도해주세요.");
        }
        
        return "users/find-id-result"; // 결과 페이지
    }
    
    
 // 1. 비밀번호 찾기 페이지
    @GetMapping("/find-pw")
    public String findPwForm() {
        return "users/find-pw"; // find-pw.html (이메일 입력 폼)
    }

    // 2. [비밀번호 찾기] 인증번호 발송
    @PostMapping("/find-pw/send-code")
    @ResponseBody
    public ResponseEntity<String> sendCodeForResetPw(@RequestParam("email") String email, HttpSession session) {
        boolean isSent = userService.sendVerificationCodeForResetPw(email, session);
        
        if (isSent) {
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("가입되지 않은 이메일입니다.");
        }
    }

    // 3. [비밀번호 찾기] 인증번호 확인 및 비밀번호 재설정
    @PostMapping("/find-pw/reset")
    public String verifyCodeAndResetPw(@RequestParam("email") String email,
                                       @RequestParam("code") String code,
                                       @RequestParam("newPassword") String newPassword,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes rttr) {
        
        boolean isReset = userService.verifyCodeAndResetPassword(email, code, newPassword, session);

        if (isReset) {
            rttr.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다. 다시 로그인해주세요.");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        } else {
            model.addAttribute("errorMessage", "인증에 실패했거나 시간이 만료되었습니다. 다시 시도해주세요.");
            return "users/find-pw"; // 다시 비밀번호 찾기 페이지로
        }
    }
    
}
