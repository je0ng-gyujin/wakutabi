package com.wakutabi.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties에서 정의한 실제 파일 저장 경로를 주입받음
    @Value("${file.save.location}")
    private String fileSaveLocation;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        
        // 1. 웹(URL) 경로 설정
        // 브라우저에서 /upload/** 패턴으로 요청이 오면 이 핸들러가 처리함
        registry.addResourceHandler("/upload/**")
        
        // 2. 실제 파일 위치 설정
        // 위 웹 경로 요청을 fileSaveLocation에 지정된 실제 폴더 위치와 매핑함
        // 예: /upload/image.jpg 요청 -> C:/uploads/image.jpg 파일을 찾아 반환
                .addResourceLocations("file:///" + fileSaveLocation);
    }
}