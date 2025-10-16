package com.wakutabi.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;


@Data
@NoArgsConstructor
public class TravelJoinRequestDto{

    private Long id;
    private Long tripArticleId;
    private Long hostUserId;
    private Long applicantUserId;
    private Status status;

    public enum Status{
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Builder
    public TravelJoinRequestDto(Long id, Long tripArticleId, Long hostUserId,
                                Long applicantUserId, Status status) {
        this.id = id;
        this.tripArticleId = tripArticleId;
        this.hostUserId = hostUserId;
        this.applicantUserId = applicantUserId;
        this.status = status;
    }
}
