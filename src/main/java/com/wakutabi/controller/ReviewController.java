package com.wakutabi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wakutabi.domain.ReviewTravleDto;
import com.wakutabi.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/travels")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성 폼
    @GetMapping("/review")
    public String reviewForm(Model model) {
        // ReviewTravleDto 객체를 모델에 추가하여 폼에서 사용
        model.addAttribute("reviewTravleDto", new ReviewTravleDto());
        // TODO: 나중에 비즈니스 로직을 구현하여 동행자 목록을 가져올 예정입니다.
        // model.addAttribute("participants", participantList);
        return "travels/review";
    }

    // 리뷰 작성 처리
    @PostMapping("/review")
    public String reviewWrite(@Valid @ModelAttribute("reviewTravleDto") ReviewTravleDto reviewTravleDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "travels/review";
        }

        try {
            reviewService.insertReview(reviewTravleDto);
            redirectAttributes.addFlashAttribute("successMessage", "후기가 성공적으로 저장되었습니다.");
            return "redirect:/travels/success"; // 리뷰 작성 후 메인 페이지로 리다이렉트
        } catch (Exception e) {
            log.error("후기 저장 중 오류가 발생하였습니다.", e);
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/travels/review";
        }
    }

    @GetMapping("/success")
    public String reviewSuccess() {
        return "travels/success";
    }

}
