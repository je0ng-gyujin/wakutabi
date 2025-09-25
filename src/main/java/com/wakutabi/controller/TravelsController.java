package com.wakutabi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;
import com.wakutabi.service.TravelUpdateDeleteService; // ⬅️ 추가

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class TravelsController {

    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;
    private final TravelUpdateDeleteService travelUpdateDeleteService; // ⬅️ 추가
    
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

        

    @GetMapping("create")
    public String travelCreate() {
        return "travels/write";
    }

    @PostMapping("travelupload")
    @ResponseBody
    public String uploadTravel(@RequestParam(name = "tags", required = false) String tags,TravelUploadDto uploadDto, Principal principal) throws IllegalStateException, IOException {
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
            return "redirect:/errorPage";
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
        dto.setStartDate(LocalDate.parse(uploadDto.getStartDate(), formatter).atStartOfDay());
        dto.setEndDate(LocalDate.parse(uploadDto.getEndDate(), formatter).atStartOfDay());

        // ⭐ ⭐ ⭐ 추가: 태그 정보를 DTO에 설정 ⭐ ⭐ ⭐
        // ⭐⭐⭐ 변경: TravelEditDto에 태그 설정 ⭐⭐⭐
        if (tags != null && !tags.isEmpty()) {
        	// 문자열 리스트로 변환하여 설정
        	dto.setTags(List.of(tags.split(",")));
        }
        
        // 로그인한 사용자 ID 설정 (예시: 1L)
        dto.setHostUserId(1L);

        // 4. 게시글 DB 저장
        // ⭐⭐⭐ 변경: travelEditService.insertTravelEdit(dto) 대신, 태그 저장 로직이 포함된 메소드 호출 ⭐⭐⭐
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


        return "등록 완료! 생성된 글 ID: " + dto.getId();

    }


    @GetMapping("/detail")
    public String travelDetail(@RequestParam("id") Long id, Model model,Principal principal) {
        // ID로 게시글 정보 조회
        TravelEditDto travel = travelEditService.findTravelById(id);

        if (travel == null) {
            // 게시글이 없을 경우, 에러 페이지 또는 목록 페이지로 리다이렉션
            return "redirect:/error";
        }

        // 2. 게시글 ID로 이미지 목록을 조회하여 images 변수에 할당합니다.
        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);
        
        // 3. 현재 로그인한 사용자와 게시글 작성자 ID 비교
        boolean isOwner = false;
        if (principal != null) {
            // 실제 애플리케이션에서는 principal.getName()을 이용해 사용자 ID를 DB에서 조회해야 합니다.
            // 현재는 예시로 게시글 등록 시 사용했던 1L을 임시로 사용합니다.
            Long currentUserId = 1L; // 이 부분은 실제 로그인 로직에 맞게 수정 필요
            if (travel.getHostUserId() != null && travel.getHostUserId().equals(currentUserId)) {
                isOwner = true;
            }
        }
        
        // 조회한 게시글 정보를 모델에 담아 HTML로 전달
        model.addAttribute("travel", travel);
        model.addAttribute("images", images);
        model.addAttribute("isOwner", isOwner); // 작성자 여부 추가
        
        return "travels/detail"; // views/travels/detail.html 경로
    }




    /**
     * 여행 게시글 수정
     * @param dto 수정할 게시글 데이터
     * @param principal 사용자 정보
     * @return 수정 결과 메시지
     */
    @PostMapping("travelupdate")
    @ResponseBody
    public String updateTravel(@RequestBody TravelEditDto dto, Principal principal) {
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        // ⚠️ 실제 사용자 ID를 principal에서 가져오는 로직으로 변경해야 합니다.
        // 현재는 예시로 1L을 사용합니다.
        Long hostUserId = 1L; 
        dto.setHostUserId(hostUserId);

        
        boolean isUpdated = travelUpdateDeleteService.updateTravelArticle(dto);

        if (isUpdated) {
            return "게시글 수정 완료!";
        } else {
            return "게시글 수정 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
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
    }
}
