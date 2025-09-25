package com.wakutabi.controller;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;


    @ModelAttribute("userId")
    public Long addUserId(Principal principal){
        if(principal != null){
            String username = principal.getName();
            return userService.getUserId(username);
        }
        return null;
    }
    
    // 알림 목록(notices)을 모델에 추가하는 역할
    @ModelAttribute("notices")
    public List<NotificationDto> addNotifications(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Long userId = userService.getUserId(username);
            
            if (userId != null) {
                return notificationService.findNotificationsById(userId);
            }
        }
        return Collections.emptyList();
    }
    
    // 알림 개수를 모델에 별도로 추가하는 메서드
    @ModelAttribute("noticeCount")
    public Integer addNotificationCount(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Long userId = userService.getUserId(username);
            if (userId != null) {
                return notificationService.countNotificationsByUserId(userId);
            }
        }
        
        // 로그인하지 않았거나 사용자 ID가 없으면 0을 반환
        return 0; 
    }
}
