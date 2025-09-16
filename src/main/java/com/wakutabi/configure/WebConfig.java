package com.wakutabi.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	//본인 환경에 맞게 수정 바랍니다.
	@Value("${file.upload.path}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/**").addResourceLocations("file:C:/upload/");

	}
}
