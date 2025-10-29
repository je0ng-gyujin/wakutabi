package com.wakutabi.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.wakutabi.configure.FilePathConfig;
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

    public int countExistingReview(Long userId, Long tripId){
        return reviewMapper.countExistingReview(userId, tripId);
    }

    public ReviewTravelDto getTripAndParticipantsForReview(Long tripId){
        return reviewMapper.getTripAndParticipantsForReview(tripId);
    }
    @Transactional
    public void insertReview(ReviewTravelDto reviewTravleDto) throws IOException {
        String uploadDir = FilePathConfig.getUploadPath();
        reviewMapper.insertTravleReview(reviewTravleDto);
        // 업로드 및 DB 저장
        List<MultipartFile> imageFiles = reviewTravleDto.getImageFiles();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("업로드 폴더 생성 실패: " + uploadPath);
            }

            for (MultipartFile file : imageFiles) {
                if (file.isEmpty()) continue;

                String uuid = UUID.randomUUID().toString();
                String fileName = uuid + "_" + file.getOriginalFilename();
                // 로컬 저장 경로
                String savePath = uploadDir + fileName;
                file.transferTo(new File(savePath));
                // DB 저장 경로
                String webPath = "/upload/" + fileName;
                reviewMapper.insertTravleReviewImage(reviewTravleDto.getId(), webPath);
            }
        }
        // 여행리뷰DTO안에 있는 사용자리뷰DTO안에 데이터가 있다면 실행
        if (reviewTravleDto.getReviewUsers() != null && !reviewTravleDto.getReviewUsers().isEmpty()) {
            for (ReviewUserDto user : reviewTravleDto.getReviewUsers()) {
                // 본인 자신 제외
                if(user.getReviewedUserId() != null && user.getReviewId() != null &&
                    user.getReviewId().equals(user.getReviewedUserId())) {
                    continue;
                }
                reviewMapper.insertUserReview(user);
            }
        }
    }
    // 리뷰 리스트
    public List<ReviewTravelDto> getReviewList(Long tripId){
        return reviewMapper.getReviewList(tripId);
    }
}