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
    @Slf4j
    @Controller
    @RequiredArgsConstructor
    public class TravelJoinRequestController {

    private final TravelJoinRequestService travelJoinRequestService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ChatService chatService;

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
    // 호스트가 참가수락 눌렀을 때
    @PostMapping("/accept")
    public RedirectView acceptRequest(@RequestParam Long noticeId, @ModelAttribute("userId") Long userId){
        // 알림조회
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        // 알림에 저장된 hostId와 현재 로그인 유저가 같은지 검증
        Long hostUserId = notice.getUserId();
        if(!hostUserId.equals(userId)){
            throw new AccessDeniedException("여행 작성자만 참가 수락 가능합니다");
        }
        // 참가자ID (알림 title에 username이 들어있음)
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long tripArticleId = notice.getTripArticleId();
        // 여행참가수락DTO
        TravelJoinRequestDto statusToAccepted =TravelJoinRequestDto.builder()
                .tripArticleId(tripArticleId)
                .hostUserId(hostUserId)
                .applicantUserId(applicantUserId)
                .status(TravelJoinRequestDto.Status.ACCEPTED)
                .build();
        travelJoinRequestService.changeStatusToAccepted(statusToAccepted);
        // 채팅 참가자로 넣기
        Long chatRoomId = chatService.chatRoomFindByTripArticleId(tripArticleId);
        chatService.addUserToChatParticipants(chatRoomId, applicantUserId);
        // 여행참가수락알림DTO 생성
        NotificationDto sendRequestAnswer = NotificationDto.builder()
                .userId(applicantUserId)
                .tripArticleId(tripArticleId)
                .title("여행신청이 수락되었습니다.")
                .type("TRAVEL_ACCEPTED")
                .link("/schedule/detail?id=" + tripArticleId)
                .build();
        notificationService.sendRequestAnswer(sendRequestAnswer);

        notificationService.markAsRead(noticeId);

        return new RedirectView(notice.getLink());
    }

    // 호스트가 참가 거절 눌렀을 때
    @PostMapping("/reject")
    public RedirectView rejectRequest(@RequestParam Long noticeId,@ModelAttribute("userId") Long userId){
        // 알림조회
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        // 알림에 저장된 hostId와 현재 로그인 유저가 같은지 검증
        Long hostUserId = notice.getUserId();
        if(!hostUserId.equals(userId)){
            throw new AccessDeniedException("여행 작성자만 참가 수락 가능합니다");
        }
        // 참가자ID (알림 title에 username이 들어있음)
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long tripArticleId = notice.getTripArticleId();
        // 여행참가거절DTO 생성
        TravelJoinRequestDto statusToRejected =TravelJoinRequestDto.builder()
                .tripArticleId(tripArticleId)
                .hostUserId(hostUserId)
                .applicantUserId(applicantUserId)
                .status(TravelJoinRequestDto.Status.ACCEPTED)
                .build();
        travelJoinRequestService.changeStatusToRejected(statusToRejected);
        // 여행참가거절알림DTO 생성
        NotificationDto sendRequestAnswer = NotificationDto.builder()
                .userId(applicantUserId)
                .tripArticleId(tripArticleId)
                .title("여행신청이 거절되었습니다.")
                .type("TRAVEL_ACCEPTED")
                .link("/schedule/detail?id=" + tripArticleId)
                .build();
        notificationService.sendRequestAnswer(sendRequestAnswer);

        notificationService.markAsRead(noticeId);

        return new RedirectView(notice.getLink());
    }
}   