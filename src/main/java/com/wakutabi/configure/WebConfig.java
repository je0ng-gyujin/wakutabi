package com.wakutabi.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	//본인 환경에 맞게 수정 바랍니다.
	@Value("${file.upload.path}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/upload/items/**").addResourceLocations("file:D:/upload/item/");
		registry.addResourceHandler("/summer/**").addResourceLocations("file:D:/upload/item/");

	}
}
