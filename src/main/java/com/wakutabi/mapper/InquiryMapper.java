package com.wakutabi.mapper;

import com.wakutabi.domain.InquiryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InquiryMapper {
    // 문의내역 등록
    void insertInquiry(InquiryDto inquiry);
    // (임시) 문의내역 갖고오기
    List<InquiryDto> getInquiry(@Param("userId") Long userId);
}
