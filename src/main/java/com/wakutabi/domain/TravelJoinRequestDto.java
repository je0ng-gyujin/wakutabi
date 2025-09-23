package com.wakutabi.domain;

import lombok.Data;


@Data
public class TravelJoinRequestDto{

    private Long id;
    private Long tripArticleId;
    private Long hostUserId;
    private Long applicantId;
    private Status  status;

    public enum Status{
        PENDING,
        ACCEPTED,
        REJECTED   
    }
}
