package com.wakutabi.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wakutabi.domain.TravelEditDto;
import com.wakutabi.domain.TravelTagDto;
import com.wakutabi.mapper.TravelEditMapper;
import com.wakutabi.mapper.TravelTagMapper; // ✨ 1. 태그 매퍼 임포트

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelEditService {

	private final TravelEditMapper travelEditMapper;
	private final TravelTagMapper travelTagMapper; // ✨ 2. 태그 매퍼 주입

	@Transactional // 게시글과 태그 저장은 하나의 트랜잭션으로 묶는 것이 안전합니다.
	public void insertTravelEdit(TravelEditDto dto) {

		// 1. 여행 게시글 저장 및 생성된 ID 확보
		travelEditMapper.insertTravelEdit(dto);
		// MyBatis의 useGeneratedKeys 덕분에 게시글 ID가 dto.getId()에 자동 설정됩니다.
		Long newArticleId = dto.getId();

		// 2. 태그 저장 및 연결 로직
		String tagString = dto.getTagString(); // 쉼표로 구분된 태그 문자열

		if (tagString != null && !tagString.isEmpty()) {
			// 태그 문자열을 개별 태그 이름으로 분리
			String[] tagNames = tagString.split(",");

			for (String tagName : tagNames) {
				String cleanTagName = tagName.trim();
				if (cleanTagName.isEmpty())
					continue;

				// 2-1. 태그 이름으로 기존 ID 조회
				Long tagId = travelTagMapper.findTagIdByName(cleanTagName);

				if (tagId == null) {
					// 2-2. 태그가 없으면 새로 삽입합니다.
					TravelTagDto newTag = new TravelTagDto(); // ✨ 새로운 DTO를 생성하여 ID를 받아옵니다.

					newTag.setTagName(cleanTagName);

					travelTagMapper.insertNewTag(newTag); // 삽입 후 newTag.getId()에 자동 생성 ID가 설정됨

					tagId = newTag.getId(); // ✨ DTO를 통해 생성된 ID를 즉시 받아옴 (재조회 필요 없음!)
				}

				// 2-3. 게시글과 태그를 중간 테이블에 연결
				if (tagId != null) {
					travelTagMapper.linkArticleAndTag(newArticleId, tagId);
				}
			}
		}

		// 이미지 처리 로직은 여기에 추가될 것입니다.
	}

	// 태그 이름을 이모지 포함 표시용 문자열로 변환하는 유틸리티 메서드
	private static final Map<String, String> TAG_MAP = Map.ofEntries(
			Map.entry("food", "🍜 식도락"),
			Map.entry("shower", "♨️ 온천"),
			Map.entry("culture", "🏛️ 문화"),
			Map.entry("nature", "🌲 자연"),
			Map.entry("shopping", "🛍️ 쇼핑"),
			Map.entry("activity", "🏃 액티비티"),
			Map.entry("healing", "🧘 힐링"),
			Map.entry("adventure", "🎒 모험"),
			Map.entry("night", "🌃 야경"),
			Map.entry("traditional", "🏮 전통"),
			Map.entry("field", "👘 현지체험"),
			Map.entry("min", "👥 소수정예"));

	// 지역 코드-텍스트 변환 맵
	private static final Map<String, String> LOCATION_MAP = Map.ofEntries(
			Map.entry("hokkaido", "홋카이도"),
			Map.entry("tohoku", "도호쿠"),
			Map.entry("kanto", "간토"),
			Map.entry("chubu", "주부"),
			Map.entry("kansai", "간사이"),
			Map.entry("chugoku", "주고쿠"),
			Map.entry("shikoku", "시코쿠"),
			Map.entry("kyushu", "규슈"),
			Map.entry("okinawa", "오키나와"));

	// 성별 코드-텍스트 변환
	private String mapGenderCodeToText(String code) {
		return switch (code.toLowerCase()) {
			case "m" -> "남성만";
			case "f" -> "여성만";
			default -> "제한 없음";
		};
	}

	// ✨ 모집 상태 코드-텍스트 변환 메서드 추가
	private String mapStatusCodeToText(String code) {
		if (code == null)
			return "알 수 없음";
		return switch (code.toUpperCase()) {
			case "OPEN" -> "모집 중";
			case "MATCHED" -> "모집 완료";
			case "CLOSED" -> "종료";
			case "CANCELED" -> "취소";
			default -> "알 수 없음";
		};
	}

	// ✨ 여행 상세 조회 로직 (데이터 가공 추가)
	public TravelEditDto getTravelDetail(Long id) {
		// 1. Mapper를 통해 기본 데이터 및 태그 코드 목록 조회
		TravelEditDto travelDetail = travelEditMapper.selectTravelDetail(id);

		if (travelDetail == null) {
			return null; // 데이터가 없으면 null 반환
		}

		// 2. 성별 제한 코드 -> 표시용 텍스트로 변환하여 DTO에 설정
		travelDetail.setDisplayGenderLimit(mapGenderCodeToText(travelDetail.getGenderLimit()));

		// 3. 태그 코드 목록 -> 이모지 포함 표시용 텍스트 목록으로 변환하여 DTO에 설정
		// DTO에 List<String> tagNames 필드가 존재한다고 가정합니다.
		if (travelDetail.getTagNames() != null) {
			List<String> displayTags = travelDetail.getTagNames().stream()
					.map(tagCode -> TAG_MAP.getOrDefault(tagCode, "#기타")) // 매핑된 텍스트로 변환
					.collect(Collectors.toList());

			travelDetail.setDisplayTags(displayTags); // DTO의 displayTags 필드에 저장
		}

		// 4. 모집 상태 변환 로직 추가
		travelDetail.setDisplayStatus(mapStatusCodeToText(travelDetail.getStatus()));

		// 5. ✨ 지역 코드 변환 로직 추가 (새로 추가)
		if (travelDetail.getLocation() != null) {
			String locationCode = travelDetail.getLocation().toLowerCase();
			// LOCATION_MAP에서 한글 이름을 찾아 DTO의 displayLocation 필드에 설정합니다.
			String displayLoc = LOCATION_MAP.getOrDefault(locationCode, locationCode);
			travelDetail.setDisplayLocation(displayLoc);
		}

		return travelDetail;
	}

}