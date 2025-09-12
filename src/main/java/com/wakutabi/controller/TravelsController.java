package com.wakutabi.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.service.TravelEditService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class TravelsController {

    private final TravelEditService travelEditService;

    @GetMapping("create")
    	public String travelCreate() {
    		return "travels/edit";
    	}
    
    
    @PostMapping("travelupload")
    @ResponseBody
    public String uploadTravel(
    		@RequestParam("title") String title,
    		@RequestParam("location") String location,
    		@RequestParam("content") String content,
    		@RequestParam("startDate") String startDate,
    		@RequestParam("endDate") String endDate,
    		@RequestParam(value = "maxParticipants", required = false) Integer maxParticipants,
    		@RequestParam(value = "ageLimit", required = false) String ageLimit,
    		@RequestParam(value = "genderLimit", required = false) String genderLimit,
    		@RequestParam(value = "estimatedCost", required = false) Integer estimatedCost,
    		Principal principal
    ) {
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        TravelEditDto dto = new TravelEditDto();

        // DTO 세팅
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

        // 로그인한 사용자 ID 세팅 (임시로 1L, 실제는 DB에서 principal.getName()으로 조회)
        dto.setHostUserId(1L);

        // DB Insert
        travelEditService.insertTravelEdit(dto);

        return "등록 완료! 생성된 글 ID: " + dto.getId();
    }
}
