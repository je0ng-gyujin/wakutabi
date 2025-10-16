package com.wakutabi.controller;

import java.util.Arrays;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakutabi.configure.FilePathConfig;
import com.wakutabi.domain.ImageOrderDto;
import com.wakutabi.domain.NotificationDto;
import com.wakutabi.domain.ParticipantDto;
import com.wakutabi.domain.RequestStatusDto;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.domain.TravelUploadDto;


import com.wakutabi.mapper.TravelUpdateDeleteMapper;
import com.wakutabi.service.*;

import com.wakutabi.domain.TripJoinRequestDto;
import com.wakutabi.domain.TripListDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class TravelsController {

    // (검색 메서드에 사용) 사용 가능한 태그 목록 상수
    private static final List<String> AVAILABLE_TAGS = Arrays.asList(
        "foodie", "activity", "nature", "otaku", "shopping", 
        "smallGroup", "largeGroup", "indoor", "outdoor"
    );

    private final TravelEditService travelEditService;
    private final TravelImageService travelImageService;
    private final TravelUpdateDeleteService travelUpdateDeleteService; // ⬅️ 추가
    private final TravelDeadlineService travelDeadlineService; // 추가
    private final TravelUpdateDeleteMapper travelUpdateDeleteMapper;
    private final ChatService chatService;
    private final ChatParticipantsService chatParticipantsService;
    private final TripService tripService;
    
    // 중복 요청 방지를 위한 캐시
    private final ConcurrentHashMap<String, Long> requestCache = new ConcurrentHashMap<>();
    private static final long REQUEST_TIMEOUT = 5000; // 5초

    private final NotificationService notificationService;

    /**
     * 영어 태그를 한글로 번역하는 메서드
     */
    private String translateTag(String tag) {
        return switch (tag) {
            case "foodie" -> "🍜 식도락";
            case "activity" -> "🏃 액티비티";
            case "nature" -> "🌲 자연";
            case "otaku" -> "🎮 오타쿠";
            case "shopping" -> "🛍️ 쇼핑";
            case "smallGroup" -> "👤 소수팟";
            case "largeGroup" -> "👥 다인팟";
            case "indoor" -> "🏠 실내파";
            case "outdoor" -> "🌞 실외파";
            default -> tag; // 매핑되지 않은 태그는 원래 값 그대로
        };
    }

    // 검색
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
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            Model model) {

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        log.info(
                "Received search request. Query: {}, minPrice: {}, maxPrice: {}, region: {}, startDate: {}, endDate: {}, tags: {}, groupSize: {}, status: {}",
                query, minPrice, maxPrice, region, startDate, endDate, tags, groupSize, status); // ⬅️ 로그 추가

        int offset =(page -1) * size;

        List<TravelEditDto> travels = travelEditService.findFilteredTravels(query, minPrice, maxPrice, region,
                startDateTime, endDateTime, tags, groupSize, status, offset, size); // ⬅️ status 파라미터 추가
        int totalCount = travelEditService.countFilteredTravels(query, minPrice, maxPrice, region,
                startDateTime, endDateTime, tags, groupSize, status);
        int totalPages = (int) Math.ceil((double) totalCount / size);
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

        // 3. 각 여행의 태그를 한글로 변환
        if (travels != null) {
            for (TravelEditDto travel : travels) {
                if (travel.getTags() != null) {
                    List<String> translatedTags = travel.getTags().stream()
                        .map(this::translateTag)
                        .toList();
                    travel.setTags(translatedTags);
                }
            }
        }

        // 4. 모델에 검색 결과와 필터 파라미터들을 다시 담아서 뷰로 전달합니다.
        model.addAttribute("travels", travels);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("region", region);
        model.addAttribute("availableTags", AVAILABLE_TAGS);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("tags", tags);
        model.addAttribute("groupSize", groupSize);
        model.addAttribute("status", status);
        model.addAttribute("currentPage",page);
        model.addAttribute("totalPages", totalPages);

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
    public Map<String, Object> uploadTravel(@RequestParam(name = "tags", required = false) String tags, TravelUploadDto uploadDto,
                            Principal principal, @ModelAttribute("userId") Long userId) throws IllegalStateException, IOException {
        Map<String, Object> result = new HashMap<>();
    try {
        // 1. 사용자 인증 및 기본 데이터 유효성 검사
        if (principal == null) {
            result.put("status", "error");
            result.put("message", "로그인 후 이용 가능합니다.");
            return result;
        }

        log.info("uploadDto: {}", uploadDto);

        // 2. JSON 문자열을 객체로 변환 (orderNumber → ImageOrderDto 리스트)
        ObjectMapper objectMapper = new ObjectMapper();
        List<ImageOrderDto> imageOrders;
        try {
            imageOrders = objectMapper.readValue(
                    uploadDto.getOrderNumber(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ImageOrderDto.class));
        } catch (IOException e) {
            log.error("이미지 순서 변환 실패", e);
            result.put("status", "error");
            result.put("message", "이미지 순서 처리 실패");
            return result;
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
        LocalDate startDate = LocalDate.parse(uploadDto.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(uploadDto.getEndDate(), formatter);
        
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        // 모집종료날짜 처리 - 입력받은 값이 있으면 사용, 없으면 여행종료날짜와 동일하게 설정
        LocalDate recruitEndDate;
        if (uploadDto.getRecruitEndDate() != null && !uploadDto.getRecruitEndDate().isEmpty()) {
            recruitEndDate = LocalDate.parse(uploadDto.getRecruitEndDate(), formatter);
            
            log.info("유효성 검사 - 모집종료일: {}, 출발일: {}, 오늘: {}", recruitEndDate, startDate, LocalDate.now());
            
            // 모집종료일 유효성 검사
            if (recruitEndDate.isBefore(LocalDate.now())) {
                log.warn("모집종료일이 오늘보다 이전: {} < {}", recruitEndDate, LocalDate.now());
                result.put("status", "error");
                result.put("message", "모집종료일은 오늘 이후로 선택해주세요.");
                return result;
            }
            if (!recruitEndDate.isBefore(startDate)) {  // 수정: 모집종료일이 출발일과 같거나 늦으면 에러
                log.warn("모집종료일이 출발일과 같거나 이후: {} >= {}", recruitEndDate, startDate);
                result.put("status", "error");
                result.put("message", "모집종료일은 출발일 이전으로 선택해주세요.");
                return result;
            }
            
            dto.setRecruitEndDate(recruitEndDate);
        } else {
            dto.setRecruitEndDate(endDate);  // 기본값: 여행종료일과 동일
        }
        // 예: 여행종료 3일 전까지 모집
        // dto.setRecruitEndDate(endDate.minusDays(3));

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
                    String uploadDir = FilePathConfig.getUploadPath();
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
                    imgDto.setImagePath(savePath.replaceFirst("C:/upload", "/upload"));
                    imgDto.setOrderNumber(imageOrder.getOrder()); // JSON에서 받은 순서 값 사용

                    travelImageService.insertTravelImage(imgDto);
                }
            }
        }
        // 여행 등록시 채팅방 만들기, 채팅방에 호스트 넣기
        chatService.setChatRoom(dto.getId(), dto.getHostUserId());

        // 6. 등록 된 여행에 대한 알림 테이블 저장
        NotificationDto noticeDto = new NotificationDto();
        String uploadedTravelUrl = "/schedule/detail?id=" + dto.getId();
        noticeDto.setUserId(userId);
        noticeDto.setType("TRAVEL_UPLOADED");
        noticeDto.setTitle(uploadDto.getTitle());
        noticeDto.setLink(uploadedTravelUrl);

        notificationService.insertNotification(noticeDto);

        result.put("status", "success");
        result.put("message", "등록 완료! 생성된 글 ID: " + dto.getId());
        result.put("redirectUrl", "/myTrips");
        return result;
    } catch (Exception e) {
        log.error("여행 등록 중 오류 발생", e);
        result.put("status", "error");
        result.put("message", "여행 등록 중 오류가 발생했습니다.");
        return result;
    }
    }

    // ---------------------------------------------
    // 3. 여행 상세 조회
    // ---------------------------------------------
    @GetMapping("/detail")
    public String travelDetail(@RequestParam("id") Long id,
                               Model model, Principal principal) {

        // 1. 여행 게시글 정보 조회
        TravelEditDto travel = travelEditService.findTravelById(id);
        if (travel == null) {
            log.error("존재하지 않는 여행 게시글 ID입니다: {}", id);
            return "redirect:/error";
        }

        // 2. 여행 이미지 목록 조회
        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);

        // 3. 현재 로그인 사용자와 작성자 일치 여부 확인
        boolean isOwner = false;
        if (principal != null) {
            // 참고: Principal에서 사용자 ID를 직접 가져오는 로직은
            // Spring Security 설정이나 UserDetails 구현에 따라 다를 수 있습니다.
            // 아래는 일반적인 예시입니다. Long.parseLong(principal.getName()) 등을 사용할 수 있습니다.
            // Long currentUserId = userService.findByUsername(principal.getName()).getId();
            
            // 임시로 travel 객체에서 가져온 host ID와 비교하는 로직을 유지하되,
            // 실제로는 principal 기반으로 조회하는 것이 좋습니다.
            // 이 예제에서는 임의의 ID 1L로 가정하겠습니다. 실제 프로젝트에 맞게 수정하세요.
            Long currentUserId = 1L; // <<-- 이 부분은 실제 로그인 유저 ID를 가져오는 로직으로 변경해야 합니다.
            isOwner = travel.getHostUserId() != null && travel.getHostUserId().equals(currentUserId);
        }

        // CANCELED(소프트 삭제) 글은 작성자 본인만 접근 가능하도록 제한
        if ("CANCELED".equalsIgnoreCase(travel.getStatus()) && !isOwner) {
            return "redirect:/error";
        }

        // 4. 채팅방 ID 조회
        Long chatRoomId = chatService.chatRoomFindByTripArticleId(travel.getId());


        // 5. Model에 모든 정보 담기
        model.addAttribute("travel", travel);
        model.addAttribute("images", images);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("chatRoomId", chatRoomId);
        

        return "travels/detail";
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
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "deletedImageIds", required = false) String deletedImageIds,
            @RequestParam(value = "remainImageIds", required = false) String remainImageIds,
            @RequestParam(value = "orderNumber", required = false) String imageOrdersJson,
            Principal principal, RedirectAttributes redirectAttributes) {


        if (principal == null) {
            return "로그인 후 이용 가능합니다.";
        }
        
        // 중복 요청 방지 체크
        long currentTime = System.currentTimeMillis();
        
        // 같은 여행 ID와 사용자에 대한 최근 요청 체크
        String userTravelKey = dto.getId() + "_" + userId;
        Long lastRequestTime = requestCache.get(userTravelKey);
        
        if (lastRequestTime != null && (currentTime - lastRequestTime) < REQUEST_TIMEOUT) {
            log.warn("중복 요청 감지 - 사용자: {}, 여행 ID: {}, 마지막 요청: {}ms 전", 
                userId, dto.getId(), currentTime - lastRequestTime);
            return "요청이 처리 중입니다. 잠시 후 다시 시도해주세요.";
        }
        
        // 현재 요청 시간 기록
        requestCache.put(userTravelKey, currentTime);
        
        try {
            log.info("여행 수정 시작 - ID: {}, 새 이미지 개수: {}", dto.getId(), 
                images != null ? images.size() : 0);
            
            dto.setHostUserId(userId);
            
            // 날짜 유효성 검사
            if (dto.getRecruitEndDate() != null) {
                if (dto.getRecruitEndDate().isBefore(LocalDate.now())) {
                    return "모집종료일은 오늘 이후로 선택해주세요.";
                }
                if (dto.getStartDate() != null && !dto.getRecruitEndDate().isBefore(dto.getStartDate())) {  // 수정: 모집종료일이 출발일과 같거나 늦으면 에러
                    return "모집종료일은 출발일 이전으로 선택해주세요.";
                }
            }
            
            // recruitEndDate가 null인 경우 endDate와 같게 설정
            if (dto.getRecruitEndDate() == null && dto.getEndDate() != null) {
                dto.setRecruitEndDate(dto.getEndDate());
                log.info("recruitEndDate가 null이어서 endDate로 설정: {}", dto.getEndDate());
            }
            
            boolean isUpdated = travelUpdateDeleteService.updateTravelArticle(dto);
            
            if (!isUpdated) {
                log.warn("게시글 본문 수정 실패 - ID: {}, 권한 없거나 게시글을 찾을 수 없음", dto.getId());
                return "게시글 수정 실패! (권한 없거나 게시글을 찾을 수 없습니다)";
            }

            // 태그 업데이트
            travelEditService.updateTravelTags(dto.getId(), tags);

            // 1. 삭제/유지 이미지 관리
            List<Long> remainIds = parseIdList(remainImageIds);
            List<Long> deleteIds = parseIdList(deletedImageIds);
            log.info("이미지 관리 - 남길 이미지 ID: {}, 삭제할 이미지 ID: {}", remainIds, deleteIds);

            List<TravelImageDto> allImages = travelImageService.findImagesByTripArticleId(dto.getId());
            for (TravelImageDto img : allImages) {
                // 삭제할 이미지: deletedImageIds에 포함되거나, remainImageIds에 없는 경우
                if (deleteIds.contains(img.getId()) || !remainIds.contains(img.getId())) {
                    log.info("이미지 삭제 - ID: {}, Path: {}", img.getId(), img.getImagePath());
                    travelImageService.deleteImageById(img.getId());
                }
            }

            // 2. 기존 이미지 순서 업데이트 (imageOrdersJson)
            if (imageOrdersJson != null && !imageOrdersJson.isEmpty()) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Map<String, Object>> imageOrders = mapper.readValue(imageOrdersJson, new TypeReference<List<Map<String, Object>>>() {});
                    for (Map<String, Object> item : imageOrders) {
                        Long id = null;
                        Integer order = null;
                        if (item.get("id") != null) {
                            id = Long.valueOf(item.get("id").toString());
                        }
                        if (item.get("order") != null) {
                            order = Integer.valueOf(item.get("order").toString());
                        }
                        if (id != null && order != null) {
                            Map<String, Object> param = new HashMap<>();
                            param.put("id", id);
                            param.put("orderNumber", order);
                            travelImageService.updateOrderNumber(param);
                        }
                    }
                } catch (Exception ex) {
                    log.error("이미지 순서 업데이트 파싱 오류", ex);
                }
            }

            // 3. 새 이미지 업로드 (추가) - remainImageIds에 포함되지 않은 새 파일만 업로드
            if (images != null && !images.isEmpty()) {
                int actualImageCount = 0;
                List<MultipartFile> newImages = new ArrayList<>();
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        // remainImageIds에 포함된 파일명과 비교하여 중복 추가 방지
                        boolean isRemain = false;
                        for (Long remainId : remainIds) {
                            TravelImageDto remainImg = travelImageService.findImageById(remainId);
                            if (remainImg != null && file.getOriginalFilename() != null && remainImg.getImagePath() != null && remainImg.getImagePath().contains(file.getOriginalFilename())) {
                                isRemain = true;
                                break;
                            }
                        }
                        if (!isRemain) {
                            newImages.add(file);
                            actualImageCount++;
                        }
                    }
                }
                log.info("실제 업로드할 새 이미지 개수: {}", actualImageCount);
                if (actualImageCount > 0) {
                    travelImageService.addImages(dto.getId(), newImages);
                    log.info("새 이미지 업로드 완료");
                }
            }

            log.info("여행 수정 완료 - ID: {}", dto.getId());
            return "수정 완료! ID: " + dto.getId();
            
        } catch (Exception e) {
            log.error("여행 수정 중 오류 발생 - ID: {}, Error: {}", dto.getId(), e.getMessage(), e);
            return "수정 중 오류가 발생했습니다: " + e.getMessage();
        } finally {
            // 처리 완료 후 캐시에서 제거 (5초 후)
            new Thread(() -> {
                try {
                    Thread.sleep(REQUEST_TIMEOUT);
                    requestCache.remove(userTravelKey);
                    log.debug("요청 캐시 정리 완료 - {}", userTravelKey);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    // 문자열로 된 ID 리스트를 Long 리스트로 변환하는 헬퍼 메서드
    private List<Long> parseIdList(String ids) {
        List<Long> result = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (String idStr : ids.split(",")) {
                idStr = idStr.trim();
                if (!idStr.isEmpty()) {
                    try {
                        result.add(Long.valueOf(idStr));
                    } catch (NumberFormatException e) {
                        log.warn("Invalid image ID: {}", idStr);
                    }
                }
            }
        }
        return result;
    }

    // ---------------------------------------------
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
    @PatchMapping("/travelCanceled")
    @ResponseBody
    public String canceledTravel(@RequestParam("id")Long id, Principal principal){
        try {
            if(principal == null){
                return "로그인이 필요합니다.";
            }
            
            // Principal에서 username을 가져와 userId 조회
            String username = principal.getName();
            Long hostUserId = tripService.findUserIdByUsername(username);
            
            if(hostUserId == null){
                log.error("사용자 정보 조회 실패 - username: {}", username);
                return "사용자 정보를 찾을 수 없습니다.";
            }
            
            // 여행일정 status 상태 가져오기
            String status = travelUpdateDeleteMapper.statusByTravelArticleId(id);
            
            // 여행 상태가 MATCHED, CLOSED, CANCELED 면
            if(status != null && (status.equalsIgnoreCase("MATCHED") ||
               status.equalsIgnoreCase("CLOSED"))){
                return "해당 여행은 ["+status+"] 상태로 취소할 수 없습니다.";
            }
            
            boolean isCanceled = travelUpdateDeleteService.canceledTravelArticle(id, hostUserId);

            return isCanceled ? "여행이 취소 완료되었습니다." : "여행 취소 도중 오류가 발생했습니다.";
        } catch (Exception e) {
            log.error("여행 취소 중 예외 발생 - tripId: {}, error: {}", id, e.getMessage(), e);
            return "여행 취소 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
    // TravelsController.java
    // ...
    // ---------------------------------------------
    // 6. 여행 글 수정 페이지
    // ---------------------------------------------
    @GetMapping("/edit")
    public String travelEdit(@RequestParam("id") Long id, @ModelAttribute("userId") Long userId,
            Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        // 여행일정 status 상태 가져오기
        String status = travelUpdateDeleteMapper.statusByTravelArticleId(id);
        // 여행 상태가 MATCHED, CLOSED, CANCELED 면
        if(status.equalsIgnoreCase("MATCHED") ||
           status.equalsIgnoreCase("CLOSED") ||
           status.equalsIgnoreCase("CANCELED")){
        //
            redirectAttributes.addFlashAttribute("errorMessage",
                              "해당 여행은 ["+status+"] 상태로 수정할 수 없습니다.");
            return "redirect:/schedule/detail?id="+id;
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

        List<TravelImageDto> images = travelImageService.findImagesByTripArticleId(id);

        // 3. 데이터를 Model에 담아 Thymeleaf로 전달
        model.addAttribute("travel", travel);
        model.addAttribute("images", images);

        // 4. 새로운 수정 폼 HTML 페이지 반환
        return "travels/edit";
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
            @PathVariable("requestId") Long requestId,
            @RequestBody RequestStatusDto requestStatusDto,
            Principal principal) {

        Long currentUserId;
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다."); // 401 명시
        }
        
        try {
            // principal.getName()이 ID(Long)를 반환한다는 가정 하에 Long으로 파싱
            currentUserId = Long.parseLong(principal.getName()); // ⬅️ 중복 파싱 제거, 하나만 남김
        } catch (NumberFormatException e) {
            return ResponseEntity.status(401).body("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        try {
            String status = requestStatusDto.getStatus().toUpperCase(); // 상태를 대문자로 통일
            
            // 요청 상태에 따라 로직 분기 (CANCELED는 신청자 취소/나가는 로직, 나머지는 호스트 수락/거절 로직)
            if ("CANCELED".equals(status)) { 
                tripService.cancelJoinRequest(requestId, currentUserId); 
            
            } else if ("ACCEPT".equals(status) || "REJECT".equals(status)) {
                tripService.processJoinRequest(requestId, status, currentUserId);
            } else {
                return ResponseEntity.badRequest().body("유효하지 않은 상태 값입니다.");
            }
            
            // 모든 처리 성공 시 응답 반환
            return ResponseEntity.ok().build(); 
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // 유효하지 않은 요청 데이터나 상태(400)
            log.warn("신청 처리 오류 (400): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (RuntimeException e) { 
            // 기타 런타임 오류 (500)
            log.error("신청 처리 중 오류 발생 (500): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(e.getMessage()); 
        } catch (Exception e) { 
            // 예상치 못한 서버 내부 오류 (500)
            log.error("신청 처리 중 알 수 없는 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("서버 내부 오류가 발생했습니다."); 
        }
    }
    @PutMapping("/api/trip/article/{tripArticleId}/status")
    @ResponseBody
    public ResponseEntity<?> updateTripStatus(
            // ⭐ 경로 변수가 tripArticleId 임에 유의 ⭐
            @PathVariable("tripArticleId") Long tripArticleId, 
            // RequestStatusDto를 재활용하여 status 값만 받습니다.
            @RequestBody RequestStatusDto requestStatusDto, 
            Principal principal) {
        
        Long currentUserId;
        try {
            currentUserId = Long.parseLong(principal.getName()); 
        } catch (Exception e) {
            return ResponseEntity.status(401).body("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        
        try {
            String newStatus = requestStatusDto.getStatus();
            
            // ⭐ 서비스 로직 호출 (단순화된 형태) ⭐
            // Service에서 권한 검증과 상태 값 검증(OPEN/CLOSED)을 모두 처리합니다.
            tripService.updateTripStatus(tripArticleId, newStatus, currentUserId);
            
            // 성공 응답 반환
            return ResponseEntity.ok().body(newStatus.toUpperCase() + "로 상태 변경 완료."); 
            
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 상태 값(OPEN/CLOSED가 아님) 또는 ID 오류 등 (HTTP 400 Bad Request)
            log.warn("여행 상태 변경 오류 (400): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (RuntimeException e) { 
            // 권한 없음(호스트 불일치) 또는 DB 업데이트 실패 등 (HTTP 500 Internal Server Error)
            // 호스트 불일치와 같은 에러는 403 Forbidden으로 반환하는 것이 더 정확할 수 있으나, 기존 패턴 유지를 위해 500으로 처리합니다.
            log.error("여행 상태 변경 중 오류 발생 (500): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(e.getMessage()); 
        } catch (Exception e) {
            // 기타 예상치 못한 오류
            log.error("여행 상태 변경 중 알 수 없는 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 서버 오류가 발생했습니다.");
        }
    }
    @PutMapping("/api/trip/article/{tripArticleId}/article-status")
    public ResponseEntity<String> updateTripArticleStatus(
        @PathVariable Long tripArticleId,
        @RequestBody Map<String, String> statusUpdate, // { "status": "OPEN" } 또는 { "status": "CLOSED" }
        Principal principal) {
        
        // 1. 현재 로그인된 사용자 ID를 가져옵니다. (호스트 권한 검증에 사용)
        // Long currentHostId = tripService.findUserIdByUsername(principal.getName()); // 기존 메서드 사용 가정
        Long currentHostId;
        try {
            currentHostId = tripService.findUserIdByUsername(principal.getName());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        String newStatus = statusUpdate.get("status").toUpperCase();
        
        try {
            tripService.updateTripArticleStatus(tripArticleId, newStatus, currentHostId);
            
            String message = (newStatus.equals("OPEN") ? "모집이 다시 시작되었습니다." : "모집이 마감되었습니다.");
            return ResponseEntity.ok(message); // 성공 메시지 반환
            
        } catch (IllegalArgumentException e) {
            log.warn("상태 변경 오류 (400): {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // 권한 없음 또는 게시글 ID 오류
            log.error("상태 변경 오류 (500): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
