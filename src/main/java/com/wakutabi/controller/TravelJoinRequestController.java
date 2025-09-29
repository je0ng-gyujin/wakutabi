    package com.wakutabi.controller;

    import com.wakutabi.mapper.TravelDeadlineMapper;
    import com.wakutabi.service.TravelDeadlineService;
    import com.wakutabi.service.TravelJoinFacadeService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.*;

    import com.wakutabi.domain.TravelJoinRequestDto;
    import com.wakutabi.service.NotificationService;
    import com.wakutabi.service.TravelJoinRequestService;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.Objects;
    @Slf4j
    @Controller
    @RequiredArgsConstructor
    public class TravelJoinRequestController {

        private final TravelJoinRequestService travelJoinRequestService;
        private final NotificationService notificationService;
        private final TravelJoinFacadeService travelJoinFacadeService;
        // 여행참가신청
        @PostMapping("/join-request")
        @ResponseBody   // 중요! String redirect가 아니라 JSON 응답으로
        public Map<String, Object> insertTravelJoinRequestAjax(TravelJoinRequestDto travelJoinRequest,
                                                               @RequestParam("chatRoomId")Long chatRoomId,
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
                boolean closed = travelJoinFacadeService.joinTravel(travelJoinRequest,chatRoomId,userId);

                result.put("status", "success");
                result.put("tripArticleId", travelJoinRequest.getTripArticleId());
                if(closed){
                    result.put("message", "인원이 다 찼습니다.");
                }else {
                    result.put("message", "참가 신청이 완료되었습니다.");
                }

            } catch (Exception e) {
                log.error("참가신청 중 오류",e);
                result.put("status", "fail");
                result.put("message", "참가 신청 중 오류가 발생했습니다.");
            }

            return result;
        }
    }