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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        model.addAttribute("travel", travel);
        model.addAttribute("images", images);
        model.addAttribute("participants", participants);

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
    @GetMapping("/edit")
    public String travelEdit(@RequestParam("id") Long id,
                             @ModelAttribute("userId") Long userId,
                             Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        TravelEditDto travel = travelEditService.findTravelById(id);
        if (travel == null) {
            return "redirect:/error";
        }

        Long currentUserId = userId;
        if (!travel.getHostUserId().equals(currentUserId)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("travel", travel);
        return "travels/edit";
    }
}