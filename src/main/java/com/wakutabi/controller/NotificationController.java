package com.wakutabi.controller;

import com.wakutabi.domain.TravelJoinRequestDto;
import com.wakutabi.service.*;
import com.wakutabi.domain.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
    private final ChatService chatService;
    private final ChatParticipantsService chatParticipantsService;

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
        chatParticipantsService.addUserToChatParticipants(chatRoomId, applicantUserId);
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

