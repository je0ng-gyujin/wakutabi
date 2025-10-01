package com.wakutabi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.*; // DTO 패키지 통합
import com.wakutabi.service.*; // Service 패키지 통합
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class TravelsController {

    // Service 의존성 주입
    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;
    private final TravelUpdateDeleteService travelUpdateDeleteService;
    private final TravelDeadlineService travelDeadlineService;
    private final ChatService chatService;
    private final TripService tripService;
    private final NotificationService notificationService;
    private final ParticipantService participantService; // ⭐ ParticipantService 추가

    // (기존의 다른 메서드들은 여기에 그대로 유지됩니다)
    // /search, /create, /travelupload 등...
    
    // ---------------------------------------------
    // 3. 여행 상세 조회 (⭐ 수정된 메서드)
    // ---------------------------------------------
      
    @GetMapping("/detail")
    public String travelDetail(@RequestParam("id") Long id,
                               @ModelAttribute("userId") Long userId,
                               Model model, Principal principal) {

        // 1. 기본 여행 정보 조회
        TravelEditDto travel = travelEditService.findTravelById(id);
        if (travel == null) {
            return "redirect:/error"; // 예외 처리
        }
        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);

        // 2. 작성자 여부 확인
        boolean isOwner = false;
        if (principal != null) {
            isOwner = travel.getHostUserId() != null && travel.getHostUserId().equals(userId);
        }

        // 3. 채팅방 ID 조회
        Long chatRoomId = chatService.chatRoomFindByTripArticleId(travel.getId());

        // 4. ⭐ 호스트 및 참여자 목록 조회
        List<ParticipantDto> allParticipants = participantService.getParticipants(id);

        ParticipantDto host = null;
        List<ParticipantDto> participants = new ArrayList<>();

        if (allParticipants != null && !allParticipants.isEmpty()) {
            // SQL에서 HOST를 항상 첫 번째로 정렬했으므로, 첫 요소를 호스트로 설정
            host = allParticipants.get(0);
            // 두 번째 요소부터는 순수 참여자로 설정
            if (allParticipants.size() > 1) {
                participants = allParticipants.subList(1, allParticipants.size());
            }
        }

        // 5. 모든 정보를 모델에 담아 View로 전달
        model.addAttribute("travel", travel);
        model.addAttribute("images", images);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("host", host);             // 호스트 정보 추가
        model.addAttribute("participants", participants); // 참여자 목록 추가

        return "travels/detail";
    }

    // (이하 다른 메서드들은 여기에 그대로 유지됩니다)
    // /travelupdate, /traveldelete, /edit, /myTrips 등...
}