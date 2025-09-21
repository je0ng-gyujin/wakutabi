package com.wakutabi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InquiryDto {
    private Long id;
    private Long userId;
    private Long tripArticleId;
    private type type;
    private String title;
    private String content;
    private status status;
    private LocalDateTime createdAt;

    public enum type{
        COMMON,
        ACCOUNT,
        PAYMENT,
        TRAVEL,
        BUG,
        ETC
    }
    public enum status{
        PROCESSING,
        DONE
    }
}
