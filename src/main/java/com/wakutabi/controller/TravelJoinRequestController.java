package com.wakutabi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.service.TravelJoinRequestService;




@Controller
@RequiredArgsConstructor
public class TravelJoinRequestController {

    private final TravelJoinRequestService travelJoinRequestService;

    // 여행참가신청
    @PostMapping("/join-request")
    public String insertTravelJoinRequest(TravelJoinRequestDto travelJoinRequest){
        travelJoinRequestService.insertTravelJoinRequest(travelJoinRequest);

        return "redirect:/detail";
    }
    // 참가수락
    @PostMapping("/request-accepted")
    public String changeStatusToAccepted(@ModelAttribute("userId") Long userId){
        travelJoinRequestService.changeStatusToAccepted(userId);
        return "redirect:detail";
    }
    // 참가거절
    @PostMapping("/request-rejected")
    public String changeStatusTorejected(@ModelAttribute("userId") Long userId){
        travelJoinRequestService.changeStatusToRejected(userId);
        return "redirect:detail";
    }
    
}   