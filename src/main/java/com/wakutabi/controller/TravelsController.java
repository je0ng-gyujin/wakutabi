package com.wakutabi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;
import com.wakutabi.service.NotificationService;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;
import com.wakutabi.service.TravelUpdateDeleteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class TravelsController {

    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;

    private final TravelUpdateDeleteService travelUpdateDeleteService;
    private final UserService userService;
    private final NotificationService notificationService;



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
    public String uploadTravel(@ModelAttribute("userId") Long userId, TravelUploadDto uploadDto, Principal principal) throws IOException {

        if (principal == null) return "로그인 후 이용 가능합니다.";

        log.info("uploadDto: {}", uploadDto);

        // 이미지 순서 JSON -> 객체 리스트 변환
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

        // 게시글 DTO 생성
        TravelEditDto dto = new TravelEditDto();
        dto.setTitle(uploadDto.getTitle());
        dto.setLocation(uploadDto.getLocation());
        dto.setContent(uploadDto.getContent());
        dto.setMaxParticipants(uploadDto.getMaxParticipants() != null ? uploadDto.getMaxParticipants() : 10);
        dto.setAgeLimit(uploadDto.getAgeLimit() != null ? uploadDto.getAgeLimit().toUpperCase() : "NO");
        dto.setGenderLimit(uploadDto.getGenderLimit() != null ? uploadDto.getGenderLimit().toUpperCase() : "N");
        dto.setEstimatedCost(uploadDto.getEstimatedCost() != null ? uploadDto.getEstimatedCost() : 0);
        dto.setStatus("OPEN");

        // 날짜 변환 (수정된 부분)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dto.setStartDate(LocalDate.parse(uploadDto.getStartDate(), formatter));
        dto.setEndDate(LocalDate.parse(uploadDto.getEndDate(), formatter));

        // 로그인한 사용자 ID 설정 (예시: 1L)
        dto.setHostUserId(userId);

        // 게시글 DB 저장
        travelEditService.insertTravelEdit(dto);


        // 5. 이미지 파일 처리 및 DB 저장
        log.info("uploadDto.getImages = {}", uploadDto.getImages());

        List<MultipartFile> images = uploadDto.getImages();
        if (images != null && !images.isEmpty()) {
            String uploadDir = "C:/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                ImageOrderDto imageOrder = imageOrders.get(i);

                if (!file.isEmpty()) {
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

        // 6. 등록 된 여행에 대한 알림 테이블 저장
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
    public String travelDetail(@RequestParam("id") Long id, Model model, Principal principal) {

        TravelEditDto travel = travelEditService.findTravelById(id);
        if (travel == null) return "redirect:/error";

        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);


        // 3. 현재 로그인한 사용자와 게시글 작성자 ID 비교

        boolean isOwner = false;
        if (principal != null) {
            Long currentUserId = 1L; // 실제 구현 시 principal 기반으로 조회
            isOwner = travel.getHostUserId() != null && travel.getHostUserId().equals(currentUserId);
        }

        // 조회한 게시글 정보를 모델에 담아 HTML로 전달
        model.addAttribute("travel", travel);
        model.addAttribute("images", images);
        model.addAttribute("isOwner", isOwner); // 작성자 여부 추가

        return "travels/detail"; // views/travels/detail.html 경로
    }



    // ---------------------------------------------
    // 4. 여행 글 수정
    // ---------------------------------------------
 // TravelsController.java
    @PostMapping("/travelupdate")
    @ResponseBody
    public String updateTravel(@ModelAttribute TravelEditDto dto, Principal principal) {
        if (principal == null) return "로그인 후 이용 가능합니다.";

        dto.setHostUserId(1L); // 실제 구현 시 principal 기반으로 설정

        // 현재 컨트롤러는 `updateTravelArticle` 메소드에 dto를 직접 전달하고 있는데,
        // 이 메소드에서 어떤 데이터를 필요로 하는지 확인해야 합니다.
        // 예를 들어, 이미지 정보도 함께 업데이트해야 한다면,
        // DTO에 MultipartFile 필드가 추가되어야 합니다.

        boolean isUpdated = travelUpdateDeleteService.updateTravelArticle(dto);

        return isUpdated ? "게시글 수정 완료!" : "게시글 수정 실패! (권한 없거나 게시글을 찾을 수 없습니다)";

    }
}


/**
 * 여행 게시글 삭제
 * @param dto 삭제할 게시글 ID를 포함한 DTO
 * @param principal 사용자 정보
 * @return 삭제 결과 메시지
 */
@PostMapping("traveldelete")
@ResponseBody
public String deleteTravel(@RequestBody TravelEditDto dto, Principal principal) {
    if (principal == null) {
        return "로그인 후 이용 가능합니다.";
    }

    // ⚠️ 실제 사용자 ID를 principal에서 가져오는 로직으로 변경해야 합니다.
    // 현재는 예시로 1L을 사용합니다.
    Long hostUserId = 1L;

    boolean isDeleted = travelUpdateDeleteService.deleteTravelArticle(dto.getId(), hostUserId);

    if (isDeleted) {
        return "게시글 삭제 완료!";
    } else {
        return "게시글 삭제 실패! (권한 없거나 게시글을 찾을 수 없습니다)";

    }
    
    
    
 // TravelsController.java
 // ...
 // ---------------------------------------------
 // 6. 여행 글 수정 페이지
 // ---------------------------------------------
 @GetMapping("/edit")
 public String travelEdit(@RequestParam("id") Long id, Model model, Principal principal) {
     if (principal == null) {
         return "redirect:/login"; // 로그인 페이지로 리다이렉트
     }

     // 1. 게시글 ID로 기존 데이터 조회
     TravelEditDto travel = travelEditService.findTravelById(id);
     if (travel == null) {
         return "redirect:/error"; // 게시글이 없으면 에러 페이지로
     }

     // 2. 작성자 본인인지 확인 (실제 사용자 ID와 비교)
     Long currentUserId = 1L; // TODO: principal.getName()을 사용해 실제 사용자 ID 가져오기
     if (!travel.getHostUserId().equals(currentUserId)) {
         return "redirect:/access-denied"; // 권한 없으면 접근 거부 페이지로
     }

     // 3. 데이터를 Model에 담아 Thymeleaf로 전달
     model.addAttribute("travel", travel);

     // 4. 새로운 수정 폼 HTML 페이지 반환
     return "travels/edit";
 }
 // ...
}
}