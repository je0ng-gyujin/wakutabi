package com.wakutabi.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // properties에서 웹 경로(/upload/)를 주입받음
    @Value("${uploadPath}")
    private String webPath;

    // properties에서 실제 파일 저장 경로(C:/uploads/)를 주입받음
    @Value("${file.upload.path}")
    private String filePath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 주입받은 webPath 변수를 사용
        registry.addResourceHandler(webPath + "**")
                // 'file:///'는 로컬 파일 시스템의 절대 경로를 나타내는 표준 방식입니다.
                .addResourceLocations("file:///" + filePath);
    }
}

