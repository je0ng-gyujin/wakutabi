package com.wakutabi.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TripTagDto;
import com.wakutabi.mapper.TravelEditMapper;
import com.wakutabi.mapper.TravelTagMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelEditService {

    private final TravelEditMapper travelEditmapper;
    private final TravelTagMapper travelTagMapper;

    @Transactional
    public void insertTravelEdit(TravelEditDto traveledit) {
        travelEditmapper.insertTravelEdit(traveledit);
    }

    // DB에서 게시글 정보를 가져옵니다.
    public TravelEditDto findTravelById(Long id) {
        // 1️⃣ Mapper에서 여행 게시글 조회
        TravelEditDto travelEditDto = travelEditmapper.findById(id);

        // 2️⃣ 해당 게시글의 태그 조회 후 DTO에 세팅
        if (travelEditDto != null) {
            List<String> tags = travelTagMapper.findTagsByTripArticleId(id);
            travelEditDto.setTags(tags);
        }

        // 3️⃣ 완성된 DTO 반환
        return travelEditDto;
    }

    private static final Map<String, String> KOREAN_TO_ROMANIZED = Map.of(
            "훗카이도", "hokkaido",
            "홋카이도", "hokkaido",
            "도호쿠", "Tohoku",
            "간토", "Kanto",
            "주부", "Chubu",
            "간사이", "Kansai",
            "주고쿠", "Chugoku",
            "시코쿠", "Shikoku",
            "규슈", "Kyushu",
            "오키나와", "Okinawa"
    // 여기에 추가적인 지역 매핑을 넣으세요.
    );

    // 복합 검색 및 필터링 기능을 위한 메서드
    public List<TravelEditDto> findFilteredTravels(String query, Integer minPrice, Integer maxPrice,
            String region, LocalDateTime startDate, LocalDateTime endDate,
            List<String> tags, List<String> groupSize, String genderLimit, String ageLimit, String status, int offset, int size) {

        String translatedQuery = null;
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            // 쿼리가 매핑 맵에 있으면 번역된 값을 설정
            if (KOREAN_TO_ROMANIZED.containsKey(lowerQuery)) {
                translatedQuery = KOREAN_TO_ROMANIZED.get(lowerQuery);
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("translatedQuery", translatedQuery); // ⬅️ 번역된 쿼리 추가
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("region", region);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("tagsList", tags); // Mapper XML에서 <foreach collection="tagsList"> 사용
        params.put("groupSize", groupSize);
        params.put("genderLimit", genderLimit);
        params.put("ageLimit", ageLimit);
        params.put("status", status);
        params.put("offset", offset);
        params.put("size", size);

        // 1️⃣ Mapper에서 여행 게시글 조회
        List<TravelEditDto> travels = travelEditmapper.selectTravels(params);

        // null 체크 추가
        if (travels != null) {
            // 2️⃣ 각 여행 게시글에 대한 태그 조회 후 DTO에 세팅
            for (TravelEditDto travel : travels) {
                List<String> tagList = travelTagMapper.findTagsByTripArticleId(travel.getId());
                travel.setTags(tagList);
            }
        }
        return travels; // 3️⃣ 완성된 DTO 리스트 반환
    }

    public int countFilteredTravels(String query, Integer minPrice, Integer maxPrice,
            String region, LocalDateTime startDate, LocalDateTime endDate,
            List<String> tags, List<String> groupSize, String genderLimit, String ageLimit, String status) {

        String translatedQuery = null;
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            // KOREAN_TO_ROMANIZED 맵에서 번역된 값을 가져옵니다.
            translatedQuery = KOREAN_TO_ROMANIZED.getOrDefault(lowerQuery, null);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("translatedQuery", translatedQuery); // ⬅️ Map에 포함
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("region", region);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("tagsList", tags);
        params.put("groupSize", groupSize);
        params.put("genderLimit", genderLimit);
        params.put("ageLimit", ageLimit);
        params.put("status", status);

        return travelEditmapper.countFilteredTravels(params);
    }

    @Transactional // ⭐트랜잭션 처리를 위해 어노테이션을 붙입니다.
    public void saveTravelWithTags(TravelEditDto travel) {
        // 1. 여행글 등록
        travelEditmapper.insertTravelEdit(travel);

        // 2. 선택한 태그를 중간 테이블에 저장
        if (travel.getTags() != null) {
            for (String tagName : travel.getTags()) {
                // ⭐⭐⭐ 로직 변경: 태그를 먼저 찾아보고, 없으면 생성합니다 ⭐⭐⭐
                Long tagId = travelTagMapper.findTagIdByName(tagName);
                // ⭐ ⭐ ⭐ 변경: tagId가 null이면 새로운 태그를 생성하고 ID를 가져오는 로직 ⭐ ⭐ ⭐
                if (tagId == null) {
                    TripTagDto newTag = new TripTagDto();
                    newTag.setTagName(tagName);
                    travelTagMapper.insertTag(newTag); // 새로운 태그 저장
                    tagId = newTag.getId(); // ⭐⭐⭐ 생성된 ID를 DTO에서 직접 가져옴 ⭐⭐⭐
                }

                if (tagId != null) {
                    travelTagMapper.insertTripTag(travel.getId(), tagId);
                }
            }
        }
    }

    @Transactional
    public void updateTravelTags(Long travelId, List<String> newTags) {
        // 1. 기존 태그 매핑 삭제
        travelTagMapper.deleteTripTagsByTravelId(travelId);

        // 2. 새 태그 매핑 (없는 태그는 생성)
        // newTags가 null일 때를 대비해 null 체크 추가
        if (newTags != null) {
            for (String tagName : newTags) {
                Long tagId = travelTagMapper.findTagIdByName(tagName);
                if (tagId == null) {
                    TripTagDto newTag = new TripTagDto();
                    newTag.setTagName(tagName);
                    travelTagMapper.insertTag(newTag);
                    tagId = newTag.getId();
                }
                travelTagMapper.insertTripTag(travelId, tagId);
            }
        }
    }
}