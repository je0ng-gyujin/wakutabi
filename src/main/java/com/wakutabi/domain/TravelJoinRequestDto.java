package com.wakutabi.domain;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TravelJoinRequestDto{

    private Long id;
    private Long tripArticleId;
    private Long hostUserId;
    private Long applicantUserId;
    private Status  status;

    public enum Status{
        PENDING,
        ACCEPTED,
        REJECTED   
    }
}
