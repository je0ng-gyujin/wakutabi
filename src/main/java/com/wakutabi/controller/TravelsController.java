package com.wakutabi.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.NotificationDto;

import com.wakutabi.domain.ParticipantDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.ParticipantService; // import 추가
import com.wakutabi.service.TravelDeadlineService;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;
import com.wakutabi.service.TravelUpdateDeleteService;


import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;
import com.wakutabi.service.TravelUpdateDeleteService;
import com.wakutabi.service.TripService;


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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class TravelsController {

    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;

    private final TravelUpdateDeleteService travelUpdateDeleteService;
    private final TravelDeadlineService travelDeadlineService;
    private final NotificationService notificationService;
    private final ParticipantService participantService; // 의존성 주입 추가

    private final ChatService chatService;

    private final TripService tripService; 
    

    //검색
    @GetMapping("/search")
    public String searchTravels(
            @RequestParam(value = "keyward", required = false) String query,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "groupSize", required = false) List<String> groupSize,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        log.info("Received search request. Query: {}, minPrice: {}, maxPrice: {}, region: {}, startDate: {}, endDate: {}, tags: {}, groupSize: {}, status: {}",
                query, minPrice, maxPrice, region, startDate, endDate, tags, groupSize, status);

        List<TravelEditDto> travels = travelEditService.findFilteredTravels(query, minPrice, maxPrice, region, startDateTime, endDateTime, tags, groupSize, status);

        log.info("검색 날짜 파라미터 - startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);

        if (travels != null) {
            for (TravelEditDto travel : travels) {
                if (travel != null && travel.getId() != null) {
                    List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(travel.getId());
                    if (images != null && !images.isEmpty()) {
                        TravelImageDto mainImage = images.get(0);
                        if (mainImage != null && mainImage.getImagePath() != null) {
                            travel.setMainImagePath(mainImage.getImagePath());
                        } else {
                            travel.setMainImagePath("/images/default.jpg");
                        }
                    } else {
                        travel.setMainImagePath("/images/default.jpg");
                    }
                } else {
                    log.warn("Null travel object found in the search result list.");
                }
            }
        }

        model.addAttribute("travels", travels);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("region", region);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("tags", tags);
        model.addAttribute("groupSize", groupSize);
        model.addAttribute("status", status);

        return "travels/search";
    }

    // ---------------------------------------------
    // 1. 여행 글 작성 페이지
    // ---------------------------------------------
    @GetMapping("/create")
    public String travelCreate() {
        return "travels/write";
    }

    // ---------------------------------------------
    // 2. 여행 글 업로드 (POST, AJAX/JSON)
    // ---------------------------------------------
    @PostMapping("/travelupload")
    @ResponseBody
    public String uploadTravel(@RequestParam(name = "tags", required = false) String tags, TravelUploadDto uploadDto, Principal principal, @ModelAttribute("userId") Long userId) throws IllegalStateException, IOException {
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        log.info("uploadDto: {}", uploadDto);

        ObjectMapper objectMapper = new ObjectMapper();
        List<ImageOrderDto> imageOrders;
        try {
            imageOrders = objectMapper.readValue(
                    uploadDto.getOrderNumber(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ImageOrderDto.class)
            );
        } catch (IOException e) {
            log.error("이미지 순서 변환 실패", e);
            return "이미지 순서 처리 실패";
        }

        TravelEditDto dto = new TravelEditDto();
        dto.setTitle(uploadDto.getTitle());
        dto.setLocation(uploadDto.getLocation());
        dto.setContent(uploadDto.getContent());
        dto.setMaxParticipants(uploadDto.getMaxParticipants() != null ? uploadDto.getMaxParticipants() : 10);
        dto.setAgeLimit(uploadDto.getAgeLimit() != null ? uploadDto.getAgeLimit().toUpperCase() : "NO");
        dto.setGenderLimit(uploadDto.getGenderLimit() != null ? uploadDto.getGenderLimit().toUpperCase() : "N");
        dto.setEstimatedCost(uploadDto.getEstimatedCost() != null ? uploadDto.getEstimatedCost() : 0);
        dto.setStatus("OPEN");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dto.setStartDate(LocalDate.parse(uploadDto.getStartDate(), formatter));
        dto.setEndDate(LocalDate.parse(uploadDto.getEndDate(), formatter));

        if (tags != null && !tags.isEmpty()) {
            dto.setTags(List.of(tags.split(",")));
        }

        dto.setHostUserId(userId);

        travelEditService.saveTravelWithTags(dto);

        log.info("uploadDto.getImages = {}", uploadDto.getImages());
        List<MultipartFile> images = uploadDto.getImages();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                ImageOrderDto imageOrder = imageOrders.get(i);

                if (!file.isEmpty()) {
                    String uploadDir = "C:/uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    String savePath = uploadDir + imageOrder.getUuid() + "_" + file.getOriginalFilename();
                    file.transferTo(new File(savePath));

                    TravelImageDto imgDto = new TravelImageDto();
                    imgDto.setTripArticleId(dto.getId());
                    imgDto.setImagePath(savePath.replaceFirst("C:/uploads", "/upload"));
                    imgDto.setOrderNumber(imageOrder.getOrder());

                    travelImageService.insertTravelImage(imgDto);
                }
            }
        }

        NotificationDto noticeDto = new NotificationDto();
        String uploadedTravelUrl = "/schedule/detail?id=" + dto.getId();
        noticeDto.setUserId(userId);
        noticeDto.setType("TRAVEL_UPLOADED");
        noticeDto.setTitle(uploadDto.getTitle());
        noticeDto.setLink(uploadedTravelUrl);

        notificationService.insertNotification(noticeDto);

        return "등록 완료! 생성된 글 ID: " + dto.getId();
    }

    // ---------------------------------------------
    // 3. 여행 상세 조회
    // ---------------------------------------------
    @GetMapping("/detail")
    public String travelDetail(@RequestParam("id") Long id,
                               @ModelAttribute("userId") Long userId,
                               Model model, Principal principal) {

        TravelEditDto travel = travelEditService.findTravelById(id);
        if (travel == null) return "redirect:/error";

        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);

        List<ParticipantDto> participants = participantService.participant(id); // 수정된 부분


        Long chatRoomId = chatService.chatRoomFindByTripArticleId(travel.getId());
		// 조회한 게시글 정보를 모델에 담아 HTML로 전달
		model.addAttribute("travel", travel);
		model.addAttribute("images", images);
		model.addAttribute("isOwner", isOwner); // 작성자 여부 추가
        model.addAttribute("chatRoomId", chatRoomId);// 채팅룸ID 추가

        return "travels/detail";
    }

    // ---------------------------------------------
    // 4. 여행 글 수정
    // ---------------------------------------------
    @PostMapping("/travelupdate")
    @ResponseBody
    public String updateTravel(@ModelAttribute TravelEditDto dto,
                               @ModelAttribute("userId") Long userId,
                               Principal principal) {
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        dto.setHostUserId(userId);
        boolean isUpdated = travelUpdateDeleteService.updateTravelArticle(dto);

        return isUpdated ? "게시글 수정 완료!" : "게시글 수정 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
    }

    @DeleteMapping("/traveldelete")
    @ResponseBody
    public String deleteTravel(@RequestBody TravelEditDto dto,
                               @ModelAttribute("userId") Long userId,
                               Principal principal) {
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        Long hostUserId = userId;
        boolean isDeleted = travelUpdateDeleteService.deleteTravelArticle(dto.getId(), hostUserId);

        return isDeleted ? "게시글 삭제 완료!" : "게시글 삭제 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
    }

    // ---------------------------------------------
    // 6. 여행 글 수정 페이지
    // ---------------------------------------------

 // TravelsController.java
 // TravelsController.java
 // ...
 @PostMapping("/travelupdate")
 @ResponseBody
 public String updateTravel(@ModelAttribute TravelEditDto dto,
                            @ModelAttribute("userId") Long userId,
                            Principal principal) {
     if (principal == null) {
         return "로그인 후 이용 가능합니다.";
     }

     dto.setHostUserId(userId); // Set the hostUserId from the authenticated user
     boolean isUpdated = travelUpdateDeleteService.updateTravelArticle(dto);

     return isUpdated ? "게시글 수정 완료!" : "게시글 수정 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
 }

 @DeleteMapping("/traveldelete")
 @ResponseBody
 public String deleteTravel(@RequestBody TravelEditDto dto,
                            @ModelAttribute("userId") Long userId,
                            Principal principal) {
     if (principal == null) {
         return "로그인 후 이용 가능합니다.";
     }

     Long hostUserId = userId; // Get the hostUserId from the authenticated user
     boolean isDeleted = travelUpdateDeleteService.deleteTravelArticle(dto.getId(), hostUserId);

     return isDeleted ? "게시글 삭제 완료!" : "게시글 삭제 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
 }
 // ...

    
    
 // TravelsController.java
 // ...
 // ---------------------------------------------
 // 6. 여행 글 수정 페이지
 // ---------------------------------------------
 @GetMapping("/edit")
 public String travelEdit(@RequestParam("id") Long id, 
                          @ModelAttribute("userId") Long userId,
                          Model model, Principal principal) {
     if (principal == null) {
         return "redirect:/login"; // 로그인 페이지로 리다이렉트
     }

		// 1. 게시글 ID로 기존 데이터 조회
		TravelEditDto travel = travelEditService.findTravelById(id);
		if (travel == null) {
			return "redirect:/error"; // 게시글이 없으면 에러 페이지로
		}

     // 2. 작성자 본인인지 확인 (실제 사용자 ID와 비교)
     Long currentUserId = userId; // TODO: principal.getName()을 사용해 실제 사용자 ID 가져오기
     if (!travel.getHostUserId().equals(currentUserId)) {
         return "redirect:/access-denied"; // 권한 없으면 접근 거부 페이지로
     }

		// 3. 데이터를 Model에 담아 Thymeleaf로 전달
		model.addAttribute("travel", travel);


		// 4. 새로운 수정 폼 HTML 페이지 반환
		return "travels/edit";
	}
	// ...
	@GetMapping("/myTrips")
	public String MyTrips(Principal principal, Model model) {
		
		// 1. 현재 로그인된 사용자 ID를 가져옵니다.
	    String stringUsername = principal.getName(); // Spring Security는 String 반환
	    
	    // 2. ⭐ Long 타입의 사용자 PK를 조회하는 로직을 사용 ⭐
	    Long currentUserId = tripService.findUserIdByUsername(stringUsername); // 👈 새 메서드 호출
	    
	    if (currentUserId == null) {
	        log.error("로그인된 사용자 이름으로 DB PK를 찾을 수 없습니다: {}", stringUsername);
	        return "redirect:/login"; // 인증 문제로 간주하고 로그인 페이지로 리다이렉트
	    }
	    
	    // 3. 사용자가 등록한 여행 목록을 서비스 계층에서 조회합니다.
	    List<TripListDto> registeredTrips = tripService.getRegisteredTrips(currentUserId);

	    model.addAttribute("registeredTrips", registeredTrips);
        
        // (선택) 사용자가 신청한 여행 목록도 필요하다면 여기서 추가합니다.
        // List<TripDto> appliedTrips = tripService.getAppliedTrips(currentUserId);
        // model.addAttribute("appliedTrips", appliedTrips);
        
        // myTrips.html 템플릿 반환
		return "travels/myTrips";
	}
	
 // ⭐ 수정된 신청자 목록 조회 API ⭐
    @GetMapping("/api/schedule/{tripArticleId}/applicants")
    @ResponseBody 
    public List<TripJoinRequestDto> getApplicants(@PathVariable("tripArticleId") Long tripArticleId) {
        // 기존 로직 유지: tripService.getPendingJoinRequests(tripArticleId) 호출
        return tripService.getPendingJoinRequests(tripArticleId);
    }
    
    // ⭐ 수정된 신청 수락/거절 처리 API ⭐
    @PutMapping("/api/request/{requestId}/status")
    @ResponseBody
    public ResponseEntity<?> updateJoinRequestStatus(
            // ⭐ 이 부분 수정 ⭐
            @PathVariable("requestId") Long requestId, 
            @RequestBody RequestStatusDto requestStatusDto, 
            Principal principal) {
        
        // 1. 현재 로그인된 사용자 ID를 Long으로 가져옵니다. 
        Long currentUserId;
        try {
            // principal.getName()이 ID(Long)를 반환한다는 가정 하에 Long으로 파싱
            currentUserId = Long.parseLong(principal.getName()); 
        } catch (Exception e) {
            return ResponseEntity.status(401).body("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        try {
            // 2. 서비스 로직 호출
            tripService.processJoinRequest(requestId, requestStatusDto.getStatus(), currentUserId);
            
            // 3. 성공 응답 반환
            return ResponseEntity.ok().build(); 
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("신청 처리 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (Exception e) {
            log.error("신청 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("신청 처리 중 서버 오류가 발생했습니다.");
        }
    }
}