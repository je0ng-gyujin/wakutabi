package com.wakutabi.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class TravelsController {

    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;
    @GetMapping("create")
    	public String travelCreate() {
    		return "travels/write";
    	}
    
    
    @PostMapping("travelupload")
    @ResponseBody
    public String uploadTravel(
            @RequestParam("title") String title,
            @RequestParam("location") String location,
            @RequestParam("content") String content,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("maxParticipants") Integer maxParticipants,
            @RequestParam("ageLimit") String ageLimit,
            @RequestParam("genderLimit") String genderLimit,
            @RequestParam("estimatedCost") Integer estimatedCost,
            @RequestParam("tag") String tag,
            @RequestParam("orderNumber") String orderNumber,
            @RequestParam("image") List<MultipartFile> images,
            Principal principal
    ) throws IllegalStateException, IOException {
        // 1. 사용자 인증 및 기본 데이터 유효성 검사
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        // 2. JSON 문자열을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<ImageOrderDto> imageOrders;
        try {
            imageOrders = objectMapper.readValue(orderNumber, objectMapper.getTypeFactory().constructCollectionType(List.class, ImageOrderDto.class));
        } catch (IOException e) {
            return "Failed to parse orderNumber JSON.";
        }

        // 3. 게시글 DTO 생성 및 값 설정
        TravelEditDto dto = new TravelEditDto();
        dto.setTitle(title);
        dto.setLocation(location);
        dto.setContent(content);
        dto.setMaxParticipants(maxParticipants != null ? maxParticipants : 10);
        dto.setAgeLimit(ageLimit != null ? ageLimit.toUpperCase() : "NO");
        dto.setGenderLimit(genderLimit != null ? genderLimit.toUpperCase() : "N");
        dto.setEstimatedCost(estimatedCost != null ? estimatedCost : 0);
        dto.setStatus("OPEN");

        // 날짜 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dto.setStartDate(LocalDate.parse(startDate, formatter).atStartOfDay());
        dto.setEndDate(LocalDate.parse(endDate, formatter).atStartOfDay());

        // 로그인한 사용자 ID 설정 (예시)
        dto.setHostUserId(1L);

        // 4. 게시글 DB 저장
        travelEditService.insertTravelEdit(dto);
        
        // 5. 이미지 파일 처리 및 DB 저장
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                ImageOrderDto imageOrder = imageOrders.get(i);

                if (!file.isEmpty()) {
                    // 파일명에 UUID를 사용하여 저장 경로 생성
                    String savePath = "/uploads/" + imageOrder.getUuid() + "_" + file.getOriginalFilename();
                    file.transferTo(new File(savePath));

                    // 이미지 DTO 생성 및 DB 저장
                    TravelImageDto imgDto = new TravelImageDto();
                    imgDto.setTripArticleId(dto.getId()); // 방금 생성된 게시글 ID
                    imgDto.setImagePath(savePath);
                    imgDto.setIsMain(i == 0); // 첫 번째 이미지를 메인으로 설정
                    imgDto.setOrderNumber(imageOrder.getOrder()); // JSON에서 받은 순서 값 사용

                    travelImageService.insertTravelImage(imgDto);
                }
            }
        }
        
        return "등록 완료! 생성된 글 ID: " + dto.getId();
    }
}
