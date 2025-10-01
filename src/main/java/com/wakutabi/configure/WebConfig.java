package com.wakutabi.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath; // "C:/upload/" 값이 여기에 주입됩니다.

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        
        // 웹 브라우저에서 /upload/** 로 시작하는 모든 요청을
        registry.addResourceHandler("/upload/**")
                // @Value로 주입받은 실제 로컬 경로와 연결합니다.
                .addResourceLocations("file:///" + uploadPath); // ⬅️ 이 부분을 수정!
    }
}