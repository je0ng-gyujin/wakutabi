package com.wakutabi.service;

import com.wakutabi.mapper.TravelDeadlineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelDeadlineScheduler {

    private final TravelDeadlineMapper travelDeadlineMapper;

    // 매 1분마다 실행 (cron 표현식: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 * * * * *")
    public void autoMatchedExpiredArticles() {
        int updated = travelDeadlineMapper.autoMatchedExpiredArticles();
        if(updated > 0) {
            log.info("자동 처리 된 마감 여행글 수: {} ",updated);
        }
    }
}
