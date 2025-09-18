package com.wakutabi.domain;

import lombok.Data;

@Data
public class TravelImageDto {
    private Long id;
    private Long tripArticleId;
    private String imagePath;
    private Integer orderNumber;
}
