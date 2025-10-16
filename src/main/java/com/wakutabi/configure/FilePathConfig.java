package com.wakutabi.configure;

import org.springframework.stereotype.Component;
import java.nio.file.*;

@Component
public class FilePathConfig {

    public static String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;

        if (os.contains("win")) {
            basePath = "C:/upload/";
        } else {
            basePath = System.getProperty("user.home") + "/upload/";
        }

        try {
            Files.createDirectories(Paths.get(basePath)); // 없으면 자동 생성
        } catch (Exception e) {
            throw new RuntimeException("업로드 폴더 생성 실패: " + basePath, e);
        }

        return basePath;
    }
}
