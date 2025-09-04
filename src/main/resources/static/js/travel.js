/*$(document)
	.ready(*/
		function() {
			// 태그 클릭 이벤트
			$('.tag-item').click(function() {
				$(this).toggleClass('active');
				updateSearch();
			});

			// 검색 기능
			$('#searchInput').on('input', function() {
				updateSearch();
			});

			// 필터 변경 감지
			$('input[type="checkbox"], select').change(
				function() {
					updateSearch();
				});

			function updateSearch() {
				// 선택된 태그들
				var selectedTags = [];
				$('.tag-item.active').each(function() {
					selectedTags.push($(this).data('tag'));
				});

				// 검색어
				var searchQuery = $('#searchInput').val();

				// 실제 구현에서는 AJAX로 서버에 요청
				console.log('검색 조건:', {
					tags: selectedTags,
					query: searchQuery,
					filters: getFilterValues()
				});

				// 검색 결과 업데이트 (예시)
				updateResultCount(selectedTags.length,
					searchQuery);
			}

			function getFilterValues() {
				var filters = {
					regions: [],
					duration: $('select').first().val(),
					difficulties: [],
					groupSizes: []
				};

				// 지역 필터
				$('input[type="checkbox"]:checked')
					.each(
						function() {
							var id = $(this).attr(
								'id');
							if (['tokyo', 'osaka',
								'kyoto',
								'hiroshima']
								.includes(id)) {
								filters.regions
									.push(id);
							} else if (['easy',
								'medium',
								'hard']
								.includes(id)) {
								filters.difficulties
									.push(id);
							} else if (['small',
								'medium-group',
								'large']
								.includes(id)) {
								filters.groupSizes
									.push(id);
							}
						});

				return filters;
			}

			function updateResultCount(tagCount, query) {
				var baseCount = 24;
				var newCount = Math.max(1, baseCount
					- (tagCount * 3)
					- (query.length > 0 ? 5 : 0));
				$('.search-result-count').text(
					'총 ' + newCount + '개');
			}

			// 카드 호버 효과
			$('.schedule-card')
				.hover(
					function() {
						$(this)
							.find('.btn')
							.removeClass(
								'btn-wakutabi-outline')
							.addClass(
								'btn-wakutabi-primary');
					},
					function() {
						$(this)
							.find('.btn')
							.first()
							.removeClass(
								'btn-wakutabi-primary')
							.addClass(
								'btn-wakutabi-outline');
					});
		}/*);*/