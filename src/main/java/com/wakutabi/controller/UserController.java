package com.wakutabi.controller;

import com.wakutabi.domain.SignUpDto;
import com.wakutabi.domain.UserPasswordUpdateDto;
import com.wakutabi.domain.UserUpdateDto;
import com.wakutabi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Slf4j // 1. 로깅을 위한 어노테이션 추가
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 2. application.properties의 파일 저장 경로를 주입받을 변수 선언
    @Value("${file.save.location}")
    private String fileSaveLocation;

    // [회원가입 처리] - 기존 코드 유지
    @PostMapping("/signup")
    public String signRegister(
            @Valid @ModelAttribute("user") SignUpDto user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }
        if (userService.countByUsername(user.getUsername()) > 0) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/user/signup";
        }
        try {
            userService.register(user);
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/user/signup";
        }
        redirectAttributes.addFlashAttribute("message", "회원가입이 거의 완료되었습니다. 이메일을 확인하여 계정을 활성화해주세요.");
        return "redirect:/user/signup-complete";
    }

    // [회원정보 수정 페이지] - 기존 코드 유지
    @GetMapping("/update")
    public String userInfoUpdateForm(Principal principal, Model model) {
        String username = principal.getName();
        UserUpdateDto user = userService.getUserInfo(username);
        model.addAttribute("user", user);
        return "infos/info";
    }

    /**
     * [회원정보 수정 처리] - 전체적으로 수정된 메소드
     */
    @PostMapping("/update")
    public String userInfoUpdate(
            @ModelAttribute UserUpdateDto user,
            @RequestParam("profileImage") MultipartFile profileImage,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // 1. 보안을 위해 현재 로그인된 사용자의 정보를 기준으로 작업
        String username = principal.getName();
        UserUpdateDto originalUser = userService.getUserInfo(username);
        user.setId(originalUser.getId()); // 수정할 사용자의 ID를 DTO에 설정

        // 2. 새 프로필 이미지가 업로드되었는지 확인
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 새 이미지 저장 및 DTO에 웹 경로 설정
                String savedFilename = saveProfileImage(profileImage);
                user.setImagePath("/upload/" + savedFilename);
            } catch (IOException e) {
                log.error("프로필 이미지 저장 중 오류 발생 (사용자: {})", username, e);
                redirectAttributes.addFlashAttribute("error", "이미지 저장 중 오류가 발생했습니다.");
                return "redirect:/user/update";
            }
        } else {
            // 새 이미지가 없으면, 기존 이미지 경로를 그대로 사용하도록 설정
            user.setImagePath(originalUser.getImagePath());
        }

        // 3. 서비스 호출하여 정보 업데이트
        userService.userInfoUpdate(user);
        redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");

        return "redirect:/user/mypage";
    }

    /**
     * 프로필 이미지 저장 로직을 처리하는 별도 메소드
     */
    private String saveProfileImage(MultipartFile file) throws IOException {
        File dir = new File(fileSaveLocation);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        // 파일 확장자 추출
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 고유한 파일명 생성
        String savedFilename = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(fileSaveLocation + savedFilename);
        Files.write(path, file.getBytes());
        return savedFilename;
    }
    
    // (이하 다른 컨트롤러 메소드들은 여기에 그대로 유지...)
    // 예: /signup-complete, /verify-email, /check-username, /mypage 등
    
    @GetMapping("/mypage")
    public String mypage(Principal principal, Model model) {
        // 1. 현재 로그인된 사용자의 이름을 가져옵니다.
        String username = principal.getName();
        
        // 2. 사용자 이름으로 DB에서 전체 정보를 조회합니다.
        UserUpdateDto user = userService.getUserInfo(username);

        // 3. Model에 사용자 정보를 담아 Thymeleaf 템플릿으로 전달합니다.
        model.addAttribute("user", user);

        // 4. 보여줄 템플릿 파일의 경로를 반환합니다.
        // (src/main/resources/templates/infos/mypage.html 파일을 의미)
        return "infos/mypage";
    }
}