package com.wakutabi.service;

import com.wakutabi.domain.NotificationDto;
import com.wakutabi.mapper.ChatParticipantsMapper;
import com.wakutabi.mapper.TravelDeadlineMapper;
import com.wakutabi.mapper.TravelEndMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TravelEndScheduler {

    private final TravelEndMapper travelEndMapper;
    private final ChatParticipantsMapper chatParticipantsMapper;
    private final NotificationService notificationService;

    // 매 1분마다 실행 (초 분 시 일 월 요일)
    @Scheduled(cron = "0 * * * * *")
    public void autoEndArticles() {
        // 1) 종료 대상 여행글 조회(end_date 경과 & 상태 OPEN/MATCHED)
        List<Long> expiredIds = travelEndMapper.findOpenAndMatchedArticleIds();
        log.info("종료 대상 여행글 ID들: {}", expiredIds); // 비어있어도 한 번 찍자

        if (expiredIds.isEmpty()) return;

        // 2) 여행 CLOSED 처리
        int updated = travelEndMapper.updateEndTravels(expiredIds);
        log.info("자동 종료 처리된 여행글 수: {}", updated);

        // 3) 종료된 여행글의 참가자 조회 → 알림 발송
        for (Long articleId : expiredIds) {
            List<Long> participants = chatParticipantsMapper.findParticipantsStayedUntilEnd(articleId);

            if (participants.isEmpty()) {
                log.info("리뷰 알림 대상 없음 - 여행ID: {}", articleId);
                continue;
            }

            for (Long userId : participants) {
                NotificationDto reviewForEndTravel = NotificationDto.builder()
                        .userId(userId)
                        .tripArticleId(articleId)
                        .title("여행이 종료되었습니다. 리뷰를 남겨주세요!")
                        .type("TRAVEL_REVIEW_REQUEST")
                        // .link("/review/write?tripId=" + articleId) // 링크 붙일 거면 활성화
                        .build();
                try {
                    notificationService.insertNotification(reviewForEndTravel);
                } catch (Exception e) {
                    log.error("리뷰 알림 발송 실패 - 여행ID: {}, 사용자ID: {}", articleId, userId, e);
                }
            }
            // ⬅️ 여기! 참가자 전원 처리 후 '한 번'만 찍기
            log.info("리뷰 알림 발송 완료 - 여행ID: {}, 참가자수: {}", articleId, participants.size());
        }
    }
}
