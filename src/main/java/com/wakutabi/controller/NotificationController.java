package com.wakutabi.controller;

import com.wakutabi.service.NotificationService;
import com.wakutabi.domain.NotificationDto;
import com.wakutabi.service.TravelJoinRequestService;
import com.wakutabi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final TravelJoinRequestService travelJoinRequestService;

    @GetMapping("/read")
    public RedirectView markAsReadAndRedirect(@ModelAttribute("userId") Long userId, @RequestParam("id") Long notificationId, Principal principal) {
        
        // 2. 알림 ID로 알림 정보를 조회합니다.
        NotificationDto notice = notificationService.findNotificationById(notificationId);
        
        // 3. 유효성 검사: 알림이 존재하지 않거나, 현재 사용자의 알림이 아닐 경우
        if (notice == null || !notice.getUserId().equals(userId)) {
            log.warn("Unauthorized access attempt on notification ID {}. User ID: {}", notificationId, userId);
            return new RedirectView("/access-denied"); // 또는 홈 페이지로 리다이렉트
        }
        
        // 4. 알림을 읽음 상태로 업데이트합니다.
        notificationService.markAsRead(notificationId);
        
        log.info("Notification with ID {} has been marked as read by user {}.", notificationId, userId);

        // 5. 알림에 저장된 원래 링크로 사용자를 리다이렉트합니다.
        return new RedirectView(notice.getLink());
    }

    // 호스트가 참가수락 눌렀을 때
    @PostMapping("/accept")
    public RedirectView acceptRequest(@RequestParam Long noticeId,
                                      Principal principal){
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        Long hostUserId = notice.getUserId();
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long travelArticleId = notice.getTravelArticleId();
        travelJoinRequestService.changeStatusToAccepted(travelArticleId,hostUserId,applicantUserId);
        // 채팅 참가자로 넣기
        //chatService.addUserToCharRoom(notificationDto.getTravelArticleId(), notificationDto.getTitle)())
        notificationService.sendRequestAnswer(
                applicantUserId,
                travelArticleId,
                "여행신청이 수락되었습니다.",
                "TRAVEL_ACCEPTED"
        );

        notificationService.markAsRead(noticeId);

        return new RedirectView(notice.getLink());
    }

    // 호스트가 참가 거절 눌렀을 때
    @PostMapping("/reject")
    public RedirectView rejectRequest(@RequestParam Long noticeId,
                                      Principal principal){
        NotificationDto notice = notificationService.findNotificationById(noticeId);
        Long hostUserId = notice.getUserId();
        Long applicantUserId = userService.getUserId(notice.getTitle());
        Long travelArticleId = notice.getTravelArticleId();
        travelJoinRequestService.changeStatusToRejected(travelArticleId,hostUserId,applicantUserId);

        notificationService.sendRequestAnswer(
                applicantUserId,
                travelArticleId,
                "참가신청이 거절되었습니다.",
                "TRAVEL_REJECT"
        );

        notificationService.markAsRead(noticeId);

        return new RedirectView(notice.getLink());
    }
}