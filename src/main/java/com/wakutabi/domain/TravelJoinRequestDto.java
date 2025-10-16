package com.wakutabi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
