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
    public class TravelJoinRequestController { // 여행참가신청 컨트롤러

        private final TravelJoinFacadeService travelJoinFacadeService;
        private final ChatService chatService; // 채팅방 생성을 위해 채팅 서비스 주입
        private final TravelJoinRequestService travelJoinRequestService;

        // 여행참가신청
        @PostMapping("/join-request")
        @ResponseBody   // 중요! String redirect가 아니라 JSON 응답으로
        public Map<String, Object> insertTravelJoinRequestAjax(TravelJoinRequestDto travelJoinRequest,
                                                               @RequestParam(name = "chatRoomId", required = false)Long chatRoomId,
                                                               @ModelAttribute("userId") Long userId) {

            Map<String, Object> result = new HashMap<>();

            try {
                if (chatRoomId == null) { // 채팅방이 없으면 새로 생성
                    chatService.setChatRoom(travelJoinRequest.getTripArticleId(), travelJoinRequest.getHostUserId()); // 채팅방 생성
                    chatRoomId = chatService.chatRoomFindByTripArticleId(travelJoinRequest.getTripArticleId()); // 생성된 채팅방 ID 가져오기
                }

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

        @PatchMapping("/join-request/cancel")
        @ResponseBody
        public Map<String, Object> cancelTravelJoinRequest(@RequestParam("tripId") Long tripId, @ModelAttribute("userId") Long userId) {
            log.info("Controller - cancelTravelJoinRequest: tripId={}, userId={}", tripId, userId);
            Map<String, Object> result = new HashMap<>();
            try {
                travelJoinRequestService.cancelJoinRequest(tripId, userId);
                result.put("status", "success");
            } catch (Exception e) {
                log.error("Error canceling join request", e);
                result.put("status", "fail");
                result.put("message", "신청 취소 중 오류가 발생했습니다.");
            }
            return result;
        }
    }