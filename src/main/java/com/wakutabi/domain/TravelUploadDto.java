package com.wakutabi.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class TravelUploadDto {

	private String location;
    private String content;
    private String title;
    private String startDate;
    private String endDate;
    private Integer maxParticipants;
    private String ageLimit;
    private String genderLimit;
    private Integer estimatedCost;
    private String tag;
    private String orderNumber;
    private List<MultipartFile> images;
}
