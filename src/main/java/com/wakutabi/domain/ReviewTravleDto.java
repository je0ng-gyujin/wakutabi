package com.wakutabi.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewTravleDto {

    private Long id;
    
    /** 
     * @NotNull, @NotBlank, @Size 등의 어노테이션을 사용하여 유효성 검사 추가
     * @NotNull: null이 아닌지 검사
     * @NotBlank: null이 아니고, 공백이 아닌지 검사 (문자열에만 사용)
     * @Size: 문자열의 길이를 제한
     */

    @NotNull(message = "별점을 선택해주세요.")
    private int rating;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 50, message = "제목은 최대 50자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "공개 여부를 선택해주세요.")
    private Boolean isPublic;

    /** 
     * MultipartFile: 스프링에서 파일 업로드를 처리하기 위한 인터페이스
     * List<MultipartFile>: 여러 파일을 업로드할 수 있도록 리스트로 선언
     * 유효성 어노테이션은 없으나 Controller에서 파일 개수나 크기 등을 추가로 검사할 수 있음
     */

    private List<MultipartFile> imageFiles;

    private List<ReviewUserDto> reviewUsers;
}
