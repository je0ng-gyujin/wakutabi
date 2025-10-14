package com.wakutabi.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String uploadPath = FilePathConfig.getUploadPath();
		registry.addResourceHandler("/upload/**")
				.addResourceLocations("file:" + uploadPath);
	}
}
