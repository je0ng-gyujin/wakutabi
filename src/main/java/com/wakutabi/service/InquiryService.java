package com.wakutabi.service;

import com.wakutabi.domain.InquiryDto;
import com.wakutabi.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryMapper inquiryMapper;

    // 문의등록
    public void setInquiry(InquiryDto inquiry){
        inquiryMapper.insertInquiry(inquiry);
    }
    // 문의내역가져오기
    public List<InquiryDto> getInquiry(Long userId){
        List<InquiryDto> inquiry = inquiryMapper.getInquiry(userId);
        return inquiry;
    }

}
