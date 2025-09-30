package com.wakutabi.service;

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

	// 여행 상세 조회
	public TravelEditDto getTravelDetail(Long id) {
		return travelEditMapper.selectTravelDetail(id);
	}
}