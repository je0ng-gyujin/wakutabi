package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;




@Controller
public class ProfileController {
    
    @GetMapping("/profile")
    public String enterProfile(){
        return "infos/profile";
    }
     @GetMapping("/mypage")
    public String enterMypage(){
        return "infos/mypage";
    }
     @GetMapping("/my-Inquiries")
    public String enterMyInquiries(){
        return "infos/my-Inquiries";
    }
     @GetMapping("/info")
    public String enterInfo(){
        return "infos/info";
    }
     @GetMapping("/user-inquiry")
    public String enterUserInquiry(){
        return "infos/user-inquiry";
    }
     @GetMapping("/change-password")
    public String enterChangePassword(){
        return "infos/change-password";
    }
     @GetMapping("/delete")
    public String enterDelete(){
        return "infos/delete";
    }
     @GetMapping("/inquiry-read")
    public String enterInquiryRead(){
        return "infos/inquiry-read";
    }

}