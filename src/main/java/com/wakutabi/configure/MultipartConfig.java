package com.wakutabi.configure;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 개별 파일 최대 크기
        factory.setMaxFileSize(DataSize.parse("20MB"));
        // 요청 전체 최대 크기
        factory.setMaxRequestSize(DataSize.parse("100MB"));
        // 디스크 임시 저장 기준
        factory.setFileSizeThreshold(DataSize.parse("20MB"));

        // Tomcat용 파일 개수 제한 설정 (기본 10개)
        factory.setMaxRequestSize(DataSize.ofBytes(50)); // 한번에 50개 파일까지 허용

        return factory.createMultipartConfig();
    }
}