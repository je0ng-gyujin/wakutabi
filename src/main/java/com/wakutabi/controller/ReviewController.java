package com.wakutabi.controller;

import com.wakutabi.domain.ReviewUserDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.service.TravelEditService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wakutabi.domain.ReviewTravelDto;
import com.wakutabi.service.ReviewService;

import jakarta.validation.Valid;

import java.security.Principal;

@Controller
@RequestMapping("/travels")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final TravelEditService travelEditService;

    // 리뷰 작성 폼
    @GetMapping("/review")
    public String reviewForm(@RequestParam("tripId")Long tripId, Model model,
                             @ModelAttribute("userId")Long userId) {
        //tripId로 리뷰 대상 여행 정보 조회
        ReviewTravelDto reviewTravelDto = reviewService.getTripAndParticipantsForReview(tripId);
        if(reviewTravelDto == null || reviewTravelDto.getTripId() == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 여행을 찾을 수 없습니다.");
        }
        model.addAttribute("userId", userId);
        // ReviewTravleDto 객체를 모델에 추가하여 폼에서 사용
        model.addAttribute("reviewTravelDto", reviewTravelDto);
        return "travels/review";
    }

    // 리뷰 작성 처리
    @PostMapping("/review")
    public String reviewWrite(@Valid @ModelAttribute("reviewTravelDto") ReviewTravelDto reviewTravelDto,
                                BindingResult bindingResult, @ModelAttribute("userId")Long userId,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("유효성 검사 실패: {}", bindingResult.getAllErrors());
            return "travels/review";
        }

        try {
            if(reviewTravelDto.getReviewUsers() != null){
                for(ReviewUserDto dto : reviewTravelDto.getReviewUsers()){
                   dto.setReviewId(userId);
                }
            }
            reviewService.insertReview(reviewTravelDto);
            redirectAttributes.addFlashAttribute("successMessage", "후기가 성공적으로 저장되었습니다.");
            return "redirect:/travels/success"; // 리뷰 작성 후 메인 페이지로 리다이렉트
        } catch (Exception e) {
            log.error("후기 저장 중 오류가 발생하였습니다.", e);
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 저장 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/travels/review?tripId="+reviewTravelDto.getTripId();
        }
    }

    @GetMapping("/success")
    public String reviewSuccess() {
        return "travels/success";
    }

}
