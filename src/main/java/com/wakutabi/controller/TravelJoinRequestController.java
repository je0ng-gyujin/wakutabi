package com.wakutabi.controller;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.service.ChatService;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.TravelJoinFacadeService; // import 추가
import com.wakutabi.service.TravelJoinRequestService;
import com.wakutabi.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TravelJoinRequestController {

    private final TravelJoinRequestService travelJoinRequestService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ChatService chatService;
    private final TravelJoinFacadeService travelJoinFacadeService; // 의존성 주입 추가

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
                result.put("message", "호스트는 참가신청 할 수 없습니다.");
                return result;
            }

            Long chatRoomId = chatService.chatRoomFindByTripArticleId(travelJoinRequest.getTripArticleId()); // chatRoomId 선언
            boolean closed = travelJoinFacadeService.joinTravel(travelJoinRequest, chatRoomId, userId);

            result.put("status", "success");
            result.put("tripArticleId", travelJoinRequest.getTripArticleId());
            if (closed) {
                result.put("message", "인원이 다 찼습니다.");
            } else {
                result.put("message", "참가 신청이 완료되었습니다.");
            }

        } catch (Exception e) {
            log.error("참가신청 중 오류", e);
            result.put("status", "fail");
            result.put("message", "참가 신청 중 오류가 발생했습니다.");
        }

        return result;
    }

    // 호스트가 참가수락 눌렀을 때
    @PostMapping("/accept")
    public RedirectView acceptRequest(@RequestParam Long noticeId, @ModelAttribute("userId") Long userId) throws AccessDeniedException {
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        Long hostUserId = notice.getUserId();
        if (!hostUserId.equals(userId)) {
            throw new AccessDeniedException("여행 작성자만 참가 수락 가능합니다");
        }
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long tripArticleId = notice.getTripArticleId();
        TravelJoinRequestDto statusToAccepted = TravelJoinRequestDto.builder()
                .tripArticleId(tripArticleId)
                .hostUserId(hostUserId)
                .applicantUserId(applicantUserId)
                .status(TravelJoinRequestDto.Status.ACCEPTED)
                .build();
        travelJoinRequestService.changeStatusToAccepted(statusToAccepted);

        Long chatRoomId = chatService.chatRoomFindByTripArticleId(tripArticleId);
        chatService.addUserToChatParticipants(chatRoomId, applicantUserId);
        
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
    public RedirectView rejectRequest(@RequestParam Long noticeId, @ModelAttribute("userId") Long userId) throws AccessDeniedException {
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        Long hostUserId = notice.getUserId();
        if (!hostUserId.equals(userId)) {
            throw new AccessDeniedException("여행 작성자만 참가 수락 가능합니다");
        }
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long tripArticleId = notice.getTripArticleId();

        TravelJoinRequestDto statusToRejected = TravelJoinRequestDto.builder()
                .tripArticleId(tripArticleId)
                .hostUserId(hostUserId)
                .applicantUserId(applicantUserId)
                .status(TravelJoinRequestDto.Status.REJECTED) // 수정된 부분
                .build();
        travelJoinRequestService.changeStatusToRejected(statusToRejected);
        
        NotificationDto sendRequestAnswer = NotificationDto.builder()
                .userId(applicantUserId)
                .tripArticleId(tripArticleId)
                .title("여행신청이 거절되었습니다.")
                .type("TRAVEL_REJECTED") // 수정된 부분
                .link("/schedule/detail?id=" + tripArticleId)
                .build();
        notificationService.sendRequestAnswer(sendRequestAnswer);

        notificationService.markAsRead(noticeId);

        return new RedirectView(notice.getLink());
    }
}