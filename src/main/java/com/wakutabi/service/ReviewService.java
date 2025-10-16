package com.wakutabi.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wakutabi.domain.ReviewTravelDto;
import com.wakutabi.domain.ReviewUserDto;
import com.wakutabi.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final ReviewMapper reviewMapper;

    public ReviewTravelDto getTripAndParticipantsForReview(Long tripId){
        return reviewMapper.getTripAndParticipantsForReview(tripId);
    }
    @Transactional
    public void insertReview(ReviewTravelDto reviewTravleDto) throws IOException {
        reviewMapper.insertTravleReview(reviewTravleDto);

        if (reviewTravleDto.getImageFiles() != null && !reviewTravleDto.getImageFiles().isEmpty()) {
            for (MultipartFile file : reviewTravleDto.getImageFiles()) {
                if (!file.isEmpty()) {
                    String uuid = UUID.randomUUID().toString();
                    String originalFilename = file.getOriginalFilename();
                    String fileName = uuid + "_" + originalFilename;

                    File saveFile = new File(uploadPath, fileName);

                    if (!saveFile.getParentFile().exists()) {
                        saveFile.getParentFile().mkdirs();
                    }

                    file.transferTo(saveFile);

                    reviewMapper.insertTravleReviewImage(reviewTravleDto.getId(), fileName);
                }
            }
        }

        if (reviewTravleDto.getReviewUsers() != null && !reviewTravleDto.getReviewUsers().isEmpty()) {
            for (ReviewUserDto user : reviewTravleDto.getReviewUsers()) {
                reviewMapper.insertUserReview(reviewTravleDto.getId(), user.getReviewedUserId(), user.getUserRating());
            }
        }
    }
}