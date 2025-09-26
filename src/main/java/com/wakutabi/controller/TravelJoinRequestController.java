package com.wakutabi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.TravelJoinRequestService;

@Controller
@RequiredArgsConstructor
public class TravelJoinRequestController {

    private final TravelJoinRequestService travelJoinRequestService;

    // 여행참가신청
    @PostMapping("/join-request")
    public String insertTravelJoinRequest(TravelJoinRequestDto travelJoinRequest,
                                          @ModelAttribute("userId") Long userId){
        travelJoinRequest.setApplicantUserId(userId);                                         
        travelJoinRequestService.insertTravelJoinRequest(travelJoinRequest);
        
        // NotificationService.sendJoinRequest(
        //     travelJoinRequest.getTripArticleId(),
        //     travelJoinRequest.getHostUserId(),
        //     userId
        // );

        return "redirect:/schedule/detail?id="+travelJoinRequest.getTripArticleId();
    }
    // 참가수락
    @PostMapping("/request-accepted/accept/{id}")
    public String changeStatusToAccepted(@PathVariable Long id,
                                         @ModelAttribute("userId") Long hostUserId){
        travelJoinRequestService.changeStatusToAccepted(id,hostUserId);
        return "redirect:/host/join-requests";
    }
    // 참가거절
    @PostMapping("/request-rejected/reject/{id}")
    public String changeStatusTorejected(@PathVariable Long id,
                                         @ModelAttribute("userId") Long hostUserId){
        travelJoinRequestService.changeStatusToRejected(id, hostUserId);
        return "redirect:/host/join-requests";
    }
    
}   