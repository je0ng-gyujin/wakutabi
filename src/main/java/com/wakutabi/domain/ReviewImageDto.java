package com.wakutabi.domain;

import lombok.Data;

@Data
public class ReviewImageDto {
    private Long reviewId;    // trip_reviews_image.trip_reviews_id 로 매핑
    private String imagePath; // trip_reviews_image.image_path 로 매핑
}