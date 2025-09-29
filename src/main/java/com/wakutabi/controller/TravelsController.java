package com.wakutabi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;
import com.wakutabi.mapper.TravelDeadlineMapper;
import com.wakutabi.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
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
    private final TravelUpdateDeleteService travelUpdateDeleteService; // ⬅️ 추가
    private final TravelDeadlineService travelDeadlineService; // 추가
    
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
	    	LocalDateTime endDateTime   = endDate != null ? endDate.atTime(23, 59, 59) : null;

	    	log.info("Received search request. Query: {}, minPrice: {}, maxPrice: {}, region: {}, startDate: {}, endDate: {}, tags: {}, groupSize: {}, status: {}", 
	                query, minPrice, maxPrice, region, startDate, endDate, tags, groupSize, status); // ⬅️ 로그 추가

	       List<TravelEditDto> travels = travelEditService.findFilteredTravels(query, minPrice, maxPrice, region, startDateTime, endDateTime, tags, groupSize, status); // ⬅️ status 파라미터 추가

        
            log.info("검색 날짜 파라미터 - startDateTime: {}, endDateTime: {}", startDateTime, endDateTime);
            
        // 2. 각 여행 게시글에 대한 대표 이미지를 조회합니다.
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
        
        
        //3. 모델에 검색 결과와 필터 파라미터들을 다시 담아서 뷰로 전달합니다.
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
    public String uploadTravel(@RequestParam(name = "tags", required = false) String tags,TravelUploadDto uploadDto, Principal principal,@ModelAttribute("userId") Long userId) throws IllegalStateException, IOException {
        // 1. 사용자 인증 및 기본 데이터 유효성 검사
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        log.info("uploadDto: {}", uploadDto);

        // 2. JSON 문자열을 객체로 변환 (orderNumber → ImageOrderDto 리스트)
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

        // 3. 게시글 DTO 생성 및 값 설정
        TravelEditDto dto = new TravelEditDto();
        dto.setTitle(uploadDto.getTitle());
        dto.setLocation(uploadDto.getLocation());
        dto.setContent(uploadDto.getContent());
        dto.setMaxParticipants(uploadDto.getMaxParticipants() != null ? uploadDto.getMaxParticipants() : 10);
        dto.setAgeLimit(uploadDto.getAgeLimit() != null ? uploadDto.getAgeLimit().toUpperCase() : "NO");
        dto.setGenderLimit(uploadDto.getGenderLimit() != null ? uploadDto.getGenderLimit().toUpperCase() : "N");
        dto.setEstimatedCost(uploadDto.getEstimatedCost() != null ? uploadDto.getEstimatedCost() : 0);
        dto.setStatus("OPEN");

        // 날짜 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dto.setStartDate(LocalDate.parse(uploadDto.getStartDate(), formatter));
        dto.setEndDate(LocalDate.parse(uploadDto.getEndDate(), formatter));

        
        // TravelEditDto에 태그 설정
        if (tags != null && !tags.isEmpty()) {
        	// 문자열 리스트로 변환하여 설정
        	dto.setTags(List.of(tags.split(",")));
        }
        
        // 로그인한 사용자 ID 설정 (예시: 1L)
        dto.setHostUserId(userId);

        // 4. 게시글 DB 저장
        travelEditService.saveTravelWithTags(dto);
        
        // 5. 이미지 파일 처리 및 DB 저장
        log.info("uploadDto.getImages = {}", uploadDto.getImages());
        List<MultipartFile> images = uploadDto.getImages();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                ImageOrderDto imageOrder = imageOrders.get(i);

                if (!file.isEmpty()) {
                    // 업로드 폴더 보장
                    String uploadDir = "C:/uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    // 파일명에 UUID를 사용하여 저장 경로 생성
                    String savePath = uploadDir + imageOrder.getUuid() + "_" + file.getOriginalFilename();
                    file.transferTo(new File(savePath));

                    // 이미지 DTO 생성 및 DB 저장
                    TravelImageDto imgDto = new TravelImageDto();
                    imgDto.setTripArticleId(dto.getId()); // 방금 생성된 게시글 ID
                    imgDto.setImagePath(savePath.replaceFirst("C:/uploads", "/upload"));
                    imgDto.setOrderNumber(imageOrder.getOrder()); // JSON에서 받은 순서 값 사용

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
    public String travelDetail(@RequestParam("id") Long id,
                               @ModelAttribute("userId") Long userId,
                                 Model model, Principal principal) {

		TravelEditDto travel = travelEditService.findTravelById(id);
		if (travel == null)
			return "redirect:/error";

		List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);

		// 3. 현재 로그인한 사용자와 게시글 작성자 ID 비교

        boolean isOwner = false;
        if (principal != null) {
            Long currentUserId = userId; // 실제 구현 시 principal 기반으로 조회
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
}
