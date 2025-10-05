package com.wakutabi.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wakutabi.domain.TravelImageDto;
import com.wakutabi.mapper.TravelImageMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelImageService {
    private final TravelImageMapper travelImageMapper;

    public void insertTravelImage(TravelImageDto dto) {
        travelImageMapper.insertTravelImage(dto);
    }

    // 이 메서드를 추가하여 이미지 목록을 조회합니다.
    public List<TravelImageDto> findImagesByTripArticleId(Long tripArticleId) {
        return travelImageMapper.findByTripArticleId(tripArticleId);
    }

    @Transactional
    public void updateTravelImages(Long tripArticleId, List<MultipartFile> newImages)
            throws IllegalStateException, IOException {
        // 1. 기존 이미지 삭제 (DB + 파일)
        List<TravelImageDto> existingImages = travelImageMapper.findByTripArticleId(tripArticleId);
        for (TravelImageDto img : existingImages) {
            travelImageMapper.deleteImageById(img.getId());
            // 파일 시스템에서 삭제 (실제 경로에 맞게 수정)
            if (img.getImagePath() != null) {
                File file = new File("C:/uploads/" + img.getImagePath().replace("/upload/", ""));
                if (file.exists())
                    file.delete();
            }
        }
        // 2. 새 이미지 등록 (파일 저장 + DB insert)
        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String uploadDir = "C:/uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists())
                        dir.mkdirs();
                    String savePath = uploadDir + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    file.transferTo(new File(savePath));
                    TravelImageDto imgDto = new TravelImageDto();
                    imgDto.setTripArticleId(tripArticleId);
                    imgDto.setImagePath(savePath.replaceFirst("C:/uploads", "/upload"));
                    imgDto.setOrderNumber(0); // 필요시 순서값 추가
                    travelImageMapper.insertTravelImage(imgDto);
                }
            }
        }
    }

    public void deleteImageById(Long imageId) {
        TravelImageDto img = travelImageMapper.findImageById(imageId);
        if (img != null && img.getImagePath() != null) {
            File file = new File("C:/uploads" + img.getImagePath().replace("/upload", ""));
            if (file.exists())
                file.delete();
        }
        travelImageMapper.deleteImageById(imageId);
    }

    public void addImages(Long tripArticleId, List<MultipartFile> newImages) throws IllegalStateException, IOException {
        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    String uploadDir = "C:/uploads/";
                    File dir = new File(uploadDir);
                    if (!dir.exists())
                        dir.mkdirs();
                    String savePath = uploadDir + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    file.transferTo(new File(savePath));
                    TravelImageDto imgDto = new TravelImageDto();
                    imgDto.setTripArticleId(tripArticleId);
                    imgDto.setImagePath(savePath.replaceFirst("C:/uploads", "/upload"));
                    imgDto.setOrderNumber(0); // 필요시 순서값 추가
                    travelImageMapper.insertTravelImage(imgDto);
                }
            }
        }
    }
}
