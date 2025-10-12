package com.wakutabi.controller;

import com.wakutabi.service.TravelDeadlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TravelDeadlineController {

    private final TravelDeadlineService travelDeadlineService;

    @PostMapping("close")
    public Map<String, Object> travelDeadlineHostClick(@ModelAttribute("userId")Long userId,
                                                       @RequestParam("travelArticleId")Long travelArticleId){
        Map<String, Object> result = new HashMap<>();
        try{
            // 1. 방장권한 확인하기
            Long hostUserId = travelDeadlineService.hostUserIdByTravelArticleId(travelArticleId);
            if(Objects.equals(userId, hostUserId)){
                result.put("status", "fail");
                result.put("message","방장만 마감할 수 있습니다.");
                return result;
            }
            boolean closed = travelDeadlineService.travelDeadlineHostClick(travelArticleId);
            if(closed){
                result.put("status","success");
                result.put("message","여행 모집이 마감되었습니다.");
            }else{
                result.put("status","fail");
                result.put("message","이미 마감되었거나 존재하지 않는 여행입니다.");
            }
        }catch(Exception e){
            log.error("방장 마감 중 오류",e);
            result.put("status","error");
            result.put("message","마감 처리 중 오류가 발생했습니다.");
        }
        return result;
    }
}
