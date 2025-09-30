package com.wakutabi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;
import com.wakutabi.service.TravelEditService;
import com.wakutabi.service.TravelImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // 여행 일정 작성 페이지로 이동
    @GetMapping("create")
    public String travelCreate() {
        return "travels/write";
    }

    // 여행 일정 업로드 처리
    @PostMapping("travelupload")
    @ResponseBody
    public String uploadTravel(TravelUploadDto uploadDto, Principal principal)
            throws IllegalStateException, IOException {
        // 1. 사용자 인증 및 기본 데이터 유효성 검사
        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }

        // 1-1. 콘솔 상 업로드 로그 출력
        log.info("uploadDto: {}", uploadDto);

        // 2. JSON 문자열을 객체로 변환 (orderNumber → ImageOrderDto 리스트)
        ObjectMapper objectMapper = new ObjectMapper();
        List<ImageOrderDto> imageOrders;
        try {
            imageOrders = objectMapper.readValue(
                    uploadDto.getOrderNumber(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ImageOrderDto.class));
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

        dto.setTagString(uploadDto.getTag());
        log.info("설정된 태그: {}", uploadDto.getTag());

        // 3-1. 날짜 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dto.setStartDate(LocalDate.parse(uploadDto.getStartDate(), formatter).atStartOfDay());
        dto.setEndDate(LocalDate.parse(uploadDto.getEndDate(), formatter).atStartOfDay());

        // 3-2. 로그인한 사용자 ID 설정 (예시: 1L)
        dto.setHostUserId(1L);

        // 4. 게시글 DB 저장
        travelEditService.insertTravelEdit(dto);

        // 5. 이미지 파일 처리 및 DB 저장
        log.info("uploadDto.getImages = {}", uploadDto.getImages());
        List<MultipartFile> images = uploadDto.getImages();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                ImageOrderDto imageOrder = imageOrders.get(i);

                if (!file.isEmpty()) {
                    // 업로드 디렉토리 설정
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

    // ✨ 여행 상세 조회 (View 렌더링)
    // 최종 URL: GET /schedule/detail/{id}
    @GetMapping("/detail/{id}")
    // @PathVariable에 경로 변수 이름 "id"를 명시적으로 지정합니다.
    public String getTravelDetailView(@PathVariable("id") Long id, Model model) {
        log.info("View 요청 처리: /schedule/detail/{}", id);

        // 1. 서비스 호출 및 데이터 조회
        TravelEditDto travelDetail = travelEditService.getTravelDetail(id);

        // 2. 데이터가 없다면 예외 처리
        if (travelDetail == null) {
            log.warn("게시글 ID {} 을 찾을 수 없어 404 페이지로 이동합니다.", id);
            return "redirect:/error/404";
        }

        // 3. 모델에 데이터 추가
        model.addAttribute("travelDetail", travelDetail);

        // 4. 뷰 반환
        return "travels/detail";
    }
}