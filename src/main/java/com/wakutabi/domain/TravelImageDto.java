package com.wakutabi.domain;

import lombok.Data;

@Data
public class TravelImageDto {
	private Long id;
    private Long tripArticleId;
    private String imagePath;
    private String imagePathContent;
    private Boolean isMain;
    private Integer orderNumber;
}
