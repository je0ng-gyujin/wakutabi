package com.wakutabi.controller;

import com.wakutabi.domain.InquiryDto;
import com.wakutabi.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    //문의 내역
    @GetMapping("/user/my-inquiries")
    public String enterMyInquiries(@ModelAttribute("userId") Long userId,
                                   Model model){
//        List<InquiryDto> inquiry = inquiryService.getInquiry(userId);
//        model.addAttribute("inquiries", inquiry);
        return "infos/my-inquiries";
    }
    // 문의 작성
    @GetMapping("/user/user-inquiry")
    public String enterUserInquiry(){
             return "infos/user-inquiry";
    }
    // 문의 작성 처리
    @PostMapping("/user/user-inquiry")
    public String writerUserInquiry(@ModelAttribute InquiryDto inquiry,
                                    @ModelAttribute("userId") Long userId){
        inquiry.setUserId(userId);
        inquiryService.setInquiry(inquiry);
        return "redirect:/user/my-inquiries";
    }
    // 문의 내역 상세
    @GetMapping("/user/inquiry-read")
    public String enterInquiryRead(){
        return "infos/inquiry-read";
    }
}
