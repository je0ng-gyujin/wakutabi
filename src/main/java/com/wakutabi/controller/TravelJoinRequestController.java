    package com.wakutabi.controller;

    import com.wakutabi.service.*;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;

    import com.wakutabi.domain.TravelJoinRequestDto;

    import java.util.HashMap;
    import java.util.Map;

    @Slf4j
    @Controller
    @RequiredArgsConstructor
    public class TravelJoinRequestController {

        private final TravelJoinFacadeService travelJoinFacadeService;
        // 여행참가신청
        @PostMapping("/join-request")
        @ResponseBody   // 중요! String redirect가 아니라 JSON 응답으로
        public Map<String, Object> insertTravelJoinRequestAjax(TravelJoinRequestDto travelJoinRequest,
                                                               @RequestParam("chatRoomId")Long chatRoomId,
                                                               @ModelAttribute("userId") Long userId) {

            Map<String, Object> result = new HashMap<>();

            try {
                String message = travelJoinFacadeService.joinTravel(travelJoinRequest,chatRoomId,userId);

                result.put("status", "success");
                result.put("tripArticleId", travelJoinRequest.getTripArticleId());
                result.put("message", message);

            } catch (IllegalStateException e) {
                result.put("status","fail");
                result.put("message", e.getMessage());
            }catch (Exception e) {
                log.error("참가신청 중 오류",e);
                result.put("status", "fail");
                result.put("message", "참가 신청 중 오류가 발생했습니다.");
            }

            return result;
        }
    }