package com.wakutabi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.TravelJoinRequestService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class TravelJoinRequestController {

    private final TravelJoinRequestService travelJoinRequestService;
    private final NotificationService notificationService;

    // 여행참가신청
    @PostMapping("/join-request")
    @ResponseBody   // 중요! String redirect가 아니라 JSON 응답으로
    public Map<String, Object> insertTravelJoinRequestAjax(
            TravelJoinRequestDto travelJoinRequest,
            @ModelAttribute("userId") Long userId) {

        Map<String, Object> result = new HashMap<>();

        try {
            // userId 검증
            if (userId == null) {
                result.put("status", "fail");
                result.put("message", "로그인이 필요합니다.");
                return result;
            }
            if (Objects.equals(userId, travelJoinRequest.getHostUserId())) {
                result.put("status", "fail");
                result.put("message", "호스트는 참가신청 할 수 없습니다.");
                return result;
            }

            travelJoinRequest.setApplicantUserId(userId);
            travelJoinRequestService.insertTravelJoinRequest(travelJoinRequest);

            notificationService.sendJoinRequest(
                    travelJoinRequest.getTripArticleId(),
                    travelJoinRequest.getHostUserId(),
                    userId
            );

            result.put("status", "success");
            result.put("message", "참가 신청이 완료되었습니다.");
            result.put("tripArticleId", travelJoinRequest.getTripArticleId());

        } catch (Exception e) {
            result.put("status", "fail");
            result.put("message", "참가 신청 중 오류가 발생했습니다.");
        }

        return result;
    }
}   