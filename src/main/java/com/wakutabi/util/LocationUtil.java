package com.wakutabi.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 지역 코드를 한글명으로 변환하는 유틸리티 클래스
 */
public class LocationUtil {
    
    private static final Map<String, String> LOCATION_MAP = new HashMap<>();
    
    static {
        LOCATION_MAP.put("tokyo", "도쿄");
        LOCATION_MAP.put("osaka", "오사카");
        LOCATION_MAP.put("kyoto", "교토");
        LOCATION_MAP.put("hokkaido", "홋카이도");
        LOCATION_MAP.put("okinawa", "오키나와");
        LOCATION_MAP.put("shikoku", "시코쿠");
        LOCATION_MAP.put("kyushu", "규슈");
        LOCATION_MAP.put("chubu", "주부");
        LOCATION_MAP.put("tohoku", "도호쿠");
        LOCATION_MAP.put("chugoku", "주고쿠");
        LOCATION_MAP.put("kansai", "간사이");
        LOCATION_MAP.put("kanto", "간토");
    }
    
    /**
     * 영어 지역 코드를 한글명으로 변환
     * @param locationCode 영어 지역 코드
     * @return 한글 지역명
     */
    public static String toKorean(String locationCode) {
        if (locationCode == null) {
            return "지역 미정";
        }
        return LOCATION_MAP.getOrDefault(locationCode.toLowerCase(), locationCode);
    }
    
    /**
     * 한글 지역명을 영어 코드로 변환
     * @param koreanName 한글 지역명
     * @return 영어 지역 코드
     */
    public static String toEnglish(String koreanName) {
        if (koreanName == null) {
            return null;
        }
        
        for (Map.Entry<String, String> entry : LOCATION_MAP.entrySet()) {
            if (entry.getValue().equals(koreanName)) {
                return entry.getKey();
            }
        }
        return koreanName;
    }
}